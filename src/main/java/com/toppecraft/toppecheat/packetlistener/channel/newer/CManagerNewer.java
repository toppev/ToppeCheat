package com.toppecraft.toppecheat.packetlistener.channel.newer;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.packetlistener.channel.CHandlerCommon;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CManagerNewer {

    private static CManagerNewer channelManager;
    private ToppeCheat plugin;

    public CManagerNewer(ToppeCheat plugin) {
        this.plugin = plugin;
    }

    public static void register(ToppeCheat plugin) {
        channelManager = new CManagerNewer(plugin);
        //plugin.getNMSAccessProvider().getAccess().registerServerConnectionChannel();
        Bukkit.getPluginManager().registerEvents(channelManager.new BukkitListener(), plugin);
    }

    public static CManagerNewer getChannelManager() {
        return channelManager;
    }

    public void injectPlayer(Player p) {
        Channel channel = getChannel(p);
        Object c = channel.pipeline().get(CHandlerCommon.HANDLER_NAME);
        if (c == null) {
            channel.pipeline().addBefore("packet_handler", CHandlerCommon.HANDLER_NAME, new CHandlerNewer(p));
        }
    }

    public void unregisterPlayer(Player p, boolean disabling) {
		if (disabling) {
			return;
		}
        final Channel channel = getChannel(p);
        channel.eventLoop().execute(new Runnable() {

            @Override
            public void run() {
                channel.pipeline().remove(CHandlerCommon.HANDLER_NAME);
            }

        });
    }

    public Channel getChannel(Object o) throws ClassCastException {
        return (Channel) plugin.getNMSAccessProvider().getAccess().getChannel((Player) o);
    }

    public void registerAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            injectPlayer(p);
        }
    }

    public void unregisterAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            unregisterPlayer(p, false);
        }
    }

    class BukkitListener implements Listener {

        @EventHandler
        public void onLogin(PlayerJoinEvent e) {
            getChannelManager().injectPlayer(e.getPlayer());
        }
    }
}
