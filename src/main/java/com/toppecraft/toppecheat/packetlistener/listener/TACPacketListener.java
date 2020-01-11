package com.toppecraft.toppecheat.packetlistener.listener;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.checksystem.combat.MultipleClickersCheck;
import com.toppecraft.toppecheat.checksystem.combat.ReachCheck;
import com.toppecraft.toppecheat.checksystem.combat.killaura.JoinCheck;
import com.toppecraft.toppecheat.checksystem.combat.killaura.KillauraLookCheck;
import com.toppecraft.toppecheat.checksystem.combat.killaura.MissHitRation;
import com.toppecraft.toppecheat.checksystem.misc.BadPacketsCheck;
import com.toppecraft.toppecheat.packetlistener.PacketEvent;
import com.toppecraft.toppecheat.packetlistener.PacketType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TACPacketListener extends PacketListener {

    public static BadPacketsCheck badPackets;
    public static MultipleClickersCheck mcc;
    public static ReachCheck reachCheck;
    public static KillauraLookCheck kaCheck;
    public static MissHitRation mhCheck;
    public static JoinCheck jCheck;

    private ToppeCheat plugin;

    public TACPacketListener(ToppeCheat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketOutAsync(PacketEvent e) {}

    @Override
    public void onPacketInAsync(final PacketEvent e) {
        final Player p = e.getPlayer();
        PacketType type = e.getType();
        if (type != null) {
            if (jCheck != null && p != null && !jCheck.handled.contains(p.getUniqueId())) {
                jCheck.handled.add(p.getUniqueId());
                jCheck.join.put(p.getUniqueId(), 0);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (p != null) {
                            int i = jCheck.join.getOrDefault(p.getUniqueId(), 0) + 1;
                            if (i == 4) {
                                jCheck.join.remove(p.getUniqueId());
                                this.cancel();
                            } else {
                                jCheck.join.put(p.getUniqueId(), i);
                            }
                        }
                    }
                }.runTaskTimer(plugin, 1, 1);
            }
            if (type.equals(PacketType.PLAY_IN_USE_ENTITY)) {
                //	try{
                //Entity target = getEntity(p, plugin.getNMSAccessProvider().getAccess().getVictim(e.getPacket()));
                //	if(target != null) {
                if (mhCheck != null) {
                    mhCheck.onAttackPacket(p);
                }
				/*
					if(reachCheck != null) {
						reachCheck.onPacket(p, target);
					}
					if(kaCheck != null) {
						kaCheck.onPacket(p, target);
					}
					if(mcc != null) {
						mcc.onClick(p);
					}
				 */
            }

            //}catch(Exception ex) {}
            if (type.equals(PacketType.PLAY_IN_FLYING)) {
                plugin.getLagMeter().onPacket(p);
                if (badPackets != null) {
                    badPackets.onFlyingPacket(p);
                }
            } else if (type.equals(PacketType.PLAY_IN_POSITION) || type.equals(PacketType.PLAY_IN_POSITION_LOOK)
                    || type.equals(PacketType.PLAY_IN_POSITION_LOOK)) {
                plugin.getLagMeter().onPacket(p);
            }
        }
    }

	/*
	private Entity getEntity(Player p, int id) {
		for(Entity ent : p.getWorld().getLivingEntities()) {
			if(ent.getEntityId() == id) {
				return ent;
			}
		}
		return null;
	}
	 */
}
