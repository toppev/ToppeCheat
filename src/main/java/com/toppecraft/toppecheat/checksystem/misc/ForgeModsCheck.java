package com.toppecraft.toppecheat.checksystem.misc;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ForgeModsCheck extends Check implements PluginMessageListener, Listener {


    private final JSONParser parser = new JSONParser();
    private final Map<UUID, Map<String, String>> forgeMods = new HashMap<>();
    private ToppeCheat plugin;

    public ForgeModsCheck(ToppeCheat plugin) {
        super(CheckType.FORGE_MODS);
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] data) {
        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        if ("ForgeMods".equals(input.readUTF())) {
            String json = input.readUTF();
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> mods = (Map<String, String>) parser.parse(json);
                forgeMods.put(p.getUniqueId(), mods);
                String client = getClientType(p);
                if (client != null) {
                    new Alert(p, "Client: " + client, this, plugin);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        forgeMods.remove(event.getPlayer().getUniqueId());
    }

    private String getClientType(Player player) {
        Map<String, String> mods = forgeMods.get(player.getUniqueId());
        if (mods != null) {
            if (mods.containsKey("gc")) {
                return "Hacked Client: gc";
            } else if (mods.containsKey("ethylene")) {
                return "Hacked Client: ethylene";
            } else if ("1.0".equals(mods.get("OpenComputers"))) {
                return "Hacked Client: OpenComputers";
            } else if ("1.7.6.git".equals(mods.get("Schematica"))) {
                return "Hacked Client: Schematica";
            }
        }
        return null;
    }


    @Override
    public void enable() {
        super.enable();
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }
}
