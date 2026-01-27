package com.hytale.ctf.ui;

import com.hytale.api.server.HytaleServer;
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
    private final HytaleServer server;
    
    /**
     * Constructs a new AlertManager with the specified message utility.
     * 
     * @param messageUtil the message utility for formatting messages
     * @param server the Hytale server instance
     */
    public AlertManager(MessageUtil messageUtil, HytaleServer server) {
        this.messageUtil = messageUtil;
        this.server = server;
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
        server.getPlayer(playerName).ifPresent(player -> 
            player.sendTitle(title, subtitle, 10, 70, 20)
        );
    }
    
    /**
     * Shows an action bar message to a specific player.
     * 
     * @param playerName the name of the player to show the message to
     * @param message the message to display in the action bar
     */
    public void showActionBar(String playerName, String message) {
        server.getPlayer(playerName).ifPresent(player -> 
            player.sendActionBar(message)
        );
    }
    
    /**
     * Broadcasts a message to all players.
     * 
     * @param message the message to broadcast
     * @param type the type of alert
     */
    private void broadcast(String message, AlertType type) {
        server.broadcast(message);
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
