package com.toppecraft.toppecheat.playerdata;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Toppe5
 * @since 2.0
 */
public class PlayerDataFile {

    private UUID uuid;
    private File file;
    private YamlConfiguration config;

    /**
     * Won't create files automatically.
     *
     * @param uuid UUID of the player
     */
    public PlayerDataFile(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerDataFile(File file) {
        this.uuid = UUID.fromString(file.getName().replace(".yml", ""));
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Gets a PlayerDataFile of the UUID. Doesn't create any new files
     *
     * @param uuid the UUID of the player
     *
     * @return PlayerDataFile of the UUID or null if not found
     */
    public static PlayerDataFile byUUID(UUID uuid) {
        ToppeCheat plugin = ToppeCheat.getInstance();
        File f = new File(plugin.getDataFolder() + "/playerdata");
        if (f.exists()) {
            File file = new File(plugin.getDataFolder() + "/playerdata", File.separator + uuid.toString() + ".yml");
            if (file.exists()) {
                return new PlayerDataFile(file);
            }
        }
        return null;
    }

    /**
     * Create the files and load config.
     */
    public void createFiles() {
        ToppeCheat plugin = ToppeCheat.getInstance();
        File f = new File(plugin.getDataFolder() + "/playerdata");
        if (!f.exists()) {
            f.mkdirs();
        }
        this.file = new File(plugin.getDataFolder() + "/playerdata", File.separator + uuid.toString() + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(getFile());
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the config
     */
    public YamlConfiguration getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }

    /**
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Saves the config
     */
    public void save() {
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
