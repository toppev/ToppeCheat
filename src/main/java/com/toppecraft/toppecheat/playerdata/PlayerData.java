package com.toppecraft.toppecheat.playerdata;

import com.toppecraft.toppecheat.ToppeCheat;
import com.toppecraft.toppecheat.utils.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Toppe5
 * @since 2.0
 */
@SerializableAs("PlayerData")
public class PlayerData implements ConfigurationSerializable {

    public static final String playerDataMeta = "ToppeCheatPlayerDataMeta";

    private List<String> punishments;
    private List<String> notes;
    private List<String> violations;
    private List<Double> clicking = new ArrayList<Double>();
    private int allRecordedClicks;
    private HashSet<String> addresses;

    public PlayerData() {
        punishments = new ArrayList<String>();
        notes = new ArrayList<String>();
        violations = new ArrayList<String>();
        addresses = new HashSet<String>();
    }

    @SuppressWarnings("unchecked")
    public PlayerData(Map<String, Object> serialized) {
        try {
            punishments = SerializationUtils.getValue("punishments", serialized, ArrayList.class, new ArrayList<String>());
            notes = SerializationUtils.getValue("notes", serialized, ArrayList.class, new ArrayList<String>());
            violations = SerializationUtils.getValue("violations", serialized, ArrayList.class, new ArrayList<String>());
            ArrayList<String> ips = SerializationUtils.getValue("addresses", serialized, ArrayList.class, new ArrayList<String>());
            if (serialized.containsKey("clicking")) {
                clicking = SerializationUtils.getValue("clicking", serialized, ArrayList.class, new ArrayList<Double>());
                if (clicking == null) {
                    clicking = new ArrayList<Double>();
                } else {
                    for (int i = 0; i < clicking.size(); i++) {
                        double x = clicking.get(i);
                        clicking.set(i, x * 10);
                    }
                }
                addresses = new HashSet<>(ips);
                if (serialized.containsKey("recorded-clicks")) {
                    Object obj = serialized.get("recorded-clicks");
                    if (obj instanceof Integer) {
                        allRecordedClicks = (int) obj;
                    }
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to serialize playerdata!");
            e.printStackTrace();
        }
    }

    /**
     * Deserializes a PlayerData from configuration. It is not recommended to use this manually, as it is intended for
     * the Bukkit configuration serialization system.
     *
     * @param serialized map to deserialize.
     */
    public static PlayerData deserialize(Map<String, Object> serialized) {
        return new PlayerData(serialized);
    }

    /**
     * Deserializes a PlayerData from configuration. It is not recommended to use this manually, as it is intended for
     * the Bukkit configuration serialization system.
     *
     * @param serialized map to deserialize.
     */
    public static PlayerData valueOf(Map<String, Object> serialized) {
        return new PlayerData(serialized);
    }

    public static PlayerData getOnlinePlayerData(Player p) {
        if (p.hasMetadata(playerDataMeta)) {
            MetadataValue m = ToppeCheat.getInstance().getMeta(p, playerDataMeta);
            if (m != null && m.value() != null && m.value() instanceof PlayerData) {
                return (PlayerData) m.value();
            }
        }
        PlayerData data = getPlayerData(p.getUniqueId(), false);
        p.setMetadata(playerDataMeta, new FixedMetadataValue(ToppeCheat.getInstance(), data));
        return data;
    }

    public static void getPlayerDataAsync(final UUID uuid, final PlayerDataCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(ToppeCheat.getInstance(), new Runnable() {

            @Override
            public void run() {
                final PlayerData result = getPlayerData(uuid);
                Bukkit.getScheduler().runTask(ToppeCheat.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        callback.onSuccess(result);
                    }
                });
            }
        });
    }

    /**
     * Gets the player's PlayerData for the players who don't have saved data it will return a new PlayerData, never
     * null
     *
     * @param uuid the player's UUID
     *
     * @return the player's PlayerData, never null
     */
    public static PlayerData getPlayerData(UUID uuid) {
        return getPlayerData(uuid, true);
    }

    private static PlayerData getPlayerData(UUID uuid, boolean tryOnlinePlayerData) {
        if (tryOnlinePlayerData && Bukkit.getPlayer(uuid) != null) {
            return getOnlinePlayerData(Bukkit.getPlayer(uuid));
        }
        PlayerData data = new PlayerData();
        if (!ToppeCheat.getInstance().isMySQL()) {
            PlayerData d = getPlayerDataFromFile(uuid);
            if (d != null) {
                data = d;
            }
        } else {
            //TODO
        }
        return data;
    }

