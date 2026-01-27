package com.hytale.ctf.commands;

import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.game.CTFTeam;
import com.hytale.ctf.game.GameState;
import com.hytale.ctf.player.CTFPlayer;
import com.hytale.ctf.player.PlayerManager;
import com.hytale.ctf.player.PlayerStats;
import com.hytale.ctf.storage.DataManager;
import com.hytale.ctf.team.TeamManager;
import com.hytale.ctf.util.MessageUtil;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Player commands for game participation and statistics.
 * Handles joining, leaving, team selection, and stats viewing.
 * 
 * <p>Commands: join, leave, team, stats, top</p>
 */
public class PlayerCommands {
    
    private final PlayerManager playerManager;
    private final TeamManager teamManager;
    private final CTFGame game;
    private final DataManager dataManager;
    
    /**
     * Create new player commands handler
     * 
     * @param playerManager the player manager
     * @param teamManager the team manager
     * @param game the CTF game instance
     * @param dataManager the data manager
     */
    public PlayerCommands(
        PlayerManager playerManager,
        TeamManager teamManager,
        CTFGame game,
        DataManager dataManager
    ) {
        this.playerManager = Objects.requireNonNull(playerManager);
        this.teamManager = Objects.requireNonNull(teamManager);
        this.game = Objects.requireNonNull(game);
        this.dataManager = Objects.requireNonNull(dataManager);
    }
    
    /**
     * Execute a player command
     * 
     * @param sender the command sender
     * @param command the command name
     * @param args the command arguments
     * @return true if successful
     */
    public boolean execute(Object sender, String command, String[] args) {
        return switch (command) {
            case "join" -> handleJoin(sender, args);
            case "leave" -> handleLeave(sender, args);
            case "team" -> handleTeam(sender, args);
            case "stats" -> handleStats(sender, args);
            case "top" -> handleTop(sender, args);
            default -> {
                sendMessage(sender, MessageUtil.error("Unknown player command"));
                yield false;
            }
        };
    }
    
    /**
     * Join a game
     */
    private boolean handleJoin(Object sender, String[] args) {
        String playerName = getPlayerName(sender);
        
        // Check if player exists in manager (which means they're in game)
        CTFPlayer player = playerManager.getPlayer(playerName);
        if (player.isOnTeam()) {
            sendMessage(sender, MessageUtil.error("You are already in a game!"));
            sendMessage(sender, MessageUtil.info("Use /ctf leave to leave first"));
            return false;
        }
        
        // Add player to game
        playerManager.addPlayer(playerName);
        
        sendMessage(sender, MessageUtil.success("You joined the game!"));
        sendMessage(sender, MessageUtil.info("Use /ctf team <red|blue> to choose your team"));
        
        return true;
    }
    
    /**
     * Leave the current game
     */
    private boolean handleLeave(Object sender, String[] args) {
        String playerName = getPlayerName(sender);
        
        CTFPlayer player = playerManager.removePlayer(playerName);
        if (player == null) {
            sendMessage(sender, MessageUtil.error("You are not in a game!"));
            return false;
        }
        
        sendMessage(sender, MessageUtil.success("You left the game"));
        
        return true;
    }
    
    /**
     * Choose a team
     */
    private boolean handleTeam(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf team <red|blue>"));
            return false;
        }
        
        String playerName = getPlayerName(sender);
        CTFPlayer player = playerManager.getPlayer(playerName);
        
        if (player == null) {
            sendMessage(sender, MessageUtil.error("You must join a game first!"));
            sendMessage(sender, MessageUtil.info("Use /ctf join to join a game"));
            return false;
        }
        
        CTFTeam team = parseTeam(args[0]);
        if (team == null) {
            sendMessage(sender, MessageUtil.error("Invalid team! Use 'red' or 'blue'"));
            return false;
        }
        
        // Assign player to team
        player.joinTeam(team);
        
        String teamColor = team == CTFTeam.RED ? "§c" : "§9";
        sendMessage(sender, MessageUtil.teamJoin(teamColor + team.name() + "§a"));
        
