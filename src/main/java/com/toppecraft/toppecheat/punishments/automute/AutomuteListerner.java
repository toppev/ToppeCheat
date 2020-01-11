package com.toppecraft.toppecheat.punishments.automute;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.punishments.automute.filters.AutomuteFilter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;

public class AutomuteListerner implements Listener {

    private HashSet<AutomuteFilter> filters = new HashSet<AutomuteFilter>();
    private ToppeCheat plugin;

    public AutomuteListerner(ToppeCheat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!Automute.canSpeak(p)) {
            p.sendMessage(plugin.getFileManager().getMessagesFile().getMessage("muted"));
            e.setCancelled(true);
        } else {
            for (AutomuteFilter filter : getFilters()) {
                if (!filter.isAllowed(p, e.getMessage())) {
                    int x = plugin.getConfig().getInt("automute.filters." + filter.toString() + ".mute-level");
                    if (x == Automute.getFilterLevel(p, filter)) {
                        Automute.setFilterLevel(p, filter, 0);
                        if (p.hasPermission("toppecheat.mutebypass") && plugin.getConfig().getBoolean("automute.notify-when-staff-bypass")) {
                            p.sendMessage(plugin.getFileManager().getMessagesFile().getMessage("mute-bypass").replace("<reason>", filter.getReason()));
                            return;
                        }
                        new Automute(p, filter.getReason(), filter.getTime());
                        break;
                    }
                    Automute.setFilterLevel(p, filter, x++);
                }
            }
        }
    }

    public void registerFilter(AutomuteFilter filter) {
        getFilters().add(filter);
    }

    public HashSet<AutomuteFilter> getFilters() {
        return filters;
    }

}
