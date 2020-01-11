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
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CriticalsCheck extends Check implements Listener {

    private ToppeCheat plugin;

    public CriticalsCheck(ToppeCheat plugin) {
        super(CheckType.CRITICALS);
        this.plugin = plugin;
    }


    @SuppressWarnings("deprecation")
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (plugin.getCheckBypassManager().hasBypass(p, getType()) || p.isFlying() || plugin.getLagMeter().isLagger(p)) {
                return;
            }
            if (p.getLocation().getY() - p.getLocation().getBlockY() == 0 && !p.isOnGround()) {
                int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                ViolationLevels.setVL(p, getType(), i + 1);
                if (i % (getSettings().getThreshold()) == 0) {
                    new Alert(p, "", this, plugin);
                }
                if (i >= getSettings().getAutobanThreshold()) {
                    new Autoban(p, this, plugin);
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
