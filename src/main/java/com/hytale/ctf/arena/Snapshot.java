package com.hytale.ctf.arena;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Block snapshot system for arena restoration.
 * Captures and restores block states within an arena.
 * 
 * @param arenaName the name of the arena this snapshot belongs to
 * @param blocks map of locations to their block data
 */
public record Snapshot(
    String arenaName,
    Map<Location, BlockData> blocks
) {
    
    /**
     * Simple block data record
     * 
     * @param type the block type identifier
     * @param metadata additional block properties
     */
    public record BlockData(
        String type,
        Map<String, String> metadata
    ) {
        /**
         * Compact constructor with validation
         */
        public BlockData {
            Objects.requireNonNull(type, "Block type cannot be null");
            
            if (type.isBlank()) {
                throw new IllegalArgumentException("Block type cannot be blank");
            }
            
            // Make metadata map immutable and non-null
            if (metadata == null) {
                metadata = Map.of();
            } else {
                metadata = Map.copyOf(metadata);
            }
        }
        
        /**
         * Create BlockData with no metadata
         */
        public BlockData(String type) {
            this(type, Map.of());
        }
        
        /**
         * Check if this block has metadata
         */
        public boolean hasMetadata() {
            return !metadata.isEmpty();
        }
        
        /**
         * Get a metadata value
         */
        public String getMetadata(String key) {
            return metadata.get(key);
        }
        
        /**
         * Check if a metadata key exists
         */
        public boolean hasMetadata(String key) {
            return metadata.containsKey(key);
        }
    }
    
    /**
     * Compact constructor with validation
     */
    public Snapshot {
        Objects.requireNonNull(arenaName, "Arena name cannot be null");
        Objects.requireNonNull(blocks, "Blocks map cannot be null");
        
        if (arenaName.isBlank()) {
            throw new IllegalArgumentException("Arena name cannot be blank");
        }
        
        // Make blocks map immutable
        blocks = Map.copyOf(blocks);
    }
    
    /**
     * Create an empty snapshot for an arena
     */
    public static Snapshot empty(String arenaName) {
        return new Snapshot(arenaName, Map.of());
    }
    
    /**
     * Create a snapshot by capturing blocks in a region
     * 
     * @param arenaName the arena name
     * @param region the region to capture
     * @param blockProvider function to get block data at a location
     * @return the created snapshot
     */
    public static Snapshot capture(
        String arenaName, 
        Region region,
        java.util.function.Function<Location, BlockData> blockProvider
    ) {
        Objects.requireNonNull(arenaName, "Arena name cannot be null");
        Objects.requireNonNull(region, "Region cannot be null");
        Objects.requireNonNull(blockProvider, "Block provider cannot be null");
        
        Map<Location, BlockData> capturedBlocks = new ConcurrentHashMap<>();
        Location min = region.getMin();
        Location max = region.getMax();
        
        // Iterate through all blocks in the region
        for (int x = (int) min.x(); x <= max.x(); x++) {
            for (int y = (int) min.y(); y <= max.y(); y++) {
                for (int z = (int) min.z(); z <= max.z(); z++) {
                    Location loc = new Location(min.world(), x, y, z);
                    BlockData data = blockProvider.apply(loc);
                    if (data != null) {
                        capturedBlocks.put(loc, data);
                    }
                }
            }
        }
        
        return new Snapshot(arenaName, capturedBlocks);
    }
    
    /**
     * Restore blocks from this snapshot
     * 
     * @param blockSetter consumer to set block data at a location
     */
    public void restore(java.util.function.BiConsumer<Location, BlockData> blockSetter) {
        Objects.requireNonNull(blockSetter, "Block setter cannot be null");
        blocks.forEach(blockSetter);
    }
    
    /**
     * Restore blocks in a specific region only
     * 
     * @param region the region to restore
     * @param blockSetter consumer to set block data at a location
     */
    public void restoreRegion(
        Region region,
        java.util.function.BiConsumer<Location, BlockData> blockSetter
    ) {
        Objects.requireNonNull(region, "Region cannot be null");
        Objects.requireNonNull(blockSetter, "Block setter cannot be null");
        
        blocks.entrySet().stream()
            .filter(entry -> region.contains(entry.getKey()))
            .forEach(entry -> blockSetter.accept(entry.getKey(), entry.getValue()));
    }
    
    /**
     * Get the number of blocks in this snapshot
     */
    public int getBlockCount() {
        return blocks.size();
    }
    
    /**
     * Check if this snapshot is empty
     */
    public boolean isEmpty() {
        return blocks.isEmpty();
    }
    
    /**
     * Check if this snapshot contains a specific location
     */
    public boolean hasBlock(Location location) {
        return blocks.containsKey(location);
    }
    
    /**
     * Get block data at a specific location
     */
    public BlockData getBlock(Location location) {
        return blocks.get(location);
    }
    
    /**
     * Merge this snapshot with another, with the other taking precedence
     * 
     * @param other the other snapshot to merge with
     * @return a new merged snapshot
     */
    public Snapshot merge(Snapshot other) {
        if (!arenaName.equals(other.arenaName)) {
            throw new IllegalArgumentException("Cannot merge snapshots from different arenas");
        }
        
        Map<Location, BlockData> merged = new HashMap<>(this.blocks);
        merged.putAll(other.blocks);
        return new Snapshot(arenaName, merged);
    }
    
    /**
     * Get all unique block types in this snapshot
     */
    public Set<String> getBlockTypes() {
        return blocks.values().stream()
            .map(BlockData::type)
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
    }
}
