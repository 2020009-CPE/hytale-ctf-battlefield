package com.hytale.ctf.player;

/**
 * Player statistics record using Java 25 records for immutability.
 * All stats are tracked per player.
 */
public record PlayerStats(
    String playerName,
    int captures,
    int returns,
    int kills,
    int deaths,
    int wins,
    int losses,
    long playtime,
    int blocksPlaced,
    int blocksBroken
) {
    
    /**
     * Creates new empty stats for a player
     */
    public static PlayerStats empty(String playerName) {
        return new PlayerStats(playerName, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    
    /**
     * Record a capture
     */
    public PlayerStats addCapture() {
        return new PlayerStats(
            playerName, captures + 1, returns, kills, deaths,
            wins, losses, playtime, blocksPlaced, blocksBroken
        );
    }
    
    /**
     * Record a return (killed flag carrier)
     */
    public PlayerStats addReturn() {
        return new PlayerStats(
            playerName, captures, returns + 1, kills, deaths,
            wins, losses, playtime, blocksPlaced, blocksBroken
        );
    }
    
    /**
     * Record a kill
     */
    public PlayerStats addKill() {
        return new PlayerStats(
            playerName, captures, returns, kills + 1, deaths,
            wins, losses, playtime, blocksPlaced, blocksBroken
        );
    }
    
    /**
     * Record a death
     */
    public PlayerStats addDeath() {
        return new PlayerStats(
            playerName, captures, returns, kills, deaths + 1,
            wins, losses, playtime, blocksPlaced, blocksBroken
        );
    }
    
    /**
     * Record a win
     */
    public PlayerStats addWin() {
        return new PlayerStats(
            playerName, captures, returns, kills, deaths,
            wins + 1, losses, playtime, blocksPlaced, blocksBroken
        );
    }
    
    /**
     * Record a loss
     */
    public PlayerStats addLoss() {
        return new PlayerStats(
            playerName, captures, returns, kills, deaths,
            wins, losses + 1, playtime, blocksPlaced, blocksBroken
        );
    }
    
    /**
     * Add playtime
     */
    public PlayerStats addPlaytime(long seconds) {
        return new PlayerStats(
            playerName, captures, returns, kills, deaths,
            wins, losses, playtime + seconds, blocksPlaced, blocksBroken
        );
    }
    
    /**
     * Record a block placement
     */
    public PlayerStats addBlockPlaced() {
        return new PlayerStats(
            playerName, captures, returns, kills, deaths,
            wins, losses, playtime, blocksPlaced + 1, blocksBroken
        );
    }
    
    /**
     * Record a block break
     */
    public PlayerStats addBlockBroken() {
        return new PlayerStats(
            playerName, captures, returns, kills, deaths,
            wins, losses, playtime, blocksPlaced, blocksBroken + 1
        );
    }
    
    /**
     * Calculate K/D ratio
     */
    public double getKDRatio() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }
    
    /**
     * Calculate win rate
     */
    public double getWinRate() {
        int total = wins + losses;
        return total == 0 ? 0.0 : (double) wins / total * 100;
    }
}
