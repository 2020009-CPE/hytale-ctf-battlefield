package com.hytale.api;

import com.hytale.api.command.CommandManager;
import com.hytale.api.event.EventManager;
import com.hytale.api.player.HytalePlayer;
import com.hytale.api.server.HytaleServer;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

/**
 * Main Hytale Mod interface.
 * This is the base class all Hytale mods must extend.
 * 
 * Based on typical modding API patterns for voxel games.
 */
public abstract class HytaleMod {
    
    private HytaleServer server;
    private Logger logger;
    private File dataFolder;
    private boolean enabled = false;
    
    /**
     * Called when the mod is loaded
     */
    public abstract void onLoad();
    
    /**
     * Called when the mod is enabled
     */
    public abstract void onEnable();
    
    /**
     * Called when the mod is disabled
     */
    public abstract void onDisable();
    
    /**
     * Get the server instance
     */
    public final HytaleServer getServer() {
        return server;
    }
    
    /**
     * Get the mod's logger
     */
    public final Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(getClass().getSimpleName());
        }
        return logger;
    }
    
    /**
     * Get the mod's data folder
     */
    public final File getDataFolder() {
        if (dataFolder == null) {
            dataFolder = new File("mods/" + getClass().getSimpleName());
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
        }
        return dataFolder;
    }
    
    /**
     * Check if the mod is enabled
     */
    public final boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Get the command manager
     */
    public final CommandManager getCommandManager() {
        return server.getCommandManager();
    }
    
    /**
     * Get the event manager
     */
    public final EventManager getEventManager() {
        return server.getEventManager();
    }
    
    /**
     * Get all online players
     */
    public final List<HytalePlayer> getOnlinePlayers() {
        return server.getOnlinePlayers();
    }
    
    /**
     * Internal - set server instance
     */
    public final void setServer(HytaleServer server) {
        this.server = server;
    }
    
    /**
     * Internal - set enabled state
     */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
