package com.toppecraft.toppecheat.checksystem.misc;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.HashSet;
import java.util.UUID;

public class VapeCheck extends Check implements Listener, PluginMessageListener {


    public static HashSet<UUID> vapers = new HashSet<UUID>();
    private ToppeCheat plugin;

    public VapeCheck(ToppeCheat plugin) {
        super(CheckType.VAPE);
        this.plugin = plugin;
    }

    public static void vapesContains(String str, final VapeCheckCallback callback) {
        final String text = str.toLowerCase();
        final ToppeCheat plugin = ToppeCheat.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                File file = new File(plugin.getDataFolder(), File.separator + "vapedatabase.txt");
                if (!file.exists()) {
                    plugin.saveResource("vapedatabase.txt", true);
                }
                BufferedReader br = null;
                boolean found = false;
                try {
                    br = new BufferedReader(new FileReader(file));
                    String l;
                    while ((l = br.readLine()) != null) {
                        final String line = l.toLowerCase();
                        if (line.contains(text)) {
                            found = true;
                            Bukkit.getScheduler().runTask(plugin, new Runnable() {

                                @Override
                                public void run() {
                                    callback.result(true, line);
                                }
                            });
                        }
                    }
                    if (!found) {
                        callback.result(false, null);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
				/*
			try {
				boolean found = false;
				int counter = 0;
				br = new BufferedReader(new FileReader(new File(posLoc)));
				String availalbe;
				while((availalbe = br.readLine()) != null) {
				}
				Scanner scanner = new Scanner(file, "UTF-8");
				while (scanner.hasNextLine()) {
					counter++;
					Bukkit.broadcastMessage(counter + "");
					final String line = scanner.nextLine().toLowerCase();
					if(line.contains(text)) {
						found = true;
						Bukkit.getScheduler().runTask(plugin, new Runnable() {

							@Override
							public void run() {
								callback.result(true, line);
							}
						});
					}
				}
				scanner.close();
				if(found) return;
			} catch(FileNotFoundException e) {}
			Bukkit.getScheduler().runTask(plugin, new Runnable() {

				@Override
				public void run() {
					callback.result(false, null);
				}
			});
				 */
            }
        });
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && ToppeCheat.random.nextInt(25) == 0 && vapers.contains(e.getDamager().getUniqueId())) {
            Player p = (Player) e.getDamager();
            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) + 1;
            ViolationLevels.setVL(p, getType(), i);
            new Alert(p, "", this, plugin);
            if (i >= getSettings().getAutobanThreshold()) {
                new Autoban(p, this, plugin);
            }
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "LOLIMAHCKER", this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPreJoin(PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        final String ip = e.getRealAddress().getHostAddress();
        vapesContains("|" + p.getName() + "|", new VapeCheckCallback() {

            @Override
            public void result(boolean found, String result) {
                if (found && p != null) {
                    int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) + 1;
                    ViolationLevels.setVL(p, getType(), i);
                    new Alert(p, "Query: " + "|" + p.getName() + "|", VapeCheck.this, plugin);
                    new Alert("Vape info: " + result, false);
                    if (i >= getSettings().getAutobanThreshold()) {
                        new Autoban(p, VapeCheck.this, plugin);
                    }
                }
            }
        });
        vapesContains("|" + ip + "|", new VapeCheckCallback() {

            @Override
            public void result(boolean found, String result) {
                if (found && p != null) {
                    int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration()) + 1;
                    ViolationLevels.setVL(p, getType(), i);
                    new Alert(p, "Query: " + "|" + ip + "|", VapeCheck.this, plugin);
                    new Alert("Vape info: " + result, false);
                    if (i >= getSettings().getAutobanThreshold()) {
                        new Autoban(p, VapeCheck.this, plugin);
                    }
                }
            }
        });
    }

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] data) {
        String str;
        try {
            str = new String(data);
        } catch (Exception ex) {
            str = "";
        }
        vapers.add(p.getUniqueId());
    }
}
