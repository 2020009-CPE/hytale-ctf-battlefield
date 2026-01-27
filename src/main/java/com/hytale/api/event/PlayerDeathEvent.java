package com.hytale.api.event;

import com.hytale.api.player.HytalePlayer;

/**
 * Event fired when a player dies.
 */
public class PlayerDeathEvent extends HytaleEvent {
    
    private final HytalePlayer player;
    private final HytalePlayer killer; // null if not killed by player
    private String deathMessage;
    
    public PlayerDeathEvent(HytalePlayer player, HytalePlayer killer, String deathMessage) {
        this.player = player;
        this.killer = killer;
        this.deathMessage = deathMessage;
    }
    
    public HytalePlayer getPlayer() {
        return player;
    }
    
    public HytalePlayer getKiller() {
        return killer;
    }
    
    public String getDeathMessage() {
        return deathMessage;
    }
    
    public void setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
    }
}
