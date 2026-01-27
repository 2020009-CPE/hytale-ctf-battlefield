package com.hytale.ctf.storage;

/**
 * Interface for managing persistent data storage.
 * Provides methods for saving and loading game data.
 */
public interface DataManager {
    
    /**
     * Save data to persistent storage
     * 
     * @param key the key to store data under
     * @param data the data to save
     */
    void save(String key, Object data);
    
    /**
     * Load data from persistent storage
     * 
     * @param key the key to retrieve data from
     * @param type the class type to deserialize to
     * @return the loaded data, or null if not found
     */
    <T> T load(String key, Class<T> type);
    
    /**
     * Delete data from persistent storage
     * 
     * @param key the key to delete
     */
    void delete(String key);
    
    /**
     * Check if data exists for a key
     * 
     * @param key the key to check
     * @return true if data exists
     */
    boolean exists(String key);
}
