package com.toppecraft.toppecheat.events;

import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.script.ScriptManager;
import com.toppecraft.toppecheat.violations.ViolationLevels;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ViolationChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private int newViolation;
    private Player player;
    private int violation;
    private CheckType checkType;

    public ViolationChangeEvent(Player player, CheckType checkType, int newViolation) {
        this.player = player;
        this.newViolation = newViolation;
        this.checkType = checkType;
        this.violation = ViolationLevels.getLevel(player, checkType);
        ScriptManager.getManager().onViolationEvent(getPlayer(), getCheckType(),
                ViolationLevels.getLevel(getPlayer(), getCheckType(), ViolationLevels.getDefaultExpiration()));
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public int getNewViolation() {
        return newViolation;
    }

    public int getViolationNow() {
        return violation;
    }

    public Player getPlayer() {
        return player;
    }

    public CheckType getCheckType() {
        return checkType;
    }
}
