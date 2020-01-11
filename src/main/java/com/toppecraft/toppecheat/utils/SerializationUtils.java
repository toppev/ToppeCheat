package com.toppecraft.toppecheat.utils;

import org.bukkit.Bukkit;

import java.util.Map;

public class SerializationUtils {

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key, Map<?, ?> serialized, Class<T> type, T def) {
        if (!serialized.containsKey(key)) {
            return def;
        }
        Object o = serialized.get(key);
        if (!type.isInstance(o)) {
            return def;
        }
        T casted = null;
        try {
            casted = (T) o;
        } catch (ClassCastException e) {
            Bukkit.getLogger().info("Failed to cast object in field \"" + key + "\" to " + type.getSimpleName() + ".");
        } catch (Throwable t) {
            Bukkit.getLogger().info("Failed to deserialize object");
        }
        if (casted == null) {
            return def;
        }
        return casted;
    }
}