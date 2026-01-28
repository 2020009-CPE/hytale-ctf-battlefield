package com.hytale.ctf.arena;

import java.util.Objects;

/**
 * Arena data class representing a CTF battlefield.
 * Uses Java 17 record for immutable arena configuration.
 * 
 * @param name unique identifier for the arena
 * @param region the playable area boundaries
 * @param redSpawn spawn location for red team
 * @param blueSpawn spawn location for blue team
 * @param redFlag flag location for red team
 * @param blueFlag flag location for blue team
 * @param lobby pre-game lobby location
 */
public record Arena(
    String name,
    Region region,
    Location redSpawn,
    Location blueSpawn,
    Location redFlag,
    Location blueFlag,
    Location lobby
) {
    
    /**
     * Compact constructor with validation
     */
    public Arena {
        Objects.requireNonNull(name, "Arena name cannot be null");
        Objects.requireNonNull(region, "Region cannot be null");
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("Arena name cannot be blank");
        }
        
        validateLocation(redSpawn, "Red spawn");
        validateLocation(blueSpawn, "Blue spawn");
        validateLocation(redFlag, "Red flag");
        validateLocation(blueFlag, "Blue flag");
        validateLocation(lobby, "Lobby");
        
        // Ensure all locations are in the same world as the region
        String world = region.pos1().world();
        ensureSameWorld(redSpawn, world, "Red spawn");
        ensureSameWorld(blueSpawn, world, "Blue spawn");
        ensureSameWorld(redFlag, world, "Red flag");
        ensureSameWorld(blueFlag, world, "Blue flag");
        ensureSameWorld(lobby, world, "Lobby");
    }
    
    /**
     * Validate that a location is not null
     */
    private void validateLocation(Location location, String name) {
        Objects.requireNonNull(location, name + " location cannot be null");
    }
    
    /**
     * Ensure a location is in the expected world
     */
    private void ensureSameWorld(Location location, String expectedWorld, String name) {
        if (!location.world().equals(expectedWorld)) {
            throw new IllegalArgumentException(
                name + " must be in the same world as the region (" + expectedWorld + ")"
            );
        }
    }
    
    /**
     * Check if arena is fully configured and valid
     */
    public boolean isValid() {
        return name != null && !name.isBlank()
            && region != null
            && redSpawn != null && blueSpawn != null
            && redFlag != null && blueFlag != null
            && lobby != null;
    }
    
    /**
     * Check if a location is within the arena boundaries
     */
    public boolean contains(Location location) {
        return region.contains(location);
    }
    
    /**
     * Get the world this arena is in
     */
    public String world() {
        return region.pos1().world();
    }
    
    /**
     * Validate that spawn points are within the arena region
     */
    public boolean validateSpawnLocations() {
        return region.contains(redSpawn) 
            && region.contains(blueSpawn)
            && region.contains(redFlag)
            && region.contains(blueFlag);
    }
    
    /**
     * Get the distance between team spawns
     */
    public double getSpawnDistance() {
        return redSpawn.distance(blueSpawn);
    }
    
    /**
     * Get the distance between team flags
     */
    public double getFlagDistance() {
        return redFlag.distance(blueFlag);
    }
}
