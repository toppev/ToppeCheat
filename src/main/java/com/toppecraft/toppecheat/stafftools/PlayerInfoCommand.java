package com.toppecraft.toppecheat.stafftools;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.checksystem.misc.VapeCheck;
import com.toppecraft.toppecheat.checksystem.misc.VapeCheckCallback;
import com.toppecraft.toppecheat.permission.Permission;
import com.toppecraft.toppecheat.permission.PermissionManager;
import com.toppecraft.toppecheat.playerdata.PlayerData;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.utils.Paste;
import com.toppecraft.toppecheat.utils.Paste.ExpireDate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PlayerInfoCommand extends StaffCommand {

    private long lastPasteCreated;

    private ToppeCheat plugin;

    public PlayerInfoCommand(ToppeCheat plugin) {
        super("playerinfo", "Shows info about specific player");
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> violations [page]");
            sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> pasteviolations");
            sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> punishments [page]");
            sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> notes [add|remove]");
            sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> accounts [extra check (true or false)]");
            sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> pasteaccounts [extra check (true or false)]");
            sender.sendMessage(ChatColor.BLUE + "/playerinfo vape <query>");
        } else if (args.length == 2 && args[0].equals("vape")) {
            sender.sendMessage(ChatColor.GREEN + "Checking " + args[1] + "...");
            VapeCheck.vapesContains(args[1], new VapeCheckCallback() {

                @Override
                public void result(boolean found, String result) {
					if (found) {
						sender.sendMessage(ChatColor.RED + "Found match in vape database:");
						sender.sendMessage(ChatColor.GRAY + result);
					} else {
						sender.sendMessage(ChatColor.GREEN + "Didn't find any matching vape clients in the database.");
					}
                }
            });
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                if (VapeCheck.vapers.contains(target.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED + "Cracked Vape: true");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Cracked Vape: false");
                }
            }
        } else {
            new BukkitRunnable() {

                @Override
                public void run() {
                    @SuppressWarnings("deprecation")
                    OfflinePlayer tar = Bukkit.getOfflinePlayer(args[0]);
                    if (tar == null || !tar.hasPlayedBefore()) {
                        tar = Bukkit.getPlayer(args[0]);
                        if (tar == null) {
                            sender.sendMessage(ChatColor.RED + "The player has never played on this server.");
                            return;
                        }
                    }
                    final OfflinePlayer target = tar;
                    final UUID uuid = target.getUniqueId();
                    final PlayerData data = PlayerData.getPlayerData(uuid);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (data == null) {
                                sender.sendMessage(ChatColor.RED + "The player's data is invalid!");
                            } else if (args.length == 1 || (args.length > 1 && (args[1].equalsIgnoreCase("help") || args[1].equalsIgnoreCase("?")))) {
                                sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> violations [page]");
                                sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> pasteviolations");
                                sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> punishments [page]");
                                sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> notes [add|remove]");
                                sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> accounts [extra check (true or false)]");
                                sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> pasteaccounts [extra check (true or false)]");
                            } else {
                                if (args[1].equalsIgnoreCase("violations")) {
                                    int page = 1;
                                    if (args.length > 2) {
                                        try {
                                            page = Integer.parseInt(args[2]);
                                        } catch (NumberFormatException e) {
                                        }
                                    }
                                    int x = page * 8;
                                    int totalPages = data.getViolations().size() / 8;
									if (totalPages * 8 < data.getViolations().size()) {
										totalPages++;
									}
                                    sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + "'s violations " + ChatColor.GOLD + "(" + ChatColor.RED + (page) + "/" + totalPages + ChatColor.GOLD + ")");
                                    for (int i = x - 8; i < x; i++) {
                                        if (data.getViolations().size() > i) {
                                            String s = data.getViolations().get(i);
                                            sender.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + ((i + 1) + "").replaceFirst("^0+(?!$)", "") + ChatColor.YELLOW + "] " + s);
                                        }
                                    }
                                } else if (args[1].equalsIgnoreCase("pasteviolations")) {
                                    if (System.currentTimeMillis() - lastPasteCreated < 3000) {
                                        sender.sendMessage(ChatColor.RED + "Do not paste alerts so frequently! You may post every 15 seconds...");
                                    } else {
                                        sender.sendMessage(ChatColor.YELLOW + "Creating a paste of the player's violations...");
                                        final long st = System.currentTimeMillis();
                                        Paste paste = new Paste(data.getViolations(), ExpireDate.NEVER, target.getName() + "'s ( " + target.getUniqueId() + ") violations");
                                        paste.createPaste(paste.new LinkCallback() {

                                            @Override
                                            public void onSuccess(String link) {
                                                if (link == null) {
                                                    sender.sendMessage(ChatColor.RED + "Paste failed! Could not create a new paste. Be sure you have a valid api key!");
                                                } else {
                                                    long ms = System.currentTimeMillis() - st;
                                                    lastPasteCreated = System.currentTimeMillis();
                                                    sender.sendMessage(ChatColor.YELLOW + "The new paste of " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + "'s violations was created successfully in " + ms + " ms!");
                                                    sender.sendMessage(ChatColor.BLUE + "Link: " + link);
                                                }
                                            }
                                        });
                                    }
                                } else if (args[1].equalsIgnoreCase("ml")) {
                                    double average = 0;
                                    String allData = "";
                                    for (double c : data.getClicking()) {
                                        average += c;
                                        allData += MathUtils.toTwoDecimals(c) / 10 + ", ";
                                    }
                                    average /= data.getClicking().size();
                                    sender.sendMessage("All data: " + allData);
                                    sender.sendMessage(ChatColor.BLUE + "Data amount: " + data.getAllRecordedClicks());
                                    sender.sendMessage(ChatColor.BLUE + "Average: " + average / 10);
                                } else if (args[1].equalsIgnoreCase("mlreset")) {
                                    sender.sendMessage(ChatColor.RED + "Reseting...");
                                    data.getClicking().clear();
                                    data.setAllRecordedClicks(0);
                                    data.save(uuid);
                                } else if (args[1].equalsIgnoreCase("accounts")) {
                                    boolean extra = args.length > 2 && (args[2].equalsIgnoreCase("extra") || args[2].equalsIgnoreCase("true"));
                                    sender.sendMessage(ChatColor.YELLOW + "Running a search for possible alt accounts. This might take some time. Extra check: " + extra);
                                    data.getAllAccountsAsync(data.new AltsCallback() {

                                        @Override
                                        public void onSuccess(HashSet<UUID> alts) {
                                            if (sender != null) {
                                                if (alts.size() > 1) {
                                                    String comma = "";
                                                    StringBuilder sb = new StringBuilder();
                                                    for (UUID uuid : alts) {
                                                        sb.append(comma);
                                                        sb.append(Bukkit.getOfflinePlayer(uuid).getName());
                                                        comma = ", ";
                                                    }
                                                    String s = sb.toString();
                                                    sender.sendMessage(ChatColor.BLUE + "Found " + (alts.size() - 1) + " possible alt accounts for the following addresses: ");
                                                    int x = 0;
                                                    for (String address : data.getAddresses()) {
                                                        sender.sendMessage(ChatColor.YELLOW + " - " + address);
                                                        x++;
                                                        if (x == 5) {
                                                            sender.sendMessage(ChatColor.BLUE + "...and " + (data.getAddresses().size() - 5) + " more.");
                                                            break;
                                                        }
                                                    }
                                                    sender.sendMessage(ChatColor.YELLOW + "All accounts found:");
                                                    String msg = plugin.getFileManager().getMessagesFile().getMessage("alts").replaceAll("<player>", target.getName()).replace("<alts>", s);
                                                    if (sender != null) {
                                                        sender.sendMessage(msg);
                                                    }
                                                } else {
                                                    sender.sendMessage(ChatColor.RED + "No possible alt accounts found for the following addresses: ");
                                                    int x = 0;
                                                    for (String address : data.getAddresses()) {
                                                        sender.sendMessage(ChatColor.YELLOW + " - " + address);
                                                        x++;
                                                        if (x == 5) {
                                                            sender.sendMessage(ChatColor.BLUE + "...and " + (data.getAddresses().size() - 5) + " more.");
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }, extra);
                                } else if (args[1].equalsIgnoreCase("pasteaccounts")) {
                                    if (System.currentTimeMillis() - lastPasteCreated < 3000) {
                                        sender.sendMessage(ChatColor.RED + "Do not paste alerts so frequently! You may post every 15 seconds...");
                                    } else {
                                        boolean extra = args.length > 2 && (args[2].equalsIgnoreCase("extra") || args[2].equalsIgnoreCase("true"));
                                        sender.sendMessage(ChatColor.YELLOW + "Running a search for possible alt accounts. This might take some time. Extra check: " + extra);
                                        data.getAllAccountsAsync(data.new AltsCallback() {

                                            @Override
                                            public void onSuccess(HashSet<UUID> alts) {
                                                if (sender != null) {
                                                    if (alts.size() > 1) {
                                                        String comma = "";
                                                        StringBuilder sb = new StringBuilder();
                                                        for (UUID uuid : alts) {
                                                            sb.append(comma);
                                                            sb.append(Bukkit.getOfflinePlayer(uuid).getName());
                                                            comma = ", ";
                                                        }
                                                        String s = sb.toString();
                                                        sender.sendMessage(ChatColor.BLUE + "Found " + (alts.size() - 1) + " possible alt accounts for the following addresses: ");
                                                        int x = 0;
                                                        for (String address : data.getAddresses()) {
                                                            sender.sendMessage(ChatColor.YELLOW + " - " + address);
                                                            x++;
                                                            if (x == 5) {
                                                                sender.sendMessage(ChatColor.BLUE + "...and " + (data.getAddresses().size() - 5) + " more.");
                                                                break;
                                                            }
                                                        }
                                                        sender.sendMessage(ChatColor.YELLOW + "All accounts found:");
                                                        String msg = plugin.getFileManager().getMessagesFile().getMessage("alts").replaceAll("<player>", target.getName()).replace("<alts>", s);
                                                        if (sender != null) {
                                                            sender.sendMessage(msg);
                                                        }
                                                        sender.sendMessage(ChatColor.YELLOW + "Creating a new paste...");
                                                        List<String> lines = new ArrayList<String>();
                                                        for (UUID uuid : alts) {
                                                            lines.add(uuid.toString() + " - " + Bukkit.getOfflinePlayer(uuid).getName());
                                                        }
                                                        final long st = System.currentTimeMillis();
                                                        Paste paste = new Paste(lines, ExpireDate.NEVER, "Accounts");
                                                        paste.createPaste(paste.new LinkCallback() {

                                                            @Override
                                                            public void onSuccess(String link) {
                                                                if (link == null) {
                                                                    sender.sendMessage(ChatColor.RED + "Paste failed! Could not create a new paste. Be sure you have a valid api key!");
                                                                } else {
                                                                    long ms = System.currentTimeMillis() - st;
                                                                    lastPasteCreated = System.currentTimeMillis();
                                                                    sender.sendMessage(ChatColor.YELLOW + "The new paste of " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + "'s possible accounts was created successfully in " + ms + " ms!");
                                                                    sender.sendMessage(ChatColor.BLUE + "Link: " + link);
                                                                }
                                                            }
                                                        });
                                                    } else {
                                                        sender.sendMessage(ChatColor.RED + "No possible alt accounts found for the following addresses: ");
                                                        int x = 0;
                                                        for (String address : data.getAddresses()) {
                                                            sender.sendMessage(ChatColor.YELLOW + " - " + address);
                                                            x++;
                                                            if (x == 5) {
                                                                sender.sendMessage(ChatColor.BLUE + "...and " + (data.getAddresses().size() - 5) + " more.");
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }, extra);
                                    }
                                } else if (args[1].equalsIgnoreCase("punishments")) {
                                    int page = 1;
                                    if (args.length > 2) {
                                        try {
                                            page = Integer.parseInt(args[2]);
                                        } catch (NumberFormatException e) {
                                        }
                                    }
                                    int x = page * 8;
                                    int totalPages = data.getPunishments().size() / 8;
									if (totalPages * 8 < data.getPunishments().size()) {
										totalPages++;
									}
                                    sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + "'s punishments " + ChatColor.GOLD + "(" + ChatColor.RED + (page) + "/" + totalPages + ChatColor.GOLD + ")");
                                    for (int i = x - 8; i < x; i++) {
                                        if (data.getViolations().size() > i) {
                                            String s = data.getPunishments().get(i);
                                            sender.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GOLD + ((i + 1) + "").replaceFirst("^0+(?!$)", "") + ChatColor.YELLOW + "] " + s);
                                        }
                                    }
                                } else if (args[1].equalsIgnoreCase("notes")) {
                                    if (args.length > 3) {
                                        if (args[2].equalsIgnoreCase("add")) {
                                            StringBuilder note = new StringBuilder();
                                            for (int i = 3; i < args.length; i++) {
                                                note.append(args[i] + " ");
                                            }
                                            data.getNotes().add(note.toString());
                                            data.save(uuid);
                                            for (Player p : Bukkit.getOnlinePlayers()) {
                                                if (p.hasPermission("toppecheat.admin")) {
                                                    p.sendMessage(ChatColor.BLUE + sender.getName() + " gave " + target.getName() + " the following note: " + ChatColor.GOLD + note.toString());
                                                }
                                            }
                                        } else if (args[2].equalsIgnoreCase("remove")) {
                                            int i;
                                            try {
                                                i = Integer.parseInt(args[3]);
                                            } catch (NumberFormatException e) {
                                                sender.sendMessage(ChatColor.BLUE + "That's an invalid number!");
                                                return;
                                            }
                                            if (i < 1) {
                                                sender.sendMessage(ChatColor.BLUE + "That's an invalid number!");
                                                return;
                                            }
                                            if (data.getNotes().size() < i) {
                                                sender.sendMessage(ChatColor.BLUE + "The player doesn't have that many notes.");
                                                return;
                                            }
                                            String s = data.getNotes().get(i - 1);
                                            data.getNotes().remove(i - 1);
                                            for (Player p : Bukkit.getOnlinePlayers()) {
                                                if (PermissionManager.hasPermission(p, Permission.NOTIFY)) {
                                                    p.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has removed " + ChatColor.GOLD + target.getName() + "'s" + ChatColor.YELLOW + " note '" + ChatColor.GREEN + s + ChatColor.YELLOW + "'");
                                                }
                                            }
                                            data.save(uuid);
                                        }
                                    } else {
                                        sender.sendMessage(plugin.getFileManager().getMessagesFile().getMessage("following-notes").replace("<player>", target.getName()));
                                        String note = plugin.getFileManager().getMessagesFile().getMessage("note");
                                        for (String n : data.getNotes()) {
                                            sender.sendMessage(note.replace("<note>", n));
                                        }
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> violations [page]");
                                    sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> pasteviolations");
                                    sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> punishments [page]");
                                    sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> notes [add|remove]");
                                    sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> accounts [extra check (true or false)]");
                                    sender.sendMessage(ChatColor.BLUE + "/playerinfo <player> pasteaccounts [extra check (true or false)]");
                                }
                            }
                        }
                    }.runTask(plugin);
                }
            }.runTaskAsynchronously(plugin);
        }
        return true;
    }
}
