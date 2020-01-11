package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.MathUtils;
import com.toppecraft.toppecheat.utils.PlayerLocationUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;

public class AimbotCheck extends Check implements Listener {

    private static final String lastMove = "ToppeCheatLastMove";
    private ToppeCheat plugin;

    public AimbotCheck(ToppeCheat plugin) {
        super(CheckType.AIMBOT);
        this.plugin = plugin;
    }

    public static float getRotation(Location one, Location two) {
        double dx = two.getX() - one.getX();
        double dz = two.getZ() - one.getZ();
        float yaw = (float) (MathUtils.atan2((float) dz, (float) dx) * 180.0D / 3.141592653589793) - 90.0F;
        return yaw;
    }

    public static double clamp(double theta) {
        theta %= 360;
        if (theta >= 180) {
            theta -= 360;
        }
        if (theta < -180) {
            theta += 360;
        }
        return theta;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
		if (plugin.getCheckBypassManager().hasBypass(p, getType())) {
			return;
		}
        AimbotData data = AimbotData.getData(p);
        for (Player pl : PlayerLocationUtils.getPlayersNearby(p, 4)) {
            double d = clamp(getRotation(p.getLocation(), pl.getLocation()));
            if (d < 50 && e.getTo().getYaw() == 0 && p.isOnGround() && pl.getLocation().getBlock().getY() == p.getLocation().getBlock().getY()) {
                if (data.c + 10000 > System.currentTimeMillis()) {
                    data.cCounter = 0;
                    data.c = System.currentTimeMillis();
                } else {
                    data.cCounter++;
                    if (data.cCounter == 25) {
                        data.cCounter = 0;
                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                        ViolationLevels.setVL(p, getType(), i++);
                        new Alert(p, "type C", this, plugin);
                        if (i >= getSettings().getAutobanThreshold()) {
                            new Autoban(p, this, plugin);
                        }
                    }
                }
                break;
            }
            double avrg = Math.abs(d) / clamp(MathUtils.calcDistanceClamped(clamp(e.getTo().getYaw()), clamp(e.getFrom().getYaw())));
            if (data.avrgD == 0) {
                data.avrgD = avrg;
                data.d = System.currentTimeMillis();
            } else {
                if (MathUtils.calcDistance(data.avrgD, avrg) < getSettings().getThreshold() / 100) {
                    data.dCounter++;
                    if (data.dCounter == 15) {
                        data.dCounter = 0;
                        int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                        ViolationLevels.setVL(p, getType(), i++);
                        new Alert(p, "type A", this, plugin);
                        if (i >= getSettings().getAutobanThreshold()) {
                            new Autoban(p, this, plugin);
                        }
                    }
                }
            }
            if (data.d + 10000 > System.currentTimeMillis()) {
                data.dCounter = 0;
                data.avrgD = 0;
            }
        }
        Location lastLoc;
        if (p.hasMetadata(lastMove)) {
            MetadataValue m = plugin.getMeta(p, lastMove);
            if (m != null && m.value() != null && m.value() instanceof Location) {
                lastLoc = ((Location) m.value()).clone();
            } else {
                p.setMetadata(lastMove, new FixedMetadataValue(plugin, e.getTo()));
                return;
            }
        } else {
            p.setMetadata(lastMove, new FixedMetadataValue(plugin, e.getTo()));
            return;
        }
        p.setMetadata(lastMove, new FixedMetadataValue(plugin, e.getTo()));
        if (lastLoc != null) {
            if (MathUtils.calcDistanceClamped(clamp(lastLoc.getYaw()), clamp(p.getLocation().getYaw())) > 2) {
                if ((int) (lastLoc.getPitch() * 10) == (int) (e.getTo().getPitch() * 10)) {
                    if (data.b + 10000 > System.currentTimeMillis()) {
                        data.bCounter = 0;
                        data.b = System.currentTimeMillis();
                    } else {
                        data.bCounter++;
                        if (data.bCounter == 10) {
                            data.bCounter = 0;
                            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                            ViolationLevels.setVL(p, getType(), i++);
                            new Alert(p, "type B", this, plugin);
                            if (i >= getSettings().getAutobanThreshold()) {
                                new Autoban(p, this, plugin);
                            }
                        }
                    }
                } else {
                    data.bCounter = 0;
                }
            }
            if (MathUtils.calcDistanceClamped(clamp(lastLoc.getPitch()), clamp(p.getLocation().getPitch())) > 2) {
                if ((int) (lastLoc.getYaw() * 10) == (int) (e.getTo().getYaw() * 10)) {
                    if (data.e + 10000 > System.currentTimeMillis()) {
                        data.eCounter = 0;
                        data.e = System.currentTimeMillis();
                    } else {
                        data.eCounter++;
                        if (data.eCounter == 8) {
                            data.eCounter = 0;
                            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
                            ViolationLevels.setVL(p, getType(), i++);
                            new Alert(p, "type D", this, plugin);
                            if (i >= getSettings().getAutobanThreshold()) {
                                new Autoban(p, this, plugin);
                            }
                        }
                    }
                } else {
                    data.bCounter = 0;
                }
            }
        }
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, plugin);
		/*
		lockDistance = plugin.getFileManager().getSettingsFile().getConfig().getDouble(getType().getName() + ".lock-aimbot-distance");
		smoothDistance = plugin.getFileManager().getSettingsFile().getConfig().getInt(getType().getName() + ".smooth-aimbot-distance");
		aimsNeeded = plugin.getFileManager().getSettingsFile().getConfig().getInt(getType().getName() + ".aims-needed-to-check");
		 */
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
        plugin.getLagMeter().getRegisteredMatters().remove(this);
    }
}

class AimbotData {

    private static final String meta = "ToppeCheatAimdata";
    public long a;
    public int aCounter;
    public long b;
    public int bCounter;
    public long c;
    public int cCounter;
    public long d;
    public long dCounter;
    public double avrgD;
    public long e;
    public int eCounter;
    private List<Float> aims = new ArrayList<Float>();

    public static AimbotData getData(Player p) {
        if (p.hasMetadata(meta)) {
            MetadataValue m = ToppeCheat.getInstance().getMeta(p, meta);
            if (m != null && m.value() != null && m.value() instanceof AimbotData) {
                return (AimbotData) m.value();
            }
        }
        AimbotData data = new AimbotData();
        p.setMetadata(meta, new FixedMetadataValue(ToppeCheat.getInstance(), data));
        return data;
    }

    public List<Float> getAims() {
        return aims;
    }

    public void setAims(List<Float> aims) {
        this.aims = aims;
    }
}
