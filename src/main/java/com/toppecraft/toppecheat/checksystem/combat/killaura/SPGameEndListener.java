package com.toppecraft.toppecheat.checksystem.combat.killaura;

import com.toppecraft.toppecheat.ToppeCheat;
import ga.strikepractice.events.BotDuelEndEvent;
import ga.strikepractice.events.DuelEndEvent;
import ga.strikepractice.events.PartyFFAEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class SPGameEndListener implements Listener {

    private JoinCheck jCheck;

    public SPGameEndListener(JoinCheck joinCheck) {
        this.jCheck = joinCheck;
    }


    @EventHandler
    public void onBotEnd(BotDuelEndEvent e) {
        if (Bukkit.getPlayer(e.getWinner()) == null) {
            return;
        }
        final Player p = e.getPlayer();
        new BukkitRunnable() {

            @Override
            public void run() {
                jCheck.join.put(p.getUniqueId(), 0);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (p != null) {
                            int i = jCheck.join.getOrDefault(p.getUniqueId(), 0) + 1;
                            if (i == 4) {
                                jCheck.join.remove(p.getUniqueId());
                                this.cancel();
                            } else {
                                jCheck.join.put(p.getUniqueId(), i);
                            }
                        }
                    }
                }.runTaskTimer(ToppeCheat.getInstance(), 1, 1);
            }
        }.runTaskLater(ToppeCheat.getInstance(),
                Bukkit.getPluginManager().getPlugin("StrikePractice").getConfig().getLong("wait-before-teleport") * 20);
    }

    @EventHandler
    public void onDuelEnd(DuelEndEvent e) {
        final Player p = e.getWinner();
        new BukkitRunnable() {

            @Override
            public void run() {
                jCheck.join.put(p.getUniqueId(), 0);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (p != null) {
                            int i = jCheck.join.getOrDefault(p.getUniqueId(), 0) + 1;
                            if (i == 4) {
                                jCheck.join.remove(p.getUniqueId());
                                this.cancel();
                            } else {
                                jCheck.join.put(p.getUniqueId(), i);
                            }
                        }
                    }
                }.runTaskTimer(ToppeCheat.getInstance(), 1, 1);
            }
        }.runTaskLater(ToppeCheat.getInstance(),
                Bukkit.getPluginManager().getPlugin("StrikePractice").getConfig().getLong("wait-before-teleport") * 20);
    }

    @EventHandler
    public void onPartyFFAEnd(PartyFFAEndEvent e) {
        final Player p = e.getWinner();
        new BukkitRunnable() {

            @Override
            public void run() {
                jCheck.join.put(p.getUniqueId(), 0);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (p != null) {
                            int i = jCheck.join.getOrDefault(p.getUniqueId(), 0) + 1;
                            if (i == 4) {
                                jCheck.join.remove(p.getUniqueId());
                                this.cancel();
                            } else {
                                jCheck.join.put(p.getUniqueId(), i);
                            }
                        }
                    }
                }.runTaskTimer(ToppeCheat.getInstance(), 1, 1);
            }
        }.runTaskLater(ToppeCheat.getInstance(),
                Bukkit.getPluginManager().getPlugin("StrikePractice").getConfig().getLong("wait-before-teleport") * 20);
    }
}
