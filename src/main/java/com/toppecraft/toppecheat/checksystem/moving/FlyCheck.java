package com.toppecraft.toppecheat.checksystem.moving;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.utils.MineCraftVersionManager;
import com.toppecraft.toppecheat.utils.PlayerLocationUtils;
import com.toppecraft.toppecheat.utils.PotionEffectUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author Toppe5
 * @since 2.0
 */
public class FlyCheck extends Check implements Listener {

    private final static String lv = "ToppeCheatFlyLevel";
    private final static String fromLoc = "ToppeCheatFlyCheckFromLocaiton";
    private final static String firstLoc = "ToppeCheatFlyCheckFirstFailLocaiton";
    private ToppeCheat plugin;
    private BukkitTask task;

    /**
     * Creates a new FlyCheck system with the given ToppeCheat plugin.
     *
     * @param plugin the ToppeCheat plugin.
     */
    public FlyCheck(ToppeCheat plugin) {
        super(CheckType.FLY);
        this.plugin = plugin;
    }

    private void startTask() {
        if (task != null) {
            task.cancel();
        }
        task = new BukkitRunnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!MineCraftVersionManager.getVersionManager().isGliding(p)
                            && MineCraftVersionManager.getVersionManager().getLevitationAmplifier(p) < 1
                            && !p.hasMetadata("MauLaunchers-Disable_fall_damage") && !p.isInsideVehicle() && !p.getAllowFlight()
                            && !p.isFlying() && !plugin.getCheckBypassManager().hasBypass(p, getType())) {
                        if (!PlayerLocationUtils.isReallyInAir(p, plugin) || plugin.getLagMeter().isLagger(p)) {
                            p.setMetadata(firstLoc, new FixedMetadataValue(plugin, p.getLocation()));
                            p.removeMetadata(lv, plugin);
                        } else if (!p.hasMetadata(firstLoc) || p.isOnGround()) {
                            p.setMetadata(firstLoc, new FixedMetadataValue(plugin, p.getLocation()));
                        } else {
                            Location from = getLastLocation(p).clone();
                            double y1 = from.getY();
                            double y2 = p.getLocation().getY();
                            p.setMetadata(fromLoc, new FixedMetadataValue(plugin, p.getLocation().clone()));
                            if (flyMove(y1, y2)) {
                                int i = 1;
                                if (p.hasMetadata(lv)) {
                                    MetadataValue m = plugin.getMeta(p, lv);
                                    if (m != null && m.value() != null) {
                                        i = m.asInt() + 1;
                                    }
                                }
                                p.setMetadata(lv, new FixedMetadataValue(plugin, i));
                                if (i % (getSettings().getThreshold()) == 0 && !plugin.getLagMeter().isLagger(p)) {
                                    ViolationLevels.increase(p, getType(), i / 10, ViolationLevels.getDefaultExpiration());
                                    if (p.hasMetadata(firstLoc)) {
                                        MetadataValue m = plugin.getMeta(p, firstLoc);
                                        Location loc = null;
                                        if (m != null && m.value() != null && m.value() instanceof Location) {
                                            loc = (Location) m.value();
                                            Block b = p.getWorld().getHighestBlockAt(loc);
                                            if (b.getY() < loc.getY()) {
                                                loc = b.getLocation().add(0, 1, 0);
                                            }
                                        }
                                        new Alert(p, "in air ticks: " + i + ", distance: " + MathUtils.toTwoDecimals(loc.distance(p.getLocation())) + "", FlyCheck.this, plugin);
                                        if (loc != null && plugin.getFileManager().getSettingsFile().getConfig().getBoolean("fly.tp-back-on-fail")) {
                                            loc.setYaw(p.getLocation().getYaw());
                                            loc.setPitch(p.getLocation().getPitch());
                                            p.teleport(loc);
                                        }
                                        if (ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) == getSettings().getAutobanThreshold()) {
                                            new Autoban(p, FlyCheck.this, plugin);
                                        }
                                    }
                                }
                            } else {
                                p.setMetadata(firstLoc, new FixedMetadataValue(plugin, p.getLocation()));
                                p.removeMetadata(lv, plugin);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 2, 1);
    }

