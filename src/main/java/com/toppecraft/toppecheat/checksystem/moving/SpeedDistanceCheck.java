package com.toppecraft.toppecheat.checksystem.moving;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MineCraftVersionManager;
import com.toppecraft.toppecheat.utils.PlayerLocationUtils;
import com.toppecraft.toppecheat.utils.PotionEffectUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;

public class SpeedDistanceCheck extends Check implements Listener {

    private static final String lv = "ToppeCheatSpeedLevel";
    private static final String time = "ToppeCheatSpeedTime";
    private ToppeCheat plugin;

    public SpeedDistanceCheck(ToppeCheat plugin) {
        super(CheckType.DISTANCE_SPEED);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void distanceCheckMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (MineCraftVersionManager.getVersionManager().isGliding(p) || MineCraftVersionManager.getVersionManager().getLevitationAmplifier(p) > 0) {
            return;
        }
        if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
            return;
        }
        if (plugin.getLagMeter().isLagger(p) || p.getAllowFlight() || p.getGameMode() == GameMode.CREATIVE || p.getLocation().getBlock().getType() != Material.AIR || p.getEyeLocation().getBlock().getType() != Material.AIR) {
            p.removeMetadata(lv, plugin);
            return;
        }
        if (p.isInsideVehicle() || !e.getFrom().getWorld().getName().equalsIgnoreCase(e.getTo().getWorld().getName()) || p.hasMetadata("MauLaunchers-Disable_fall_damage")) {
            p.removeMetadata(lv, plugin);
            return;
        }
        double s = 0.333;
        HashSet<Material> materials = PlayerLocationUtils.getMaterials(p.getLocation().subtract(0, 2, 0));
        if (materials.contains(Material.ICE) || materials.contains(Material.PACKED_ICE)) {
            s += 0.2;
        }
        for (Material mat : PlayerLocationUtils.getMaterials(p.getLocation().add(0, 2, 0))) {
            if (mat != Material.AIR) {
                s += 0.2;
                break;
            }
        }
        if (p.hasPotionEffect(PotionEffectType.SPEED)) {
            s += (PotionEffectUtils.getAmplifier(p, PotionEffectType.SPEED) / 4);
            s += 0.1;
        }
        Location from = e.getFrom().clone();
        Location to = e.getTo().clone();
        from.setY(0);
        to.setY(0);
        double dis = from.distanceSquared(to);
        if (p.hasMetadata(time)) {
            MetadataValue m = plugin.getMeta(p, time);
            if (m != null && m.value() != null && System.currentTimeMillis() > m.asLong()) {
                p.removeMetadata(lv, plugin);
            }
        }
        if (p.hasMetadata(lv)) {
            MetadataValue m = plugin.getMeta(p, lv);
            if (m != null && m.value() != null) {
                if (m.asDouble() > getSettings().getThreshold()) {
                    p.removeMetadata(lv, plugin);
                    int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                    i = i + 1;
                    if (i > 1) {
                        ViolationLevels.setVL(p, getType(), i);
                        new Alert(p, "", this, plugin);
                        if (i == getSettings().getAutobanThreshold()) {
                            new Autoban(p, this, plugin);
                        }
                    } else {
                        ViolationLevels.setVL(p, getType(), i);
                    }
                } else {
                    double i = m.asDouble() + dis - s * s;
                    p.setMetadata(lv, new FixedMetadataValue(plugin, i));
                    return;
                }
            }
        }
        p.setMetadata(time, new FixedMetadataValue(plugin, System.currentTimeMillis() + 2000));
        p.setMetadata(lv, new FixedMetadataValue(plugin, dis - s * s));
    }

    @EventHandler
    public void onVelocityChange(PlayerVelocityEvent e) {
        double d = Math.abs(e.getVelocity().getX()) + Math.abs(e.getVelocity().getZ());
        long bypassTime = (long) (d * 2500);
        if (plugin.getCheckBypassManager().getBypass(e.getPlayer(), getType()) < bypassTime) {
            plugin.getCheckBypassManager().setBypass(e.getPlayer(), getType(), bypassTime);
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }

}
