package com.hytale.ctf.commands;

import com.hytale.ctf.arena.Arena;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.game.GameState;
import com.hytale.ctf.util.MessageUtil;

import java.util.Objects;

/**
 * Game control commands for managing game flow.
 * Handles starting, stopping, pausing, and restarting games.
 * 
 * <p>Commands: start, stop, pause, resume, restart</p>
 */
public class GameCommands {
    
    private final CTFGame game;
    private final ArenaManager arenaManager;
    
    /**
     * Create new game commands handler
     * 
     * @param game the CTF game instance
     * @param arenaManager the arena manager
     */
    public GameCommands(CTFGame game, ArenaManager arenaManager) {
        this.game = Objects.requireNonNull(game);
        this.arenaManager = Objects.requireNonNull(arenaManager);
    }
    
    /**
     * Execute a game control command
     * 
     * @param sender the command sender
     * @param command the command name
     * @param args the command arguments
     * @return true if successful
     */
    public boolean execute(Object sender, String command, String[] args) {
        if (!hasPermission(sender, "ctf.admin.game")) {
            sendMessage(sender, MessageUtil.error("You don't have permission to use this command"));
            return false;
        }
        
        return switch (command) {
            case "start" -> handleStart(sender, args);
            case "stop" -> handleStop(sender, args);
            case "pause" -> handlePause(sender, args);
            case "resume" -> handleResume(sender, args);
            case "restart" -> handleRestart(sender, args);
            default -> {
                sendMessage(sender, MessageUtil.error("Unknown game command"));
                yield false;
            }
        };
    }
    
    /**
     * Start a new game
     */
    private boolean handleStart(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf start <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        if (!arena.isValid()) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' is not fully configured!"));
            sendMessage(sender, MessageUtil.info("Use /ctf info " + arenaName + " to check setup"));
            return false;
        }
        
        try {
            game.start(arena);
            sendMessage(sender, MessageUtil.success("Starting game in arena §f" + arenaName));
            broadcastMessage(MessageUtil.gameStarting(10));
            return true;
        } catch (IllegalStateException e) {
            sendMessage(sender, MessageUtil.error("A game is already running!"));
            sendMessage(sender, MessageUtil.info("Use /ctf stop to stop the current game first"));
            return false;
        }
    }
    
    /**
     * Stop the current game
     */
    private boolean handleStop(Object sender, String[] args) {
        try {
            game.stop();
            sendMessage(sender, MessageUtil.success("Game stopped"));
            broadcastMessage(MessageUtil.format("§cGame has been stopped by an administrator"));
            return true;
        } catch (Exception e) {
            sendMessage(sender, MessageUtil.error("No game is currently running!"));
            return false;
        }
    }
    
    /**
     * Pause the current game
     */
    private boolean handlePause(Object sender, String[] args) {
        try {
            game.pause();
            sendMessage(sender, MessageUtil.success("Game paused"));
            broadcastMessage(MessageUtil.format("§eGame has been paused"));
            return true;
        } catch (IllegalStateException e) {
            sendMessage(sender, MessageUtil.error(e.getMessage()));
            return false;
        }
    }
    
    /**
     * Resume a paused game
     */
    private boolean handleResume(Object sender, String[] args) {
        try {
            game.resume();
            sendMessage(sender, MessageUtil.success("Game resumed"));
            broadcastMessage(MessageUtil.format("§aGame has been resumed!"));
            return true;
        } catch (IllegalStateException e) {
            sendMessage(sender, MessageUtil.error(e.getMessage()));
            return false;
        }
    }
    
    /**
     * Restart the current game
     */
    private boolean handleRestart(Object sender, String[] args) {
        try {
            game.restart();
            sendMessage(sender, MessageUtil.success("Game restarted"));
            broadcastMessage(MessageUtil.format("§6Game is restarting..."));
            return true;
        } catch (IllegalStateException e) {
            sendMessage(sender, MessageUtil.error(e.getMessage()));
            return false;
        }
    }
    
    // Helper methods
    
    private boolean hasPermission(Object sender, String permission) {
        // TODO: Implement actual permission check
        return true;
    }
    
    private void sendMessage(Object sender, String message) {
        System.out.println(message);
    }
    
    private void broadcastMessage(String message) {
        // TODO: Broadcast to all players in game
        System.out.println("[BROADCAST] " + message);
    }
}
