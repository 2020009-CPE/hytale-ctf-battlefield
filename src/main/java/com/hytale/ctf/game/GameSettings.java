package com.hytale.ctf.game;

/**
 * Immutable game settings record using Java 25 records.
 * All game configuration is stored in this record.
 */
public record GameSettings(
    int captureLimit,
    int timeLimit,
    int buildZoneRadius,
    int noBuildZoneRadius,
    boolean requireOwnFlag,
    boolean pvpEnabled,
    boolean buildEnabled,
    boolean breakEnabled,
    boolean overtimeEnabled,
    int mercyRuleDifference
) {
    
    /**
     * Creates default game settings
     */
    public static GameSettings defaults() {
        return new GameSettings(
            3,      // captureLimit
            0,      // timeLimit (unlimited)
            10,     // buildZoneRadius
            3,      // noBuildZoneRadius
            true,   // requireOwnFlag
            true,   // pvpEnabled
            true,   // buildEnabled
            true,   // breakEnabled
            true,   // overtimeEnabled
            5       // mercyRuleDifference
        );
    }
    
    /**
     * Creates a copy with modified capture limit
     */
    public GameSettings withCaptureLimit(int newLimit) {
        return new GameSettings(
            newLimit, timeLimit, buildZoneRadius, noBuildZoneRadius,
            requireOwnFlag, pvpEnabled, buildEnabled, breakEnabled,
            overtimeEnabled, mercyRuleDifference
        );
    }
    
    /**
     * Creates a copy with modified time limit
     */
    public GameSettings withTimeLimit(int newLimit) {
        return new GameSettings(
            captureLimit, newLimit, buildZoneRadius, noBuildZoneRadius,
            requireOwnFlag, pvpEnabled, buildEnabled, breakEnabled,
            overtimeEnabled, mercyRuleDifference
        );
    }
}
