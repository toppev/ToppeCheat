package com.toppecraft.toppecheat.playerreports;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.stafftools.AlertsCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;

public class ReportCommand implements CommandExecutor {

    private static final String reported = "ToppeCheatPlayeReportCooldown";

    private HashMap<String, Integer> reports = new HashMap<String, Integer>();

    private ToppeCheat plugin;

    public ReportCommand(ToppeCheat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (p.hasMetadata(reported)) {
                MetadataValue m = plugin.getMeta(p, reported);
                if (m != null && m.value() != null) {
                    if (m.asLong() + plugin.getConfig().getLong("report-cooldown-time") * 1000 > System.currentTimeMillis()) {
                        p.sendMessage(plugin.getFileManager().getMessagesFile().getMessage("report-cooldown"));
                        return true;
                    }
                }
            }
            if (args.length == 0) {
                return false;
            }
            Player target = Bukkit.getPlayer(args[0]);
            StringBuilder report = new StringBuilder();
            for (int i = target == null && args.length > 1 ? 0 : 1; i < args.length; i++) {
                report.append(args[i] + " ");
            }
            if (plugin.getConfig().getBoolean("notify-all-reports")) {
                String s;
                if (target != null && args.length > 1) {
                    s = ChatColor.GREEN + p.getName() + " reported " + ChatColor.GOLD + target.getName() + ChatColor.GREEN + " for " + ChatColor.GOLD + report.toString();

                } else {
                    s = ChatColor.GREEN + p.getName() + ": " + ChatColor.GOLD + report.toString();
                }
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (players.hasPermission("toppecheat.notify") && !AlertsCommand.isAlertsOff(players)) {
                        players.sendMessage(ChatColor.GOLD + "[Report] " + s);
                    }
                }
            }
            if (target != null) {
                if (reports.containsKey(target.getName())) {
                    int i = reports.get(target.getName());
                    reports.put(target.getName(), i + 1);
                    if (i % plugin.getConfig().getInt("alert-after-reports") == 0) {
                        for (Player players : Bukkit.getOnlinePlayers()) {
                            if (players.hasPermission("toppecheat.notify") && !AlertsCommand.isAlertsOff(players)) {
                                players.sendMessage(ChatColor.GOLD + "[Report] " + ChatColor.GREEN + target.getName() + " has been reported " + ChatColor.GOLD + i + " times.");
                            }
                        }
                    }
                } else {
                    reports.put(target.getName(), 1);
                }
            }
            p.setMetadata(reported, new FixedMetadataValue(plugin, System.currentTimeMillis()));
        }
        return true;
    }

}
