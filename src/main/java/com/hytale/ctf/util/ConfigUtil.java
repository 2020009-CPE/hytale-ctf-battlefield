package com.hytale.ctf.util;

import java.util.Map;

/**
 * Utility class for configuration management.
 * Provides type-safe access to configuration values.
 */
public final class ConfigUtil {
    
    private ConfigUtil() {
        // Utility class
    }
    
    /**
     * Get a string value from config map with default
     */
    public static String getString(Map<String, Object> config, String key, String defaultValue) {
        Object value = config.get(key);
        return value instanceof String s ? s : defaultValue;
    }
    
    /**
     * Get an integer value from config map with default
     */
    public static int getInt(Map<String, Object> config, String key, int defaultValue) {
        Object value = config.get(key);
        if (value instanceof Number n) {
            return n.intValue();
        }
        return defaultValue;
    }
    
    /**
     * Get a boolean value from config map with default
     */
    public static boolean getBoolean(Map<String, Object> config, String key, boolean defaultValue) {
        Object value = config.get(key);
        return value instanceof Boolean b ? b : defaultValue;
    }
    
    /**
     * Get a double value from config map with default
     */
    public static double getDouble(Map<String, Object> config, String key, double defaultValue) {
        Object value = config.get(key);
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        return defaultValue;
    }
    
    /**
     * Get a nested config section
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getSection(Map<String, Object> config, String key) {
        Object value = config.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }
}
