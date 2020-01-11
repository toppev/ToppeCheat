package com.toppecraft.toppecheat.checksystem.moving;

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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ImprovedMovementsCheck extends Check implements Listener {

    private static final String eps = "ToppeCheatImprovedMovementsPackets";
    private static final String epsTime = "ToppeCheatImprovedMovementsTime";

    private ToppeCheat plugin;

    public ImprovedMovementsCheck(ToppeCheat plugin) {
        super(CheckType.IMPROVED_MOVEMENTS);
        this.plugin = plugin;
    }

    @EventHandler
    public void packetCheckMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!plugin.getLagMeter().isLagger(p) && !plugin.getCheckBypassManager().hasBypass(p, getType())) {
            if (p.hasMetadata(eps) && p.hasMetadata(epsTime)) {
                MetadataValue m = plugin.getMeta(p, eps);
                if (m != null && m.value() != null) {
                    MetadataValue m2 = plugin.getMeta(p, epsTime);
                    if (m2 != null && m2.value() != null) {
                        int events = m.asInt() + 1;
                        if (m2.asLong() <= System.currentTimeMillis()) {
                            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                            if (events > 60 && events <= 90 && !plugin.getLagMeter().isLagger(p)) {
                                int x = i + 1;
                                ViolationLevels.setVL(p, getType(), x);
                                p.setMetadata(eps, new FixedMetadataValue(plugin, 1));
                                p.setMetadata(epsTime, new FixedMetadataValue(plugin, System.currentTimeMillis() + 3000));
                                if (x % (getSettings().getThreshold()) == 0) {
                                    new Alert(p, "", this, plugin);
                                }
                                if (x == getSettings().getAutobanThreshold()) {
                                    new Autoban(p, this, plugin);
                                }
                            } else if (events >= 50) {
                                i--;
                                ViolationLevels.setVL(p, getType(), i);
                            }
                        } else {
                            p.setMetadata(eps, new FixedMetadataValue(plugin, events));
                        }
                        return;
                    }
                }
            }
            p.setMetadata(eps, new FixedMetadataValue(plugin, 1));
            p.setMetadata(epsTime, new FixedMetadataValue(plugin, System.currentTimeMillis() + 3000));
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
