package com.toppecraft.toppecheat.script;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.checksystem.CheckType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

public class ScriptManager {

    private static ScriptManager manager;

    private ScriptParser parser;
    private ToppeCheat plugin;

    public ScriptManager(ToppeCheat plugin) {
        this.plugin = plugin;
        manager = this;
    }

    public static ScriptManager getManager() {
        return manager;
    }

    public static String trim(String s) {
        return s.replace("the", "").replace("an", "").replace("a", "").toLowerCase();
    }

    private static int getDoubleSpacesAtStart(String s) {
        int result = 0;
        for (char c : s.toCharArray()) {
            if (c == ' ') {
                result++;
            }
        }
        return result / 2;
    }

    private static String ignoreAtStartSpaces(String s) {
        String result = "";
        boolean b = false;
        for (char c : s.toCharArray()) {
            if (c != ' ' && !b) {
                result += c;
            } else {
                b = true;
            }
        }
        return result;
    }

    public void loadScriptSystem() {
        parser = new ScriptParser(getFile());
        parser.parse();
    }

    public void onViolationEvent(Player p, CheckType type, int violation) {
        if (parser != null) {
            int ds = 1;
            for (String s : parser.getFunction()) {
                if (ignoreAtStartSpaces(s).startsWith("stop")) {
                    break;
                } else if (ignoreAtStartSpaces(s).startsWith("if")) {
                    if (new ScriptBoolean(s, type).getResult(p)) {
                        ds = getDoubleSpacesAtStart(s) + 1;
                    }
                } else if (getDoubleSpacesAtStart(s) <= ds) {
                    new ScriptAction(ignoreAtStartSpaces(s)).performAction(p, type, violation);
                }
            }
        }
    }

    private File getFile() {
        File file = new File(plugin.getDataFolder(), File.separator + "script.txt");
        if (file == null || !file.exists()) {
            Bukkit.getLogger().info("Creating script.txt...");
            plugin.saveResource("script.txt", false);
        }
        return file;
    }

}
