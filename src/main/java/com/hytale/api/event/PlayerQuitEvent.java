package com.hytale.api.event;

import com.hytale.api.player.HytalePlayer;

/**
 * Event fired when a player quits the server.
 */
public class PlayerQuitEvent extends HytaleEvent {
    
    private final HytalePlayer player;
    private String quitMessage;
    
    public PlayerQuitEvent(HytalePlayer player, String quitMessage) {
        this.player = player;
        this.quitMessage = quitMessage;
    }
    
    public HytalePlayer getPlayer() {
        return player;
    }
    
    public String getQuitMessage() {
        return quitMessage;
    }
    
    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }
}
