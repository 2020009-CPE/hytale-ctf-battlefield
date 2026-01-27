package com.hytale.api.event;

import com.hytale.api.player.HytalePlayer;

/**
 * Event fired when a player joins the server.
 */
public class PlayerJoinEvent extends HytaleEvent {
    
    private final HytalePlayer player;
    private String joinMessage;
    
    public PlayerJoinEvent(HytalePlayer player, String joinMessage) {
        this.player = player;
        this.joinMessage = joinMessage;
    }
    
    public HytalePlayer getPlayer() {
        return player;
    }
    
    public String getJoinMessage() {
        return joinMessage;
    }
    
    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }
}
