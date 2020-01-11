package com.toppecraft.toppecheat.checksystem;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.Bukkit;

public class Check {

    private CheckSettings settings;
    private CheckType type;
    private boolean tempDisabled;

    public Check(CheckType type) {
        this.type = type;
    }

    /**
     * Load the settings of the Check
     */
    public void loadSettings() {
        settings = new CheckSettings(ToppeCheat.getInstance(), this);
    }

    /**
     * Enables the check system.
     */
    public void enable() {
        if (getType() == null) {
            Bukkit.getLogger().warning("Failed to load the check " + getType().getName() + " (" + getType().getCustomName() + ")...");
            return;
        }
        tempDisabled = false;
    }

    /**
     * Disables the check system.
     */
    public void disable() {
        if (getType() == null) {
            Bukkit.getLogger().warning("Failed to unload the check " + getType().getName() + " (" + getType().getCustomName() + "). Force disabling...");
        }
        tempDisabled = true;
        if (getSettings() != null) {
            getSettings().save();
        }
    }

    /**
     * Gets the settings of the check.
     *
     * @return CheckSettings of the check.
     */
    public CheckSettings getSettings() {
        return settings;
    }

    /**
     * Gets if the check is enabled.
     *
     * @return true if the check is enabled, false if it's not.
     */
    public boolean isEnabled() {
        if (settings == null || tempDisabled) {
            return false;
        }
        return settings.isEnabled();
    }

    /**
     * Gets the CheckType the check is for.
     *
     * @return the CheckType the check is for
     */
    public CheckType getType() {
        return type;
    }
}
