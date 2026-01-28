package com.hytale.ctf.flag;

import com.hytale.ctf.arena.Location;
import com.hytale.ctf.game.CTFTeam;

/**
 * Represents a CTF flag entity with state tracking.
 * Manages flag location, carrier, and status transitions.
 */
public class Flag {
    
    private final CTFTeam team;
    private final Location baseLocation;
    private FlagStatus status;
    private String carrierName;
    
    /**
     * Flag status enumeration
     */
    public enum FlagStatus {
        AT_BASE,
        STOLEN,
        DROPPED
    }
    
    /**
     * Creates a new flag for a team at its base location
     * 
     * @param team the team this flag belongs to
     * @param baseLocation the home location of the flag
     */
    public Flag(CTFTeam team, Location baseLocation) {
        this.team = team;
        this.baseLocation = baseLocation;
        this.status = FlagStatus.AT_BASE;
        this.carrierName = null;
    }
    
    /**
     * Picks up the flag
     * 
     * @param playerName the name of the player picking up the flag
     * @throws IllegalStateException if flag is already being carried
     */
    public void pickUp(String playerName) {
        if (status == FlagStatus.STOLEN) {
            throw new IllegalStateException("Flag is already being carried");
        }
        this.carrierName = playerName;
        this.status = FlagStatus.STOLEN;
    }
    
    /**
     * Drops the flag at current location
     * 
     * @throws IllegalStateException if flag is not being carried
     */
    public void drop() {
        if (status != FlagStatus.STOLEN) {
            throw new IllegalStateException("Flag is not being carried");
        }
        this.carrierName = null;
        this.status = FlagStatus.DROPPED;
    }
    
    /**
     * Returns the flag to its base location
     */
    public void returnToBase() {
        this.carrierName = null;
        this.status = FlagStatus.AT_BASE;
    }
    
    public CTFTeam getTeam() {
        return team;
    }
    
    public Location getBaseLocation() {
        return baseLocation;
    }
    
    public FlagStatus getStatus() {
        return status;
    }
    
    public String getCarrierName() {
        return carrierName;
    }
    
    /**
     * Checks if the flag is at its base
     * 
     * @return true if flag is at base
     */
    public boolean isAtBase() {
        return status == FlagStatus.AT_BASE;
    }
    
    /**
     * Checks if the flag is being carried
     * 
     * @return true if flag is stolen
     */
    public boolean isStolen() {
        return status == FlagStatus.STOLEN;
    }
    
    /**
     * Checks if the flag is dropped
     * 
     * @return true if flag is dropped
     */
    public boolean isDropped() {
        return status == FlagStatus.DROPPED;
    }
}
