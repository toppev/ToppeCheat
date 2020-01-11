package com.toppecraft.toppecheat.punishments.autoban;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.punishments.Punishment;
import com.toppecraft.toppecheat.stafftools.AlertsCommand;
import com.toppecraft.toppecheat.utils.ClickableMessage;
import ga.strikepractice.StrikePractice;
import ga.strikepractice.fights.Fight;
import ga.strikepractice.fights.duel.Duel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Toppe5
 * @since 1.0
 */
public class Autoban extends Punishment {

    public static final List<String> getting = new ArrayList<String>();

    private ToppeCheat plugin;
    private String name;
    private Check check;
    private int counter;

    public Autoban(final Player p, final Check check, final ToppeCheat plugin) {
        super(p.getUniqueId(), plugin.getFileManager().getMessagesFile().getMessage("autoban-messages." + check.getType().getName()), PunishmentType.AUTOBAN);
        if (getting.contains(p.getName())) {
            return;
        }
        this.plugin = plugin;
        getting.add(p.getName());
        this.name = p.getName();
        this.check = check;
        if (check.getSettings().canAutoban()) {
            String s = plugin.getFileManager().getSettingsFile().getConfig().getString(check.getType().getName() + ".autoban-time");
            String[] sp = s.split("-");
            if (sp.length < 2) {
                try {
                    this.counter = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    this.counter = 15;
                    new Alert(ChatColor.RED + "The config doesn't have custom autoban time randomizer, using 15 second ban time!", false);
                    new AutobanTask();
                }
            } else {
                int l = 0;
                int h = 0;
                try {
                    l = Integer.parseInt(sp[0]);
                    h = Integer.parseInt(sp[1]);
                } catch (NumberFormatException e) {
                    this.counter = 15;
                    new Alert(ChatColor.RED + "The config doesn't have custom autoban time randomizer, using 15 second ban time!", false);
                    new AutobanTask();
                }
                this.counter = ToppeCheat.random.nextInt(h - l) + l;
            }
            new AutobanTask();
            if (counter > 0 && Bukkit.getPluginManager().getPlugin("StrikePractice") != null) {
                Fight fight = StrikePractice.getAPI().getFight(p);
                if (fight != null && fight instanceof Duel) {
                    final Duel duel = (Duel) fight;
                    if (duel.getRecorder() != null) {
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                try {
                                    if (duel.getRecorder().getRecordedMatch() != null) {
                                        UUID uuid = duel.getRecorder().getRecordedMatch().getUUID();
                                        new Alert(p, "Autoban Alert! Playback UUID: " + uuid, check, plugin);
                                        this.cancel();
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }.runTaskLater(plugin, 20);
                    }
                }
            }
        }
    }

    private void ban() {
        getting.remove(name);
        if (plugin.getConfig().getBoolean("autoban.lightning")) {
            Player tar = Bukkit.getPlayer(getPlayerUUID());
            if (tar != null) {
                tar.getWorld().strikeLightningEffect(tar.getLocation());
            }
        }
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("autoban.broadcast-message").replace("<player>", name)).replace("<reason>", getReason()));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getConfig().getString("autoban.command").replace("<player>", name).replace("<reason>", getReason()));
    }

    @Override
    public String getReason() {
        return plugin.getFileManager().getSettingsFile().getConfig().getString(check.getType().getName() + ".autoban-reason");
    }

    private class AutobanTask extends BukkitRunnable {

        public AutobanTask() {
            runTaskTimer(plugin, 20, 20);
        }

        @Override
        public void run() {
            if (!getting.contains(name)) {
                this.cancel();
                return;
            }
            if (counter <= 0) {
                ban();
                this.cancel();
            } else {
                if (counter % 15 == 0 || counter <= 3 || (counter < 30 && counter % 5 == 0)) {
                    String s = plugin.getFileManager().getMessagesFile()
                                     .getMessageWithoutPrefix("autobanning").replace("<type>",
                                    check.getType().getName()).replace("<#>", Integer.toString(check.getType().getNum())).replace("<player>", name).replace("<reason>", getReason()).replace("<seconds>",
                                    Integer.toString(counter));
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (players.hasPermission("toppecheat.notify") && !AlertsCommand.isAlertsOff(players)) {
                            ClickableMessage.sendClickableMessage(players, s, "/autoban cancel " + name, ChatColor.YELLOW + "" + ChatColor.BOLD + "Click this message to cancel " + name + "'s autoban!");
                        }
                    }
                }
                counter--;
            }
        }
    }
}
