package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ReachACheck extends Check implements Listener {


    public static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};
    private ToppeCheat plugin;
    private Map<UUID, ReachEntry> ReachTicks = new HashMap();

    public ReachACheck(ToppeCheat plugin) {
        super(CheckType.REACH_A);
        this.plugin = plugin;
    }

    public static double averageDouble(List<Double> list) {
        Double add = Double.valueOf(0.0D);
        for (Double listlist : list) {
            add = Double.valueOf(add.doubleValue() + listlist.doubleValue());
        }
        return add.doubleValue() / list.size();
    }

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static Location getEyeLocation(Player player) {
        Location eye = player.getLocation();
        eye.setY(eye.getY() + player.getEyeHeight());
        return eye;
    }

    public static BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, false);
    }

    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[(Math.round(yaw / 45.0F) & 0x7)];
        }
        return axis[(Math.round(yaw / 90.0F) & 0x3)];
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
        if ((yawToFace(damager.getLocation().getYaw()) == BlockFace.NORTH) && (yawToFace(player.getLocation().getYaw()) != BlockFace.SOUTH)) {
            return;
        }
        if ((yawToFace(damager.getLocation().getYaw()) == BlockFace.SOUTH) && (yawToFace(player.getLocation().getYaw()) != BlockFace.NORTH)) {
            return;
        }
        if ((yawToFace(damager.getLocation().getYaw()) == BlockFace.EAST) && (yawToFace(player.getLocation().getYaw()) != BlockFace.WEST)) {
            return;
        }
        if ((yawToFace(damager.getLocation().getYaw()) == BlockFace.WEST) && (yawToFace(player.getLocation().getYaw()) != BlockFace.EAST)) {
            return;
        }
        if (damager.getAllowFlight()) {
            return;
        }
        if (player.getAllowFlight()) {
            return;
        }
        long Time = System.currentTimeMillis();
        List<Double> Reachs = new ArrayList();
        if (this.ReachTicks.containsKey(damager.getUniqueId())) {
            Time = this.ReachTicks.get(damager.getUniqueId()).getLastTime().longValue();
            Reachs = new ArrayList(this.ReachTicks.get(damager.getUniqueId()).getReachs());
        }
        double MaxReach = 3.4D;
        if (damager.hasPotionEffect(PotionEffectType.SPEED)) {
            int Level2 = 0;
            for (PotionEffect Effect : damager.getActivePotionEffects()) {
                if (Effect.getType().equals(PotionEffectType.SPEED)) {
                    Level2 = Effect.getAmplifier();
                    break;
                }
            }
            switch (Level2) {
                case 0:
                    MaxReach = 3.4D;
                    break;
                case 1:
                    MaxReach = 3.5D;
                    break;
                case 2:
                    MaxReach = 3.6D;
                    break;
                case 3:
                    MaxReach = 3.7D;
                    break;
                case 4:
                    MaxReach = 3.8D;
                    break;
                case 5:
                    MaxReach = 3.9D;
                    break;
                default:
                    return;
            }
        }
        if ((player.getVelocity().length() > 0.08D)) {
            return;
        }
        double Reach = getEyeLocation(damager).distance(player.getLocation());
        int Ping = plugin.getNMSAccessProvider().getAccess().getPing(player);
        if ((Ping >= 130) && (Ping < 150)) {
            MaxReach += 0.07D;
        } else if ((Ping >= 150) && (Ping < 250)) {
            MaxReach += 0.1D;
        } else if ((Ping >= 250) && (Ping < 300)) {
            MaxReach += 0.2D;
        } else if ((Ping >= 300) && (Ping < 350)) {
            MaxReach += 0.5D;
        } else if ((Ping >= 350) && (Ping < 400)) {
            MaxReach += 0.7D;
        } else if (Ping > 400) {
            return;
        }
        if (Reach > (MaxReach = MaxReach + Math.abs((getEyeLocation(player).getY() - getEyeLocation(damager).getY()) / 2.0D))) {
            Reachs.add(Double.valueOf(Reach));
            Time = System.currentTimeMillis();
        }
        if ((this.ReachTicks.containsKey(damager.getUniqueId())) && (elapsed(Time, 25000L))) {
            Reachs.clear();
            Time = System.currentTimeMillis();
        }
        if (Reachs.size() > 3) {
            Double AverageReach = Double.valueOf(averageDouble(Reachs));
            Double A = Double.valueOf(4.8D - MaxReach);
            if (A.doubleValue() < 0.0D) {
                A = Double.valueOf(0.0D);
            }
            Double.valueOf(AverageReach.doubleValue() - MaxReach).doubleValue();
            Reachs.clear();
            int i = ViolationLevels.getLevel(player, getType(), ViolationLevels.getDefaultExpiration()) + 1;
            new Alert(player, "type A", this, plugin);
            ViolationLevels.setVL(player, getType(), i);
            if (i >= getSettings().getAutobanThreshold()) {
                new Autoban(player, this, plugin);
            }
        }
        this.ReachTicks.put(damager.getUniqueId(), new ReachEntry(Reachs, Long.valueOf(Time)));
    }

    public class ReachEntry {

        public Long LastTime;
        public List<Double> Reachs;

        public ReachEntry(List<Double> reaches, long lastTime) {
            this.Reachs = reaches;
            this.LastTime = LastTime;
        }

        public Long getLastTime() {
            return this.LastTime;
        }

        public void setLastTime(Long LastTime) {
            this.LastTime = LastTime;
        }

        public List<Double> getReachs() {
            return this.Reachs;
        }

        public void setReachs(List<Double> Reachs) {
            this.Reachs = Reachs;
        }

        public void addReach(Double Reach) {
            this.Reachs.add(Reach);
        }
    }

}
