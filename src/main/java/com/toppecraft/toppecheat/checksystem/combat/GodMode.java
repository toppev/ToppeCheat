package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.UUID;

public class GodMode extends Check implements Listener {

    public HashMap<UUID, Long> timeDeath = new HashMap();
    private ToppeCheat plugin;

    public GodMode(ToppeCheat plugin) {
        super(CheckType.GOD_MODE);
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        this.timeDeath.put(p.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        this.timeDeath.remove(p.getUniqueId());
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();
        if (((entity instanceof Player))) {
            Player p = (Player) entity;
            if (p.isDead()) {
                if (this.timeDeath.containsKey(p.getUniqueId())) {
                    long l = System.currentTimeMillis() - this.timeDeath.get(p.getUniqueId()).longValue();
                    if (l > 100L) {
                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) + 1;
                        new Alert(p, "", this, plugin);
                        ViolationLevels.setVL(p, getType(), i);
                        if (i >= getSettings().getAutobanThreshold()) {
                            new Autoban(p, this, plugin);
                        }
                    }
                } else {
                    this.timeDeath.remove(p.getUniqueId());
                }
            }
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
