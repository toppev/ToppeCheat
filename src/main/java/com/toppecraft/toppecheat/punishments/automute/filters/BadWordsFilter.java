package com.toppecraft.toppecheat.punishments.automute.filters;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.entity.Player;

public class BadWordsFilter implements AutomuteFilter {

    @Override
    public boolean isAllowed(Player p, String message) {
        message = message.toLowerCase();
        for (String s : ToppeCheat.getInstance().getConfig().getStringList("automute.filters.bad-words.blacklisted-words")) {
            s = s.toLowerCase();
            if (message.contains(s) || message.startsWith(s.replace(" ", "")) || message.endsWith(" " + s.replace(" ", ""))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public long getTime() {
        return ToppeCheat.getInstance().getConfig().getLong("automute.filters.bad-words.time") * 1000;
    }

    @Override
    public String getReason() {
        return ToppeCheat.getInstance().getConfig().getString("automute.filters.bad-words.reason");
    }

    @Override
    public String toString() {
        return "bad-words";
    }

}
