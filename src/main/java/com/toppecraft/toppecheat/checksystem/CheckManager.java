package com.toppecraft.toppecheat.checksystem;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.checksystem.combat.*;
import com.toppecraft.toppecheat.checksystem.combat.killaura.*;
import com.toppecraft.toppecheat.checksystem.misc.*;
import com.toppecraft.toppecheat.checksystem.moving.FlyCheck;
import com.toppecraft.toppecheat.checksystem.moving.ImprovedMovementsCheck;
import com.toppecraft.toppecheat.checksystem.moving.PacketSpamSpeedCheck;
import com.toppecraft.toppecheat.checksystem.moving.SpeedDistanceCheck;
import org.bukkit.Bukkit;

import java.util.HashSet;

/**
 * Check manager class
 *
 * @author Toppe5
 * @since 2.0
 */
public class CheckManager {

    private HashSet<Check> checks = new HashSet<Check>();

    /**
     * Loads enabled checks
     *
     * @param plugin ToppeCheat plugin
     */
    public void loadCheckSystems(ToppeCheat plugin) {
        registerCheckSystem(new FlyCheck(plugin), plugin);
        registerCheckSystem(new BadPacketsCheck(plugin), plugin);
        registerCheckSystem(new TriggerbotCheck(plugin), plugin);
        registerCheckSystem(new RegenDelayCheck(plugin), plugin);
        registerCheckSystem(new CPSCheck(plugin), plugin);
        registerCheckSystem(new CriticalsCheck(plugin), plugin);
        registerCheckSystem(new MultipleClickersCheck(plugin), plugin);
        registerCheckSystem(new ImprovedMovementsCheck(plugin), plugin);
        registerCheckSystem(new MultipleEntitiesCheck(plugin), plugin);
        registerCheckSystem(new SwingCheck(plugin), plugin);
        registerCheckSystem(new SpeedDistanceCheck(plugin), plugin);
        registerCheckSystem(new PacketSpamSpeedCheck(plugin), plugin);
        registerCheckSystem(new InventoryCheatsCheck(plugin), plugin);
        registerCheckSystem(new FastBowCheck(plugin), plugin);
        registerCheckSystem(new AimbotCheck(plugin), plugin);
        registerCheckSystem(new ReachCheck(plugin), plugin);
        registerCheckSystem(new KillauraLookCheck(plugin), plugin);
        registerCheckSystem(new ConstantCPSCheck(plugin), plugin);
        registerCheckSystem(new TabCompleteCheck(plugin), plugin);
        registerCheckSystem(new VapeCheck(plugin), plugin);
        registerCheckSystem(new ForgeModsCheck(plugin), plugin);
        registerCheckSystem(new HeadSnapCheck(plugin), plugin);
        registerCheckSystem(new CriticalsBCheck(), plugin);
        registerCheckSystem(new ReachACheck(plugin), plugin);
        registerCheckSystem(new ReachBCheck(plugin), plugin);
        registerCheckSystem(new GodMode(plugin), plugin);
        registerCheckSystem(new MLPatternCheck(plugin), plugin);
        registerCheckSystem(new JoinCheck(plugin), plugin);
        String checkList = "";
        int size = 0;
        for (Check c : getChecks()) {
            if (c.isEnabled()) {
                size++;
                checkList += c.getType().getName();
            }
        }
        Bukkit.getLogger().info("Enabled checks ( " + size + "): " + checkList);
    }

    /**
     * Loads, enables and saves the check if it's not disabled in the config.
     */
    public void registerCheckSystem(Check check, ToppeCheat plugin) {
        getChecks().add(check);
        check.loadSettings();
        if (check.getSettings().isEnabled()) {
            check.enable();
        }
    }

    /**
     * Gets a check by its name
     *
     * @param name the name of the check.
     *
     * @return the check with the given name or null if not found.
     */
    public Check byName(String name) {
        CheckType type = CheckType.byName(name);
        if (type != null) {
            for (Check c : getChecks()) {
                if (c.getType() == type) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Reloads the check
     *
     * @param check check to reload.
     */
    public void reloadCheckSystem(Check check, ToppeCheat plugin) {
        check.disable();
        check.enable();
    }

    /**
     * Gets all enabled checks
     *
     * @return a set of enabled checks.
     */
    public HashSet<Check> getChecks() {
        return checks;
    }

    public void disable() {
        for (Check check : checks) {
            check.disable();
        }
    }


}
