package com.toppecraft.toppecheat.checksystem.combat.killaura;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.checksystem.combat.AimbotCheck;
import com.toppecraft.toppecheat.lagmeter.LagMeter;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class KillauraLookCheck extends Check implements Listener {

    private ToppeCheat plugin;

    public KillauraLookCheck(ToppeCheat plugin) {
        super(CheckType.KILLAURA_LOOK);
        this.plugin = plugin;
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
        Player p = (Player) e.getDamager();
        Entity ent = e.getEntity();
        if (ent instanceof LivingEntity && ent.getWorld().getName().equals(p.getWorld().getName())) {
            a(p, (LivingEntity) ent);
        }
    }

    private void a(Player p, LivingEntity ent) {
        Location a = p.getLocation();
        Location b = ent.getLocation();
		if (MathUtils.calcDistance(a.getY(), b.getY()) > 1.5 || plugin.getLagMeter().getLastPacket(p) > LagMeter.maxPacketDelay) {
			return;
		}
        //You can legitimately hit slightly away from the HitBox (roughly 0.1 block)
        double hb = plugin.getNMSAccessProvider().getAccess().getMaxHitboxSize(ent) + 0.1;
        double rawYaw = AimbotCheck.clamp(AimbotCheck.getRotation(a, b));
        double violationYaw = MathUtils.calcDistanceClamped(rawYaw, AimbotCheck.clamp(a.getYaw()));
        double allowedDiff = MathUtils.calcDistanceClamped(AimbotCheck.clamp(AimbotCheck.getRotation(a, b.clone().add(hb, 0, hb))), rawYaw);
        double violation = MathUtils.calcDistance(violationYaw, allowedDiff);
        if (ent instanceof Player) {
            if (((Player) ent).isSprinting()) {
                violation *= 0.9;
            }
        }
        if (p.isSprinting()) {
            violation *= 0.9;
        }
        if (violation > 0 && a.distanceSquared(b) > 1.5 * 1.5 && !plugin.getLagMeter().isLagger(p)) {
            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
            violation += i;
            ViolationLevels.setVL(p, getType(), (int) (violation / 10));
            new Alert(p, "Diff: " + MathUtils.toTwoDecimals(violation) + "°", this, plugin);
            if (i >= getSettings().getAutobanThreshold()) {
                new Autoban(p, this, plugin);
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
