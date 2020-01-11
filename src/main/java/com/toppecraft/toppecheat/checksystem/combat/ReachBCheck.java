package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class ReachBCheck extends Check implements Listener {


    private HashMap<Player, Long> lastViolation = new HashMap();
    private HashMap<Player, Integer> outOfReachHits = new HashMap();

    private ToppeCheat plugin;

    public ReachBCheck(ToppeCheat plugin) {
        super(CheckType.REACH_B);
        this.plugin = plugin;
    }

    public static double getDistance(double p1, double p2, double p3, double p4) {
        double delta1 = p3 - p1;
        double delta2 = p4 - p2;
        return Math.sqrt(delta1 * delta1 + delta2 * delta2);
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player damager = (Player) e.getDamager();
        Player player = (Player) e.getEntity();
        if (damager.getAllowFlight()) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        double MaxReach = 3.3D + player.getVelocity().length() * 3.5D + damager.getVelocity().length() * 3.5D;

        double Reach = getDistance(damager.getLocation().getX(), damager.getLocation().getZ(), player.getLocation().getX(), player.getLocation().getZ());
        int Ping = plugin.getNMSAccessProvider().getAccess().getPing(damager);
        if ((Ping >= 150) && (Ping < 200)) {
            MaxReach += 0.1D;
        } else if ((Ping >= 200) && (Ping < 250)) {
            MaxReach += 0.2D;
        } else if ((Ping >= 250) && (Ping < 300)) {
            MaxReach += 0.4D;
        } else if ((Ping >= 300) && (Ping < 350)) {
            MaxReach += 0.7D;
        } else if ((Ping >= 350) && (Ping < 400)) {
            MaxReach += 0.9D;
        } else if (Ping > 400) {
            return;
        }
        if (damager.getLocation().getY() > player.getLocation().getY()) {
            double Difference = damager.getLocation().getY() - player.getLocation().getY();
            MaxReach += Difference / 1.0D;
        } else if (player.getLocation().getY() > damager.getLocation().getY()) {
            double Difference = player.getLocation().getY() - damager.getLocation().getY();
            MaxReach += Difference / 1.0D;
        }
        if (Reach > MaxReach) {
            if (!this.outOfReachHits.containsKey(damager)) {
                this.outOfReachHits.put(damager, Integer.valueOf(1));
            } else {
                this.outOfReachHits.put(damager, Integer.valueOf(this.outOfReachHits.get(damager).intValue() + 1));
                if (this.outOfReachHits.get(damager).intValue() > 2) {
                    this.outOfReachHits.remove(damager);
                    if ((this.lastViolation.containsKey(damager)) &&
                            (System.currentTimeMillis() - this.lastViolation.get(damager).longValue() < 2000L)) {
                        return;
                    }
                    this.lastViolation.put(damager, Long.valueOf(System.currentTimeMillis()));
                    int i = ViolationLevels.getLevel(damager, getType(), ViolationLevels.getDefaultExpiration()) + 1;
                    if (i % (getSettings().getThreshold()) == 0) {
                        new Alert(damager, "type B", this, plugin);
                    }
                    ViolationLevels.setVL(damager, getType(), i);
                    if (i >= getSettings().getAutobanThreshold()) {
                        new Autoban(damager, this, plugin);
                    }
                }
            }
        } else if (this.outOfReachHits.containsKey(damager)) {
            this.outOfReachHits.put(damager, Integer.valueOf(this.outOfReachHits.get(damager).intValue() - 1));
        }
    }

    @EventHandler
    public void clear(PlayerQuitEvent e) {
        this.lastViolation.remove(e.getPlayer());
    }
}
