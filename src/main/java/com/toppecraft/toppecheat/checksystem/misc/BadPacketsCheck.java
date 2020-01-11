package com.toppecraft.toppecheat.checksystem.misc;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.lagmeter.LagMeter.LagNotifyNeeder;
import com.toppecraft.toppecheat.packetlistener.listener.TACPacketListener;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author Toppe5
 * @since 2.0
 */
public class BadPacketsCheck extends Check implements LagNotifyNeeder {

    private final static String tag = "ToppeCheatBadPacketsCount";
    private final static String time = "ToppeCheatBadPacketsTime";

    private ToppeCheat plugin;

    public BadPacketsCheck(ToppeCheat plugin) {
        super(CheckType.BAD_PACKETS);
        this.plugin = plugin;
    }

    public void onFlyingPacket(final Player p) {
        if (p != null) {
            //Bukkit.broadcastMessage("asd 0");
            if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
                return;
            }
            //Bukkit.broadcastMessage("asd 1");
            if (p.hasMetadata(tag) && p.hasMetadata(time)) {
                //	Bukkit.broadcastMessage("asd 2");
                MetadataValue m = plugin.getMeta(p, tag);
                if (m != null && m.value() != null) {
                    //	Bukkit.broadcastMessage("asd 3");
                    MetadataValue m2 = plugin.getMeta(p, time);
                    if (m2 != null && m2.value() != null) {
                        //	Bukkit.broadcastMessage("asd 4");
                        final int packets = m.asInt() + 1;
                        if (m2.asLong() <= System.currentTimeMillis()) {
                            //	Bukkit.broadcastMessage("Packets: " + packets);
                            if (packets >= getSettings().getThreshold() * 10 && !plugin.getLagMeter().isLagger(p)) {
                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        if (p == null) {
                                            return;
                                        }
                                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                                        i += packets;
                                        ViolationLevels.setVL(p, getType(), i);
                                        new Alert(p, "Packets: " + packets, BadPacketsCheck.this, plugin);
                                        if (i >= getSettings().getAutobanThreshold() * 10) {
                                            new Autoban(p, BadPacketsCheck.this, plugin);
                                        }
                                    }
                                }.runTask(plugin);
                            }
                            p.removeMetadata(tag, plugin);
                            p.removeMetadata(time, plugin);
                        } else {
                            p.setMetadata(tag, new FixedMetadataValue(plugin, packets));
                        }
                        return;
                    }
                }
            }
            p.setMetadata(tag, new FixedMetadataValue(plugin, 1));
            p.setMetadata(time, new FixedMetadataValue(plugin, System.currentTimeMillis() + 500));
        }
    }

    @Override
    public void enable() {
        super.enable();
        TACPacketListener.badPackets = this;
        plugin.getLagMeter().registerLagNotifyNeeder(this);
    }

    @Override
    public void disable() {
        super.disable();
        TACPacketListener.badPackets = null;
        plugin.getLagMeter().getRegisteredMatters().remove(this);
    }

    @Override
    public void onLag(Player p) {
        p.removeMetadata(tag, plugin);
        p.removeMetadata(time, plugin);
    }
}
