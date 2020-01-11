package com.toppecraft.toppecheat.packetlistener;


public enum PacketType {

    PLAY_IN_CLIENT_COMMAND("PacketPlayInClientCommand"),
    PLAY_IN_CLOSE_WINDOW("PacketPlayInCloseWindow"),
    PLAY_IN_FLYING("PacketPlayInFlying"),
    PLAY_IN_LOOK("PacketPlayInLook"),
    PLAY_IN_POSITION("PacketPlayInPosition"),
    PLAY_IN_POSITION_LOOK("PacketPlayInPositionLook"),
    PLAY_IN_USE_ENTITY("PacketPlayInUseEntity");

    private String name;

    PacketType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isPlayOut() {
        return getName().startsWith("PacketPlayOut");
    }

    public boolean isPlayIn() {
        return getName().startsWith("PacketPlayIn");
    }
}
