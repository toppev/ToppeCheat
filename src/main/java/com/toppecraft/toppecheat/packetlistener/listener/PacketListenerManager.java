package com.toppecraft.toppecheat.packetlistener.listener;

import java.util.ArrayList;
import java.util.List;

public class PacketListenerManager {

    public static List<PacketListener> listeners = new ArrayList<PacketListener>();

    public static void registerPacketListener(PacketListener listener) {
        listeners.add(listener);
    }

    public static void unregister(PacketListener listener) {
        listeners.remove(listener);
    }

}
