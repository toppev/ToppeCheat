package com.toppecraft.toppecheat.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class MineCraftVersionManager {

    private static MineCraftVersionManager versionManager;

    public static void load() {
		if (PotionEffectType.getByName("LEVITATION") != null && Material.getMaterial("ELYTRA") != null) {
			versionManager = new MineCraft1_9Manager();
		} else {
			versionManager = new MineCraftVersionManager();
		}
    }

    public static MineCraftVersionManager getVersionManager() {
        return versionManager;
    }

    public boolean isGliding(Player p) {
        return false;
    }

    public int getLevitationAmplifier(Player p) {
        return 0;
    }

}


class MineCraft1_9Manager extends MineCraftVersionManager {


    public MineCraft1_9Manager() {

    }
	/*
	@Override
	public boolean isGliding(Player p) {
		return p.isGliding();
	}

	@Override
	public int getLevitationAmplifier(Player p) {
		return PotionEffectUtils.getAmplifier(p, PotionEffectType.LEVITATION);
	}
	 */
}
