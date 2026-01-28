package com.hytale.ctf.commands;

import com.hytale.ctf.arena.Arena;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.arena.Location;
import com.hytale.ctf.flag.FlagManager;
import com.hytale.ctf.game.CTFTeam;
import com.hytale.ctf.util.MessageUtil;

import java.util.Objects;

/**
 * Flag setup commands for configuring flag locations and behavior.
 * Manages flag placement, removal, and configuration.
 * 
 * <p>Commands: setflag, removeflag, moveflag, flagreturn</p>
 */
public class FlagCommands {
    
    private final FlagManager flagManager;
    private final ArenaManager arenaManager;
    
    /**
     * Create new flag commands handler
     * 
     * @param flagManager the flag manager
     * @param arenaManager the arena manager
     */
    public FlagCommands(FlagManager flagManager, ArenaManager arenaManager) {
        this.flagManager = Objects.requireNonNull(flagManager);
        this.arenaManager = Objects.requireNonNull(arenaManager);
    }
    
    /**
     * Execute a flag command
     * 
     * @param sender the command sender
     * @param command the command name
     * @param args the command arguments
     * @return true if successful
     */
    public boolean execute(Object sender, String command, String[] args) {
        if (!hasPermission(sender, "ctf.admin.flag")) {
            sendMessage(sender, MessageUtil.error("You don't have permission to use this command"));
            return false;
        }
        
        return switch (command) {
            case "setflag" -> handleSetFlag(sender, args);
            case "removeflag" -> handleRemoveFlag(sender, args);
            case "moveflag" -> handleMoveFlag(sender, args);
            case "flagreturn" -> handleFlagReturn(sender, args);
            default -> {
                sendMessage(sender, MessageUtil.error("Unknown flag command"));
                yield false;
            }
        };
    }
    
    /**
     * Set flag location for a team
     */
    private boolean handleSetFlag(Object sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf setflag <arena> <red|blue>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        CTFTeam team = parseTeam(args[1]);
        if (team == null) {
            sendMessage(sender, MessageUtil.error("Invalid team! Use 'red' or 'blue'"));
            return false;
        }
        
        Location location = getPlayerLocation(sender);
        if (location == null) {
            sendMessage(sender, MessageUtil.error("Could not get your location"));
            return false;
        }
        
        if (!arena.contains(location)) {
            sendMessage(sender, MessageUtil.error("Flag must be placed inside the arena!"));
            return false;
        }
        
        // Update arena with new flag location
        Arena updatedArena = switch (team) {
            case RED -> new Arena(
                arena.name(), arena.region(),
                arena.redSpawn(), arena.blueSpawn(),
                location, arena.blueFlag(),
                arena.lobby()
            );
            case BLUE -> new Arena(
                arena.name(), arena.region(),
                arena.redSpawn(), arena.blueSpawn(),
                arena.redFlag(), location,
                arena.lobby()
            );
        };
        
        arenaManager.updateArena(updatedArena);
        
        String teamColor = team == CTFTeam.RED ? "§c" : "§9";
        sendMessage(sender, MessageUtil.success(String.format(
            "%s%s TEAM§a flag set at: §f(%d, %d, %d)",
            teamColor, team.name(),
            (int) location.x(), (int) location.y(), (int) location.z()
        )));
        
        return true;
    }
    
    /**
     * Remove flag from a team
     */
    private boolean handleRemoveFlag(Object sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf removeflag <arena> <red|blue>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        CTFTeam team = parseTeam(args[1]);
        if (team == null) {
            sendMessage(sender, MessageUtil.error("Invalid team! Use 'red' or 'blue'"));
            return false;
        }
        
        // Reset flag location to center of arena
        Location centerLoc = new Location(
            arena.world(),
            (arena.region().getMin().x() + arena.region().getMax().x()) / 2,
            (arena.region().getMin().y() + arena.region().getMax().y()) / 2,
            (arena.region().getMin().z() + arena.region().getMax().z()) / 2
        );
        
        Arena updatedArena = switch (team) {
            case RED -> new Arena(
                arena.name(), arena.region(),
                arena.redSpawn(), arena.blueSpawn(),
                centerLoc, arena.blueFlag(),
                arena.lobby()
            );
            case BLUE -> new Arena(
                arena.name(), arena.region(),
                arena.redSpawn(), arena.blueSpawn(),
                arena.redFlag(), centerLoc,
                arena.lobby()
            );
        };
        
        arenaManager.updateArena(updatedArena);
        
        String teamColor = team == CTFTeam.RED ? "§c" : "§9";
        sendMessage(sender, MessageUtil.success(String.format(
            "%s%s TEAM§a flag removed!", teamColor, team.name()
        )));
        
        return true;
    }
    
    /**
     * Move flag to player's current location
     */
    private boolean handleMoveFlag(Object sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf moveflag <arena> <red|blue>"));
            return false;
        }
        
        // This is essentially the same as setflag
        return handleSetFlag(sender, args);
    }
    
    /**
     * Set flag auto-return time
     */
    private boolean handleFlagReturn(Object sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf flagreturn <arena> <seconds>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        int seconds;
        try {
            seconds = Integer.parseInt(args[1]);
            if (seconds < 0 || seconds > 300) {
                sendMessage(sender, MessageUtil.error("Return time must be between 0 and 300 seconds"));
                return false;
            }
        } catch (NumberFormatException e) {
            sendMessage(sender, MessageUtil.error("Invalid number: " + args[1]));
            return false;
        }
        
        // TODO: Store flag return time in game settings
        sendMessage(sender, MessageUtil.success(String.format(
            "Flag auto-return time set to §f%d§a seconds for arena §f%s",
            seconds, arenaName
        )));
        
        if (seconds == 0) {
            sendMessage(sender, MessageUtil.info("Flags will not auto-return"));
        }
        
        return true;
    }
    
    // Helper methods
    
    private CTFTeam parseTeam(String teamStr) {
        return switch (teamStr.toLowerCase()) {
            case "red", "r" -> CTFTeam.RED;
            case "blue", "b" -> CTFTeam.BLUE;
            default -> null;
        };
    }
    
    private boolean hasPermission(Object sender, String permission) {
        // TODO: Implement actual permission check
        return true;
    }
    
    private void sendMessage(Object sender, String message) {
        System.out.println(message);
    }
    
    private Location getPlayerLocation(Object sender) {
        // TODO: Get actual player location
        return new Location("world", 0, 64, 0);
    }
}