    private static PlayerData getPlayerDataFromFile(UUID uuid) {
        PlayerDataFile pdf = PlayerDataFile.byUUID(uuid);
        if (pdf != null && pdf.getConfig() != null) {
            Object o = pdf.getConfig().get("data");
            if (o != null && o instanceof PlayerData) {
                return (PlayerData) o;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("punishments", getPunishments());
        serialized.put("notes", getNotes());
        serialized.put("violations", getViolations());
        serialized.put("addresses", getAddresses().toArray());
        List<Double> c = new ArrayList<Double>();
        for (double cl : clicking) {
            c.add(cl / 10);
        }
        serialized.put("clicking", c);
        serialized.put("recorded-clicks", allRecordedClicks);
        return serialized;
    }

    /**
     * @return the punishments
     */
    public List<String> getPunishments() {
        if (punishments == null) {
            punishments = new ArrayList<String>();
        }
        return punishments;
    }

    /**
     * @param punishments the punishments to set
     */
    public void setPunishments(List<String> punishments) {
        this.punishments = punishments;
    }

    /**
     * @return the notes
     */
    public List<String> getNotes() {
        return notes;
    }

    /**
     * @param notes the notes to set
     */
    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public List<Double> getClicking() {
        return clicking;
    }

    public int getAllRecordedClicks() {
        return allRecordedClicks;
    }

    public void setAllRecordedClicks(int allRecordedClicks) {
        this.allRecordedClicks = allRecordedClicks;
    }

    public void addCPS(double cps) {
		if (clicking.size() >= 300) {
			int r = ToppeCheat.random.nextInt(300);
			double oldCps = clicking.get(r);
			clicking.set(r, (oldCps * 2 + cps) / 3);
		} else {
			clicking.add(cps);
		}
        allRecordedClicks++;
    }

    /**
     * @return the alerts
     */
    public List<String> getViolations() {
        return violations;
    }

    /**
     * @param alerts the alerts to set
     */
    public void setViolations(List<String> alerts) {
        this.violations = alerts;
    }

    /**
     * @return the addresses
     */
    public HashSet<String> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses the addresses to set
     */
    public void setAddresses(HashSet<String> addresses) {
        this.addresses = addresses;
    }

    public void getAllAccountsAsync(final AltsCallback callback, final boolean extraCheck) {
        Bukkit.getScheduler().runTaskAsynchronously(ToppeCheat.getInstance(), new Runnable() {

            @Override
            public void run() {
                final HashSet<UUID> acc = new HashSet<UUID>();
                HashSet<String> ips = new HashSet<String>();
                HashSet<String> ips2 = new HashSet<String>();
                HashSet<UUID> uuids = new HashSet<UUID>();
                ips.addAll(getAddresses());
                if (ToppeCheat.getInstance().isMySQL()) {
                    for (UUID uuid : ToppeCheat.getInstance().getMySQL().getUUIDs()) {
                        uuids.add(uuid);
                    }
                } else {
                    File file = new File(ToppeCheat.getInstance().getDataFolder() + "/playerdata");
                    for (File f : file.listFiles()) {
                        try {
                            uuids.add(UUID.fromString(f.getName().replace(".yml", "")));
                        } catch (IllegalArgumentException e) {
                            continue;
                        }
                    }
                }
                for (UUID uuid : uuids) {
                    PlayerData data = getPlayerData(uuid);
                    if (data != null) {
                        for (String s : ips) {
                            if (data.getAddresses().contains(s)) {
                                acc.add(uuid);
                                if (extraCheck) {
                                    ips2.addAll(data.getAddresses());
                                }
                            }
                        }
                    }
                }
                if (extraCheck) {
                    ips.addAll(ips2);
                    for (UUID uuid : uuids) {
                        PlayerData data = getPlayerData(uuid);
                        if (data != null) {
                            for (String s : ips) {
                                if (data.getAddresses().contains(s)) {
                                    acc.add(uuid);
                                }
                            }
                        }
                    }
                }
                Bukkit.getScheduler().runTask(ToppeCheat.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        callback.onSuccess(acc);
                    }
                });
            }
        });
    }

    public void save(final UUID uuid) {
        if (ToppeCheat.getInstance().isMySQL()) {
            //TODO
        } else {
            PlayerDataFile pdf = PlayerDataFile.byUUID(uuid);
            if (pdf == null) {
                pdf = new PlayerDataFile(uuid);
                pdf.createFiles();
            }
            pdf.getConfig().set("data", this);
            try {
                pdf.getConfig().save(pdf.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public abstract class AltsCallback {

        public abstract void onSuccess(HashSet<UUID> alts);
    }

}
