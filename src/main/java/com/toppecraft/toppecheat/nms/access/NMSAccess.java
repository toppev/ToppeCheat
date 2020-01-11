package com.toppecraft.toppecheat.nms.access;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface NMSAccess {

    /**
     * Gets ping of the player
     *
     * @param p player to check
     *
     * @return ping of the player or -1 if the check failed;
     */
    int getPing(Player p);

    /**
     * Gets the distance from the location of the entity to the farthest HitBox border
     *
     * @param ent the LivingEntity
     *
     * @return the distance from the location of the entity to the farthest HitBox border
     */
    double getMaxHitboxSize(LivingEntity ent);

    /**
     * @deprecated probably going to be removed because it's useless
     */
    void registerServerConnectionChannel();

    /**
     * Gets the player's channel as an Object since this returns io.netty.channel.Channel with newer versions (1.8+) and
     * net.minecraft.util.io.netty.channel.Channel with older versions (1.7 and even older (that aren't supported))
     *
     * @param p the player
     *
     * @return the player's channel (or null if none found)
     */
    Object getChannel(Player p);

    int getVictim(Object packet);

}
