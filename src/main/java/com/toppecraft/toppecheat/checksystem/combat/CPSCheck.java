package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.lagmeter.LagMeter.LagNotifyNeeder;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class CPSCheck extends Check implements Listener, LagNotifyNeeder {

    private static final String time = "ToppeCheatCPSTimestamp";
    private static final String tag = "ToppeCheatCPSCount";

    private ToppeCheat plugin;

    public CPSCheck(ToppeCheat plugin) {
        super(CheckType.CPS);
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
                            if (cps >= getSettings().getThreshold() && !plugin.getLagMeter().isLagger(p)) {
                                int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                                int x = cps - getSettings().getThreshold();
                                if (x > 20) {
                                    i += 20;
                                } else {
                                    i += x;
                                }
                                ViolationLevels.setVL(p, getType(), i);
                                if (i <= 5) {
                                    new Alert(p, "CPS: " + cps + " (experimental)", this, plugin);
                                } else {
                                    new Alert(p, "CPS: " + cps, this, plugin);
                                }
                                if (i >= getSettings().getAutobanThreshold()) {
                                    new Autoban(p, this, plugin);
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
