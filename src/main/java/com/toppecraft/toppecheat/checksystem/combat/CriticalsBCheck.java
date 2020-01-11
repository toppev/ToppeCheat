package com.toppecraft.toppecheat.checksystem.combat;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.alert.Alert;
import com.toppecraft.toppecheat.checksystem.Check;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.punishments.autoban.Autoban;
import com.toppecraft.toppecheat.utils.PlayerLocationUtils;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class CriticalsBCheck extends Check implements Listener {

    private Map<UUID, Map.Entry<Integer, Long>> CritTicks = new HashMap();
    private Map<UUID, Double> FallDistance = new HashMap();
    private Map<Player, Long> lastHit = new HashMap();
    public CriticalsBCheck() {
        super(CheckType.CRITICALS_B);
    }

    public static boolean blocksNear(Player player) {
        return blocksNear(player.getLocation());
    }

    public static boolean blocksNear(Location loc) {
        boolean nearBlocks = false;
        for (Block block2 : getSurrounding(loc.getBlock(), true)) {
            if (block2.getType() != Material.AIR) {
                nearBlocks = true;
                break;
            }
        }
        for (Block block2 : getSurrounding(loc.getBlock(), false)) {
            if (block2.getType() != Material.AIR) {
                nearBlocks = true;
                break;
            }
        }
        loc.setY(loc.getY() - 0.5D);
        if (loc.getBlock().getType() != Material.AIR) {
            nearBlocks = true;
        }
        if (isBlock(loc.getBlock().getRelative(BlockFace.DOWN), new Material[]{Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER})) {
            nearBlocks = true;
        }
        return nearBlocks;
    }

    public static boolean isBlock(Block block, Material[] materials) {
        Material type = block.getType();
        Material[] arrmaterial = materials;
        int n = arrmaterial.length;
        int n2 = 0;
        while (n2 < n) {
            Material m = arrmaterial[n2];
            if (m == type) {
                return true;
            }
            n2++;
        }
        return false;
    }

    public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
        ArrayList<Block> blocks = new ArrayList();
        if (diagonals) {
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if ((x != 0) || (y != 0) || (z != 0)) {
                            blocks.add(block.getRelative(x, y, z));
                        }
                    }
                }
            }
        } else {
            blocks.add(block.getRelative(BlockFace.UP));
            blocks.add(block.getRelative(BlockFace.DOWN));
            blocks.add(block.getRelative(BlockFace.NORTH));
            blocks.add(block.getRelative(BlockFace.SOUTH));
            blocks.add(block.getRelative(BlockFace.EAST));
            blocks.add(block.getRelative(BlockFace.WEST));
        }
        return blocks;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        Player player = (Player) e.getDamager();
        Player p = (Player) e.getEntity();
        if (!this.lastHit.containsKey(p)) {
            this.lastHit.put(p, System.currentTimeMillis());
        } else {
            if (System.currentTimeMillis() - this.lastHit.get(p).longValue() < 1500L) {
                return;
            }
            this.lastHit.remove(p);
        }
        if (player.isFlying()) {
            return;
        }
        for (Material mat : PlayerLocationUtils.getMaterialsBelow(p)) {
            String s = mat.toString().toLowerCase();
            if (s.contains("slab")
                    || s.contains("carpet") || s.contains("vine") || s.contains("ladder")
                    || s.contains("fence") || s.contains("bed") || s.contains("rail")
                    || s.contains("so")) {
                return;
            }
        }
        Location pL = player.getLocation().clone();
        pL.add(0.0D, player.getEyeHeight() + 1.0D, 0.0D);
        if (blocksNear(pL)) {
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (this.CritTicks.containsKey(player.getUniqueId())) {
            Count = ((Integer) ((Map.Entry) this.CritTicks.get(player.getUniqueId())).getKey()).intValue();
            Time = ((Long) ((Map.Entry) this.CritTicks.get(player.getUniqueId())).getValue()).longValue();
        }
        if (!this.FallDistance.containsKey(player.getUniqueId())) {
            return;
        }
        double realFallDistance = this.FallDistance.get(player.getUniqueId()).doubleValue();
        Count++;
        Count = (player.getFallDistance() > 0.0D) && (!player.isOnGround()) && (realFallDistance == 0.0D) ? Count : 0;
        if ((this.CritTicks.containsKey(player.getUniqueId())) && System.currentTimeMillis() - Time > 10000) {
            Count = 0;
            Time = System.currentTimeMillis();
        }
        if (Count >= 2) {
            Count = 0;
            int i = ViolationLevels.getLevel(p, getType(), ViolationLevels.getDefaultExpiration());
            ViolationLevels.setVL(p, getType(), i + 1);
            if (i % (getSettings().getThreshold()) == 0) {
                new Alert(p, "Type B", this, ToppeCheat.getInstance());
            }
            if (i >= getSettings().getAutobanThreshold()) {
                new Autoban(p, this, ToppeCheat.getInstance());
            }
        }
        this.CritTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry(Integer.valueOf(Count), Long.valueOf(Time)));
    }

    @EventHandler
    public void Move(PlayerMoveEvent e) {
        Player Player2 = e.getPlayer();
        double Falling = 0.0D;
        if ((!Player2.isOnGround()) && (e.getFrom().getY() > e.getTo().getY())) {
            if (this.FallDistance.containsKey(Player2.getUniqueId())) {
                Falling = this.FallDistance.get(Player2.getUniqueId()).doubleValue();
            }
            Falling += e.getFrom().getY() - e.getTo().getY();
        }
        this.FallDistance.put(Player2.getUniqueId(), Double.valueOf(Falling));
    }

    @Override
    public void enable() {
        super.enable();
        Bukkit.getPluginManager().registerEvents(this, ToppeCheat.getInstance());
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(this);
    }
}