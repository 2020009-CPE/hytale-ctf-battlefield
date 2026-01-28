package com.hytale.ctf.ui;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages statistics leaderboard for CTF players.
 * Tracks and displays top players across various statistics.
 * 
 * @since 1.0
 */
public class Leaderboard {
    
    private final Map<String, PlayerStatistics> statistics;
    
    /**
     * Constructs a new Leaderboard.
     */
    public Leaderboard() {
        this.statistics = new HashMap<>();
    }
    
    /**
     * Retrieves the top players for a specific statistic type.
     * 
     * @param statType the type of statistic to rank by
     * @param limit the maximum number of players to return
     * @return list of player names and their statistics, sorted by the specified stat
     */
    public List<LeaderboardEntry> getTopPlayers(StatType statType, int limit) {
        return statistics.entrySet().stream()
            .map(entry -> new LeaderboardEntry(
                entry.getKey(),
                entry.getValue(),
                getStatValue(entry.getValue(), statType)
            ))
            .sorted(Comparator.comparingInt(LeaderboardEntry::value).reversed())
            .limit(limit)
            .toList();
    }
    
    /**
     * Formats a leaderboard into a display string.
     * 
     * @param stats the map of player names to their statistics
     * @return formatted leaderboard string
     */
    public String formatLeaderboard(Map<String, PlayerStatistics> stats) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== CTF Leaderboard ===\n\n");
        
        // Top Captures
        formatted.append("Top Captures:\n");
        List<LeaderboardEntry> topCaptures = getTopPlayers(StatType.CAPTURES, 5);
        formatted.append(formatEntries(topCaptures, StatType.CAPTURES));
        
        formatted.append("\nTop Kills:\n");
        List<LeaderboardEntry> topKills = getTopPlayers(StatType.KILLS, 5);
        formatted.append(formatEntries(topKills, StatType.KILLS));
        
        formatted.append("\nTop Wins:\n");
        List<LeaderboardEntry> topWins = getTopPlayers(StatType.WINS, 5);
        formatted.append(formatEntries(topWins, StatType.WINS));
        
        return formatted.toString();
    }
    
    /**
     * Updates statistics for a specific player.
     * 
     * @param playerName the name of the player
     * @param stats the player's statistics
     */
    public void updatePlayer(String playerName, PlayerStatistics stats) {
        statistics.put(playerName, stats);
    }
    
    /**
     * Retrieves statistics for a specific player.
     * 
     * @param playerName the name of the player
     * @return the player's statistics, or empty statistics if not found
     */
    public PlayerStatistics getPlayerStats(String playerName) {
        return statistics.getOrDefault(playerName, new PlayerStatistics());
    }
    
    /**
     * Clears all leaderboard statistics.
     */
    public void clear() {
        statistics.clear();
    }
    
    /**
     * Gets the value of a specific statistic from player statistics.
     * 
     * @param stats the player statistics
     * @param type the statistic type
     * @return the value of the specified statistic
     */
    private int getStatValue(PlayerStatistics stats, StatType type) {
        return switch (type) {
            case CAPTURES -> stats.captures();
            case KILLS -> stats.kills();
            case WINS -> stats.wins();
        };
    }
    
    /**
     * Formats leaderboard entries into a string.
     * 
     * @param entries the leaderboard entries
     * @param type the statistic type
     * @return formatted string
     */
    private String formatEntries(List<LeaderboardEntry> entries, StatType type) {
        StringBuilder sb = new StringBuilder();
        int rank = 1;
        for (LeaderboardEntry entry : entries) {
            sb.append("%d. %s - %d\n".formatted(rank++, entry.playerName(), entry.value()));
        }
        return sb.toString();
    }
    
    /**
     * Types of statistics that can be tracked.
     */
    public enum StatType {
        CAPTURES,
        KILLS,
        WINS
    }
    
    /**
     * Record representing complete player statistics.
     * 
     * @param kills total kills
     * @param deaths total deaths
     * @param captures total flag captures
     * @param wins total game wins
     * @param losses total game losses
     */
    public record PlayerStatistics(
        int kills,
        int deaths,
        int captures,
        int wins,
        int losses
    ) {
        /**
         * Creates empty player statistics.
         */
        public PlayerStatistics() {
            this(0, 0, 0, 0, 0);
        }
    }
    
    /**
     * Record representing a leaderboard entry.
     * 
     * @param playerName the player's name
     * @param stats the player's complete statistics
     * @param value the value for the ranked statistic
     */
    public record LeaderboardEntry(
        String playerName,
        PlayerStatistics stats,
        int value
    ) {}
}
