package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.lagmeter.LagMeter.LagNotifyNeeder;
import com.toppecraft.toppecheat.packetlistener.listener.TACPacketListener;
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

public class MultipleClickersCheck extends Check implements Listener, LagNotifyNeeder {

    private static final String time = "ToppeCheatKA/DCTimestamp";
    private static final String lastClick = "ToppeCheatKA/DCCount";
    private static final String lv = "ToppeCheatKA/DCLevel";
    private static long delay;

    private ToppeCheat plugin;

    public MultipleClickersCheck(ToppeCheat plugin) {
        super(CheckType.CLICKING_MODIFICATIONS);
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.FISHING_ROD) {
            return;
        }
        if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
            return;
        }
        if (p.hasMetadata(lastClick)) {
            MetadataValue m = plugin.getMeta(p, lastClick);
            if (m != null && m.value() != null) {
                long l = m.asLong();
                if (l + delay >= System.currentTimeMillis()) {
                    if (p.hasMetadata(time)) {
                        MetadataValue mv = plugin.getMeta(p, time);
                        if (mv != null && mv.value() != null) {
                            int level = 1;
                            if (p.hasMetadata(lv)) {
                                MetadataValue mlv = plugin.getMeta(p, lv);
                                if (mlv != null && mlv.value() != null) {
                                    level += mlv.asInt();
                                    p.setMetadata(lv, new FixedMetadataValue(plugin, level));
                                }
                            } else {
                                p.setMetadata(lv, new FixedMetadataValue(plugin, level));
                            }
                            if (mv.asLong() + 10000 < System.currentTimeMillis()) {
                                p.removeMetadata(lv, plugin);
                                p.removeMetadata(time, plugin);
                                if (level >= getSettings().getThreshold() && !plugin.getLagMeter().isLagger(p)) {
                                    int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                                    i = level < getSettings().getThreshold() + 10 ? i + level - getSettings().getThreshold() + 1 : i + 11;
                                    ViolationLevels.setVL(p, getType(), i);
                                    new Alert(p, "< " + delay + " ms. in 10s: " + level, this, plugin);
                                    if (i >= getSettings().getAutobanThreshold()) {
                                        new Autoban(p, this, plugin);
                                    }
                                }
                            }
                        }
                    } else {
                        p.setMetadata(time, new FixedMetadataValue(plugin, System.currentTimeMillis()));
                    }
                }
            }
        }
        p.setMetadata(lastClick, new FixedMetadataValue(plugin, System.currentTimeMillis()));
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLagMeter().registerLagNotifyNeeder(this);
        delay = plugin.getFileManager().getSettingsFile().getConfig().getLong(getType().getName() + ".click-delay");
        TACPacketListener.mcc = this;
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
        plugin.getLagMeter().getRegisteredMatters().remove(this);
        TACPacketListener.mcc = null;
    }

    @Override
    public void onLag(Player p) {
        p.removeMetadata(lastClick, plugin);
        p.removeMetadata(time, plugin);
        p.removeMetadata(lv, plugin);
    }
}
