package com.toppecraft.toppecheat.events;

import com.toppecraft.toppecheat.alert.Alert;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an alert is being sent.
 *
 * @author Toppe5
 * @since 2.0
 */
public class AlertEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private Alert alert;

    public AlertEvent(Alert alert) {
        this.alert = alert;
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

    /**
     * Gets the alert that is being sent.
     *
     * @return the alert that is being sent.
     */
    public Alert getAlert() {
        return alert;
    }

}
