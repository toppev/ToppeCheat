package com.toppecraft.toppecheat.script;

import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.entity.Player;

public class ScriptBoolean {

    private String sentence;
    private CheckType check;

    public ScriptBoolean(String string, CheckType type) {
        this.sentence = ScriptManager.trim(string);
        this.check = type;
    }

    public boolean getResult(Player p) {
        if (sentence.equals("if check equals '" + check.getCustomName() + "'")) {
            return true;
        }
        if (sentence.equals("if check does not equal '" + check.getCustomName() + "'")) {
            return false;
        }
        if (sentence.equals("if check equals '" + check.getName() + "'")) {
            return true;
        }
        if (sentence.equals("if check does not equal '" + check.getName() + "'")) {
            return false;
        }
        if (sentence.equals("if check equals #" + check.getNum() + "")) {
            return true;
        }
        if (sentence.equals("if check does not equal #" + check.getNum() + "")) {
            return false;
        }
        if (sentence.startsWith("if player has permission '") && sentence.endsWith("'")) {
            String s = sentence.replace("if player has permission '", "").replace("'", "");
            return p.hasPermission(s);
        }
        if (sentence.startsWith("if player does not have permission '") && sentence.endsWith("'")) {
            return false;
        }
        if (sentence.startsWith("if violation level is over ")) {
            try {
                int i = Integer.parseInt(sentence.replace("if violation level is over ", ""));
                return ViolationLevels.getLevel(p, check, ViolationLevels.getDefaultExpiration()) > i;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (sentence.startsWith("if violation level is not over ")) {
            try {
                int i = Integer.parseInt(sentence.replace("if violation level is over ", ""));
                return ViolationLevels.getLevel(p, check, ViolationLevels.getDefaultExpiration()) <= i;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (sentence.startsWith("if violation level is under ")) {
            try {
                int i = Integer.parseInt(sentence.replace("if violation level is under ", ""));
                return ViolationLevels.getLevel(p, check, ViolationLevels.getDefaultExpiration()) < i;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (sentence.startsWith("if violation level is not under ")) {
            try {
                int i = Integer.parseInt(sentence.replace("if violation level is under ", ""));
                return ViolationLevels.getLevel(p, check, ViolationLevels.getDefaultExpiration()) >= i;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (sentence.startsWith("if violation level is ")) {
            try {
                int i = Integer.parseInt(sentence.replace("if violation level is ", ""));
                return ViolationLevels.getLevel(p, check, ViolationLevels.getDefaultExpiration()) == i;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (sentence.startsWith("if violation level is not")) {
            try {
                int i = Integer.parseInt(sentence.replace("if violation level is ", ""));
                return ViolationLevels.getLevel(p, check, ViolationLevels.getDefaultExpiration()) != i;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
}
