package com.toppecraft.toppecheat.lagmeter;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class LagMeter {

    public static final String laggerMeta = "ToppeCheatLagger";
    private static final String lastPacketMeta = "ToppeCheatPacketSendLagMeter";
    public static long maxPacketDelay;
    public static int maxLag;
    private static HashSet<LagNotifyNeeder> lagNotifyNeeders = new HashSet<LagNotifyNeeder>();
    private ServerLagHandler serverLag;
    private ToppeCheat plugin;

    public LagMeter(ToppeCheat plugin) {
        this.plugin = plugin;
        serverLag = new ServerLagHandler();
    }

    private boolean sentPacket(Player p) {
        return System.currentTimeMillis() - getLastPacket(p) <= maxPacketDelay;
    }

    private void markAsLagger(Player p, boolean lagger) {
        if (lagger) {
            for (LagNotifyNeeder lnn : lagNotifyNeeders) {
                lnn.onLag(p);
            }
            p.setMetadata(laggerMeta, new FixedMetadataValue(plugin, System.currentTimeMillis()));
        } else {
            p.removeMetadata(laggerMeta, plugin);
        }
    }

    public boolean isLagger(Player p) {
        return p.hasMetadata(laggerMeta) || !sentPacket(p) || getLag(p) > maxLag || serverLag.lagging();
    }

    private boolean justLagged(Player p) {
        if (p.hasMetadata(laggerMeta)) {
            MetadataValue m = plugin.getMeta(p, laggerMeta);
            if (m != null && m.value() != null) {
                return m.asLong() + 500 > System.currentTimeMillis();
            }
        }
        return false;
    }

    /**
     * @param p the player who sent the packet
     */
    public void onPacket(final Player p) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (!justLagged(p)) {
                    markAsLagger(p, !sentPacket(p));
                }
                p.setMetadata(lastPacketMeta, new FixedMetadataValue(plugin, System.currentTimeMillis()));
            }
        });
    }

    public long getLastPacket(Player p) {
        MetadataValue m = ToppeCheat.getInstance().getMeta(p, lastPacketMeta);
        if (m != null && m.value() != null) {
            return m.asLong();
        }
        return 0;
    }

    public void registerLagNotifyNeeder(LagNotifyNeeder needer) {
        lagNotifyNeeders.add(needer);
    }

    public HashSet<LagNotifyNeeder> getRegisteredMatters() {
        return lagNotifyNeeders;
    }

    public int getLag(Player p) {
        long l = System.currentTimeMillis() - getLastPacket(p);
        if (l < 50) {
            l = 0;
        } else {
            l /= 5;
        }
        if (l < 0) {
            l = 0;
        }
        return (int) l;
    }

    public interface LagNotifyNeeder {

        void onLag(Player p);

    }

    private class ServerLagHandler extends BukkitRunnable {

        private long lastTime;

        public ServerLagHandler() {
            runTaskTimer(plugin, 1, 1);
        }

        public boolean lagging() {
            long now = System.currentTimeMillis();
            return now - lastTime > maxPacketDelay;
        }

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            if (now - lastTime > maxPacketDelay) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    markAsLagger(p, true);
                }
            }
            lastTime = System.currentTimeMillis();
        }
    }
}
