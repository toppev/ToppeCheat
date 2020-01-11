package com.toppecraft.toppecheat.checksystem;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;

public class CheckBypass {

    private static String bypass = "ToppeCheatCheckBypass";

    public boolean hasBypass(Player p, CheckType type) {
        return getBypass(p, type) > 0;
    }

    public long getBypass(Player p, CheckType type) {
        long l = BypassHandler.getHandler(p).getBypass(type) - System.currentTimeMillis();
		if (l < 0) {
			return 0;
		}
        return l;
    }

    public void setBypass(Player p, CheckType type, long timeMillis) {
        BypassHandler bh = BypassHandler.getHandler(p);
        bh.getBP().put(type, System.currentTimeMillis() + timeMillis);
    }


    private static class BypassHandler {

        private HashMap<CheckType, Long> bp = new HashMap<CheckType, Long>();

        public static BypassHandler getHandler(Player p) {
            if (p.hasMetadata(bypass)) {
                MetadataValue m = ToppeCheat.getInstance().getMeta(p, bypass);
                if (m != null && m.value() != null && m.value() instanceof BypassHandler) {
                    return (BypassHandler) m.value();
                }
            }
            BypassHandler bh = new BypassHandler();
            p.setMetadata(bypass, new FixedMetadataValue(ToppeCheat.getInstance(), bh));
            return bh;
        }

        public HashMap<CheckType, Long> getBP() {
            return bp;
        }

        public long getBypass(CheckType type) {
            if (bp.containsKey(type)) {
                return bp.get(type);
            }
            return 0;
        }
    }
}