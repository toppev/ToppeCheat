package com.toppecraft.toppecheat.playerdata;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.UUID;

public class PlayerDataListener implements Listener {

    private ToppeCheat plugin;

    public PlayerDataListener(ToppeCheat plugin) {
        this.plugin = plugin;
    }

    public void loadOnlinePlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setMetadata(PlayerData.playerDataMeta, new FixedMetadataValue(plugin, PlayerData.getOnlinePlayerData(p)));
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        final InetAddress address = e.getAddress();
        PlayerData.getPlayerDataAsync(uuid, new PlayerDataCallback() {

            @Override
            public void onSuccess(PlayerData data) {
                if (p != null && p.isOnline()) {
                    data.getAddresses().add(address.getHostAddress());
                    p.setMetadata(PlayerData.playerDataMeta, new FixedMetadataValue(plugin, data));
                    PlayerData.getOnlinePlayerData(p).save(p.getUniqueId());
                    if (plugin.getConfig().getBoolean("playerdata.alts-on-join") && !data.getNotes().contains("no alt-check")) {
                        data.getAllAccountsAsync(data.new AltsCallback() {

                            @Override
                            public void onSuccess(HashSet<UUID> alts) {
                                if (p != null && p.isOnline()) {
                                    if (alts.size() > 1) {
                                        String comma = "";
                                        StringBuilder sb = new StringBuilder();
                                        for (UUID uuid : alts) {
                                            sb.append(comma);
                                            sb.append(Bukkit.getOfflinePlayer(uuid).getName());
                                            comma = ", ";
                                        }
                                        String s = sb.toString();
                                        String msg = plugin.getFileManager().getMessagesFile().getMessage("alts").replaceAll("<player>", p.getName()).replace("<alts>", s);
                                        for (Player p : Bukkit.getOnlinePlayers()) {
                                            if (p.hasPermission("toppecheat.admin")) {
                                                p.sendMessage(msg);
                                            }
                                        }
                                    }
                                }
                            }
                        }, plugin.getConfig().getBoolean("playerdata.alts-extra-check"));
                    }
                    if (plugin.getConfig().getBoolean("playerdata.notes-on-join")) {
                        if (data.getNotes().size() > 0) {
                            String note = plugin.getFileManager().getMessagesFile().getMessageWithoutPrefix("note");
                            new Alert(plugin.getFileManager().getMessagesFile().getMessageWithoutPrefix("following-notes").replace("<player>", p.getName()), true);
                            for (String s : data.getNotes()) {
                                new Alert(note.replace("<note>", s), true);
                            }
                        }
                    }
                }
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        PlayerData.getPlayerData(e.getPlayer().getUniqueId()).save(e.getPlayer().getUniqueId());
        e.getPlayer().removeMetadata(PlayerData.playerDataMeta, plugin);
    }
}
