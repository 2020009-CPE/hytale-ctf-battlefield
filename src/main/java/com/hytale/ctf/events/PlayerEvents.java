package com.hytale.ctf.events;

import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.player.PlayerManager;
import com.hytale.ctf.team.TeamManager;
import com.hytale.ctf.flag.FlagManager;
import com.hytale.ctf.ui.AlertManager;
import com.hytale.ctf.ui.HUDManager;
import com.hytale.api.event.PlayerJoinEvent;
import com.hytale.api.event.PlayerQuitEvent;
import com.hytale.api.event.PlayerDeathEvent;

/**
 * Handles player-related events during CTF games.
 * Manages player join, quit, and death events.
 * 
 * @since 1.0
 */
public class PlayerEvents {
    
    private final CTFGame game;
    private final PlayerManager playerManager;
    private final TeamManager teamManager;
    private final FlagManager flagManager;
    private final AlertManager alertManager;
    private final HUDManager hudManager;
    
    /**
     * Constructs a new PlayerEvents handler.
     * 
     * @param game the CTF game instance
     * @param playerManager the player manager
     * @param teamManager the team manager
     * @param flagManager the flag manager
     * @param alertManager the alert manager for notifications
     * @param hudManager the HUD manager for display updates
     */
    public PlayerEvents(CTFGame game, PlayerManager playerManager, TeamManager teamManager,
                       FlagManager flagManager, AlertManager alertManager, HUDManager hudManager) {
        this.game = game;
        this.playerManager = playerManager;
        this.teamManager = teamManager;
        this.flagManager = flagManager;
        this.alertManager = alertManager;
        this.hudManager = hudManager;
    }
    
    /**
     * Handles player join events.
     * 
     * @param event the player join event
     */
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        System.out.println("%s joined the game".formatted(playerName));
        
        // Add player to manager
        playerManager.addPlayer(playerName);
        
        // Auto-assign to team if game is active
        if (game.isActive()) {
            String team = teamManager.getSmallestTeam();
            teamManager.addPlayer(playerName, team);
            System.out.println("%s assigned to %s team".formatted(playerName, team));
            
            event.getPlayer().sendMessage("Welcome to CTF! You are on " + team + " team");
            alertManager.showTitle(playerName, "Welcome to CTF!", "You are on " + team + " team");
        }
        
        // Show current HUD state
        if (game.isActive()) {
            hudManager.updateScoreboard(
                playerManager.getAllPlayers(),
                game.getScores()
            );
        }
    }
    
    /**
     * Handles player quit events.
     * 
     * @param event the player quit event
     */
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        System.out.println("%s left the game".formatted(playerName));
        
        // Handle flag if player was carrying one
        handleFlagOnLeave(playerName);
        
        // Remove from team
        String team = playerManager.getTeam(playerName);
        if (team != null) {
            teamManager.removePlayer(playerName, team);
        }
        
        // Remove from player manager
        playerManager.removePlayer(playerName);
        
        // Update scoreboard for remaining players
        if (game.isActive()) {
            hudManager.updateScoreboard(
                playerManager.getAllPlayers(),
                game.getScores()
            );
        }
    }
    
    /**
     * Handles player death events.
     * 
     * @param event the player death event
     */
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getPlayer().getName();
        String killerName = event.getKiller() != null ? event.getKiller().getName() : null;
        
        System.out.println("%s was killed%s".formatted(
            playerName,
            killerName != null ? " by " + killerName : ""
        ));
        
        if (!game.isActive()) {
            return;
        }
        
        // Update kill/death stats
        if (killerName != null) {
            playerManager.addKill(killerName);
        }
        playerManager.addDeath(playerName);
        
        // Handle flag drop if player was carrying one
        String carriedFlag = flagManager.getCarriedFlag(playerName);
        if (carriedFlag != null) {
            double[] deathLocation = playerManager.getLocation(playerName);
            flagManager.dropFlag(carriedFlag, deathLocation);
            
            String flagTeam = flagManager.getFlagTeam(carriedFlag);
            
            if (killerName != null) {
                alertManager.broadcastCarrierKilled(killerName);
                alertManager.showTitle(killerName, "Carrier Killed!", "+50 points");
                event.getKiller().sendMessage("You killed the flag carrier! +50 points");
            }
            
            event.getPlayer().sendMessage("You dropped the " + flagTeam + " flag!");
            System.out.println("%s flag dropped at death location".formatted(flagTeam));
        }
        
        // Respawn player
        String team = playerManager.getTeam(playerName);
        if (team != null) {
            playerManager.respawnPlayer(playerName, team);
            event.getPlayer().sendMessage("You have been respawned");
        }
    }
    
    /**
     * Handles flag return when a player leaves the game.
     * 
     * @param playerName the name of the player leaving
     */
    private void handleFlagOnLeave(String playerName) {
        String carriedFlag = flagManager.getCarriedFlag(playerName);
        if (carriedFlag != null) {
            // Return flag to base when player leaves
            flagManager.returnFlag(carriedFlag);
            
            String flagTeam = flagManager.getFlagTeam(carriedFlag);
            alertManager.broadcastFlagReturned(flagTeam);
            
            System.out.println("%s flag returned due to player quit".formatted(flagTeam));
        }
    }
}
