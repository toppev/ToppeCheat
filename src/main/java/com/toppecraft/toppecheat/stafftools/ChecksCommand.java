package com.toppecraft.toppecheat.stafftools;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.checksystem.Check;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ChecksCommand extends StaffCommand {

    private ToppeCheat plugin;

    public ChecksCommand(ToppeCheat plugin) {
        super("checks", "Manage the checksystem and its checks.");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1 || (args.length > 0 && (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("?")))) {
            sender.sendMessage(ChatColor.GRAY + "Usage: /checks info|enable|disable|reload|settings <check>");
        } else if (!args[0].equalsIgnoreCase("info") && !args[0].equalsIgnoreCase("enable") &&
                !args[0].equalsIgnoreCase("disable") && !args[0].equalsIgnoreCase("reload") &&
                !args[0].equalsIgnoreCase("settings")) {
            sender.sendMessage(ChatColor.GRAY + "Usage: /checks infodisable|enable|reload|settings|GUI <check>");
        } else if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Please specify the check you want to modify.");
        } else {
            Check check = plugin.getCheckManager().byName(args[1]);
            if (check == null) {
                sender.sendMessage(ChatColor.RED + "No check named '" + args[1] + "' found.");
                return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                sender.sendMessage(ChatColor.WHITE + "-----------------------------");
                sender.sendMessage(ChatColor.YELLOW + "Settings for the check " + check.getType().getName() + " (" + check.getType().getCustomName() + ")");
                sender.sendMessage(ChatColor.BLUE + "Status: " + check.isEnabled());
                sender.sendMessage(ChatColor.BLUE + "Check Settings: ");
                for (String s : plugin.getFileManager().getSettingsFile().getConfig().getConfigurationSection(check.getType().getName()).getKeys(true)) {
                    sender.sendMessage(ChatColor.AQUA + "  " + s + ": " + plugin.getFileManager().getSettingsFile().getConfig().get(check.getType().getName() + "." + s));
                }
                sender.sendMessage(ChatColor.WHITE + "-----------------------------");
            } else if (args[0].equalsIgnoreCase("enable")) {
                if (check.isEnabled()) {
                    sender.sendMessage(ChatColor.RED + "That check has already been enabled.");
                } else {
                    try {
                        check.enable();
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.DARK_RED + "An error occurred while enabling the check " + check.getType().getName() + " (" + check.getType().getCustomName() + ")");
                    }
                    sender.sendMessage(ChatColor.YELLOW + "The check " + check.getType().getName() + " (" + check.getType().getCustomName() + ") has been enabled for now.");
                    if (!check.getSettings().isEnabled()) {
                        sender.sendMessage(ChatColor.YELLOW + "Modify settings to enable the check permanently.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("disable")) {
                if (!check.isEnabled()) {
                    sender.sendMessage(ChatColor.RED + "That check has not been enabled.");
                } else {
                    check.disable();
                    sender.sendMessage(ChatColor.YELLOW + "The check " + check.getType().getName() + " (" + check.getType().getCustomName() + ") has been temporarily disabled.");
                    if (check.getSettings().isEnabled()) {
                        sender.sendMessage(ChatColor.YELLOW + "Modify settings to disable the check permanently.");
                    }
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!check.isEnabled()) {
                    sender.sendMessage(ChatColor.RED + "That check has not been enabled.");
                } else {
                    long l = System.currentTimeMillis();
                    plugin.getCheckManager().reloadCheckSystem(check, plugin);
                    sender.sendMessage(ChatColor.YELLOW + "The check " + check.getType().getName() + " (" + check.getType().getCustomName() + ") has been reloaded in " + (System.currentTimeMillis() - l) + " ms.");
                }
            } else if (args[0].equalsIgnoreCase("settings")) {
                if (args.length < 4) {
                    sender.sendMessage(ChatColor.BLUE + "Usage: /checks settings <check> <setting> <value>");
                    sender.sendMessage(ChatColor.YELLOW + "Settings every check has: notify (true to notify staff otherwise false), check-fail-threshold (threshold for alerts), autoban (true for autobans otherwise false), autoban-threshold (threshold for autobans)");
                } else {
                    if (args[2].equalsIgnoreCase("notify")) {
                        check.getSettings().setNotifyStaff(Boolean.parseBoolean(args[3]));
						if (check.getSettings().notifyStaff()) {
							sender.sendMessage(ChatColor.YELLOW + "The check is now notifying staff.");
						} else {
							sender.sendMessage(ChatColor.YELLOW + "The check is no longer notifying staff.");
						}
                    } else if (args[2].equalsIgnoreCase("autoban")) {
                        check.getSettings().setCanAutoban(Boolean.parseBoolean(args[3]));
						if (check.getSettings().canAutoban()) {
							sender.sendMessage(ChatColor.YELLOW + "The check is now able to autoban.");
						} else {
							sender.sendMessage(ChatColor.YELLOW + "The check is no longer able to autoban.");
						}
                    } else if (args[2].toLowerCase().contains("autoban-threshold")) {
                        try {
                            check.getSettings().setAutobanThreshold(Integer.parseInt(args[3]));
                            sender.sendMessage(ChatColor.YELLOW + "The fail threshold of the check is now " + args[3]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "That's not a valid number!");
                        }
                    } else if (args[2].toLowerCase().contains("threshold")) {
                        try {
                            check.getSettings().setThreshold(Integer.parseInt(args[3]));
                            sender.sendMessage(ChatColor.YELLOW + "The fail threshold of the check is now " + args[3]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "That's not a valid number!");
                        }
                    } else {
                        plugin.getFileManager().getSettingsFile().getConfig().set(check.getType().getName() + "." + args[2], args[3]);
                        sender.sendMessage(ChatColor.YELLOW + "The config value '" + args[2] + "' was set to '" + args[3] + "'.");
                    }
                    long l = System.currentTimeMillis();
                    plugin.getCheckManager().reloadCheckSystem(check, plugin);
                    sender.sendMessage(ChatColor.YELLOW + "The check " + check.getType().getName() + " (" + check.getType().getCustomName() + ") has been reloaded in " + (System.currentTimeMillis() - l) + " ms.");
                }
            }
        }
        return true;
    }
}
