package com.hytale.ctf.commands;

import com.hytale.ctf.arena.Arena;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.arena.Location;
import com.hytale.ctf.game.CTFTeam;
import com.hytale.ctf.team.TeamManager;
import com.hytale.ctf.util.MessageUtil;

import java.util.Objects;

/**
 * Team and spawn setup commands for configuring team locations.
 * Manages spawn points, lobby, portals, and hub configuration.
 * 
 * <p>Commands: setspawn, setlobby, setportal, sethub</p>
 */
public class TeamCommands {
    
    private final TeamManager teamManager;
    private final ArenaManager arenaManager;
    
    /**
     * Create new team commands handler
     * 
     * @param teamManager the team manager
     * @param arenaManager the arena manager
     */
    public TeamCommands(TeamManager teamManager, ArenaManager arenaManager) {
        this.teamManager = Objects.requireNonNull(teamManager);
        this.arenaManager = Objects.requireNonNull(arenaManager);
    }
    
    /**
     * Execute a team command
     * 
     * @param sender the command sender
     * @param command the command name
     * @param args the command arguments
     * @return true if successful
     */
    public boolean execute(Object sender, String command, String[] args) {
        if (!hasPermission(sender, "ctf.admin.team")) {
            sendMessage(sender, MessageUtil.error("You don't have permission to use this command"));
            return false;
        }
        
        return switch (command) {
            case "setspawn" -> handleSetSpawn(sender, args);
            case "setlobby" -> handleSetLobby(sender, args);
            case "setportal" -> handleSetPortal(sender, args);
            case "sethub" -> handleSetHub(sender, args);
            default -> {
                sendMessage(sender, MessageUtil.error("Unknown team command"));
                yield false;
            }
        };
    }
    
    /**
     * Set team spawn point
     */
    private boolean handleSetSpawn(Object sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf setspawn <arena> <red|blue>"));
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
            sendMessage(sender, MessageUtil.error("Spawn must be placed inside the arena!"));
            return false;
        }
        
        // Update arena with new spawn location
        Arena updatedArena = switch (team) {
            case RED -> new Arena(
                arena.name(), arena.region(),
                location, arena.blueSpawn(),
                arena.redFlag(), arena.blueFlag(),
                arena.lobby()
            );
            case BLUE -> new Arena(
                arena.name(), arena.region(),
                arena.redSpawn(), location,
                arena.redFlag(), arena.blueFlag(),
                arena.lobby()
            );
        };
        
        arenaManager.updateArena(updatedArena);
        
        String teamColor = team == CTFTeam.RED ? "§c" : "§9";
        sendMessage(sender, MessageUtil.success(String.format(
            "%s%s TEAM§a spawn set at: §f(%d, %d, %d)",
            teamColor, team.name(),
            (int) location.x(), (int) location.y(), (int) location.z()
        )));
        
        return true;
    }
    
    /**
     * Set lobby spawn point
     */
    private boolean handleSetLobby(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf setlobby <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        Location location = getPlayerLocation(sender);
        if (location == null) {
            sendMessage(sender, MessageUtil.error("Could not get your location"));
            return false;
        }
        
        // Update arena with new lobby location
        Arena updatedArena = new Arena(
            arena.name(), arena.region(),
            arena.redSpawn(), arena.blueSpawn(),
            arena.redFlag(), arena.blueFlag(),
            location
        );
        
        arenaManager.updateArena(updatedArena);
        
        sendMessage(sender, MessageUtil.success(String.format(
            "Lobby spawn set at: §f(%d, %d, %d)",
            (int) location.x(), (int) location.y(), (int) location.z()
        )));
        
        return true;
    }
    
    /**
     * Set portal location for joining arena
     */
    private boolean handleSetPortal(Object sender, String[] args) {
        if (args.length < 1) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf setportal <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        Location location = getPlayerLocation(sender);
        if (location == null) {
            sendMessage(sender, MessageUtil.error("Could not get your location"));
            return false;
        }
        
        // TODO: Store portal location in a separate configuration
        sendMessage(sender, MessageUtil.success(String.format(
            "Portal for arena §f%s§a set at: §f(%d, %d, %d)",
            arenaName,
            (int) location.x(), (int) location.y(), (int) location.z()
        )));
        sendMessage(sender, MessageUtil.info("Players can now use this portal to join the arena"));
        
        return true;
    }
    
    /**
     * Set hub spawn point
     */
    private boolean handleSetHub(Object sender, String[] args) {
        Location location = getPlayerLocation(sender);
        if (location == null) {
            sendMessage(sender, MessageUtil.error("Could not get your location"));
            return false;
        }
        
        // TODO: Store hub location in global configuration
        sendMessage(sender, MessageUtil.success(String.format(
            "Hub spawn set at: §f(%d, %d, %d)",
            (int) location.x(), (int) location.y(), (int) location.z()
        )));
        sendMessage(sender, MessageUtil.info("Players will spawn here when leaving games"));
        
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
