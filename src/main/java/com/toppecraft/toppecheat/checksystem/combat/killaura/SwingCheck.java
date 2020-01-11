package com.toppecraft.toppecheat.checksystem.combat.killaura;

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
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class SwingCheck extends Check implements Listener {

    private static final String swung = "ToppeCheatSwungMeta";
    //private static boolean eventCheck;

    private ToppeCheat plugin;

    public SwingCheck(ToppeCheat plugin) {
        super(CheckType.NO_SWING_KILLAURA);
        this.plugin = plugin;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();
            if (Bukkit.getPlayer(p.getUniqueId()) == null) {
                return;
            }
            if (!swinging(p) && !plugin.getCheckBypassManager().hasBypass(p, getType())) {
                int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                i++;
                ViolationLevels.setVL(p, getType(), i);
                if (i % (getSettings().getThreshold()) == 0) {
                    new Alert(p, "abnormal packet sending", this, plugin);
                }
                if (i >= getSettings().getAutobanThreshold()) {
                    new Autoban(p, this, plugin);
                }
            }
        }
    }


    private boolean swinging(Player p) {
        if (p.hasMetadata(swung)) {
            p.removeMetadata(swung, plugin);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent e) {
        if (e.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            Player p = e.getPlayer();
            p.setMetadata(swung, new FixedMetadataValue(plugin, System.currentTimeMillis()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.getPlayer().removeMetadata(swung, plugin);
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
