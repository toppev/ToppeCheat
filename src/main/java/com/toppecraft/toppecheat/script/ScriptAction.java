package com.toppecraft.toppecheat.script;

import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.CheckType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ScriptAction {

    private String sentence;

    public ScriptAction(String string) {
        this.sentence = ScriptManager.trim(string);
    }

    public void performAction(Player p, CheckType type, int violation) {
        if (sentence.startsWith("broadcast '") && sentence.endsWith("'")) {
            String s = sentence.replace("broadcast '", "").replace("'", "");
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', replaceVariables(s, p, type, violation)));
        } else if (sentence.startsWith("alert '") && sentence.endsWith("'")) {
            String s = sentence.replace("alert '", "").replace("'", "");
            new Alert(ChatColor.translateAlternateColorCodes('&', replaceVariables(s, p, type, violation)), true);
        } else if (sentence.startsWith("perform command '") && sentence.endsWith("'")) {
            String s = sentence.replace("perform command '", "").replace("'", "");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), replaceVariables(s, p, type, violation));
        } else if (sentence.startsWith("log '") && sentence.endsWith("'")) {
            String s = sentence.replace("log '", "").replace("'", "");
            Bukkit.getLogger().info(replaceVariables(s, p, type, violation));
        }
    }

    private String replaceVariables(String s, Player p, CheckType type, int violation) {
        return s.replace("<player>", p.getName()).replace("<uuid>", p.getUniqueId().toString())
                .replace("<check>", type.getCustomName()).replace("<checkrealname>", type.getName())
                .replace("<checknumber>", Integer.toString(type.getNum())).replace("<violation>", Integer.toString(violation))
                .replace("<displayname>", p.getDisplayName()).replace("<address>", p.getAddress().getAddress().getHostAddress());
    }
}
