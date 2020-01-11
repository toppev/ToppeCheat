package com.toppecraft.toppecheat.stafftools;

import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AutobanCommand extends StaffCommand {

    public AutobanCommand() {
        super("autoban", "");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("cancel")) {
                String asd = null;
                for (String s : Autoban.getting) {
                    if (s.equalsIgnoreCase(args[1])) {
                        asd = s;
                        new Alert(ChatColor.RED + asd + "'s autoban was cancelled by " + sender.getName() + "!", true);
                    }
                }
                if (asd != null) {
                    Autoban.getting.remove(asd);
                }
            }
        }
        return true;
    }

}
