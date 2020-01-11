package com.toppecraft.toppecheat.checksystem.moving;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.lagmeter.LagMeter.LagNotifyNeeder;
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

public class PacketSpamSpeedCheck extends Check implements Listener, LagNotifyNeeder {

    private static final String lv = "ToppeCheatEPSSpeedCounter";
    private static final String timeTag = "ToppeCheatMoveLastTime";

    private int ticks;
    private ToppeCheat plugin;

    public PacketSpamSpeedCheck(ToppeCheat plugin) {
        super(CheckType.PACKET_SPAM_SPEED);
        this.plugin = plugin;
    }


    @EventHandler
    public void packetCheckMoveEvent(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (plugin.getCheckBypassManager().hasBypass(p, getType()) || plugin.getLagMeter().isLagger(p)) {
            return;
        }
        if (p.hasMetadata(timeTag) && p.hasMetadata(lv)) {
            MetadataValue m = plugin.getMeta(p, lv);
            if (m != null && m.value() != null) {
                MetadataValue m2 = plugin.getMeta(p, timeTag);
                if (m2 != null && m2.value() != null) {
                    int c = m.asInt() + 1;
                    if (m2.asLong() <= System.currentTimeMillis()) {
                        int extras = m.asInt() - ticks;
                        if (extras >= getSettings().getThreshold()) {
                            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                            i += c - getSettings().getThreshold();
                            new Alert(p, "Extra events: " + extras, this, plugin);
                            ViolationLevels.setVL(p, getType(), i);
                            if (i >= getSettings().getAutobanThreshold()) {
                                new Autoban(p, this, plugin);
                            }
                        }
                        p.removeMetadata(lv, plugin);
                        p.removeMetadata(timeTag, plugin);
                    } else {
                        p.setMetadata(lv, new FixedMetadataValue(plugin, c));
                    }
                    return;
                }
            }
        }
        p.setMetadata(lv, new FixedMetadataValue(plugin, 1));
        p.setMetadata(timeTag, new FixedMetadataValue(plugin, System.currentTimeMillis() + ticks * 50));
    }

    @Override
    public void enable() {
        super.enable();
        plugin.getLagMeter().registerLagNotifyNeeder(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ticks = plugin.getFileManager().getSettingsFile().getConfig().getInt(getType().getName() + ".ticks-to-check");
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }

    @Override
    public void onLag(Player p) {
        p.removeMetadata(timeTag, plugin);
        p.removeMetadata(lv, plugin);
    }
}