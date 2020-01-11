package com.toppecraft.toppecheat.punishments.automute;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.punishments.Punishment;
import com.toppecraft.toppecheat.punishments.automute.filters.AutomuteFilter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Automute extends Punishment {

    private static final String muted = "ToppeCheatAutomuted";
    private static final String level = "ToppeCheatFilterLevel";

    private long time;

    public Automute(Player p, String reason, long time) {
        super(p.getUniqueId(), reason, PunishmentType.AUTOMUTE);
        this.time = time;
        punish(p);
    }

    public static boolean canSpeak(Player p) {
        if (p.hasMetadata(muted)) {
            MetadataValue m = ToppeCheat.getInstance().getMeta(p, muted);
            if (m != null && m.value() != null) {
                long l = m.asLong();
                if (l != 0 && l >= System.currentTimeMillis()) {
                    return false;
                }
            }
            p.removeMetadata(muted, ToppeCheat.getInstance());
        }
        return true;
    }

    public static void setFilterLevel(Player p, AutomuteFilter filter, int i) {
        p.setMetadata(level + filter.toString(), new FixedMetadataValue(ToppeCheat.getInstance(), i));
    }

    public static int getFilterLevel(Player p, AutomuteFilter filter) {
        if (p.hasMetadata(level + filter.toString())) {
            MetadataValue m = ToppeCheat.getInstance().getMeta(p, level + filter.toString());
            if (m != null && m.value() != null) {
                return m.asInt();
            }
        }
        return 0;
    }

    private void punish(Player p) {
        p.setMetadata(muted, new FixedMetadataValue(ToppeCheat.getInstance(), System.currentTimeMillis() + time));
        p.sendMessage(ToppeCheat.getInstance().getFileManager().getMessagesFile().getMessage("automuted").replace("<reason>", getReason()));
        new Alert(ChatColor.AQUA + p.getName() + ChatColor.GREEN + " was muted for " + ChatColor.AQUA + getReason() + ChatColor.GREEN + " (" + (time / 1000) + " seconds)", true);
    }

    public long getTime() {
        return time;
    }

}
