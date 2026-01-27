package com.hytale.api.server;

import com.hytale.api.command.CommandManager;
import com.hytale.api.event.EventManager;
import com.hytale.api.player.HytalePlayer;
import com.hytale.api.world.HytaleWorld;

import java.util.List;
import java.util.Optional;

/**
 * Represents the Hytale server instance.
 * Provides access to worlds, players, and core server functionality.
 */
public interface HytaleServer {
    
    /**
     * Get all online players
     */
    List<HytalePlayer> getOnlinePlayers();
    
    /**
     * Get a player by name
     */
    Optional<HytalePlayer> getPlayer(String name);
    
    /**
     * Get all loaded worlds
     */
    List<HytaleWorld> getWorlds();
    
    /**
     * Get a world by name
     */
    Optional<HytaleWorld> getWorld(String name);
    
    /**
     * Get the default world
     */
    HytaleWorld getDefaultWorld();
    
    /**
     * Get the command manager
     */
    CommandManager getCommandManager();
    
    /**
     * Get the event manager
     */
    EventManager getEventManager();
    
    /**
     * Broadcast a message to all players
     */
    void broadcast(String message);
    
    /**
     * Get the server's tick rate (TPS)
     */
    int getTicksPerSecond();
    
    /**
     * Schedule a task to run later
     */
    void scheduleTask(Runnable task, long delayTicks);
    
    /**
     * Schedule a repeating task
     */
    void scheduleRepeatingTask(Runnable task, long delayTicks, long periodTicks);
}
