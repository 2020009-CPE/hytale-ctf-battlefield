package com.hytale.ctf.player;

import com.hytale.ctf.game.CTFTeam;
import com.hytale.ctf.storage.DataManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages all CTF players.
 * Handles player registration, retrieval, and stats persistence.
 */
public class PlayerManager {
    
    private final Map<String, CTFPlayer> players;
    private final DataManager dataManager;
    
    /**
     * Creates a new player manager
     * 
     * @param dataManager the data manager for stats persistence
     */
    public PlayerManager(DataManager dataManager) {
        this.players = new ConcurrentHashMap<>();
        this.dataManager = dataManager;
    }
    
    /**
     * Gets a player by name, creating if necessary
     * 
     * @param playerName the player's name
     * @return the CTF player instance
     */
    public CTFPlayer getPlayer(String playerName) {
        return players.computeIfAbsent(playerName, name -> {
            PlayerStats stats = loadStats(name);
            return new CTFPlayer(name, stats);
        });
    }
    
    /**
     * Adds a player to the manager
     * 
     * @param playerName the player to add
     * @return the created CTF player
     */
    public CTFPlayer addPlayer(String playerName) {
        return getPlayer(playerName);
    }
    
    /**
     * Removes a player from the manager
     * 
     * @param playerName the player to remove
     * @return the removed player, or null if not found
     */
    public CTFPlayer removePlayer(String playerName) {
        CTFPlayer player = players.remove(playerName);
        if (player != null) {
            saveStats(player);
        }
        return player;
    }
    
    /**
     * Gets all players on a specific team
     * 
     * @param team the team to get players for
     * @return list of players on the team
     */
    public List<CTFPlayer> getPlayersOnTeam(CTFTeam team) {
        return players.values().stream()
            .filter(player -> player.isOnTeam(team))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all active players
     * 
     * @return collection of all players
     */
    public Collection<CTFPlayer> getAllPlayers() {
        return Collections.unmodifiableCollection(players.values());
    }
    
    /**
     * Saves player stats to persistent storage
     * 
     * @param player the player whose stats to save
     */
    public void saveStats(CTFPlayer player) {
        dataManager.saveStats(player.getStats());
    }
    
    /**
     * Loads player stats from persistent storage
     * 
     * @param playerName the player to load stats for
     * @return the loaded stats, or empty stats if not found
     */
    private PlayerStats loadStats(String playerName) {
        PlayerStats stats = dataManager.loadStats(playerName);
        return stats != null ? stats : PlayerStats.empty(playerName);
    }
    
    /**
     * Saves all player stats
     */
    public void saveAllStats() {
        players.values().forEach(this::saveStats);
    }
    
    /**
     * Removes all players from the manager
     */
    public void clearPlayers() {
        players.clear();
    }
}
