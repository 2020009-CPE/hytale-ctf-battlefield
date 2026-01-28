package com.hytale.ctf.structure;

import com.hytale.ctf.arena.Location;
import com.hytale.ctf.game.CTFTeam;

/**
 * Sealed interface representing a flag structure in CTF.
 * All flag structures must implement this interface to ensure type safety.
 * 
 * <p>Flag structures are physical builds in the game world that mark
 * team flag locations and provide visual identity to flag bases.</p>
 * 
 * @since 1.0
 */
public sealed interface FlagStructure permits 
    PedestalStructure,
    TowerStructure,
    FortressStructure,
    ShrineStructure,
    PillarStructure {
    
    /**
     * Builds the structure at the specified location for the given team.
     * This method should handle all block placement and world modification
     * needed to create the structure.
     * 
     * @param location the center location where the structure should be built
     * @param team the team this structure belongs to (affects coloring)
     * @throws IllegalArgumentException if location or team is null
     */
    void build(Location location, CTFTeam team);
    
    /**
     * Removes the structure from the specified location.
     * This method should clean up all blocks placed by the build method,
     * restoring the area to its original state.
     * 
     * @param location the center location of the structure to remove
     * @throws IllegalArgumentException if location is null
     */
    void remove(Location location);
    
    /**
     * Gets the name of this structure type.
     * 
     * @return the structure name
     */
    String getName();
    
    /**
     * Gets the structure type enum value.
     * 
     * @return the structure type
     */
    StructureType getType();
    
    /**
     * Gets the approximate build radius of the structure.
     * Used for collision detection and placement validation.
     * 
     * @return the build radius in blocks
     */
    default int getBuildRadius() {
        return 5;
    }
    
    /**
     * Gets the height of the structure.
     * 
     * @return the height in blocks
     */
    default int getHeight() {
        return 5;
    }
}
