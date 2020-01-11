package com.toppecraft.toppecheat.filemanager;

import com.toppecraft.toppecheat.ToppeCheat;

public class FileManager {

    private MessagesFile messagesFile;
    private SettingsFile settingsFile;

    public FileManager(ToppeCheat plugin) {
        messagesFile = new MessagesFile(plugin);
        settingsFile = new SettingsFile(plugin);
    }

    /**
     * @return the messagesFile
     */
    public MessagesFile getMessagesFile() {
        return messagesFile;
    }

    /**
     * @return the settingsFile
     */
    public SettingsFile getSettingsFile() {
        return settingsFile;
    }

}
