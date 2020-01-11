package com.toppecraft.toppecheat.violations;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.checksystem.CheckType;
import com.toppecraft.toppecheat.events.ViolationChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * @author Toppe5
 * @since 2.0
 */
public class ViolationLevels {

    private static final String violationStoreMeta = "ToppeCheatViolationMetaTag";
    private static int defaultExpiration;

    public static int getLevel(Player p, CheckType type, int seconds) {
        if (p.hasMetadata(violationStoreMeta)) {
            MetadataValue m = ToppeCheat.getInstance().getMeta(p, violationStoreMeta);
            if (m != null && m.value() != null && m.value() instanceof ViolationLevelStore) {
                ViolationLevelStore vlStore = (ViolationLevelStore) m.value();
                if (vlStore.getStore().containsKey(type)) {
                    Violation v = vlStore.getStore().get(type);
                    if (v.getLastUpdate() + seconds * 1000 > System.currentTimeMillis()) {
                        return v.getVL();
                    }
                }
            }
        }
        return 0;
    }

    public static int getLevel(Player p, CheckType type) {
        return getLevel(p, type, getDefaultExpiration());
    }

    public static ViolationLevelStore getStore(Player p) {
        if (p.hasMetadata(violationStoreMeta)) {
            MetadataValue m = ToppeCheat.getInstance().getMeta(p, violationStoreMeta);
            if (m != null && m.value() != null && m.value() instanceof ViolationLevelStore) {
                return (ViolationLevelStore) m.value();
            }
        }
        return new ViolationLevelStore();
    }

    public static int increase(Player p, CheckType type, int vlToAdd, int seconds) {
        int vl = getLevel(p, type, seconds);
        vl += vlToAdd;
        setVL(p, type, vl);
        return vl;
    }

    public static void setVL(Player p, CheckType type, int vl) {
        ViolationChangeEvent event = new ViolationChangeEvent(p, type, vl);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            ViolationLevelStore store = getStore(p);
            store.getStore().put(type, new Violation(vl));
            p.setMetadata(violationStoreMeta, new FixedMetadataValue(ToppeCheat.getInstance(), store));
        }
    }

    public static int getDefaultExpiration() {
        return defaultExpiration;
    }

    public static void setDefaultExpiration(int defaultExpiration) {
        ViolationLevels.defaultExpiration = defaultExpiration;
    }

}
