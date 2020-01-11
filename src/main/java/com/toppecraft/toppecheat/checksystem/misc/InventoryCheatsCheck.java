package com.toppecraft.toppecheat.checksystem.misc;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class InventoryCheatsCheck extends Check implements Listener {

    private ToppeCheat plugin;
    private HashMap<String, Long> time = new HashMap<String, Long>();
    private HashMap<String, Long> justReported = new HashMap<String, Long>();

    public InventoryCheatsCheck(ToppeCheat plugin) {
        super(CheckType.INVENTORY_CHEATS);
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item != null && item.getType() != Material.AIR) {
            Player p = (Player) e.getWhoClicked();
            time.put(p.getName(), System.currentTimeMillis() + 200 - getSettings().getThreshold() * 10);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e) {
        if ((e.getAction() != Action.RIGHT_CLICK_AIR) && (e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player p = e.getPlayer();
        if (p.getItemInHand() != null && p.getItemInHand().getType() != Material.AIR) {
            Material m = p.getItemInHand().getType();
            if (time.containsKey(p.getName())) {
                long lo = this.time.get(p.getName());
                long l = lo - System.currentTimeMillis();
                if (l > 0) {
                    report(p, m);
                }
            }
        }
    }

    private void report(Player p, Material m) {
        if (plugin.getLagMeter().isLagger(p)) {
            return;
        }
        if (this.justReported.containsKey(p.getName()) && this.justReported.get(p.getName()) + 50 > System.currentTimeMillis()) {
            return;
        }
        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
        ViolationLevels.setVL(p, getType(), i + 1);
        new Alert(p, "Material: " + m.toString().toLowerCase().replace("_", " "), this, plugin);
        if (i >= getSettings().getAutobanThreshold()) {
            new Autoban(p, this, plugin);
        }
        this.justReported.put(p.getName(), Long.valueOf(System.currentTimeMillis()));
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }
}
