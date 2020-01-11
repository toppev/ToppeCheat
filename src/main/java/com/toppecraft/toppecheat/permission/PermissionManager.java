package com.toppecraft.toppecheat.permission;

import org.bukkit.command.CommandSender;

public class PermissionManager {


    public static boolean hasPermission(CommandSender sender, String permission) {
        return sender.isOp() || sender.hasPermission(Permission.ALL.getPermission()) || sender.hasPermission(permission);
    }

    public static boolean hasPermission(CommandSender sender, Permission permission) {
        return hasPermission(sender, permission.getPermission());
    }
}
