package com.hytale.ctf.flag;

import com.hytale.ctf.arena.Location;
import com.hytale.ctf.game.CTFTeam;
import com.hytale.ctf.structure.StructureManager;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages all flags in the CTF game.
 * Handles flag spawning, removal, and retrieval.
 */
public class FlagManager {
    
    private final Map<CTFTeam, Flag> flags;
    private final StructureManager structureManager;
    
    /**
     * Creates a new flag manager
     * 
     * @param structureManager the structure manager for flag locations
     */
    public FlagManager(StructureManager structureManager) {
        this.flags = new EnumMap<>(CTFTeam.class);
        this.structureManager = structureManager;
    }
    
    /**
     * Spawns a flag for a team at the specified location
     * 
     * @param team the team to spawn the flag for
     * @param location the base location for the flag
     * @return the spawned flag
     */
    public Flag spawnFlag(CTFTeam team, Location location) {
        Flag flag = new Flag(team, location);
        flags.put(team, flag);
        return flag;
    }
    
    /**
     * Removes a flag from the game
     * 
     * @param team the team whose flag to remove
     * @return the removed flag, or null if no flag existed
     */
    public Flag removeFlag(CTFTeam team) {
        return flags.remove(team);
    }
    
    /**
     * Gets the flag for a specific team
     * 
     * @param team the team to get the flag for
     * @return optional containing the flag if it exists
     */
    public Optional<Flag> getFlag(CTFTeam team) {
        return Optional.ofNullable(flags.get(team));
    }
    
    /**
     * Gets the player currently carrying a flag
     * 
     * @param team the team whose flag to check
     * @return optional containing carrier name if flag is stolen
     */
    public Optional<String> getFlagCarrier(CTFTeam team) {
        return getFlag(team)
            .filter(Flag::isStolen)
            .map(Flag::getCarrierName);
    }
    
    /**
     * Finds which team's flag is being carried by a player
     * 
     * @param playerName the player to check
     * @return optional containing the team whose flag the player is carrying
     */
    public Optional<CTFTeam> getFlagCarriedBy(String playerName) {
        return flags.entrySet().stream()
            .filter(entry -> entry.getValue().isStolen())
            .filter(entry -> playerName.equals(entry.getValue().getCarrierName()))
            .map(Map.Entry::getKey)
            .findFirst();
    }
    
    /**
     * Removes all flags from the game
     */
    public void clearAllFlags() {
        flags.clear();
    }
    
    /**
     * Gets the structure manager
     * 
     * @return the structure manager
     */
    public StructureManager getStructureManager() {
        return structureManager;
    }
}
