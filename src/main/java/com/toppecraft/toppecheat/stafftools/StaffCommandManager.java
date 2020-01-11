package com.toppecraft.toppecheat.stafftools;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

public class StaffCommandManager implements CommandExecutor {

    private ToppeCheat plugin;
    private HashSet<StaffCommand> staffCommands = new HashSet<StaffCommand>();

    public StaffCommandManager(ToppeCheat plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.getCommand("stafftools").setExecutor(this);
        register(new ChecksCommand(plugin));
        register(new PlayerInfoCommand(plugin));
        register(new AlertsCommand(plugin));
        register(new AutobanCommand());
    }

    private void register(StaffCommand staffCommand) {
        plugin.getCommand(staffCommand.getCommand()).setExecutor(staffCommand);
        staffCommands.add(staffCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1 || (args.length > 0 && (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("?")))) {
            for (StaffCommand st : staffCommands) {
                sender.sendMessage(ChatColor.BLUE + st.getCommand() + " - " + st.getDescription());
            }
        }
        return true;
    }

    public StaffCommand getStaffCommand(String command) {
        for (StaffCommand sc : staffCommands) {
            if (sc.getCommand().equalsIgnoreCase(command)) {
                return sc;
            }
        }
        return null;
    }


}
