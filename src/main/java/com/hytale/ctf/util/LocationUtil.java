package com.hytale.ctf.util;

import com.hytale.ctf.arena.Location;

/**
 * Utility class for location calculations and manipulations.
 */
public final class LocationUtil {
    
    private LocationUtil() {
        // Utility class
    }
    
    /**
     * Format a location as a readable string
     */
    public static String format(Location location) {
        return String.format("%s (%.2f, %.2f, %.2f)",
            location.world(),
            location.x(),
            location.y(),
            location.z()
        );
    }
    
    /**
     * Format a location as block coordinates
     */
    public static String formatBlock(Location location) {
        return String.format("%s (%d, %d, %d)",
            location.world(),
            (int) Math.floor(location.x()),
            (int) Math.floor(location.y()),
            (int) Math.floor(location.z())
        );
    }
    
    /**
     * Check if two locations are in the same world
     */
    public static boolean isSameWorld(Location loc1, Location loc2) {
        return loc1.world().equals(loc2.world());
    }
    
    /**
     * Calculate 2D distance (ignoring Y coordinate)
     */
    public static double distance2D(Location loc1, Location loc2) {
        if (!isSameWorld(loc1, loc2)) {
            return Double.MAX_VALUE;
        }
        
        double dx = loc1.x() - loc2.x();
        double dz = loc1.z() - loc2.z();
        return Math.sqrt(dx * dx + dz * dz);
    }
    
    /**
     * Check if a location is within a spherical radius
     */
    public static boolean isWithinRadius(Location center, Location point, double radius) {
        return center.distance(point) <= radius;
    }
    
    /**
     * Check if a location is within a cylindrical radius (2D)
     */
    public static boolean isWithinRadius2D(Location center, Location point, double radius) {
        return distance2D(center, point) <= radius;
    }
    
    /**
     * Get a location offset by certain amounts
     */
    public static Location offset(Location location, double dx, double dy, double dz) {
        return new Location(
            location.world(),
            location.x() + dx,
            location.y() + dy,
            location.z() + dz,
            location.yaw(),
            location.pitch()
        );
    }
}
