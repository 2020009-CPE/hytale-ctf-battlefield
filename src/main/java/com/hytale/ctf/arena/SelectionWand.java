package com.hytale.ctf.arena;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Wand tool for region selection in arena creation.
 * Manages position selections for multiple players simultaneously.
 */
public class SelectionWand {
    
    private final Map<String, Location> pos1Selections;
    private final Map<String, Location> pos2Selections;
    
    /**
     * Create a new SelectionWand
     */
    public SelectionWand() {
        this.pos1Selections = new ConcurrentHashMap<>();
        this.pos2Selections = new ConcurrentHashMap<>();
    }
    
    /**
     * Set the first position for a player
     * 
     * @param playerId the player identifier
     * @param location the location to set
     */
    public void setPos1(String playerId, Location location) {
        Objects.requireNonNull(playerId, "Player ID cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        pos1Selections.put(playerId, location);
    }
    
    /**
     * Set the second position for a player
     * 
     * @param playerId the player identifier
     * @param location the location to set
     */
    public void setPos2(String playerId, Location location) {
        Objects.requireNonNull(playerId, "Player ID cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        pos2Selections.put(playerId, location);
    }
    
    /**
     * Get the first position for a player
     * 
     * @param playerId the player identifier
     * @return the location, or null if not set
     */
    public Location getPos1(String playerId) {
        return pos1Selections.get(playerId);
    }
    
    /**
     * Get the second position for a player
     * 
     * @param playerId the player identifier
     * @return the location, or null if not set
     */
    public Location getPos2(String playerId) {
        return pos2Selections.get(playerId);
    }
    
    /**
     * Get the complete selection as a Region
     * 
     * @param playerId the player identifier
     * @return the region, or null if both positions are not set
     */
    public Region getSelection(String playerId) {
        Location pos1 = pos1Selections.get(playerId);
        Location pos2 = pos2Selections.get(playerId);
        
        if (pos1 == null || pos2 == null) {
            return null;
        }
        
        try {
            return new Region(pos1, pos2);
        } catch (IllegalArgumentException e) {
            // Positions are in different worlds
            return null;
        }
    }
    
    /**
     * Check if a player has both positions set
     * 
     * @param playerId the player identifier
     * @return true if both positions are set
     */
    public boolean hasCompleteSelection(String playerId) {
        return pos1Selections.containsKey(playerId) 
            && pos2Selections.containsKey(playerId);
    }
    
    /**
     * Check if a player has the first position set
     * 
     * @param playerId the player identifier
     * @return true if position 1 is set
     */
    public boolean hasPos1(String playerId) {
        return pos1Selections.containsKey(playerId);
    }
    
    /**
     * Check if a player has the second position set
     * 
     * @param playerId the player identifier
     * @return true if position 2 is set
     */
    public boolean hasPos2(String playerId) {
        return pos2Selections.containsKey(playerId);
    }
    
    /**
     * Clear both positions for a player
     * 
     * @param playerId the player identifier
     */
    public void clearSelection(String playerId) {
        pos1Selections.remove(playerId);
        pos2Selections.remove(playerId);
    }
    
    /**
     * Clear the first position for a player
     * 
     * @param playerId the player identifier
     */
    public void clearPos1(String playerId) {
        pos1Selections.remove(playerId);
    }
    
    /**
     * Clear the second position for a player
     * 
     * @param playerId the player identifier
     */
    public void clearPos2(String playerId) {
        pos2Selections.remove(playerId);
    }
    
    /**
     * Clear all selections for all players
     */
    public void clearAll() {
        pos1Selections.clear();
        pos2Selections.clear();
    }
    
    /**
     * Get selection information for a player
     * 
     * @param playerId the player identifier
     * @return a selection info record, or null if no selections
     */
    public SelectionInfo getSelectionInfo(String playerId) {
        Location pos1 = pos1Selections.get(playerId);
        Location pos2 = pos2Selections.get(playerId);
        Region region = getSelection(playerId);
        
        if (pos1 == null && pos2 == null) {
            return null;
        }
        
        return new SelectionInfo(pos1, pos2, region);
    }
    
    /**
     * Get all players with active selections
     * 
     * @return unmodifiable set of player IDs
     */
    public Set<String> getActivePlayers() {
        Set<String> active = new HashSet<>();
        active.addAll(pos1Selections.keySet());
        active.addAll(pos2Selections.keySet());
        return Collections.unmodifiableSet(active);
    }
    
    /**
     * Get the number of active selections
     * 
     * @return the count of players with at least one position set
     */
    public int getActiveSelectionCount() {
        return getActivePlayers().size();
    }
    
    /**
     * Selection information record
     * 
     * @param pos1 the first position (may be null)
     * @param pos2 the second position (may be null)
     * @param region the complete region (null if incomplete)
     */
    public record SelectionInfo(
        Location pos1,
        Location pos2,
        Region region
    ) {
        /**
         * Check if the selection is complete
         */
        public boolean isComplete() {
            return pos1 != null && pos2 != null && region != null;
        }
        
        /**
         * Get the selection volume, or -1 if incomplete
         */
        public long getVolume() {
            return region != null ? region.getVolume() : -1;
        }
        
        /**
         * Check if positions are in the same world
         */
        public boolean isSameWorld() {
            if (pos1 == null || pos2 == null) {
                return false;
            }
            return pos1.world().equals(pos2.world());
        }
    }
}
