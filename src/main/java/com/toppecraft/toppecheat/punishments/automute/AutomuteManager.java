package com.toppecraft.toppecheat.punishments.automute;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.punishments.automute.filters.BadWordsFilter;
import com.toppecraft.toppecheat.punishments.automute.filters.CapsFilter;
import com.toppecraft.toppecheat.punishments.automute.filters.SpamFilter;
import org.bukkit.Bukkit;

public class AutomuteManager {

    private static void registerFilters(ToppeCheat plugin, AutomuteListerner amListener) {
        if (plugin.getConfig().getBoolean("automute.filters.spam.enabled")) {
            amListener.registerFilter(new SpamFilter());
        }
        if (plugin.getConfig().getBoolean("automute.filters.bad-words.enabled")) {
            amListener.registerFilter(new BadWordsFilter());
        }
        if (plugin.getConfig().getBoolean("automute.filters.caps.enabled")) {
            amListener.registerFilter(new CapsFilter());
        }
    }

    public static void register(ToppeCheat plugin) {
        AutomuteListerner amListener = new AutomuteListerner(plugin);
        Bukkit.getPluginManager().registerEvents(amListener, plugin);
        registerFilters(plugin, amListener);
    }
}
