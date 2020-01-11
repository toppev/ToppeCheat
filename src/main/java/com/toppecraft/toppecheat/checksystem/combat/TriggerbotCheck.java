package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.UUID;

public class TriggerbotCheck extends Check implements Listener {

    private static final String lvMeta = "ToppeCheatTriggerbotHits";

    private ToppeCheat plugin;
    private float targetThreshold;

    public TriggerbotCheck(ToppeCheat plugin) {
        super(CheckType.TRIGGERBOT);
        this.plugin = plugin;
    }

    public static Entity getTarget(Player p, float threshold, double distance) {
        float tr = threshold;
        Entity target = null;
        for (Entity ent : p.getNearbyEntities(distance, distance, distance)) {
            if (ent instanceof LivingEntity && !ent.getUniqueId().equals(p.getUniqueId())) {
                float f = (float) AimbotCheck.clamp(AimbotCheck.getRotation(p.getLocation(), ent.getLocation()));
                if (f < tr) {
                    tr = f;
                    target = ent;
                }
            }
        }
        return target;
    }

    public static Entity getTarget(Location to, int threshold, UUID playerUUID, double distance) {
        float tr = threshold;
        Entity target = null;
        for (Player p : to.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(to) < distance * distance && !playerUUID.equals(p.getUniqueId())) {
                float f = (float) AimbotCheck.clamp(AimbotCheck.getRotation(to, p.getLocation()));
                if (f < tr) {
                    tr = f;
                    target = p;
                }
            }
        }
        return target;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
            return;
        }
        if (e.getAction().equals(Action.LEFT_CLICK_AIR) && e.getPlayer().isSprinting()) {
            if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.FISHING_ROD) {
                return;
            }
            Entity target = getTarget(p, targetThreshold, 4);
            int lv = 1;
            if (target != null) {
                if (p.hasMetadata(lvMeta)) {
                    MetadataValue m = plugin.getMeta(p, lvMeta);
                    if (m != null && m.value() != null) {
                        lv += m.asInt();
                    }
                }
            } else {
                p.removeMetadata(lvMeta, plugin);
                return;
            }
            p.setMetadata(lvMeta, new FixedMetadataValue(plugin, lv));
            if (lv >= getSettings().getThreshold() * 10) {
                int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                ViolationLevels.setVL(p, getType(), i + 1);
                new Alert(p, "(smoothaim/trigger)", this, plugin);
                if (i >= getSettings().getAutobanThreshold()) {
                    new Autoban(p, this, plugin);
                }
                p.removeMetadata(lvMeta, plugin);
            }
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        targetThreshold = (float) plugin.getFileManager().getSettingsFile().getConfig().getDouble(getType().getName() + ".target-threshold");
    }


    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }

}