package com.toppecraft.toppecheat.utils;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PlayerLocationUtils {

    /**
     * Gets if the player is actually in air, not in water or web or anything.
     *
     * @param p      player to check
     * @param plugin ToppeCheat plugin
     *
     * @return false if it was found the player actually not in air, otherwise true
     */
    @SuppressWarnings("deprecation")
    public static boolean isReallyInAir(Player p, ToppeCheat plugin) {
		if (p.isOnGround()) {
			return false;
		}
        if (p.getLocation().add(1, 0, 0).getBlock().isLiquid() || p.getLocation().add(1, 0, 0).getBlock().getType().equals(Material.WEB)) {
            return false;
        }
        if (p.getLocation().add(-1, 0, 0).getBlock().isLiquid() || p.getLocation().add(-1, 0, 0).getBlock().getType().equals(Material.WEB)) {
            return false;
        }
        if (p.getLocation().add(0, 0, 1).getBlock().isLiquid() || p.getLocation().add(0, 0, 1).getBlock().getType().equals(Material.WEB)) {
            return false;
        }
        if (p.getLocation().add(0, 0, -1).getBlock().isLiquid() || p.getLocation().add(0, 0, -1).getBlock().getType().equals(Material.WEB)) {
            return false;
        }
        if (p.getLocation().add(1, 0, 1).getBlock().isLiquid() || p.getLocation().add(1, 0, 1).getBlock().getType().equals(Material.WEB)) {
            return false;
        }
        if (p.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {
            return false;
        }
        if (p.getLocation().getBlock().getType() != Material.AIR) {
            return false;
        }
        if (p.getLocation().add(0, 1, 0).getBlock().getType() != Material.AIR) {
            return false;
        }
        if (p.getLocation().add(-1, 0, -1).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(-1, 0, 1).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(1, 0, -1).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(1, 1, 0).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(-1, 1, 0).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(0, 1, 1).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(0, 1, -1).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(1, 1, 1).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(-1, 1, -1).getBlock().isLiquid()) {
            return false;
        }
        if (p.getLocation().add(-1, 1, 1).getBlock().isLiquid()) {
            return false;
        }
        return !p.getLocation().add(1, 1, -1).getBlock().isLiquid();
    }

    public static boolean isTrapped(Player p) {
        return isTrapped(p.getLocation());
    }

    public static boolean isTrapped(Location l) {
        for (int i = 0; i < 2; i++) {
			if (isTrapBlock(l.clone().add(0, i, 0).getBlock())) {
				return true;
			}
			if (isTrapBlock(l.clone().add(1, i, 0).getBlock())) {
				return true;
			}
			if (isTrapBlock(l.clone().add(-1, i, 0).getBlock())) {
				return true;
			}
			if (isTrapBlock(l.clone().add(0, i, 1).getBlock())) {
				return true;
			}
			if (isTrapBlock(l.clone().add(0, i, -1).getBlock())) {
				return true;
			}
        }
        return false;
    }

    public static HashSet<Material> getMaterialsAround(Location l) {
        HashSet<Material> materials = new HashSet<Material>();
        materials.add(l.getBlock().getType());
        materials.add(l.clone().add(1, 0, 0).getBlock().getType());
        materials.add(l.clone().add(0, 0, 1).getBlock().getType());
        materials.add(l.clone().add(-1, 0, 0).getBlock().getType());
        materials.add(l.clone().add(0, 0, -1).getBlock().getType());
        materials.add(l.clone().add(-1, 0, -1).getBlock().getType());
        materials.add(l.clone().add(1, 0, 1).getBlock().getType());
        materials.add(l.clone().add(1, 0, -1).getBlock().getType());
        materials.add(l.clone().add(-1, 0, 1).getBlock().getType());
        return materials;
    }

    private static boolean isTrapBlock(Block b) {
        return b.getType().isSolid();
    }

    public static HashSet<Material> getMaterialsBelow(Player p) {
        return getMaterials(p.getLocation().subtract(0, 1, 0));
    }

    public static HashSet<Material> getMaterials(Location loc) {
        HashSet<Material> m = new HashSet<Material>();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        World world = loc.getWorld();
        m.add(new Location(world, x + 0.3, y, z - 0.3).getBlock().getType());
        m.add(new Location(world, x - 0.3, y, z - 0.3).getBlock().getType());
        m.add(new Location(world, x + 0.3, y, z + 0.3).getBlock().getType());
        m.add(new Location(world, x - 0.3, y, z + 0.3).getBlock().getType());
        return m;
    }

    public static List<Player> getPlayersNearby(Entity ent, double rad) {
        List<Player> list = new ArrayList<Player>();
        for (Entity e : ent.getNearbyEntities(rad, rad, rad)) {
            if (e instanceof Player) {
                list.add((Player) e);
            }
        }
        return list;
    }

}
