package com.hytale.ctf.structure;

/**
 * Enum representing different types of flag structures in CTF.
 * Each structure type has unique visual and gameplay characteristics.
 * 
 * @since 1.0
 */
public enum StructureType {
    /**
     * Simple raised platform for the flag
     */
    PEDESTAL("Pedestal", "A simple raised platform"),
    
    /**
     * Small watchtower structure
     */
    TOWER("Tower", "A small watchtower structure"),
    
    /**
     * Mini fortified defensive structure
     */
    FORTRESS("Fortress", "A mini fortified structure"),
    
    /**
     * Decorative shrine structure
     */
    SHRINE("Shrine", "A decorative shrine"),
    
    /**
     * Tall pillar structure
     */
    PILLAR("Pillar", "A tall pillar"),
    
    /**
     * Custom user-defined structure
     */
    CUSTOM("Custom", "A custom structure");
    
    private final String displayName;
    private final String description;
    
    /**
     * Creates a structure type with display name and description.
     * 
     * @param displayName the human-readable name
     * @param description the structure description
     */
    StructureType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the display name of this structure type.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of this structure type.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
