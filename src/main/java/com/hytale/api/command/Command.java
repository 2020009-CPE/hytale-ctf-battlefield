package com.hytale.api.command;

/**
 * Interface for executable commands.
 */
public interface Command {
    
    /**
     * Execute the command
     * 
     * @param sender The command sender
     * @param args Command arguments
     * @return true if command executed successfully
     */
    boolean execute(CommandSender sender, String[] args);
    
    /**
     * Get command name
     */
    String getName();
    
    /**
     * Get command description
     */
    String getDescription();
    
    /**
     * Get command usage
     */
    String getUsage();
    
    /**
     * Get required permission
     */
    default String getPermission() {
        return null;
    }
}
