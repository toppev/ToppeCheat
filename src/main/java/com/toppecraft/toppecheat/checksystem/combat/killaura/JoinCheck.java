package com.toppecraft.toppecheat.checksystem.combat.killaura;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class JoinCheck extends Check implements Listener {


    public HashMap<UUID, Integer> join = new HashMap<UUID, Integer>();
    public HashSet<UUID> handled = new HashSet<UUID>();
    private ToppeCheat plugin;
    private SPGameEndListener spListener;

    public JoinCheck(ToppeCheat plugin) {
        super(CheckType.KILLAURA_JOIN);
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        handled.remove(e.getPlayer().getUniqueId());
        join.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent e) {
        Player p = e.getPlayer();
        if (e.getAnimationType() == PlayerAnimationType.ARM_SWING && join.containsKey(p.getUniqueId())) {
            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) + 1;
            new Alert(p, "Type A" + join.getOrDefault(p.getUniqueId(), 0), this, plugin);
            ViolationLevels.setVL(p, getType(), i);
            if (i >= getSettings().getAutobanThreshold()) {
                new Autoban(p, this, plugin);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            if (join.containsKey(p.getUniqueId())) {
                int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) + 1;
                new Alert(p, "Type B" + join.getOrDefault(p.getUniqueId(), 0), this, plugin);
                ViolationLevels.setVL(p, getType(), i);
                if (i >= getSettings().getAutobanThreshold()) {
                    new Autoban(p, this, plugin);
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Player p = (Player) e.getDamager();
        if (e.getDamager() instanceof Player && join.containsKey(p.getUniqueId())) {
            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) + 1;
            new Alert(p, "Type C" + join.getOrDefault(p.getUniqueId(), 0) + (join.getOrDefault(p.getUniqueId(), 0) > 2 ? (" severe") : ""), this, plugin);
            ViolationLevels.setVL(p, getType(), i);
            if (i >= getSettings().getAutobanThreshold()) {
                new Autoban(p, this, plugin);
            }
        }
    }


    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (Bukkit.getPluginManager().getPlugin("StrikePractice") != null) {
            Bukkit.getPluginManager().registerEvents(spListener = new SPGameEndListener(this), plugin);
        }
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
        if (spListener != null) {
            HandlerList.unregisterAll(spListener);
        }
    }
}
