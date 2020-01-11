package com.toppecraft.toppecheat.filemanager;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesFile {

    private ToppeCheat plugin;
    private File file;
    private YamlConfiguration config;

    public MessagesFile(ToppeCheat plugin) {
        this.plugin = plugin;
        create();
    }

    private void create() {
        file = new File(plugin.getDataFolder(), File.separator + "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
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

    /**
     * Gets a message from the file and includes the prefix.
     *
     * @param msg message path to get
     *
     * @return a ChatColor translated message with prefix
     */
    public String getMessage(String msg) {
        String s = getConfig().getString("prefix");
        return ChatColor.translateAlternateColorCodes('&', s + getConfig().getString(msg));
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
    }

    /**
     * Gets a message without prefix from the file.
     *
     * @param msg message path to get
     *
     * @return a ChatColor translated message without prefix
     */
    public String getMessageWithoutPrefix(String msg) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(msg));
    }

}
