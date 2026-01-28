package com.hytale.ctf.arena;

/**
 * 3D region defined by two corners using Java 25 records.
 * Represents a cuboid selection in the world.
 */
public record Region(
    Location pos1,
    Location pos2
) {
    
    /**
     * Validate that both positions are in the same world
     */
    public Region {
        if (!pos1.world().equals(pos2.world())) {
            throw new IllegalArgumentException("Region positions must be in the same world");
        }
    }
    
    /**
     * Get the minimum corner
     */
    public Location getMin() {
        return new Location(
            pos1.world(),
            Math.min(pos1.x(), pos2.x()),
            Math.min(pos1.y(), pos2.y()),
            Math.min(pos1.z(), pos2.z())
        );
    }
    
    /**
     * Get the maximum corner
     */
    public Location getMax() {
        return new Location(
            pos1.world(),
            Math.max(pos1.x(), pos2.x()),
            Math.max(pos1.y(), pos2.y()),
            Math.max(pos1.z(), pos2.z())
        );
    }
    
    /**
     * Check if a location is within this region
     */
    public boolean contains(Location location) {
        if (!location.world().equals(pos1.world())) {
            return false;
        }
        
        Location min = getMin();
        Location max = getMax();
        
        return location.x() >= min.x() && location.x() <= max.x()
            && location.y() >= min.y() && location.y() <= max.y()
            && location.z() >= min.z() && location.z() <= max.z();
    }
    
    /**
     * Calculate the volume of this region
     */
    public long getVolume() {
        Location min = getMin();
        Location max = getMax();
        
        long dx = (long) (max.x() - min.x() + 1);
        long dy = (long) (max.y() - min.y() + 1);
        long dz = (long) (max.z() - min.z() + 1);
        
        return dx * dy * dz;
    }
    
    /**
     * Get the center location of this region
     */
    public Location getCenter() {
        Location min = getMin();
        Location max = getMax();
        
        return new Location(
            pos1.world(),
            (min.x() + max.x()) / 2,
            (min.y() + max.y()) / 2,
            (min.z() + max.z()) / 2
        );
    }
}
