package com.toppecraft.toppecheat.checksystem.combat.killaura;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.checksystem.combat.AimbotCheck;
import com.toppecraft.toppecheat.checksystem.combat.TriggerbotCheck;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Set;

public class HeadSnapCheck extends Check implements Listener {

    private ToppeCheat plugin;

    public HeadSnapCheck(ToppeCheat plugin) {
        super(CheckType.HEAD_SNAP);
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        double yaw = MathUtils.calcDistanceClamped(AimbotCheck.clamp(e.getFrom().getYaw()), AimbotCheck.clamp(e.getTo().getYaw()));
        int x = getSettings().getThreshold() * 10;
		if (yaw < x || yaw < 360 - x) {
			return;
		}
        Player p = e.getPlayer();
        Entity target = TriggerbotCheck.getTarget(e.getTo(), 1, p.getUniqueId(), 10);
        if (target != null && p.getLocation().distanceSquared(target.getLocation()) > 2 * 2) {
            Location l = p.getTargetBlock((Set<Material>) null, 10).getLocation();
            for (Player pl : l.getWorld().getPlayers()) {
                if (pl.getLocation().distanceSquared(l) < 1) {
                    if (!plugin.getLagMeter().isLagger(p)) {
                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                        ViolationLevels.setVL(p, getType(), i + 1);
                        if (i % (getSettings().getThreshold()) == 0) {
                            new Alert(p, "Diff: " + yaw, this, plugin);
                        }
                        if (i >= getSettings().getAutobanThreshold()) {
                            new Autoban(p, this, plugin);
                        }
                    }
                }
                return;
            }
        }
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
