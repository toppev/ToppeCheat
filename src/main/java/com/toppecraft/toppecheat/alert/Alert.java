package com.toppecraft.toppecheat.alert;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.events.AlertEvent;
import com.toppecraft.toppecheat.permission.Permission;
import com.toppecraft.toppecheat.permission.PermissionManager;
import com.toppecraft.toppecheat.playerdata.PlayerData;
import com.toppecraft.toppecheat.stafftools.AlertsCommand;
import com.toppecraft.toppecheat.utils.ClickableMessage;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

/**
 * Alerts staff and console
 *
 * @author Toppe5
 * @since 2.0
 */
public class Alert {

    public String hover = ChatColor.GOLD + "" + ChatColor.BOLD + "Click to teleport";
    public String command = "/tp <player>";
    private String message;
    private boolean staffMessage;
    private String details;
    private Check check;
    private ToppeCheat plugin;
    private UUID player;
    private int lag;
    private int ping;

    /**
     * Create a new alert with the given message. The AlertEvent will be called and if it's not cancelled the message
     * will be sent and logged.
     *
     * @param message the message of the alert
     * @param check   the check that failed
     * @param plugin  ToppeCheat plugin
     * @param lag     lag of the player/server
     */
    public Alert(Player p, String details, int lag, Check check, ToppeCheat plugin) {
        this.plugin = plugin;
        this.check = check;
        this.lag = lag;
        this.player = p.getUniqueId();
        this.staffMessage = check.getSettings().notifyStaff();
        this.details = details;
        this.message = getDefaultMessage(p);
        if (!callEvent()) {
            notifyStaff(true);
        }
    }

    public Alert(Player p, String details, int lag, Check check, ToppeCheat plugin, boolean notify) {
        this.plugin = plugin;
        this.check = check;
        this.lag = lag;
        this.player = p.getUniqueId();
        this.staffMessage = check.getSettings().notifyStaff();
        this.details = details;
        this.message = getDefaultMessage(p);
        if (notify && !callEvent()) {
            notifyStaff(true);
        }
    }

    /**
     * Create a new alert with the given message. The AlertEvent will be called and if it's not cancelled the message
     * will be sent and logged.
     *
     * @param message the message of the alert
     * @param check   the check that failed
     * @param plugin  ToppeCheat plugin
     */
    public Alert(Player p, String details, Check check, ToppeCheat plugin) {
        this.plugin = plugin;
        this.check = check;
        this.ping = plugin.getNMSAccessProvider().getAccess().getPing(p);
        this.lag = plugin.getLagMeter().getLag(p);
        this.player = p.getUniqueId();
        this.staffMessage = check.getSettings().notifyStaff();
        this.details = details;
        this.message = getDefaultMessage(p);
        if (!callEvent()) {
            notifyStaff(true);
        }
    }

    /**
     * Create a new alert with the given message
     *
     * @param message the message the message of the alert
     * @param true    to alert staff too, false only for console and logs
     */
    public Alert(String message, boolean staffMessage) {
        this.message = message;
        this.staffMessage = staffMessage;
        this.plugin = ToppeCheat.getInstance();
        if (!callEvent()) {
            notifyStaff(false);
        }
    }

    private String getDefaultMessage(Player p) {
        String level = Integer.toString(ViolationLevels.getLevel(p, check.getType(), ViolationLevels.getDefaultExpiration()));
        String msg = plugin.getFileManager().getMessagesFile().getMessageWithoutPrefix("alert-message");
        return msg.replace("<details>", details)
                  .replace("<ping>", Integer.toString(ping))
                  .replace("<lag>", Integer.toString(lag))
                  .replace("<type>", check.getType().getCustomName())
                  .replace("<#>", Integer.toString(check.getType().getNum()))
                  .replace("<vl>", level)
                  .replace("<player>", p.getName());
    }

    /**
     * Calls the AlertEvent.
     *
     * @return true if the event is cancelled, otherwise false
     */
    public boolean callEvent() {
        AlertEvent evt = new AlertEvent(this);
        Bukkit.getPluginManager().callEvent(evt);
        return evt.isCancelled();
    }

    /**
     * Notifies staff and console and logs the message.
     */
    public void notifyStaff(boolean logPlayerFile) {
        if (staffMessage) {
            String msg = plugin.getFileManager().getMessagesFile().getPrefix() + message;
            for (CommandSender receiver : getReceivers()) {
                if (receiver instanceof Player && player != null) {
                    Player tar = Bukkit.getPlayer(player);
                    ClickableMessage.sendClickableMessage((Player) receiver, msg, command.replace("<player>", tar.getName()), hover);
                } else {
                    receiver.sendMessage(msg);
                }
            }
        }
        if (logPlayerFile) {
            log();
        }
    }

    private void log() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        PlayerData.getPlayerData(Bukkit.getPlayer(player).getUniqueId()).getViolations().add("[" + dateFormat.format(date) + "] " + ChatColor.stripColor(getMessage()));
    }

    /**
     * Gets the staff and console sender.
     *
     * @return
     */
    private HashSet<CommandSender> getReceivers() {
        HashSet<CommandSender> receivers = new HashSet<CommandSender>();
        if (!AlertsCommand.isConsoleAlertsOff()) {
            receivers.add(Bukkit.getConsoleSender());
        }
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (PermissionManager.hasPermission(pl, Permission.NOTIFY)
                    && !AlertsCommand.isAlertsOff(pl)) {
                receivers.add(pl);
            }
        }
        return receivers;
    }

    /**
     * Gets the message that is being logged and sent to console and players that have the permission
     * "toppecheat.notify" and have alerts on.
     *
     * @return the message that is being sent.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
