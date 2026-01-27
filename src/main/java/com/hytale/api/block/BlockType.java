package com.hytale.api.block;

/**
 * Represents a block type in Hytale.
 * Each block type has a unique identifier and properties.
 */
public interface BlockType {
    
    /**
     * Get the block's type identifier
     */
    String getType();
    
    /**
     * Check if this is air
     */
    boolean isAir();
    
    /**
     * Check if this block is solid
     */
    boolean isSolid();
    
    /**
     * Check if this block is breakable
     */
    boolean isBreakable();
    
    /**
     * Get the block's hardness
     */
    float getHardness();
    
    /**
     * Common block types
     */
    class Types {
        public static final String AIR = "air";
        public static final String STONE = "stone";
        public static final String DIRT = "dirt";
        public static final String GRASS = "grass";
        public static final String WOOD = "wood";
        public static final String PLANKS = "planks";
        public static final String COBBLESTONE = "cobblestone";
        public static final String BEDROCK = "bedrock";
        public static final String SAND = "sand";
        public static final String GRAVEL = "gravel";
        public static final String GLASS = "glass";
        public static final String WOOL_RED = "wool_red";
        public static final String WOOL_BLUE = "wool_blue";
        public static final String BANNER_RED = "banner_red";
        public static final String BANNER_BLUE = "banner_blue";
        public static final String FLAG_RED = "flag_red";
        public static final String FLAG_BLUE = "flag_blue";
    }
}
