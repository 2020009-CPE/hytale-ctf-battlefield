package com.hytale.api.player;

import com.hytale.api.world.HytaleLocation;
import com.hytale.api.world.HytaleWorld;
import com.hytale.api.block.BlockType;

import java.util.UUID;

/**
 * Represents a player in Hytale.
 * Provides access to player data, inventory, and actions.
 */
public interface HytalePlayer {
    
    /**
     * Get the player's name
     */
    String getName();
    
    /**
     * Get the player's unique ID
     */
    UUID getUniqueId();
    
    /**
     * Get the player's current location
     */
    HytaleLocation getLocation();
    
    /**
     * Get the player's current world
     */
    HytaleWorld getWorld();
    
    /**
     * Teleport player to location
     */
    void teleport(HytaleLocation location);
    
    /**
     * Send a message to the player
     */
    void sendMessage(String message);
    
    /**
     * Send a title to the player
     */
    void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);
    
    /**
     * Send an action bar message
     */
    void sendActionBar(String message);
    
    /**
     * Give item to player
     */
    void giveItem(String itemType, int amount);
    
    /**
     * Remove item from player
     */
    void removeItem(String itemType, int amount);
    
    /**
     * Check if player has item
     */
    boolean hasItem(String itemType, int amount);
    
    /**
     * Clear player's inventory
     */
    void clearInventory();
    
    /**
     * Get player's health
     */
    double getHealth();
    
    /**
     * Set player's health
     */
    void setHealth(double health);
    
    /**
     * Get player's max health
     */
    double getMaxHealth();
    
    /**
     * Damage the player
     */
    void damage(double amount);
    
    /**
     * Heal the player
     */
    void heal(double amount);
    
    /**
     * Check if player is online
     */
    boolean isOnline();
    
    /**
     * Kick player with message
     */
    void kick(String reason);
    
    /**
     * Check if player has permission
     */
    boolean hasPermission(String permission);
    
    /**
     * Set player's game mode
     */
    void setGameMode(GameMode gameMode);
    
    /**
     * Get player's game mode
     */
    GameMode getGameMode();
    
    /**
     * Play sound for player
     */
    void playSound(String sound, float volume, float pitch);
    
    /**
     * Add potion effect
     */
    void addPotionEffect(String effect, int duration, int amplifier);
    
    /**
     * Remove potion effect
     */
    void removePotionEffect(String effect);
    
    /**
     * Set player's flight enabled
     */
    void setAllowFlight(boolean allow);
    
    /**
     * Check if player is flying
     */
    boolean isFlying();
    
    /**
     * Game mode enum
     */
    enum GameMode {
        SURVIVAL,
        CREATIVE,
        ADVENTURE,
        SPECTATOR
    }
}
