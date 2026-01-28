package com.hytale.ctf.arena;

import com.hytale.ctf.storage.DataManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all CTF arenas in the game.
 * Provides CRUD operations for arena configuration.
 */
public class ArenaManager {
    
    private final Map<String, Arena> arenas;
    private final DataManager dataManager;
    private static final String ARENA_DATA_KEY = "arenas";
    
    /**
     * Create a new ArenaManager
     * 
     * @param dataManager the data manager for persistence
     */
    public ArenaManager(DataManager dataManager) {
        this.dataManager = Objects.requireNonNull(dataManager, "DataManager cannot be null");
        this.arenas = new ConcurrentHashMap<>();
        loadArenas();
    }
    
    /**
     * Create a new arena
     * 
     * @param arena the arena to create
     * @return true if created successfully, false if arena with same name exists
     * @throws IllegalArgumentException if arena is invalid
     */
    public boolean createArena(Arena arena) {
        Objects.requireNonNull(arena, "Arena cannot be null");
        
        if (!arena.isValid()) {
            throw new IllegalArgumentException("Arena configuration is invalid");
        }
        
        if (arenas.containsKey(arena.name())) {
            return false;
        }
        
        arenas.put(arena.name(), arena);
        saveArenas();
        return true;
    }
    
    /**
     * Delete an arena by name
     * 
     * @param name the arena name
     * @return the deleted arena, or null if not found
     */
    public Arena deleteArena(String name) {
        Objects.requireNonNull(name, "Arena name cannot be null");
        
        Arena removed = arenas.remove(name);
        if (removed != null) {
            saveArenas();
        }
        return removed;
    }
    
    /**
     * Get an arena by name
     * 
     * @param name the arena name
     * @return the arena, or null if not found
     */
    public Arena getArena(String name) {
        return arenas.get(name);
    }
    
    /**
     * Check if an arena exists
     * 
     * @param name the arena name
     * @return true if arena exists
     */
    public boolean hasArena(String name) {
        return arenas.containsKey(name);
    }
    
    /**
     * List all arena names
     * 
     * @return unmodifiable set of arena names
     */
    public Set<String> listArenas() {
        return Collections.unmodifiableSet(arenas.keySet());
    }
    
    /**
     * Get all arenas
     * 
     * @return unmodifiable collection of arenas
     */
    public Collection<Arena> getAllArenas() {
        return Collections.unmodifiableCollection(arenas.values());
    }
    
    /**
     * Update an existing arena
     * 
     * @param arena the updated arena
     * @return true if updated, false if arena doesn't exist
     */
    public boolean updateArena(Arena arena) {
        Objects.requireNonNull(arena, "Arena cannot be null");
        
        if (!arena.isValid()) {
            throw new IllegalArgumentException("Arena configuration is invalid");
        }
        
        if (!arenas.containsKey(arena.name())) {
            return false;
        }
        
        arenas.put(arena.name(), arena);
        saveArenas();
        return true;
    }
    
    /**
     * Find arena containing a specific location
     * 
     * @param location the location to check
     * @return the arena containing the location, or null if not found
     */
    public Arena findArenaAt(Location location) {
        return arenas.values().stream()
            .filter(arena -> arena.contains(location))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get the number of arenas
     * 
     * @return arena count
     */
    public int getArenaCount() {
        return arenas.size();
    }
    
    /**
     * Clear all arenas (use with caution)
     */
    public void clearAll() {
        arenas.clear();
        saveArenas();
    }
    
    /**
     * Load arenas from persistent storage
     */
    private void loadArenas() {
        try {
            Map<String, Arena> loaded = dataManager.loadAllArenas();
            if (loaded != null) {
                arenas.putAll(loaded);
            }
        } catch (Exception e) {
            // Log error but continue with empty arena map
            System.err.println("Failed to load arenas: " + e.getMessage());
        }
    }
    
    /**
     * Save arenas to persistent storage
     */
    private void saveArenas() {
        try {
            // Save each arena individually
            for (Arena arena : arenas.values()) {
                dataManager.saveArena(arena);
            }
        } catch (Exception e) {
            System.err.println("Failed to save arenas: " + e.getMessage());
        }
    }
}
