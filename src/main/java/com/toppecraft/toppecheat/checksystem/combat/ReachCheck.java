package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.lagmeter.LagMeter.LagNotifyNeeder;
import com.toppecraft.toppecheat.packetlistener.listener.TACPacketListener;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.utils.PlayerLocationUtils;
import com.toppecraft.toppecheat.utils.PotionEffectUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class ReachCheck extends Check implements LagNotifyNeeder, Listener {

    private final static String reached = "ToppeCheatReachCheckReached";
    private final static String time = "ToppeCheatReachCheckTimer";

    private ToppeCheat plugin;
    private long checkTime;

    public ReachCheck(ToppeCheat plugin) {
        super(CheckType.REACHING);
        this.plugin = plugin;
    }

    private static double getExtraDistance(Entity damaged) {
        if (damaged instanceof EnderDragon) {
            return 6.5;
        }
        if (damaged instanceof Giant) {
            return 1.5;
        }
        return 0;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity tar = (LivingEntity) e.getEntity();
            if (tar.getMaximumNoDamageTicks() < 15) {
                return;
            }
        }
        final Player p = (Player) e.getDamager();
        Entity ent = e.getEntity();
        if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
            return;
        }
        Location pLoc = p.getLocation();
        Location dLoc = ent.getLocation();
        double maxDistance = 3.7 + getExtraDistance(ent) + (p.getGameMode().equals(GameMode.CREATIVE) ? 2.5 : 0);
        final Vector vec = dLoc.toVector().subtract(pLoc.toVector().setY(dLoc.getY()));
        double result = vec.length() - maxDistance;
        if (result > 0 && !plugin.getLagMeter().isLagger(p)) {
            double d = 0.25;
            boolean facing = MathUtils.calcDistance(AimbotCheck.clamp(dLoc.getYaw()), AimbotCheck.clamp(pLoc.getYaw())) > 145;
            if (ent instanceof Player && ((Player) ent).isSprinting() && !facing) {
                d += 0.15;
            }
            if (p.isSprinting() && !facing) {
                d += 0.15;
            }
            HashSet<Material> materials = PlayerLocationUtils.getMaterials(p.getLocation().subtract(0, 2, 0));
            if (materials.contains(Material.ICE) || materials.contains(Material.PACKED_ICE)) {
                d += 0.2;
            }
            for (Material mat : PlayerLocationUtils.getMaterials(p.getLocation().add(0, 2, 0))) {
                if (mat != Material.AIR) {
                    d += 0.2;
                    break;
                }
            }
            if (p.hasPotionEffect(PotionEffectType.SPEED)) {
                d += 0.1;
                d += (PotionEffectUtils.getAmplifier(p, PotionEffectType.SPEED) / 4);
            }
            result -= 2 * d;
            if (result > 0) {
                if (p.hasMetadata(reached)) {
                    MetadataValue m = plugin.getMeta(p, reached);
                    if (m != null && m.value() != null) {
                        final double reach = m.asDouble() + result;
                        p.setMetadata(reached, new FixedMetadataValue(plugin, reach));
                        if (p.hasMetadata(time)) {
                            MetadataValue m2 = plugin.getMeta(p, time);
                            if (m2 != null && m2.value() != null) {
                                if (m2.asLong() + checkTime < System.currentTimeMillis()) {
                                    p.removeMetadata(reached, plugin);
                                    p.removeMetadata(time, plugin);
                                    if (reach >= getSettings().getThreshold()) {
                                        new BukkitRunnable() {

                                            @Override
                                            public void run() {
                                                if (p != null) {
                                                    int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                                                    i = (int) (reach < getSettings().getThreshold() + 10 ? i + reach - getSettings().getThreshold() + 1 : i + 11);
                                                    new Alert(p, "Reached in " + (checkTime / 1000) + " s: " + MathUtils.toTwoDecimals(reach) + " blocks", ReachCheck.this, plugin);
                                                    ViolationLevels.setVL(p, getType(), i);
                                                    if (i >= getSettings().getAutobanThreshold()) {
                                                        new Autoban(p, ReachCheck.this, plugin);
                                                    }
                                                }
                                            }
                                        }.runTask(plugin);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    p.setMetadata(reached, new FixedMetadataValue(plugin, result));
                    p.setMetadata(time, new FixedMetadataValue(plugin, System.currentTimeMillis()));
                }
            }
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLagMeter().registerLagNotifyNeeder(this);
        checkTime = plugin.getFileManager().getSettingsFile().getConfig().getLong(getType().getName() + ".check-time") * 1000;
        TACPacketListener.reachCheck = this;
    }

    @Override
    public void disable() {
        super.disable();
        TACPacketListener.reachCheck = null;
        HandlerList.unregisterAll(this);
        plugin.getLagMeter().getRegisteredMatters().remove(this);
    }

    @Override
    public void onLag(Player p) {
        p.removeMetadata(reached, plugin);
        p.removeMetadata(time, plugin);
    }

}
