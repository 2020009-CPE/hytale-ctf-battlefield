package com.hytale.ctf.arena;

/**
 * 3D location record using Java 25 records.
 * Represents an immutable position in the world.
 */
public record Location(
    String world,
    double x,
    double y,
    double z,
    float yaw,
    float pitch
) {
    
    /**
     * Create a location with default yaw and pitch
     */
    public Location(String world, double x, double y, double z) {
        this(world, x, y, z, 0f, 0f);
    }
    
    /**
     * Calculate distance to another location
     */
    public double distance(Location other) {
        if (!world.equals(other.world)) {
            return Double.MAX_VALUE;
        }
        double dx = x - other.x;
        double dy = y - other.y;
        double dz = z - other.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    
    /**
     * Check if location is within a certain distance
     */
    public boolean isWithinDistance(Location other, double distance) {
        return distance(other) <= distance;
    }
    
    /**
     * Get block coordinates
     */
    public Location blockLocation() {
        return new Location(
            world,
            Math.floor(x),
            Math.floor(y),
            Math.floor(z),
            yaw,
            pitch
        );
    }
}
