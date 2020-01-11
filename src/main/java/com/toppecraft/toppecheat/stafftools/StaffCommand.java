package com.toppecraft.toppecheat.stafftools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StaffCommand implements CommandExecutor {

    private boolean enabled;
    private String command;
    private String desc;

    public StaffCommand(String command, String description) {
        this.command = command;
        this.desc = description;
    }

    public String getCommand() {
        return command;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return desc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
    }
}
