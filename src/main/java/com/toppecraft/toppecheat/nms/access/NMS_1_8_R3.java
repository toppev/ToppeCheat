package com.toppecraft.toppecheat.nms.access;

import com.toppecraft.toppecheat.utils.FieldUtils;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.ServerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NMS_1_8_R3 implements NMSAccess {

    @Override
    public int getPing(Player p) {
        CraftPlayer cp = (CraftPlayer) p;
        return cp.getHandle().ping;
    }

    @Override
    public double getMaxHitboxSize(LivingEntity ent) {
        CraftLivingEntity le = (CraftLivingEntity) ent;
        AxisAlignedBB bb = le.getHandle().getBoundingBox();
        Location l = ent.getLocation();
        double a = Math.abs(bb.a - l.getX());
        double c = Math.abs(bb.c - l.getZ());
        double d = Math.abs(bb.d - l.getX());
        double f = Math.abs(bb.f - l.getZ());
        return Math.max(Math.max(a, c), Math.max(d, f));
    }

    @Override
    public void registerServerConnectionChannel() {
        Field l = FieldUtils.getField(ServerConnection.class, "h");
        ServerConnection sc = ((CraftServer) Bukkit.getServer()).getServer().aq();
        @SuppressWarnings("unchecked")
        List<NetworkManager> mList = (List<NetworkManager>) FieldUtils.get(l, sc);
        List<Object> newList = Collections.synchronizedList(new ArrayList<Object>());
        for (Object o : mList) {
            newList.add(o);
        }
        FieldUtils.set(l, sc, newList);
    }

    @Override
    public Object getChannel(Player p) {
        Field f = FieldUtils.getField(NetworkManager.class, "channel");
        CraftPlayer cp = (CraftPlayer) p;
        NetworkManager nm = cp.getHandle().playerConnection.networkManager;
        Object o = FieldUtils.get(f, nm);
        return o;
    }

    @Override
    public int getVictim(Object packet) {
        PacketPlayInEntityAction actionPacket = (PacketPlayInEntityAction) packet;
        return actionPacket.c();
    }

}
