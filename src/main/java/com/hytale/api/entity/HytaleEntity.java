package com.hytale.api.entity;

import com.hytale.api.world.HytaleLocation;

import java.util.UUID;

/**
 * Represents an entity in Hytale (mobs, items, etc.)
 */
public interface HytaleEntity {
    
    /**
     * Get the entity's unique ID
     */
    UUID getUniqueId();
    
    /**
     * Get the entity's type
     */
    String getType();
    
    /**
     * Get the entity's current location
     */
    HytaleLocation getLocation();
    
    /**
     * Teleport entity to location
     */
    void teleport(HytaleLocation location);
    
    /**
     * Remove the entity
     */
    void remove();
    
    /**
     * Check if entity is on ground
     */
    boolean isOnGround();
    
    /**
     * Set entity's velocity
     */
    void setVelocity(double x, double y, double z);
    
    /**
     * Apply damage to entity
     */
    void damage(double amount);
    
    /**
     * Check if entity is dead
     */
    boolean isDead();
}
