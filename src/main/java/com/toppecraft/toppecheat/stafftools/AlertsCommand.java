package com.toppecraft.toppecheat.stafftools;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Alerts command executor.
 *
 * @author Toppe5
 * @since 2.0
 */
public class AlertsCommand extends StaffCommand {

    private static final String alertsOff = "ToppeCheatAlertsOff";
    private static boolean consoleAlertsOff;
    private ToppeCheat plugin;

    /**
     * Creates a new AlertsCommand executor.
     *
     * @param plugin the ToppeCheat plugin.
     */
    public AlertsCommand(ToppeCheat plugin) {
        super("alerts", "Toggle check fail alerts");
        this.plugin = plugin;
    }

    /**
     * Gets if the player has alerts off. If the alerts are off player won't get notifications about failed checks.
     *
     * @param p player to check.
     *
     * @return true if the player has alerts off, otherwise false.
     */
    public static boolean isAlertsOff(Player p) {
        return p.hasMetadata(alertsOff);
    }

    /**
     * Gets if the console has alerts off. If the alerts are on for console, all check fails will be logged in the
     * console.
     *
     * @return true if the console has alerts off, otherwise false.
     */
    public static boolean isConsoleAlertsOff() {
        return consoleAlertsOff;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasMetadata(alertsOff)) {
                p.removeMetadata(alertsOff, plugin);
                p.sendMessage(ChatColor.BLUE + "Alerts toggled on.");
            } else {
                p.setMetadata(alertsOff, new FixedMetadataValue(plugin, true));
                p.sendMessage(ChatColor.BLUE + "Alerts toggled off.");
            }
        }
        if (args.length > 0 && (args[0].equalsIgnoreCase("console") || args[0].equalsIgnoreCase("cmd") || sender instanceof ConsoleCommandSender)) {
            if (isConsoleAlertsOff()) {
                consoleAlertsOff = false;
                sender.sendMessage(ChatColor.BLUE + "Console alerts toggled on.");
            } else {
                consoleAlertsOff = true;
                sender.sendMessage(ChatColor.BLUE + "Console alerts toggled off.");
            }
        }
        return true;
    }
}
