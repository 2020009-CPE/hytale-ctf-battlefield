package com.hytale.ctf.player;

import com.hytale.ctf.game.CTFTeam;

/**
 * Wrapper class for CTF player data.
 * Tracks player team, stats, and flag carrying status.
 */
public class CTFPlayer {
    
    private final String playerName;
    private CTFTeam team;
    private PlayerStats stats;
    private boolean hasFlag;
    
    /**
     * Creates a new CTF player
     * 
     * @param playerName the player's name
     */
    public CTFPlayer(String playerName) {
        this.playerName = playerName;
        this.team = null;
        this.stats = PlayerStats.empty(playerName);
        this.hasFlag = false;
    }
    
    /**
     * Creates a CTF player with existing stats
     * 
     * @param playerName the player's name
     * @param stats the player's existing stats
     */
    public CTFPlayer(String playerName, PlayerStats stats) {
        this.playerName = playerName;
        this.team = null;
        this.stats = stats;
        this.hasFlag = false;
    }
    
    /**
     * Joins a team
     * 
     * @param team the team to join
     */
    public void joinTeam(CTFTeam team) {
        this.team = team;
    }
    
    /**
     * Leaves the current team
     */
    public void leaveTeam() {
        this.team = null;
        this.hasFlag = false;
    }
    
    /**
     * Updates the player's statistics
     * 
     * @param stats the new stats
     */
    public void updateStats(PlayerStats stats) {
        if (!playerName.equals(stats.playerName())) {
            throw new IllegalArgumentException(
                "Stats player name does not match: expected " + playerName + 
                ", got " + stats.playerName()
            );
        }
        this.stats = stats;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public CTFTeam getTeam() {
        return team;
    }
    
    public PlayerStats getStats() {
        return stats;
    }
    
    public boolean hasFlag() {
        return hasFlag;
    }
    
    public void setHasFlag(boolean hasFlag) {
        this.hasFlag = hasFlag;
    }
    
    /**
     * Checks if player is on a team
     * 
     * @return true if player has joined a team
     */
    public boolean isOnTeam() {
        return team != null;
    }
    
    /**
     * Checks if player is on the specified team
     * 
     * @param team the team to check
     * @return true if player is on the team
     */
    public boolean isOnTeam(CTFTeam team) {
        return this.team == team;
    }
}
