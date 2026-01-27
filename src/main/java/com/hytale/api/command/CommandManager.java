package com.hytale.api.command;

/**
 * Manages command registration and execution.
 */
public interface CommandManager {
    
    /**
     * Register a command
     */
    void registerCommand(Command command);
    
    /**
     * Unregister a command
     */
    void unregisterCommand(String name);
    
    /**
     * Execute a command
     */
    boolean executeCommand(CommandSender sender, String commandLine);
    
    /**
     * Get a registered command
     */
    Command getCommand(String name);
}
