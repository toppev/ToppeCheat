package com.toppecraft.toppecheat.checksystem.misc;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

public class TabCompleteCheck extends Check implements Listener {


    private ToppeCheat plugin;

    public TabCompleteCheck(ToppeCheat plugin) {
        super(CheckType.TAB_COMPLETE);
        this.plugin = plugin;
    }


    @EventHandler
    public void TabCompleteEvent(PlayerChatTabCompleteEvent e) {
        String[] args = e.getChatMessage().split(" ");
        Player p = e.getPlayer();
        if ((args[0].startsWith(".")) && (args[0].substring(1, 2).equalsIgnoreCase("/"))) {
            return;
        }
        if ((args.length > 1) && ((args[0].startsWith(".")) || (args[0].startsWith("-")) || (args[0].startsWith("#")) || (args[0].startsWith("*")))) {
            new Alert(p, "Client command: " + e.getChatMessage(), this, plugin);
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