        return true;
    }
    
    /**
     * View player statistics
     */
    private boolean handleStats(Object sender, String[] args) {
        String targetPlayer = args.length > 0 ? args[0] : getPlayerName(sender);
        
        PlayerStats stats = dataManager.loadStats(targetPlayer);
        
        if (stats.captures() == 0 && stats.kills() == 0 && stats.wins() == 0) {
            if (args.length > 0) {
                sendMessage(sender, MessageUtil.error("Player '" + targetPlayer + "' has no statistics"));
            } else {
                sendMessage(sender, MessageUtil.info("You haven't played any games yet!"));
                sendMessage(sender, MessageUtil.info("Join a game with /ctf join"));
            }
            return true;
        }
        
        sendMessage(sender, MessageUtil.format("§6§l=== Stats for " + targetPlayer + " ==="));
        sendMessage(sender, "");
        sendMessage(sender, String.format("  §7Captures: §f%d", stats.captures()));
        sendMessage(sender, String.format("  §7Returns: §f%d", stats.returns()));
        sendMessage(sender, String.format("  §7Kills: §f%d", stats.kills()));
        sendMessage(sender, String.format("  §7Deaths: §f%d", stats.deaths()));
        sendMessage(sender, String.format("  §7K/D Ratio: §f%.2f", stats.getKDRatio()));
        sendMessage(sender, "");
        sendMessage(sender, String.format("  §7Wins: §a%d", stats.wins()));
        sendMessage(sender, String.format("  §7Losses: §c%d", stats.losses()));
        sendMessage(sender, String.format("  §7Win Rate: §f%.1f%%", stats.getWinRate()));
        sendMessage(sender, "");
        sendMessage(sender, String.format("  §7Blocks Placed: §f%d", stats.blocksPlaced()));
        sendMessage(sender, String.format("  §7Blocks Broken: §f%d", stats.blocksBroken()));
        sendMessage(sender, String.format("  §7Playtime: §f%d hours", stats.playtime() / 3600));
        
        return true;
    }
    
    /**
     * View leaderboard
     */
    private boolean handleTop(Object sender, String[] args) {
        String statType = args.length > 0 ? args[0].toLowerCase() : "captures";
        
        Comparator<PlayerStats> comparator = switch (statType) {
            case "captures", "caps" -> Comparator.comparingInt(PlayerStats::captures);
            case "returns", "rets" -> Comparator.comparingInt(PlayerStats::returns);
            case "kills", "k" -> Comparator.comparingInt(PlayerStats::kills);
            case "kd", "kdr" -> Comparator.comparingDouble(PlayerStats::getKDRatio);
            case "wins", "w" -> Comparator.comparingInt(PlayerStats::wins);
            case "winrate", "wr" -> Comparator.comparingDouble(PlayerStats::getWinRate);
            case "playtime", "time" -> Comparator.comparingLong(PlayerStats::playtime);
            default -> {
                sendMessage(sender, MessageUtil.error("Invalid stat type!"));
                sendMessage(sender, MessageUtil.info("Available: captures, returns, kills, kd, wins, winrate, playtime"));
                yield null;
            }
        };
        
        if (comparator == null) {
            return false;
        }
        
        // Get top 10 players
        List<PlayerStats> topPlayers = dataManager.loadAllStats().values().stream()
            .sorted(comparator.reversed())
            .limit(10)
            .toList();
        
        if (topPlayers.isEmpty()) {
            sendMessage(sender, MessageUtil.info("No statistics available yet!"));
            return true;
        }
        
        String statDisplayName = switch (statType) {
            case "captures", "caps" -> "Captures";
            case "returns", "rets" -> "Returns";
            case "kills", "k" -> "Kills";
            case "kd", "kdr" -> "K/D Ratio";
            case "wins", "w" -> "Wins";
            case "winrate", "wr" -> "Win Rate";
            case "playtime", "time" -> "Playtime";
            default -> "Captures";
        };
        
        sendMessage(sender, MessageUtil.format("§6§l=== Top 10: " + statDisplayName + " ==="));
        sendMessage(sender, "");
        
        int rank = 1;
        for (PlayerStats stats : topPlayers) {
            String medal = switch (rank) {
                case 1 -> "§6🥇";
                case 2 -> "§7🥈";
                case 3 -> "§c🥉";
                default -> "§f" + rank + ".";
            };
            
            String value = switch (statType) {
                case "captures", "caps" -> String.valueOf(stats.captures());
                case "returns", "rets" -> String.valueOf(stats.returns());
                case "kills", "k" -> String.valueOf(stats.kills());
                case "kd", "kdr" -> String.format("%.2f", stats.getKDRatio());
                case "wins", "w" -> String.valueOf(stats.wins());
                case "winrate", "wr" -> String.format("%.1f%%", stats.getWinRate());
                case "playtime", "time" -> (stats.playtime() / 3600) + "h";
                default -> String.valueOf(stats.captures());
            };
            
            sendMessage(sender, String.format("  %s §f%s §7- §e%s", medal, stats.playerName(), value));
            rank++;
        }
        
        return true;
    }
    
    // Helper methods
    
    private CTFTeam parseTeam(String teamStr) {
        return switch (teamStr.toLowerCase()) {
            case "red", "r" -> CTFTeam.RED;
            case "blue", "b" -> CTFTeam.BLUE;
            default -> null;
        };
    }
    
    private void sendMessage(Object sender, String message) {
        System.out.println(message);
    }
    
    private String getPlayerName(Object sender) {
        // TODO: Get actual player name
        return "Player";
    }
}
