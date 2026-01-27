package com.hytale.ctf.events;

import com.hytale.api.event.BlockBreakEvent;
import com.hytale.api.event.BlockPlaceEvent;
import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.arena.Location;

/**
 * Handles block-related events during CTF games.
 * Manages block placement and breaking within game arenas.
 * Integrates with Hytale event system.
 * 
 * @since 1.0
 */
public class BlockEvents {
    
    private final CTFGame game;
    private final ArenaManager arenaManager;
    
    /**
     * Constructs a new BlockEvents handler.
     * 
     * @param game the CTF game instance
     * @param arenaManager the arena manager
     */
    public BlockEvents(CTFGame game, ArenaManager arenaManager) {
        this.game = game;
        this.arenaManager = arenaManager;
    }
    
    /**
     * Handles Hytale block placement events.
     * Checks if the player is allowed to place blocks at the specified location.
     * 
     * @param event the Hytale block place event
     */
    public void onBlockPlace(BlockPlaceEvent event) {
        // Convert Hytale location to internal location
        Location location = event.getLocation().toInternalLocation();
        
        // Check if game is active
        if (!game.isActive()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cCannot place blocks: Game is not active");
            return;
        }
        
        // Check if building is enabled
        if (!game.getSettings().buildEnabled()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cBuilding is disabled in this game");
            return;
        }
        
        // Check arena boundaries
        if (!isWithinActiveArena(location)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cCannot place blocks outside arena boundaries");
            return;
        }
        
        // Check no-build zones (near flags)
        if (isInNoBuildZone(location)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cCannot build in protected zone near flags");
            return;
        }
    }
    
    /**
     * Handles Hytale block break events.
     * Checks if the player is allowed to break blocks at the specified location.
     * 
     * @param event the Hytale block break event
     */
    public void onBlockBreak(BlockBreakEvent event) {
        // Convert Hytale location to internal location
        Location location = event.getLocation().toInternalLocation();
        String blockType = event.getBlockType().getType();
        
        // Check if this is a flag block
        if (isFlagBlock(blockType)) {
            handleFlagBreak(event);
            return;
        }
        
        // Check if game is active
        if (!game.isActive()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cCannot break blocks: Game is not active");
            return;
        }
        
        // Check if breaking is enabled
        if (!game.getSettings().breakEnabled()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cBreaking blocks is disabled in this game");
            return;
        }
    }
    
    /**
     * Check if location is within the active arena
     */
    private boolean isWithinActiveArena(Location location) {
        var currentArena = game.getCurrentArena();
        if (currentArena == null) {
            return false;
        }
        return currentArena.contains(location);
    }
    
    /**
     * Check if location is in a no-build zone (near flags)
     */
    private boolean isInNoBuildZone(Location location) {
        var currentArena = game.getCurrentArena();
        if (currentArena == null) {
            return false;
        }
        
        int noBuildRadius = game.getSettings().noBuildZoneRadius();
        
        // Check distance to red flag
        if (currentArena.redFlag() != null) {
            if (location.distance(currentArena.redFlag()) < noBuildRadius) {
                return true;
            }
        }
        
        // Check distance to blue flag
        if (currentArena.blueFlag() != null) {
            if (location.distance(currentArena.blueFlag()) < noBuildRadius) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if block type is a flag
     */
    private boolean isFlagBlock(String blockType) {
        return blockType != null && (
            blockType.contains("flag_red") || 
            blockType.contains("flag_blue")
        );
    }
    
    /**
     * Handle flag block break (flag capture)
     */
    private void handleFlagBreak(BlockBreakEvent event) {
        if (!game.isActive()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cGame is not active");
            return;
        }
        
        String blockType = event.getBlockType().getType();
        String playerName = event.getPlayer().getName();
        
        // Determine which flag was broken
        boolean isRedFlag = blockType.contains("flag_red");
        boolean isBlueFlag = blockType.contains("flag_blue");
        
        if (isRedFlag) {
            game.handleFlagSteal(playerName, "red");
        } else if (isBlueFlag) {
            game.handleFlagSteal(playerName, "blue");
        }
        
        // Allow the break (flag pickup)
        event.setCancelled(false);
    }
}