    private boolean flyMove(double from, double to) {
        if (from <= to) {
            return true;
        }
        return MathUtils.calcDistance(from, to) < 0.25;
    }

    private Location getLastLocation(Player p) {
        if (p.hasMetadata(fromLoc)) {
            MetadataValue m = plugin.getMeta(p, fromLoc);
            if (m != null && m.value() != null && m.value() instanceof Location) {
                return (Location) m.value();
            }
        }
        return p.getLocation();
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.getReason().equalsIgnoreCase("Flying is not enabled on this server")) {
            if (plugin.getFileManager().getSettingsFile().getConfig().getBoolean("fly.notify-kick")) {
                new Alert(plugin.getFileManager().getMessagesFile().getMessage("kicked-for-flying").replace("<player>", e.getPlayer().getName()), true);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
		/*
		if(e.getPlayer().getGameMode() == GameMode.ADVENTURE && e.getPlayer().getName().equals("Toppe5")) {
			e.getPlayer().sendMessage(MathUtils.toTwoDecimals(e.getPlayer().getVelocity().getX()) + ", " + MathUtils.toTwoDecimals(e.getPlayer().getVelocity().getY())+ ", " + MathUtils.toTwoDecimals(e.getPlayer().getVelocity().getZ()));
		}
		 */
        if (e.getPlayer().isOnGround()) {
            e.getPlayer().removeMetadata(lv, plugin);
            if (e.getPlayer().hasPotionEffect(PotionEffectType.JUMP)) {
                plugin.getCheckBypassManager().setBypass(e.getPlayer(), getType(),
                        plugin.getCheckBypassManager().getBypass(e.getPlayer(), getType()) + 200 * PotionEffectUtils.getAmplifier(e.getPlayer(), PotionEffectType.JUMP) + 250);
            }
        }
    }

    @EventHandler
    public void onVelocityChange(PlayerVelocityEvent e) {
        long bypassTime = (long) (e.getVelocity().getY() * 2500);
        if (plugin.getCheckBypassManager().getBypass(e.getPlayer(), getType()) < bypassTime) {
            plugin.getCheckBypassManager().setBypass(e.getPlayer(), getType(), bypassTime);
        }
    }

    @EventHandler
    public void onExplosion(ExplosionPrimeEvent e) {
        for (Entity ent : e.getEntity().getNearbyEntities(e.getRadius(), e.getRadius(), e.getRadius())) {
            if (ent instanceof Player) {
                Player p = (Player) ent;
                double r = e.getRadius() - ent.getLocation().distanceSquared(e.getEntity().getLocation());
                long bypassTime = (long) (r * 2000);
                if (plugin.getCheckBypassManager().getBypass(p, getType()) < bypassTime) {
                    plugin.getCheckBypassManager().setBypass(p, getType(), bypassTime);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == DamageCause.VOID) {
            if (e.getEntity() instanceof Player) {
                if (plugin.getCheckBypassManager().getBypass((Player) e.getEntity(), getType()) < 2000) {
                    plugin.getCheckBypassManager().setBypass((Player) e.getEntity(), getType(), 2000);
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getWorld().getName().equals(e.getTo().getWorld().getName())) {
            if (plugin.getCheckBypassManager().getBypass(e.getPlayer(), getType()) < 500) {
                plugin.getCheckBypassManager().setBypass(e.getPlayer(), getType(), 500);
            }
        } else {
            if (plugin.getCheckBypassManager().getBypass(e.getPlayer(), getType()) < 1000) {
                plugin.getCheckBypassManager().setBypass(e.getPlayer(), getType(), 1000);
            }
        }
        e.getPlayer().setMetadata(firstLoc, new FixedMetadataValue(plugin, e.getTo()));
    }


    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startTask();
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
        task.cancel();
    }
}
