package com.toppecraft.toppecheat.checksystem.combat.killaura;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.packetlistener.listener.TACPacketListener;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

public class MissHitRation extends Check implements Listener {


    private HashMap<UUID, Integer> clicks = new HashMap<UUID, Integer>();
    private HashMap<UUID, Integer> attacks = new HashMap<UUID, Integer>();

    private ToppeCheat plugin;

    public MissHitRation(ToppeCheat plugin) {
        super(CheckType.MISS_HIT_RATION);
        this.plugin = plugin;
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            if (!p.isSprinting() && ToppeCheat.random.nextInt(20) == 0) {
                clicks.clear();
                attacks.clear();
                return;
            }
            int i = clicks.getOrDefault(p.getUniqueId(), 0);
            if (i == 100) {
                double ration = attacks.getOrDefault(p.getUniqueId(), 0) / i;
                if (ration > 0.5 + (getSettings().getThreshold() / 100)) {
                    int v = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                    ViolationLevels.setVL(p, getType(), v++);
                    new Alert(p, "Ration: " + ration, this, plugin);
                    if (v >= getSettings().getAutobanThreshold()) {
                        new Autoban(p, this, plugin);
                    }
                }
                clicks.clear();
                attacks.clear();
            } else {
                clicks.put(p.getUniqueId(), i++);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.isCancelled() && ToppeCheat.random.nextInt(5) == 0) {
            clicks.clear();
            attacks.clear();
        }
    }

    public void onAttackPacket(Player p) {
        int i = attacks.getOrDefault(p.getUniqueId(), 0);
        attacks.put(p.getUniqueId(), i++);
    }


    @Override
    public void enable() {
        super.enable();
        TACPacketListener.mhCheck = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        super.disable();
        TACPacketListener.mhCheck = null;
        HandlerList.unregisterAll(this);
    }


}
