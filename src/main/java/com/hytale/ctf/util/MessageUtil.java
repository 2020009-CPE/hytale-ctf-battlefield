package com.hytale.ctf.util;

/**
 * Utility class for formatting and sending messages to players.
 * Handles color codes and message templates.
 */
public final class MessageUtil {
    
    private static String prefix = "§8[§bCTF§8]§r ";
    
    private MessageUtil() {
        // Utility class
    }
    
    /**
     * Set the message prefix
     */
    public static void setPrefix(String newPrefix) {
        prefix = translateColorCodes(newPrefix);
    }
    
    /**
     * Format a message with the CTF prefix
     */
    public static String format(String message) {
        return prefix + translateColorCodes(message);
    }
    
    /**
     * Format a message without prefix
     */
    public static String formatNoPrefx(String message) {
        return translateColorCodes(message);
    }
    
    /**
     * Translate & color codes to § codes
     */
    public static String translateColorCodes(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "§");
    }
    
    /**
     * Format a flag stolen message
     */
    public static String flagStolen(String team, String player) {
        return format(String.format("§c%s TEAM's flag has been CAPTURED by %s!", team, player));
    }
    
    /**
     * Alias for flagStolen
     */
    public static String formatFlagStolen(String team, String player) {
        return flagStolen(team, player);
    }
    
    /**
     * Format a flag captured message
     */
    public static String flagCaptured(String team, String player) {
        return format(String.format("§a%s TEAM SCORES! %s captured the flag!", team, player));
    }
    
    /**
     * Alias for flagCaptured
     */
    public static String formatFlagCaptured(String team, String player) {
        return flagCaptured(team, player);
    }
    
    /**
     * Format a flag returned message
     */
    public static String flagReturned(String team) {
        return format(String.format("§e%s TEAM's flag has returned to base!", team));
    }
    
    /**
     * Alias for flagReturned
     */
    public static String formatFlagReturned(String team) {
        return flagReturned(team);
    }
    
    /**
     * Format a carrier killed message
     */
    public static String carrierKilled(String killer) {
        return format(String.format("§c%s killed the flag carrier! Flag returned!", killer));
    }
    
    /**
     * Alias for carrierKilled
     */
    public static String formatCarrierKilled(String killer) {
        return carrierKilled(killer);
    }
    
    /**
     * Format a game start countdown message
     */
    public static String gameStarting(int seconds) {
        return format(String.format("§aGame starting in %d seconds...", seconds));
    }
    
    /**
     * Format a game end message
     */
    public static String gameEnd(String team, int redScore, int blueScore) {
        return format(String.format("§6%s TEAM WINS! Final score: §c%d §7- §9%d", team, redScore, blueScore));
    }
    
    /**
     * Format a team join message
     */
    public static String teamJoin(String team) {
        return format(String.format("§aYou joined the %s team!", team));
    }
    
    /**
     * Format an arena created message
     */
    public static String arenaCreated(String arenaName) {
        return format(String.format("§aArena '%s' created successfully!", arenaName));
    }
    
    /**
     * Format a snapshot saved message
     */
    public static String snapshotSaved(String arenaName) {
        return format(String.format("§aSnapshot saved for arena '%s'!", arenaName));
    }
    
    /**
     * Format an error message
     */
    public static String error(String message) {
        return format("§c" + message);
    }
    
    /**
     * Format a success message
     */
    public static String success(String message) {
        return format("§a" + message);
    }
    
    /**
     * Format an info message
     */
    public static String info(String message) {
        return format("§7" + message);
    }
}
