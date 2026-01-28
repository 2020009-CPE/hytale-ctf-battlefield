package com.hytale.api.command;

import com.hytale.api.player.HytalePlayer;

/**
 * Interface for command senders (players, console, etc.)
 */
public interface CommandSender {
    
    /**
     * Send a message to the sender
     */
    void sendMessage(String message);
    
    /**
     * Check if sender has permission
     */
    boolean hasPermission(String permission);
    
    /**
     * Get the sender's name
     */
    String getName();
    
    /**
     * Check if this is a player
     */
    boolean isPlayer();
    
    /**
     * Get as player (throws if not a player)
     */
    HytalePlayer asPlayer();
}
