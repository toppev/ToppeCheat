package com.toppecraft.toppecheat.checksystem;

import com.toppecraft.toppecheat.ToppeCheat;

/**
 * Settings interface of checks.
 *
 * @author Toppe5
 * @since 2.0
 */
public class CheckSettings {

    private boolean enabled;
    private boolean notifyStaff;
    private int threshold;
    private boolean canAutoban;
    private int autobanThreshold;
    private Check check;


    public CheckSettings(ToppeCheat plugin, Check checkSystem) {
        check = checkSystem;
        String check = checkSystem.getType().getName();
        enabled = plugin.getFileManager().getSettingsFile().getConfig().getBoolean(check + ".enabled");
        notifyStaff = plugin.getFileManager().getSettingsFile().getConfig().getBoolean(check + ".notify");
        threshold = plugin.getFileManager().getSettingsFile().getConfig().getInt(check + ".check-fail-threshold");
        canAutoban = plugin.getFileManager().getSettingsFile().getConfig().getBoolean(check + ".autoban");
        autobanThreshold = plugin.getFileManager().getSettingsFile().getConfig().getInt(check + ".autoban-threshold");
        String customName = plugin.getFileManager().getSettingsFile().getConfig().getString(check + ".name");
        if (customName != null) {
            checkSystem.getType().setCustomName(customName);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (this.enabled) {
            check.enable();
        } else {
            check.disable();
        }
    }

    /**
     * Gets if the check system is enabled.
     *
     * @return true if the check system is enabled, otherwise false
     */
    public boolean notifyStaff() {
        return notifyStaff;
    }

    /**
     * Gets if the check has the autoban feature on.
     *
     * @return true if the check has the autoban on, otherwise false
     */
    public boolean canAutoban() {
        return canAutoban;
    }

    /**
     * Gets the common threshold of the check system.
     *
     * @return the common threshold of the check.
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Gets the autoban threshold of the check system.
     *
     * @return the autoban threshold of the check.
     */
    public int getAutobanThreshold() {
        return autobanThreshold;
    }

    /**
     * @param autobanThreshold the autobanThreshold to set
     */
    public void setAutobanThreshold(int autobanThreshold) {
        this.autobanThreshold = autobanThreshold;
    }

    /**
     * @param notifyStaff the notifyStaff to set
     */
    public void setNotifyStaff(boolean notifyStaff) {
        this.notifyStaff = notifyStaff;
    }

    /**
     * @param canAutoban the canAutoban to set
     */
    public void setCanAutoban(boolean canAutoban) {
        this.canAutoban = canAutoban;
    }

    public void save() {
        ToppeCheat plugin = ToppeCheat.getInstance();
        plugin.getFileManager().getSettingsFile().getConfig().set(check.getType().getName() + ".enabled", isEnabled());
        plugin.getFileManager().getSettingsFile().getConfig().set(check.getType().getName() + ".notify", notifyStaff());
        plugin.getFileManager().getSettingsFile().getConfig().set(check.getType().getName() + ".check-fail-threshold", getThreshold());
        plugin.getFileManager().getSettingsFile().getConfig().set(check.getType().getName() + ".autoban", canAutoban());
        plugin.getFileManager().getSettingsFile().getConfig().set(check.getType().getName() + ".autoban-threshold", getAutobanThreshold());
        plugin.getFileManager().getSettingsFile().getConfig().set(check.getType().getName() + ".name", check.getType().getCustomName());
        plugin.getFileManager().getSettingsFile().save();
    }
}
