package com.hytale.api.world;

import com.hytale.api.block.BlockType;
import com.hytale.api.entity.HytaleEntity;

import java.util.List;

/**
 * Represents a Hytale world/dimension.
 * Provides access to blocks, entities, and world properties.
 */
public interface HytaleWorld {
    
    /**
     * Get the world's name
     */
    String getName();
    
    /**
     * Get block at location
     */
    BlockType getBlockAt(HytaleLocation location);
    
    /**
     * Get block at coordinates
     */
    BlockType getBlockAt(int x, int y, int z);
    
    /**
     * Set block at location
     */
    void setBlockAt(HytaleLocation location, BlockType blockType);
    
    /**
     * Set block at coordinates
     */
    void setBlockAt(int x, int y, int z, BlockType blockType);
    
    /**
     * Break block at location (with drop)
     */
    void breakBlock(HytaleLocation location);
    
    /**
     * Get all entities in world
     */
    List<HytaleEntity> getEntities();
    
    /**
     * Get entities near location
     */
    List<HytaleEntity> getNearbyEntities(HytaleLocation location, double radius);
    
    /**
     * Spawn entity at location
     */
    HytaleEntity spawnEntity(HytaleLocation location, String entityType);
    
    /**
     * Play sound at location
     */
    void playSound(HytaleLocation location, String sound, float volume, float pitch);
    
    /**
     * Play effect at location
     */
    void playEffect(HytaleLocation location, String effect);
    
    /**
     * Create explosion at location
     */
    void createExplosion(HytaleLocation location, float power, boolean breakBlocks);
    
    /**
     * Get spawn location for this world
     */
    HytaleLocation getSpawnLocation();
    
    /**
     * Set spawn location for this world
     */
    void setSpawnLocation(HytaleLocation location);
}
