package com.hytale.ctf.game;

/**
 * Enum representing the two teams in CTF
 */
public enum CTFTeam {
    RED("Red", "§c"),
    BLUE("Blue", "§9");
    
    private final String displayName;
    private final String colorCode;
    
    CTFTeam(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColorCode() {
        return colorCode;
    }
    
    public String getColoredName() {
        return colorCode + displayName + "§r";
    }
    
    /**
     * Returns the opposite team
     */
    public CTFTeam opposite() {
        return this == RED ? BLUE : RED;
    }
}
