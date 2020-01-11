package com.toppecraft.toppecheat.checksystem;

/**
 * Enum of CheatTypes with names.
 *
 * @author Toppe5
 * @since 2.0
 */
public enum CheckType {

    FLY("fly"),
    CRITICALS("criticals"),
    ANTIKNOCKBACK("antiknockback"),
    PACKET_SPAM_SPEED("packet_spam_speed"),
    DISTANCE_SPEED("distance_speed"),
    REGEN_DELAY("regen"),
    CPS("cps"),
    MULTIPLE_ENTITIES("killaura_multiple_entities"),
    BAD_PACKETS("bad_packets"),
    CLICKING_MODIFICATIONS("clicking_modifications"),
    AIMBOT("aimbot"),
    TRIGGERBOT("triggerbot"),
    FASTBOW("fastbow"),
    REACHING("reaching"),
    NO_SWING_KILLAURA("no_swing_killaura"),
    IMPROVED_MOVEMENTS("improved_movements"),
    KNOCKBACK_MODIFIER("knockback_modifier"),
    KILLAURA_LOOK("killaura_look"),
    INVENTORY_CHEATS("inventory_cheats"),
    CONSTANT_CPS("constant_cps"),
    TAB_COMPLETE("tab_complete"),
    VAPE("vape"),
    FORGE_MODS("forge_mods"),
    HEAD_SNAP("head_snap"),
    CRITICALS_B("criticals_b"),
    REACH_A("reach_a"),
    REACH_B("reach_b"),
    REACH_C("reach_c"),
    REACH_D("reach_d"),
    GOD_MODE("god_mode"),
    KILLAURA_JOIN("killaura_join"),
    MISS_HIT_RATION("miss_hit_ration"),
    MACHINE_LEARNING_PATTERN("machine_learning_pattern");

    private final String name;
    private String customName;
    CheckType(final String name) {
        this.name = name;
    }

    /**
     * Gets a CheatType by its name
     *
     * @param name name of the cheat
     */
    public static CheckType byName(String name) {
        for (CheckType t : CheckType.values()) {
            if (name.toLowerCase().contains(t.getName().toLowerCase()) || t.getName().toLowerCase().contains(name.toLowerCase())) {
                return t;
            }
        }
        for (CheckType t : CheckType.values()) {
            if (name.toLowerCase().contains(t.getCustomName().toLowerCase()) || t.getCustomName().toLowerCase().contains(name.toLowerCase())) {
                return t;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return getNumber();
    }

    public int getNumber() {
        int num = 0;
        for (CheckType t : values()) {
            num++;
            if (t == this) {
                return num;
            }
        }
        return 0;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
