package com.hytale.ctf.storage;

import com.google.gson.Gson;
import com.hytale.ctf.player.PlayerStats;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stats-specific storage handler using JSON.
 * Manages reading and writing player statistics to disk.
 */
public class StatsStorage {
    
    private final File storageFolder;
    private final Gson gson;
    private final Map<String, PlayerStats> cache;
    
    /**
     * Create a new StatsStorage
     * 
     * @param storageFolder the folder to store stats files
     * @param gson the Gson instance for JSON serialization
     */
    public StatsStorage(File storageFolder, Gson gson) {
        this.storageFolder = Objects.requireNonNull(storageFolder, "Storage folder cannot be null");
        this.gson = Objects.requireNonNull(gson, "Gson cannot be null");
        this.cache = new ConcurrentHashMap<>();
        
        // Create storage folder
        storageFolder.mkdirs();
    }
    
    /**
     * Save player stats to storage
     * 
     * @param stats the stats to save
     */
    public void save(PlayerStats stats) {
        Objects.requireNonNull(stats, "Stats cannot be null");
        
        File statsFile = getStatsFile(stats.playerName());
        try (FileWriter writer = new FileWriter(statsFile)) {
            gson.toJson(stats, writer);
            cache.put(stats.playerName(), stats);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save stats for: " + stats.playerName(), e);
        }
    }
    
    /**
     * Load player stats by name
     * 
     * @param playerName the player name
     * @return the loaded stats, or null if not found
     */
    public PlayerStats load(String playerName) {
        Objects.requireNonNull(playerName, "Player name cannot be null");
        
        // Check cache first
        if (cache.containsKey(playerName)) {
            return cache.get(playerName);
        }
        
        File statsFile = getStatsFile(playerName);
        if (!statsFile.exists()) {
            return null;
        }
        
        try (FileReader reader = new FileReader(statsFile)) {
            PlayerStats stats = gson.fromJson(reader, PlayerStats.class);
            cache.put(playerName, stats);
            return stats;
        } catch (IOException e) {
            System.err.println("Failed to load stats for: " + playerName);
            return null;
        }
    }
    
    /**
     * Delete player stats from storage
     * 
     * @param playerName the player name
     * @return true if deleted successfully
     */
    public boolean delete(String playerName) {
        Objects.requireNonNull(playerName, "Player name cannot be null");
        
        cache.remove(playerName);
        File statsFile = getStatsFile(playerName);
        return statsFile.exists() && statsFile.delete();
    }
    
    /**
     * Check if stats exist for a player
     * 
     * @param playerName the player name
     * @return true if stats file exists
     */
    public boolean exists(String playerName) {
        Objects.requireNonNull(playerName, "Player name cannot be null");
        return cache.containsKey(playerName) || getStatsFile(playerName).exists();
    }
    
    /**
     * Load all player stats from storage
     * 
     * @return map of player names to stats
     */
    public Map<String, PlayerStats> loadAll() {
        Map<String, PlayerStats> allStats = new ConcurrentHashMap<>();
        
        File[] files = storageFolder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return allStats;
        }
        
        for (File file : files) {
            String playerName = file.getName().replace(".json", "");
            PlayerStats stats = load(playerName);
            if (stats != null) {
                allStats.put(playerName, stats);
            }
        }
        
        return allStats;
    }
    
    /**
     * Save all cached stats
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
     * Get the number of player stats in storage
     * 
     * @return stats count
     */
    public int getStatsCount() {
        File[] files = storageFolder.listFiles((dir, name) -> name.endsWith(".json"));
        return files != null ? files.length : 0;
    }
    
    /**
     * Get all player names with stats
     * 
     * @return set of player names
     */
    public Set<String> getPlayerNames() {
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
     * Get top players by a specific stat
     * 
     * @param limit the maximum number of players to return
     * @param comparator the comparator to sort by
     * @return list of top player stats
     */
    public List<PlayerStats> getTopPlayers(int limit, Comparator<PlayerStats> comparator) {
        return loadAll().values().stream()
            .sorted(comparator.reversed())
            .limit(limit)
            .toList();
    }
    
    /**
     * Get the file for player stats
     * 
     * @param playerName the player name
     * @return the stats file
     */
    private File getStatsFile(String playerName) {
        return new File(storageFolder, playerName + ".json");
    }
}
