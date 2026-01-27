package com.hytale.ctf.ui;

import com.hytale.ctf.util.MessageUtil;
import java.util.List;

/**
 * Manages alert and notification system for CTF game events.
 * Broadcasts important game events to players using titles, subtitles, and action bars.
 * 
 * @since 1.0
 */
public class AlertManager {
    
    private final MessageUtil messageUtil;
    
    /**
     * Constructs a new AlertManager with the specified message utility.
     * 
     * @param messageUtil the message utility for formatting messages
     */
    public AlertManager(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }
    
    /**
     * Broadcasts a flag stolen notification to all players.
     * 
     * @param team the team whose flag was stolen
     * @param playerName the name of the player who stole the flag
     */
    public void broadcastFlagStolen(String team, String playerName) {
        String message = messageUtil.formatFlagStolen(team, playerName);
        broadcast(message, AlertType.FLAG_STOLEN);
    }
    
    /**
     * Broadcasts a flag captured notification to all players.
     * 
     * @param team the team that captured the flag
     * @param playerName the name of the player who captured the flag
     */
    public void broadcastFlagCaptured(String team, String playerName) {
        String message = messageUtil.formatFlagCaptured(team, playerName);
        broadcast(message, AlertType.FLAG_CAPTURED);
    }
    
    /**
     * Broadcasts a flag returned notification to all players.
     * 
     * @param team the team whose flag was returned
     */
    public void broadcastFlagReturned(String team) {
        String message = messageUtil.formatFlagReturned(team);
        broadcast(message, AlertType.FLAG_RETURNED);
    }
    
    /**
     * Broadcasts a carrier killed notification to all players.
     * 
     * @param killerName the name of the player who killed the carrier
     */
    public void broadcastCarrierKilled(String killerName) {
        String message = messageUtil.formatCarrierKilled(killerName);
        broadcast(message, AlertType.CARRIER_KILLED);
    }
    
    /**
     * Shows a title message to a specific player.
     * 
     * @param playerName the name of the player to show the title to
     * @param title the main title text
     * @param subtitle the subtitle text
     */
    public void showTitle(String playerName, String title, String subtitle) {
        // Implementation would interact with game server API
        System.out.println("Title to %s: %s | %s".formatted(playerName, title, subtitle));
    }
    
    /**
     * Shows an action bar message to a specific player.
     * 
     * @param playerName the name of the player to show the message to
     * @param message the message to display in the action bar
     */
    public void showActionBar(String playerName, String message) {
        // Implementation would interact with game server API
        System.out.println("ActionBar to %s: %s".formatted(playerName, message));
    }
    
    /**
     * Broadcasts a message to all players.
     * 
     * @param message the message to broadcast
     * @param type the type of alert
     */
    private void broadcast(String message, AlertType type) {
        // Implementation would broadcast to all online players
        System.out.println("[%s] %s".formatted(type, message));
    }
    
    /**
     * Types of alerts that can be sent.
     */
    private enum AlertType {
        FLAG_STOLEN,
        FLAG_CAPTURED,
        FLAG_RETURNED,
        CARRIER_KILLED
    }
}
