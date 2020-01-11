package com.toppecraft.toppecheat.packetlistener;

import org.bukkit.entity.Player;

public class PacketEvent {

    private Player player;
    private boolean cancelled;
    private Object packet;

    public PacketEvent(Player player, Object packet) {
        this.player = player;
        this.packet = packet;
    }

    /**
     * @return the packet
     */
    public Object getPacket() {
        return packet;
    }

    /**
     * @param packet the packet to set
     */
    public void setPacket(Object packet) {
        this.packet = packet;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @param cancelled the cancelled to set
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public PacketType getType() {
        PacketType type = null;
        for (PacketType t : PacketType.values()) {
            if (t.getName().equals(getPacket().getClass().getSimpleName())) {
                type = t;
            }
        }
        return type;
    }

}
