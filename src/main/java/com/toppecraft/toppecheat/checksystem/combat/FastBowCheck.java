package com.toppecraft.toppecheat.checksystem.combat;

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
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class FastBowCheck extends Check implements Listener {

    private String tag = "toppecheat-metadata-fastbow";

    private double minForce;
    private ToppeCheat plugin;

    public FastBowCheck(ToppeCheat plugin) {
        super(CheckType.FASTBOW);
        this.plugin = plugin;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player && e.getForce() >= minForce) {
            Player p = (Player) e.getEntity();
            if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
                return;
            }
            if (p.hasMetadata(tag)) {
                MetadataValue m = plugin.getMeta(p, tag);
                if (m != null && m.value() != null) {
                    if (m.asLong() + 200 - (getSettings().getThreshold() * 10) >= System.currentTimeMillis()) {
                        if (!plugin.getLagMeter().isLagger(p)) {
                            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                            ViolationLevels.setVL(p, getType(), i + 1);
                            new Alert(p, "", FastBowCheck.this, plugin);
                            if (i >= getSettings().getAutobanThreshold()) {
                                new Autoban(p, FastBowCheck.this, plugin);
                            }
                        }
                    }
                }
            }
            p.setMetadata(tag, new FixedMetadataValue(plugin, System.currentTimeMillis()));
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        minForce = plugin.getFileManager().getSettingsFile().getConfig().getDouble(getType().getName() + ".min-force");
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }
}
