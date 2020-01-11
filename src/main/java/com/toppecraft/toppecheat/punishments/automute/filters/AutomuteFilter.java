package com.toppecraft.toppecheat.punishments.automute.filters;

import org.bukkit.entity.Player;

public interface AutomuteFilter {

    boolean isAllowed(Player p, String message);

    long getTime();

    String getReason();
}
