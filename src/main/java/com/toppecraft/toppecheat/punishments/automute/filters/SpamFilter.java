package com.toppecraft.toppecheat.punishments.automute.filters;

import com.toppecraft.toppecheat.ToppeCheat;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;

public class SpamFilter implements AutomuteFilter {

    public static int minMessageLength;
    public static int messageHistory;

    public SpamFilter() {
        minMessageLength = ToppeCheat.getInstance().getConfig().getInt("automute.filters.spam.min-message-length");
        messageHistory = ToppeCheat.getInstance().getConfig().getInt("automute.filters.spam.history-length");
    }

    @Override
    public boolean isAllowed(Player p, String message) {
        if (message.length() >= minMessageLength) {
            MessageHistory history = MessageHistory.getMessageHistory(p);
            for (String s : history.getHistory().keySet()) {
                if (s.contains(message) || (message.contains(s) && s.length() >= 8)) {
                    if (history.getHistory().get(s) + 10000 >= System.currentTimeMillis()) {
                        return false;
                    }
                }
            }
            history.addMessage(message);
        }
        return true;
    }

    @Override
    public long getTime() {
        return ToppeCheat.getInstance().getConfig().getLong("automute.filters.spam.time") * 1000;
    }

    @Override
    public String getReason() {
        return ToppeCheat.getInstance().getConfig().getString("automute.filters.spam.reason");
    }

    @Override
    public String toString() {
        return "spam";
    }

    private static class MessageHistory {

        private static final String messageHistory = "ToppeCheatMessageHistory";

        private HashMap<String, Long> history = new HashMap<String, Long>();

        public static MessageHistory getMessageHistory(Player p) {
            if (p.hasMetadata(messageHistory)) {
                MetadataValue m = ToppeCheat.getInstance().getMeta(p, messageHistory);
                if (m != null && m.value() != null && m.value() instanceof MessageHistory) {
                    return (MessageHistory) m.value();
                }
            }
            MessageHistory mh = new MessageHistory();
            p.setMetadata(messageHistory, new FixedMetadataValue(ToppeCheat.getInstance(), mh));
            return mh;
        }

        public void addMessage(String message) {
            if (message.length() > SpamFilter.minMessageLength) {
                getHistory().put(message, System.currentTimeMillis());
                if (getHistory().size() > SpamFilter.messageHistory) {
                    getHistory().remove(0);
                }
            }
        }

        public HashMap<String, Long> getHistory() {
            return history;
        }

    }

}
