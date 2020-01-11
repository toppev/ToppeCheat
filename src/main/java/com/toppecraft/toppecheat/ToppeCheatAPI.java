package com.toppecraft.toppecheat;

import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.filemanager.MessagesFile;
import com.toppecraft.toppecheat.filemanager.SettingsFile;
import com.toppecraft.toppecheat.packetlistener.listener.PacketListener;
import com.toppecraft.toppecheat.packetlistener.listener.PacketListenerManager;
import com.toppecraft.toppecheat.playerdata.PlayerData;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ToppeCheatAPI {

    public static int getViolation(Player player, CheckType type) {
        return ViolationLevels.getLevel(player, type);
    }

    public static void setViolation(Player player, CheckType type, int time) {
        ViolationLevels.setVL(player, type, time);
    }

    public static void setViolation(Player player, CheckType type) {
        ViolationLevels.setVL(player, type, ViolationLevels.getDefaultExpiration());
    }

    public static long getBypassTime(Player player, CheckType type) {
        return ToppeCheat.getInstance().getCheckBypassManager().getBypass(player, type);
    }

    public static void setBypassTime(Player player, CheckType type, long milliseconds) {
        ToppeCheat.getInstance().getCheckBypassManager().setBypass(player, type, milliseconds);
    }

    public static boolean hasBypass(Player player, CheckType type) {
        return ToppeCheat.getInstance().getCheckBypassManager().hasBypass(player, type);
    }

    public static HashSet<Check> getChecks() {
        return ToppeCheat.getInstance().getCheckManager().getChecks();
    }

    public static void reloadCheck(Check check) {
        ToppeCheat.getInstance().getCheckManager().reloadCheckSystem(check, ToppeCheat.getInstance());
    }

    public static void registerCheck(Check check) {
        ToppeCheat.getInstance().getCheckManager().registerCheckSystem(check, ToppeCheat.getInstance());
    }

    public static HashSet<String> getAddresses(UUID uuid) {
        return PlayerData.getPlayerData(uuid).getAddresses();
    }

    public static List<String> getViolations(UUID uuid) {
        return PlayerData.getPlayerData(uuid).getViolations();
    }

    public static List<String> getPunishments(UUID uuid) {
        return PlayerData.getPlayerData(uuid).getPunishments();
    }

    public static List<String> getNotes(UUID uuid) {
        return PlayerData.getPlayerData(uuid).getNotes();
    }

    public static void setAddresses(UUID uuid, HashSet<String> addresses) {
        PlayerData.getPlayerData(uuid).setAddresses(addresses);
    }

    public static void setViolations(UUID uuid, List<String> violations) {
        PlayerData.getPlayerData(uuid).setViolations(violations);
    }

    public static void setPunishments(UUID uuid, List<String> punishments) {
        PlayerData.getPlayerData(uuid).setPunishments(punishments);
    }

    public static void setNotes(UUID uuid, List<String> notes) {
        PlayerData.getPlayerData(uuid).setNotes(notes);
    }

    public static void registerPacketListener(PacketListener listener) {
        PacketListenerManager.registerPacketListener(listener);
    }

    public static MessagesFile getMessagesFile() {
        return ToppeCheat.getInstance().getFileManager().getMessagesFile();
    }

    public static SettingsFile getSettingsFile() {
        return ToppeCheat.getInstance().getFileManager().getSettingsFile();
    }

}
