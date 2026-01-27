package com.hytale.ctf.flag;

import com.hytale.ctf.game.CTFTeam;

/**
 * Represents special flag item properties using Java 25 records.
 * Flags cannot be dropped or stored in containers.
 */
public record FlagItem(
    CTFTeam team,
    boolean canDrop,
    boolean canStore
) {
    
    /**
     * Creates a flag item with enforced restrictions
     * 
     * @param team the team this flag belongs to
     */
    public FlagItem(CTFTeam team) {
        this(team, false, false);
    }
    
    /**
     * Validates that flag restrictions are enforced
     * 
     * @throws IllegalArgumentException if restrictions are violated
     */
    public FlagItem {
        if (canDrop || canStore) {
            throw new IllegalArgumentException(
                "Flags cannot be dropped or stored: canDrop and canStore must be false"
            );
        }
    }
    
    /**
     * Validates if the player can drop this item
     * 
     * @return false, flags cannot be dropped
     */
    public boolean canPlayerDrop() {
        return canDrop;
    }
    
    /**
     * Validates if the item can be stored in a container
     * 
     * @return false, flags cannot be stored
     */
    public boolean canBeStored() {
        return canStore;
    }
    
    /**
     * Checks if this is a valid flag item
     * 
     * @return true if restrictions are properly enforced
     */
    public boolean isValid() {
        return !canDrop && !canStore && team != null;
    }
}
