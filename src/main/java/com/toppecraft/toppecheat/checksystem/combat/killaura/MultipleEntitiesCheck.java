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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class MultipleEntitiesCheck extends Check implements Listener {

    HashMap<UUID, UUID> names = new HashMap<UUID, UUID>();
    HashMap<String, Long> attackers = new HashMap<String, Long>();
    private ToppeCheat plugin;
    public MultipleEntitiesCheck(ToppeCheat plugin) {
        super(CheckType.MULTIPLE_ENTITIES);
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
        Player p = (Player) e.getDamager();
		if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
			return;
		}
        if (attackers.containsKey(p.getName())) {
            long diff = System.currentTimeMillis() - attackers.get(p.getName());
            UUID id = e.getEntity().getUniqueId();
            if (id != names.get(p.getUniqueId())) {
                if (diff < getSettings().getThreshold() * 10) {
                    if (p != null && !plugin.getLagMeter().isLagger(p)) {
                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                        ViolationLevels.setVL(p, getType(), (int) (getSettings().getThreshold() * 10 - diff + i));
                        new Alert(p, "", MultipleEntitiesCheck.this, plugin);
                        if (i >= getSettings().getAutobanThreshold()) {
                            new Autoban(p, MultipleEntitiesCheck.this, plugin);
                        }
                    }

                }
            }
        }
        names.put(p.getUniqueId(), e.getEntity().getUniqueId());
        attackers.put(p.getName(), System.currentTimeMillis());
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
