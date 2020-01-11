package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.lagmeter.LagMeter.LagNotifyNeeder;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class RegenDelayCheck extends Check implements Listener, LagNotifyNeeder {

    private final static String meta = "ToppeCheatRegenMeta";

    private ToppeCheat plugin;

    public RegenDelayCheck(ToppeCheat plugin) {
        super(CheckType.REGEN_DELAY);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegen(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED && !plugin.getCheckBypassManager().hasBypass(p, getType())) {
                if (p.hasMetadata(meta)) {
                    MetadataValue m = plugin.getMeta(p, meta);
                    if (m != null && m.value() != null) {
                        long delay = System.currentTimeMillis() - m.asLong();
                        long delayViolation = getRegenDelay(p) - delay;
                        delayViolation -= getSettings().getThreshold() * 50;
                        if (delayViolation > 0) {
                            alert(p, "regen time: " + delay + " ms.");
                        }
                    }
                }
                p.setMetadata(meta, new FixedMetadataValue(plugin, System.currentTimeMillis()));
            }
        }
    }

    private long getRegenDelay(Player p) {
        int food = p.getFoodLevel();
        if (p.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            return 500;
        }
        if (food >= 20) {
            return 500;
        }
        if (food >= 18) {
            return 4000;
        }
        return 0;
    }

    private void alert(Player p, String details) {
        if (!plugin.getLagMeter().isLagger(p)) {
            if (p != null && !plugin.getLagMeter().isLagger(p)) {
                int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                ViolationLevels.setVL(p, getType(), i + 1);
                if (i % (getSettings().getThreshold()) == 0) {
                    new Alert(p, details, RegenDelayCheck.this, plugin);
                }
                if (i >= getSettings().getAutobanThreshold()) {
                    new Autoban(p, RegenDelayCheck.this, plugin);
                }
            }
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLagMeter().registerLagNotifyNeeder(this);
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
        plugin.getLagMeter().getRegisteredMatters().remove(this);
    }

    @Override
    public void onLag(Player p) {
        p.removeMetadata(meta, plugin);
    }
}

