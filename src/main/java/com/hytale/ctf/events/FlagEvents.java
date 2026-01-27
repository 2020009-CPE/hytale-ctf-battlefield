package com.hytale.ctf.events;

import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.flag.FlagManager;
import com.hytale.ctf.player.PlayerManager;
import com.hytale.ctf.ui.AlertManager;
import com.hytale.ctf.ui.HUDManager;

/**
 * Handles flag-related events during CTF games.
 * Manages flag pickup, drop, and capture events.
 * 
 * @since 1.0
 */
public class FlagEvents {
    
    private final CTFGame game;
    private final FlagManager flagManager;
    private final PlayerManager playerManager;
    private final AlertManager alertManager;
    private final HUDManager hudManager;
    
    /**
     * Constructs a new FlagEvents handler.
     * 
     * @param game the CTF game instance
     * @param flagManager the flag manager
     * @param playerManager the player manager
     * @param alertManager the alert manager for notifications
     * @param hudManager the HUD manager for display updates
     */
    public FlagEvents(CTFGame game, FlagManager flagManager, PlayerManager playerManager,
                     AlertManager alertManager, HUDManager hudManager) {
        this.game = game;
        this.flagManager = flagManager;
        this.playerManager = playerManager;
        this.alertManager = alertManager;
        this.hudManager = hudManager;
    }
    
    /**
     * Handles flag pickup events.
     * 
     * @param player the name of the player picking up the flag
     * @param flag the identifier of the flag being picked up
     * @return true if pickup was successful, false otherwise
     */
    public boolean onFlagPickup(String player, String flag) {
        if (!game.isActive()) {
            return false;
        }
        
        String playerTeam = playerManager.getTeam(player);
        String flagTeam = flagManager.getFlagTeam(flag);
        
        // Cannot pick up own team's flag
        if (playerTeam != null && playerTeam.equals(flagTeam)) {
            System.out.println("%s cannot pick up their own team's flag".formatted(player));
            return false;
        }
        
        // Flag must be available (at base or dropped)
        if (!flagManager.isFlagAvailable(flag)) {
            return false;
        }
        
        // Execute pickup
        flagManager.pickupFlag(flag, player);
        
        // Broadcast alerts
        alertManager.broadcastFlagStolen(flagTeam, player);
        alertManager.showTitle(player, "Flag Taken!", "Capture it at your base");
        
        System.out.println("%s picked up %s flag".formatted(player, flagTeam));
        return true;
    }
    
    /**
     * Handles flag drop events.
     * 
     * @param player the name of the player dropping the flag
     * @param flag the identifier of the flag being dropped
     * @return true if drop was successful, false otherwise
     */
    public boolean onFlagDrop(String player, String flag) {
        if (!game.isActive()) {
            return false;
        }
        
        String currentCarrier = flagManager.getFlagCarrier(flag);
        
        // Verify player is carrying the flag
        if (currentCarrier == null || !currentCarrier.equals(player)) {
            return false;
        }
        
        // Get drop location
        double[] location = playerManager.getLocation(player);
        
        // Execute drop
        flagManager.dropFlag(flag, location);
        
        String flagTeam = flagManager.getFlagTeam(flag);
        System.out.println("%s dropped %s flag at %.1f, %.1f, %.1f".formatted(
            player, flagTeam, location[0], location[1], location[2]
        ));
        
        return true;
    }
    
    /**
     * Handles flag capture events.
     * 
     * @param player the name of the player capturing the flag
     * @param team the team making the capture
     * @return true if capture was successful, false otherwise
     */
    public boolean onFlagCapture(String player, String team) {
        if (!game.isActive()) {
            return false;
        }
        
        String playerTeam = playerManager.getTeam(player);
        
        // Verify player is on the capturing team
        if (!team.equals(playerTeam)) {
            return false;
        }
        
        // Get enemy flag
        String enemyFlag = flagManager.getEnemyFlag(team);
        String currentCarrier = flagManager.getFlagCarrier(enemyFlag);
        
        // Verify player is carrying enemy flag
        if (currentCarrier == null || !currentCarrier.equals(player)) {
            System.out.println("Capture failed: %s is not carrying the flag".formatted(player));
            return false;
        }
        
        // Verify player is at their base
        if (!playerManager.isAtBase(player)) {
            System.out.println("Capture failed: %s is not at their base".formatted(player));
            return false;
        }
        
        // Verify own flag is at base
        String ownFlag = flagManager.getTeamFlag(team);
        if (!flagManager.isFlagAtBase(ownFlag)) {
            System.out.println("Capture failed: %s team flag is not at base".formatted(team));
            alertManager.showActionBar(player, "Your flag must be at base to capture!");
            return false;
        }
        
        // Execute capture
        flagManager.captureFlag(enemyFlag);
        game.addScore(team, 1);
        
        // Broadcast alerts
        alertManager.broadcastFlagCaptured(team, player);
        alertManager.showTitle(player, "FLAG CAPTURED!", "+1 Point");
        
        // Update HUD
        hudManager.updateScoreboard(
            playerManager.getAllPlayers(),
            game.getScores()
        );
        
        System.out.println("%s captured the flag for %s!".formatted(player, team));
        return true;
    }
}
