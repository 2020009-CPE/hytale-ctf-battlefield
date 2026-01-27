package com.hytale.ctf.ui;

import com.hytale.api.server.HytaleServer;
import java.util.List;
import java.util.Map;

/**
 * Manages heads-up display (HUD) elements for players during CTF games.
 * Handles scoreboard updates, flag status displays, timers, and carrier indicators.
 * 
 * @since 1.0
 */
public class HUDManager {
    
    private final Scoreboard scoreboard;
    private final HytaleServer server;
    
    /**
     * Constructs a new HUDManager with a new scoreboard instance.
     * 
     * @param server the Hytale server instance
     */
    public HUDManager(HytaleServer server) {
        this.scoreboard = new Scoreboard();
        this.server = server;
    }
    
    /**
     * Updates the scoreboard for all players with current scores.
     * 
     * @param players the list of player names to update
     * @param scores the map of team names to their scores
     */
    public void updateScoreboard(List<String> players, Map<String, Integer> scores) {
        scoreboard.update(scores, players);
    }
    
    /**
     * Shows the flag status to a specific player.
     * 
     * @param playerName the name of the player
     * @param yourFlagStatus the status of the player's team flag
     * @param enemyFlagStatus the status of the enemy team flag
     */
    public void showFlagStatus(String playerName, FlagStatus yourFlagStatus, FlagStatus enemyFlagStatus) {
        String message = formatFlagStatus(yourFlagStatus, enemyFlagStatus);
        server.getPlayer(playerName).ifPresent(player -> 
            player.sendActionBar(message)
        );
    }
    
    /**
     * Updates the game timer display for all players.
     * 
     * @param players the list of player names to update
     * @param timeRemaining the remaining time in seconds
     */
    public void updateTimer(List<String> players, int timeRemaining) {
        String timeDisplay = formatTime(timeRemaining);
        players.forEach(playerName -> 
            server.getPlayer(playerName).ifPresent(player -> 
                player.sendActionBar(timeDisplay)
            )
        );
    }
    
    /**
     * Shows a carrier indicator to a player pointing to the flag carrier's location.
     * 
     * @param playerName the name of the player to show the indicator to
     * @param carrierLocation the location of the flag carrier as [x, y, z]
     */
    public void showCarrierIndicator(String playerName, double[] carrierLocation) {
        if (carrierLocation == null || carrierLocation.length != 3) {
            return;
        }
        
        String locationStr = "%.1f, %.1f, %.1f".formatted(
            carrierLocation[0], 
            carrierLocation[1], 
            carrierLocation[2]
        );
        server.getPlayer(playerName).ifPresent(player -> 
            player.sendActionBar("Carrier at: " + locationStr)
        );
    }
    
    /**
     * Formats flag status information for display.
     * 
     * @param yourFlag the status of the player's team flag
     * @param enemyFlag the status of the enemy team flag
     * @return formatted status string
     */
    private String formatFlagStatus(FlagStatus yourFlag, FlagStatus enemyFlag) {
        return "Your Flag: %s | Enemy Flag: %s".formatted(yourFlag, enemyFlag);
    }
    
    /**
     * Formats time remaining for display.
     * 
     * @param seconds the number of seconds remaining
     * @return formatted time string (MM:SS)
     */
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return "%02d:%02d".formatted(minutes, secs);
    }
    
    /**
     * Represents the status of a flag.
     */
    public enum FlagStatus {
        AT_BASE("At Base"),
        STOLEN("Stolen"),
        DROPPED("Dropped");
        
        private final String display;
        
        FlagStatus(String display) {
            this.display = display;
        }
        
        @Override
        public String toString() {
            return display;
        }
    }
}
