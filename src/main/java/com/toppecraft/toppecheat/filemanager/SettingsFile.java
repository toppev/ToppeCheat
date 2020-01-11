package com.toppecraft.toppecheat.filemanager;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Toppe5
 * @since 2.0
 */
public class SettingsFile {

    private ToppeCheat plugin;
    private File file;
    private YamlConfiguration config;

    public SettingsFile(ToppeCheat plugin) {
        this.plugin = plugin;
        create();
    }

    private void create() {
        file = new File(plugin.getDataFolder(), File.separator + "settings.yml");
        if (!file.exists()) {
            plugin.saveResource("settings.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @return the config
     */
    public YamlConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
