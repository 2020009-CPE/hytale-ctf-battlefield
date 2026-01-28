package com.hytale.api.world;

import com.hytale.api.block.BlockType;

/**
 * Represents a location in a Hytale world.
 * Immutable record for position and orientation.
 */
public record HytaleLocation(
    HytaleWorld world,
    double x,
    double y,
    double z,
    float yaw,
    float pitch
) {
    
    /**
     * Create a location without rotation
     */
    public HytaleLocation(HytaleWorld world, double x, double y, double z) {
        this(world, x, y, z, 0f, 0f);
    }
    
    /**
     * Get block at this location
     */
    public BlockType getBlock() {
        return world.getBlockAt(this);
    }
    
    /**
     * Set block at this location
     */
    public void setBlock(BlockType blockType) {
        world.setBlockAt(this, blockType);
    }
    
    /**
     * Get block coordinates
     */
    public HytaleLocation blockLocation() {
        return new HytaleLocation(
            world,
            Math.floor(x),
            Math.floor(y),
            Math.floor(z),
            yaw,
            pitch
        );
    }
    
    /**
     * Calculate distance to another location
     */
    public double distance(HytaleLocation other) {
        if (!world.equals(other.world)) {
            return Double.MAX_VALUE;
        }
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Add offset to location
     */
    public HytaleLocation add(double dx, double dy, double dz) {
        return new HytaleLocation(world, x + dx, y + dy, z + dz, yaw, pitch);
    }
    
    /**
     * Convert to internal Location format
     */
    public com.hytale.ctf.arena.Location toInternalLocation() {
        return new com.hytale.ctf.arena.Location(
            world.getName(),
            x, y, z, yaw, pitch
        );
    }
    
    /**
     * Create from internal Location format
     */
    public static HytaleLocation fromInternalLocation(
        com.hytale.ctf.arena.Location internal,
        HytaleWorld world
    ) {
        return new HytaleLocation(
            world,
            internal.x(),
            internal.y(),
            internal.z(),
            internal.yaw(),
            internal.pitch()
        );
    }
}
