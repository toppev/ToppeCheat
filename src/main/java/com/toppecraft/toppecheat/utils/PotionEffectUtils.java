package com.toppecraft.toppecheat.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectUtils {


    public static int getAmplifier(Player p, PotionEffectType potionEffectType) {
        for (PotionEffect ef : p.getActivePotionEffects()) {
            if (ef.getType().equals(potionEffectType)) {
                return ef.getAmplifier();
            }
        }
        return 0;
    }

    public static int getDuration(Player p, PotionEffectType potionEffectType) {
        for (PotionEffect ef : p.getActivePotionEffects()) {
            if (ef.getType().equals(potionEffectType)) {
                return ef.getDuration();
            }
        }
        return 0;
    }

    public static int getDurationSeconds(Player p, PotionEffectType potionEffectType) {
        return getDuration(p, potionEffectType) / 20;
    }
}
