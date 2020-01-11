package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ConstantCPSCheck extends Check implements Listener {


    private HashMap<UUID, Set<Integer>> cpsHistory = new HashMap<UUID, Set<Integer>>();
    private HashMap<UUID, Long> time = new HashMap<UUID, Long>();
    private HashMap<UUID, Integer> cps = new HashMap<UUID, Integer>();


    private ToppeCheat plugin;

    public ConstantCPSCheck(ToppeCheat plugin) {
        super(CheckType.CONSTANT_CPS);
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
            UUID uuid = p.getUniqueId();
            if (time.containsKey(uuid)) {
                if (time.get(uuid) + 1000 < System.currentTimeMillis()) {
                    Set<Integer> clicks;
                    if (cpsHistory.containsKey(uuid)) {
                        clicks = cpsHistory.get(uuid);
                    } else {
                        clicks = new HashSet<Integer>();
                    }
                    clicks.add(cps.getOrDefault(uuid, 1));
                    time.put(uuid, System.currentTimeMillis());
                    if (clicks.size() == getSettings().getThreshold()) {
                        int errors = 0;
                        int current = -1;
                        int validClicks = 0;
                        double c = 0;
                        for (int x : clicks) {
                            //is really clicking
                            if (x > 5) {
                                validClicks++;
                                if (current != -1) {
                                    if (x != current) {
                                        int er = (int) MathUtils.calcDistance(current, x);
                                        if (er < 1) {
                                            er = 1;
                                        }
                                        errors += er;
                                    }
                                }
                                current = x;
                            }
                            c += x;
                        }
                        c /= clicks.size();
                        if (errors > getSettings().getThreshold() / 12) {
                            //too many errors, let's see if the player's clicking is still too suspicious
                            //hitting
                            if (c > 15) {
                                //either very fast jitterclicking, butterflyclicking or cheating, let's allow some more errors
                                errors -= getSettings().getThreshold() / 10;
                            } else if (c > 13) {
                                //still pretty fast
                                errors -= getSettings().getThreshold() / 20;
                            } else if (c > 11) {
                                //12-13 cps, just in case
                                errors -= getSettings().getThreshold() / 25;
                            }
                        }
                        if (validClicks >= getSettings().getThreshold() / 1.25 && errors <= getSettings().getThreshold() / 10) {
                            int i = ViolationLevels.getLevel(p, getType());
                            ViolationLevels.setVL(p, getType(), i++);
                            if (i % getSettings().getThreshold() == 0) {
                                Alert alert = new Alert(p, "Avrg. Cps: " + c + " Errors: " + errors + " (hover to see clicks)", plugin.getLagMeter().getLag(p), this, plugin, false);
                                String clicksString = "";
                                for (int x : clicks) {
                                    if (x >= 17) {
                                        clicksString += ChatColor.DARK_RED + "" + ChatColor.BOLD + x;
                                    } else if (x > 15) {
                                        clicksString += ChatColor.RED + "" + ChatColor.BOLD + x;
                                    } else if (x > 12) {
                                        clicksString += ChatColor.YELLOW + "" + ChatColor.BOLD + x;
                                    } else if (x >= 10) {
                                        clicksString += ChatColor.GRAY + "" + ChatColor.BOLD + x;
                                    } else {
                                        clicksString += ChatColor.GREEN + "" + ChatColor.BOLD + x;
                                    }
                                }
                                alert.hover = clicksString;
                                if (alert.callEvent()) {
                                    alert.notifyStaff(true);
                                }
                            }
                            if (i >= getSettings().getAutobanThreshold()) {
                                new Autoban(p, this, plugin);
                            }
                            //free up some memory
                            clicks.clear();
                        }
                    }
                    cpsHistory.put(uuid, clicks);
                }
            } else {
                time.put(uuid, System.currentTimeMillis());
            }
            int c = cps.getOrDefault(uuid, 0);
            cps.put(uuid, c++);
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        cps.clear();
        time.clear();
        cpsHistory.clear();
        super.disable();
        HandlerList.unregisterAll(this);
    }
}