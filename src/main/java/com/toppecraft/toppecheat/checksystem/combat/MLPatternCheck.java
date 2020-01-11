package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.lagmeter.LagMeter.LagNotifyNeeder;
import com.toppecraft.toppecheat.playerdata.PlayerData;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.UUID;

public class MLPatternCheck extends Check implements Listener, LagNotifyNeeder {


    private static final String time = "ToppeCheatMLCPSTimestamp";
    private static final String tag = "ToppeCheatMLCPSCount";
    private HashMap<UUID, Long> firstViolation = new HashMap<UUID, Long>();
    private HashMap<UUID, Long> lastDamage = new HashMap<UUID, Long>();
    private ToppeCheat plugin;

    public MLPatternCheck(ToppeCheat plugin) {
        super(CheckType.MACHINE_LEARNING_PATTERN);
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
            return;
        }
        if (e.getAction().equals(Action.LEFT_CLICK_AIR)) {
            if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.FISHING_ROD) {
                return;
            }
            if (p.hasMetadata(tag) && p.hasMetadata(time)) {
                MetadataValue m = plugin.getMeta(p, tag);
                if (m != null && m.value() != null) {
                    MetadataValue m2 = plugin.getMeta(p, time);
                    if (m2 != null && m2.value() != null) {
                        int cps = m.asInt() + 1;
                        if (m2.asLong() <= System.currentTimeMillis()) {
                            Bukkit.broadcastMessage("b");
                            if (cps >= 4 && !plugin.getLagMeter().isLagger(p) && p.getNearbyEntities(10, 10, 10).size() > 0
                                    && lastDamage.containsKey(p.getUniqueId()) && lastDamage.get(p.getUniqueId()) + 5000 > System.currentTimeMillis()) {
                                PlayerData data = PlayerData.getOnlinePlayerData(p);
                                double average = 0;
                                for (double c : data.getClicking()) {
                                    average += c;
                                }
                                average /= data.getClicking().size();
                                double diff = cps - average;
                                Bukkit.broadcastMessage(data.getAllRecordedClicks() + "");
                                if (data.getAllRecordedClicks() >= 300) {
                                    Bukkit.broadcastMessage("d");
                                    if (diff > getSettings().getThreshold() / 10) {
                                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                                        if (i <= 0 || !firstViolation.containsKey(p.getUniqueId())) {
                                            firstViolation.put(p.getUniqueId(), System.currentTimeMillis());
                                        }
                                        int x = (int) (i + diff - getSettings().getThreshold());
                                        if (x < 1) {
                                            x = 1;
                                        }
                                        ViolationLevels.setVL(p, getType(), x);
                                        long firstVL = firstViolation.containsKey(p.getUniqueId()) ? firstViolation.get(p.getUniqueId()) : System.currentTimeMillis() - 2000;
                                        Bukkit.broadcastMessage("f");
                                        if (i % (getSettings().getThreshold()) == 0) {
                                            new Alert(p, "Fail period: " + toNiceFormat(System.currentTimeMillis() - firstVL) + "/Learned: " + toNiceFormat(data.getAllRecordedClicks() * 1000), this, plugin);
                                        }
                                        if (i >= getSettings().getAutobanThreshold()) {
                                            new Autoban(p, this, plugin);
                                        }
                                    } else if (MathUtils.calcDistance(cps, average) < getSettings().getThreshold() / 10) {
                                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) - 1;
                                        if (i >= 0) {
                                            ViolationLevels.setVL(p, getType(), i);
                                        }
                                    }
                                }
                                if (data.getClicking().size() > 60 && diff < getSettings().getThreshold() / 10 && diff > -(getSettings().getThreshold() / 10) && cps < 20) {
                                    data.addCPS(cps);
                                } else if (data.getClicking().size() <= 60) {
                                    data.addCPS(cps);
                                }
                            }
                            p.removeMetadata(tag, plugin);
                            p.removeMetadata(time, plugin);
                        } else {
                            p.setMetadata(tag, new FixedMetadataValue(plugin, cps));
                        }
                        return;
                    }
                }
            }
            p.setMetadata(tag, new FixedMetadataValue(plugin, 1));
            p.setMetadata(time, new FixedMetadataValue(plugin, System.currentTimeMillis() + 1000));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            lastDamage.put(e.getDamager().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (!e.getFrom().getWorld().getName().equals(e.getTo().getWorld().getName())
                || e.getFrom().distanceSquared(e.getTo()) > 300) {
            lastDamage.remove(e.getPlayer().getUniqueId());
        }
    }

    private String toNiceFormat(long time) {
        int seconds = (int) (time / 1000);
        if (seconds == 1) {
            return seconds + " second";
        }
        if (seconds < 60) {
            return seconds + " seconds";
        }
        int minutes = seconds / 60;
        if (minutes == 1) {
            return minutes + " minute";
        }
        if (minutes < 60) {
            return minutes + " minutes";
        }
        int hours = minutes / 60;
        if (hours == 1) {
            return hours + " hour";
        }
        return seconds + " hours";
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLagMeter().registerLagNotifyNeeder(this);
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
        plugin.getLagMeter().getRegisteredMatters().remove(this);
    }

    @Override
    public void onLag(Player p) {
        p.removeMetadata(tag, plugin);
        p.removeMetadata(time, plugin);
    }

}
