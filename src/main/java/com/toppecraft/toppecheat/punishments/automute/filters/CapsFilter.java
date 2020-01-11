package com.toppecraft.toppecheat.punishments.automute.filters;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.entity.Player;

public class CapsFilter implements AutomuteFilter {

    private static int percentage;
    private static int minLength;

    public CapsFilter() {
        percentage = ToppeCheat.getInstance().getConfig().getInt("automute.filters.caps.max-upper-case-percentage");
        minLength = ToppeCheat.getInstance().getConfig().getInt("automute.filters.caps.min-message-length");
    }

    @Override
    public boolean isAllowed(Player p, String message) {
        if (message.length() < minLength) {
            return true;
        }
        int i = 0;
        for (char c : message.toCharArray()) {
            if (Character.isUpperCase(c)) {
                i++;
            }
        }
        return (double) i / message.length() * 100 < percentage;
    }

    @Override
    public long getTime() {
        return ToppeCheat.getInstance().getConfig().getLong("automute.filters.caps.time") * 1000;
    }

    @Override
    public String getReason() {
        return ToppeCheat.getInstance().getConfig().getString("automute.filters.caps.reason");
    }

    @Override
    public String toString() {
        return "caps";
    }

}
