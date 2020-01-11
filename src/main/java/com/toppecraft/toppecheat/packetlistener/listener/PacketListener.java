package com.toppecraft.toppecheat.packetlistener.listener;

import com.toppecraft.toppecheat.packetlistener.PacketEvent;

public abstract class PacketListener {

    public PacketListener() {
        PacketListenerManager.registerPacketListener(this);
    }

    public abstract void onPacketOutAsync(PacketEvent event);

    public abstract void onPacketInAsync(PacketEvent event);

    public void unregister() {
        PacketListenerManager.unregister(this);
    }

}
