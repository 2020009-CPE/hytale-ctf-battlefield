package com.hytale.ctf.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hytale.ctf.arena.Arena;
import com.hytale.ctf.arena.Snapshot;
import com.hytale.ctf.player.PlayerStats;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central data manager for persistent storage using JSON.
 * Uses virtual threads for async I/O operations.
 * Manages arenas, player stats, and snapshots.
 */
public class DataManager {
    
    private final File dataFolder;
    private final Gson gson;
    private final ArenaStorage arenaStorage;
    private final StatsStorage statsStorage;
    private final ExecutorService ioExecutor;
    
    /**
     * Create a new DataManager
     * 
     * @param dataFolder the root folder for data storage
     */
    public DataManager(File dataFolder) {
        this.dataFolder = Objects.requireNonNull(dataFolder, "Data folder cannot be null");
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
        
        // Create data folders
        dataFolder.mkdirs();
        
        // Initialize storage components
        this.arenaStorage = new ArenaStorage(new File(dataFolder, "arenas"), gson);
        this.statsStorage = new StatsStorage(new File(dataFolder, "stats"), gson);
        
        // Use cached thread pool for async I/O operations
        this.ioExecutor = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("ctf-data-io-" + thread.getId());
            return thread;
        });
    }
    
    // ==================== Arena Methods ====================
    
    /**
     * Save an arena to storage
     * 
     * @param arena the arena to save
     */
    public void saveArena(Arena arena) {
        Objects.requireNonNull(arena, "Arena cannot be null");
        arenaStorage.save(arena);
    }
    
    /**
     * Load an arena by name
     * 
     * @param name the arena name
     * @return the loaded arena, or null if not found
     */
    public Arena loadArena(String name) {
        Objects.requireNonNull(name, "Arena name cannot be null");
        return arenaStorage.load(name);
    }
    
    /**
     * Delete an arena from storage
     * 
     * @param name the arena name
     * @return true if deleted successfully
     */
    public boolean deleteArena(String name) {
        Objects.requireNonNull(name, "Arena name cannot be null");
        return arenaStorage.delete(name);
    }
    
    /**
     * Load all arenas from storage
     * 
     * @return map of arena names to arenas
     */
    public Map<String, Arena> loadAllArenas() {
        return arenaStorage.loadAll();
    }
    
    // ==================== Stats Methods ====================
    
    /**
     * Save player stats to storage
     * 
     * @param stats the player stats to save
     */
    public void saveStats(PlayerStats stats) {
        Objects.requireNonNull(stats, "Stats cannot be null");
        statsStorage.save(stats);
    }
    
    /**
     * Load player stats by name
     * 
     * @param playerName the player name
     * @return the loaded stats, or empty stats if not found
     */
    public PlayerStats loadStats(String playerName) {
        Objects.requireNonNull(playerName, "Player name cannot be null");
        PlayerStats stats = statsStorage.load(playerName);
        return stats != null ? stats : PlayerStats.empty(playerName);
    }
    
    /**
     * Save all player stats from a map
     * 
     * @param statsMap the map of player names to stats
     */
    public void saveAllStats(Map<String, PlayerStats> statsMap) {
        Objects.requireNonNull(statsMap, "Stats map cannot be null");
        statsMap.values().forEach(this::saveStats);
    }
    
    /**
     * Load all player stats from storage
     * 
     * @return map of player names to stats
     */
    public Map<String, PlayerStats> loadAllStats() {
        return statsStorage.loadAll();
    }
    
    // ==================== Snapshot Methods ====================
    
    /**
     * Save a snapshot to storage
     * 
     * @param snapshot the snapshot to save
     */
    public void saveSnapshot(Snapshot snapshot) {
        Objects.requireNonNull(snapshot, "Snapshot cannot be null");
        File snapshotFile = new File(dataFolder, "snapshots/" + snapshot.arenaName() + ".json");
        snapshotFile.getParentFile().mkdirs();
        
        try (FileWriter writer = new FileWriter(snapshotFile)) {
            gson.toJson(snapshot, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save snapshot: " + snapshot.arenaName(), e);
        }
    }
    
    /**
     * Load a snapshot by arena name
     * 
     * @param arenaName the arena name
     * @return the loaded snapshot, or null if not found
     */
    public Snapshot loadSnapshot(String arenaName) {
        Objects.requireNonNull(arenaName, "Arena name cannot be null");
        File snapshotFile = new File(dataFolder, "snapshots/" + arenaName + ".json");
        
        if (!snapshotFile.exists()) {
            return null;
        }
        
        try (FileReader reader = new FileReader(snapshotFile)) {
            return gson.fromJson(reader, Snapshot.class);
        } catch (IOException e) {
            System.err.println("Failed to load snapshot: " + arenaName);
            return null;
        }
    }
    
    /**
     * Delete a snapshot from storage
     * 
     * @param arenaName the arena name
     * @return true if deleted successfully
     */
    public boolean deleteSnapshot(String arenaName) {
        Objects.requireNonNull(arenaName, "Arena name cannot be null");
        File snapshotFile = new File(dataFolder, "snapshots/" + arenaName + ".json");
        return snapshotFile.exists() && snapshotFile.delete();
    }
    
    // ==================== Bulk Operations ====================
    
    /**
     * Save all data (arenas, stats, snapshots)
     * Uses virtual threads for parallel I/O
     */
    public void saveAll() {
        ioExecutor.submit(() -> {
            try {
                arenaStorage.saveAll();
                statsStorage.saveAll();
            } catch (Exception e) {
                System.err.println("Error during saveAll: " + e.getMessage());
            }
        });
    }
    
    /**
     * Load all data (arenas, stats)
     * 
     * @return map containing "arenas" and "stats" keys
     */
    public Map<String, Object> loadAll() {
        Map<String, Object> data = new HashMap<>();
        data.put("arenas", loadAllArenas());
        data.put("stats", loadAllStats());
        return data;
    }
    
    // ==================== Utility Methods ====================
    
    /**
     * Check if an arena exists in storage
     * 
     * @param name the arena name
     * @return true if arena file exists
     */
    public boolean arenaExists(String name) {
        return arenaStorage.exists(name);
    }
    
    /**
     * Check if stats exist for a player
     * 
     * @param playerName the player name
     * @return true if stats file exists
     */
    public boolean statsExist(String playerName) {
        return statsStorage.exists(playerName);
    }
    
    /**
     * Get the data folder
     * 
     * @return the data folder
     */
    public File getDataFolder() {
        return dataFolder;
    }
    
    /**
     * Shutdown the data manager and executor
     */
    public void shutdown() {
        saveAll();
        ioExecutor.shutdown();
    }
}
