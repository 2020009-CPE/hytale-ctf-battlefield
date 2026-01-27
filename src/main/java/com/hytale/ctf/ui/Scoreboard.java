package com.hytale.ctf.ui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the scoreboard display for CTF games.
 * Shows team scores, player statistics, and game information.
 * 
 * @since 1.0
 */
public class Scoreboard {
    
    private final Map<String, PlayerStats> playerStats;
    private boolean created;
    
    /**
     * Constructs a new Scoreboard.
     */
    public Scoreboard() {
        this.playerStats = new ConcurrentHashMap<>();
        this.created = false;
    }
    
    /**
     * Creates and initializes the scoreboard.
     */
    public void create() {
        if (!created) {
            created = true;
            System.out.println("Scoreboard created");
        }
    }
    
    /**
     * Updates the scoreboard with current game state.
     * 
     * @param scores the map of team names to their scores
     * @param players the list of active player names
     */
    public void update(Map<String, Integer> scores, List<String> players) {
        if (!created) {
            create();
        }
        
        StringBuilder display = new StringBuilder();
        display.append("=== CTF Scoreboard ===\n");
        
        // Display team scores
        scores.forEach((team, score) -> 
            display.append("%s: %d\n".formatted(team, score))
        );
        
        display.append("\n=== Players ===\n");
        
        // Display player stats
        players.forEach(player -> {
            PlayerStats stats = playerStats.getOrDefault(player, new PlayerStats());
            display.append("%s - K: %d D: %d C: %d\n".formatted(
                player, 
                stats.kills(), 
                stats.deaths(), 
                stats.captures()
            ));
        });
        
        System.out.println(display);
    }
    
    /**
     * Removes the scoreboard display.
     */
    public void remove() {
        if (created) {
            created = false;
            playerStats.clear();
            System.out.println("Scoreboard removed");
        }
    }
    
    /**
     * Updates statistics for a specific player.
     * 
     * @param playerName the name of the player
     * @param kills the number of kills
     * @param deaths the number of deaths
     * @param captures the number of flag captures
     */
    public void updatePlayerStats(String playerName, int kills, int deaths, int captures) {
        playerStats.put(playerName, new PlayerStats(kills, deaths, captures));
    }
    
    /**
     * Record class representing player statistics.
     * 
     * @param kills the number of kills
     * @param deaths the number of deaths
     * @param captures the number of flag captures
     */
    public record PlayerStats(int kills, int deaths, int captures) {
        /**
         * Creates a new PlayerStats with zero values.
         */
        public PlayerStats() {
            this(0, 0, 0);
        }
    }
}
