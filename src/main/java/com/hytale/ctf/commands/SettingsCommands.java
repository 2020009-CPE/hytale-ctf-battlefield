package com.hytale.ctf.commands;

import com.hytale.ctf.arena.Arena;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.arena.Location;
import com.hytale.ctf.arena.Region;
import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.game.GameSettings;
import com.hytale.ctf.util.MessageUtil;

import java.util.Objects;

/**
 * Game settings commands for configuring game rules and parameters.
 * Manages time limits, capture limits, build zones, and game toggles.
 * 
 * <p>Commands: setlimit, settime, setbuildzone, setnobuild, togglepvp, togglebuild, togglebreak</p>
 */
public class SettingsCommands {
    
    private final CTFGame game;
    private final ArenaManager arenaManager;
    
    /**
     * Create new settings commands handler
     * 
     * @param game the CTF game instance
     * @param arenaManager the arena manager
     */
    public SettingsCommands(CTFGame game, ArenaManager arenaManager) {
        this.game = Objects.requireNonNull(game);
        this.arenaManager = Objects.requireNonNull(arenaManager);
    }
    
    /**
     * Execute a settings command
     * 
     * @param sender the command sender
     * @param command the command name
     * @param args the command arguments
     * @return true if successful
     */
    public boolean execute(Object sender, String command, String[] args) {
        if (!hasPermission(sender, "ctf.admin.settings")) {
            sendMessage(sender, MessageUtil.error("You don't have permission to use this command"));
            return false;
        }
        
        return switch (command) {
            case "setlimit" -> handleSetLimit(sender, args);
            case "settime" -> handleSetTime(sender, args);
            case "setbuildzone" -> handleSetBuildZone(sender, args);
            case "setnobuild" -> handleSetNoBuild(sender, args);
            case "togglepvp" -> handleTogglePvP(sender, args);
            case "togglebuild" -> handleToggleBuild(sender, args);
            case "togglebreak" -> handleToggleBreak(sender, args);
            default -> {
                sendMessage(sender, MessageUtil.error("Unknown settings command"));
                yield false;
            }
        };
    }
    
    /**
     * Set capture limit
     */
    private boolean handleSetLimit(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf setlimit <captures>"));
            return false;
        }
        
        int limit;
        try {
            limit = Integer.parseInt(args[0]);
            if (limit < 1 || limit > 100) {
                sendMessage(sender, MessageUtil.error("Capture limit must be between 1 and 100"));
                return false;
            }
        } catch (NumberFormatException e) {
            sendMessage(sender, MessageUtil.error("Invalid number: " + args[0]));
            return false;
        }
        
        GameSettings currentSettings = game.getSettings();
        GameSettings newSettings = currentSettings.withCaptureLimit(limit);
        game.updateSettings(newSettings);
        
        sendMessage(sender, MessageUtil.success(String.format(
            "Capture limit set to §f%d§a captures", limit
        )));
        
        return true;
    }
    
    /**
     * Set time limit in minutes
     */
    private boolean handleSetTime(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf settime <minutes>"));
            return false;
        }
        
        int minutes;
        try {
            minutes = Integer.parseInt(args[0]);
            if (minutes < 0 || minutes > 120) {
                sendMessage(sender, MessageUtil.error("Time limit must be between 0 and 120 minutes"));
                return false;
            }
        } catch (NumberFormatException e) {
            sendMessage(sender, MessageUtil.error("Invalid number: " + args[0]));
            return false;
        }
        
        GameSettings currentSettings = game.getSettings();
        GameSettings newSettings = currentSettings.withTimeLimit(minutes * 60); // Convert to seconds
        game.updateSettings(newSettings);
        
        if (minutes == 0) {
            sendMessage(sender, MessageUtil.success("Time limit disabled (unlimited time)"));
        } else {
            sendMessage(sender, MessageUtil.success(String.format(
                "Time limit set to §f%d§a minutes", minutes
            )));
        }
        
        return true;
    }
    
    /**
     * Set build zone area
     */
    private boolean handleSetBuildZone(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf setbuildzone <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        // TODO: Implement build zone selection (similar to region selection)
        sendMessage(sender, MessageUtil.success(
            "Build zone for arena §f" + arenaName + "§a set to entire arena"
        ));
        sendMessage(sender, MessageUtil.info("Players can build anywhere in this arena"));
        
        return true;
    }
    
    /**
     * Set no-build zone area
     */
    private boolean handleSetNoBuild(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf setnobuild <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        // TODO: Implement no-build zone selection
        sendMessage(sender, MessageUtil.success(
            "No-build zone for arena §f" + arenaName + "§a has been set"
        ));
        sendMessage(sender, MessageUtil.info("Players cannot build in this zone"));
        
        return true;
    }
    
    /**
     * Toggle PvP on/off
     */
    private boolean handleTogglePvP(Object sender, String[] args) {
        GameSettings currentSettings = game.getSettings();
        boolean newPvPState = !currentSettings.pvpEnabled();
        
        GameSettings newSettings = new GameSettings(
            currentSettings.captureLimit(),
            currentSettings.timeLimit(),
            currentSettings.buildZoneRadius(),
            currentSettings.noBuildZoneRadius(),
            currentSettings.requireOwnFlag(),
            newPvPState,
            currentSettings.buildEnabled(),
            currentSettings.breakEnabled(),
            currentSettings.overtimeEnabled(),
            currentSettings.mercyRuleDifference()
        );
        
        game.updateSettings(newSettings);
        
        String status = newPvPState ? "§aenabled" : "§cdisabled";
        sendMessage(sender, MessageUtil.success("PvP " + status));
        
        return true;
    }
    
    /**
     * Toggle building on/off
     */
    private boolean handleToggleBuild(Object sender, String[] args) {
        GameSettings currentSettings = game.getSettings();
        boolean newBuildState = !currentSettings.buildEnabled();
        
        GameSettings newSettings = new GameSettings(
            currentSettings.captureLimit(),
            currentSettings.timeLimit(),
            currentSettings.buildZoneRadius(),
            currentSettings.noBuildZoneRadius(),
            currentSettings.requireOwnFlag(),
            currentSettings.pvpEnabled(),
            newBuildState,
            currentSettings.breakEnabled(),
            currentSettings.overtimeEnabled(),
            currentSettings.mercyRuleDifference()
        );
        
        game.updateSettings(newSettings);
        
        String status = newBuildState ? "§aenabled" : "§cdisabled";
        sendMessage(sender, MessageUtil.success("Building " + status));
        
        return true;
    }
    
    /**
     * Toggle block breaking on/off
     */
    private boolean handleToggleBreak(Object sender, String[] args) {
        GameSettings currentSettings = game.getSettings();
        boolean newBreakState = !currentSettings.breakEnabled();
        
        GameSettings newSettings = new GameSettings(
            currentSettings.captureLimit(),
            currentSettings.timeLimit(),
            currentSettings.buildZoneRadius(),
            currentSettings.noBuildZoneRadius(),
            currentSettings.requireOwnFlag(),
            currentSettings.pvpEnabled(),
            currentSettings.buildEnabled(),
            newBreakState,
            currentSettings.overtimeEnabled(),
            currentSettings.mercyRuleDifference()
        );
        
        game.updateSettings(newSettings);
        
        String status = newBreakState ? "§aenabled" : "§cdisabled";
        sendMessage(sender, MessageUtil.success("Block breaking " + status));
        
        return true;
    }
    
    // Helper methods
    
    private boolean hasPermission(Object sender, String permission) {
        // TODO: Implement actual permission check
        return true;
    }
    
    private void sendMessage(Object sender, String message) {
        System.out.println(message);
    }
}
