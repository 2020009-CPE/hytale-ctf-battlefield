package com.hytale.ctf.storage;

import com.google.gson.Gson;
import com.hytale.ctf.arena.Arena;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Arena-specific storage handler using JSON.
 * Manages reading and writing arena configurations to disk.
 */
public class ArenaStorage {
    
    private final File storageFolder;
    private final Gson gson;
    private final Map<String, Arena> cache;
    
    /**
     * Create a new ArenaStorage
     * 
     * @param storageFolder the folder to store arena files
     * @param gson the Gson instance for JSON serialization
     */
    public ArenaStorage(File storageFolder, Gson gson) {
        this.storageFolder = Objects.requireNonNull(storageFolder, "Storage folder cannot be null");
        this.gson = Objects.requireNonNull(gson, "Gson cannot be null");
        this.cache = new ConcurrentHashMap<>();
        
        // Create storage folder
        storageFolder.mkdirs();
    }
    
    /**
     * Save an arena to storage
     * 
     * @param arena the arena to save
     */
    public void save(Arena arena) {
        Objects.requireNonNull(arena, "Arena cannot be null");
        
        File arenaFile = getArenaFile(arena.name());
        try (FileWriter writer = new FileWriter(arenaFile)) {
            gson.toJson(arena, writer);
            cache.put(arena.name(), arena);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save arena: " + arena.name(), e);
        }
    }
    
    /**
     * Load an arena by name
     * 
     * @param name the arena name
     * @return the loaded arena, or null if not found
     */
    public Arena load(String name) {
        Objects.requireNonNull(name, "Arena name cannot be null");
        
        // Check cache first
        if (cache.containsKey(name)) {
            return cache.get(name);
        }
        
        File arenaFile = getArenaFile(name);
        if (!arenaFile.exists()) {
            return null;
        }
        
        try (FileReader reader = new FileReader(arenaFile)) {
            Arena arena = gson.fromJson(reader, Arena.class);
            cache.put(name, arena);
            return arena;
        } catch (IOException e) {
            System.err.println("Failed to load arena: " + name);
            return null;
        }
    }
    
    /**
     * Delete an arena from storage
     * 
     * @param name the arena name
     * @return true if deleted successfully
     */
    public boolean delete(String name) {
        Objects.requireNonNull(name, "Arena name cannot be null");
        
        cache.remove(name);
        File arenaFile = getArenaFile(name);
        return arenaFile.exists() && arenaFile.delete();
    }
    
    /**
     * Check if an arena exists in storage
     * 
     * @param name the arena name
     * @return true if arena file exists
     */
    public boolean exists(String name) {
        Objects.requireNonNull(name, "Arena name cannot be null");
        return cache.containsKey(name) || getArenaFile(name).exists();
    }
    
    /**
     * Load all arenas from storage
     * 
     * @return map of arena names to arenas
     */
    public Map<String, Arena> loadAll() {
        Map<String, Arena> arenas = new ConcurrentHashMap<>();
        
        File[] files = storageFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return arenas;
        }
        
        for (File file : files) {
            String arenaName = file.getName().replace(".json", "");
            Arena arena = load(arenaName);
            if (arena != null) {
                arenas.put(arenaName, arena);
            }
        }
        
        return arenas;
    }
    
    /**
     * Save all cached arenas
     */
    public void saveAll() {
        cache.values().forEach(this::save);
    }
    
    /**
     * Clear the cache
     */
    public void clearCache() {
        cache.clear();
    }
    
    /**
     * Get the number of arenas in storage
     * 
     * @return arena count
     */
    public int getArenaCount() {
        File[] files = storageFolder.listFiles((dir, name) -> name.endsWith(".json"));
        return files != null ? files.length : 0;
    }
    
    /**
     * Get all arena names in storage
     * 
     * @return set of arena names
     */
    public Set<String> getArenaNames() {
        Set<String> names = new HashSet<>();
        File[] files = storageFolder.listFiles((dir, name) -> name.endsWith(".json"));
        
        if (files != null) {
            for (File file : files) {
                names.add(file.getName().replace(".json", ""));
            }
        }
        
        return names;
    }
    
    /**
     * Get the file for an arena
     * 
     * @param name the arena name
     * @return the arena file
     */
    private File getArenaFile(String name) {
        return new File(storageFolder, name + ".json");
    }
}
