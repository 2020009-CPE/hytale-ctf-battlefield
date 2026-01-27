package com.hytale.ctf.commands;

import com.hytale.ctf.arena.Arena;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.arena.Location;
import com.hytale.ctf.arena.Region;
import com.hytale.ctf.arena.SelectionWand;
import com.hytale.ctf.arena.Snapshot;
import com.hytale.ctf.storage.DataManager;
import com.hytale.ctf.util.MessageUtil;

import java.util.Objects;

/**
 * Map management commands for arena creation and configuration.
 * Handles selection, creation, snapshots, and arena maintenance.
 * 
 * <p>Commands: wand, pos1, pos2, define, snapshot, reset, delete, list, info</p>
 */
public class MapCommands {
    
    private final ArenaManager arenaManager;
    private final DataManager dataManager;
    private final SelectionWand wand;
    
    /**
     * Create new map commands handler
     * 
     * @param arenaManager the arena manager
     * @param dataManager the data manager
     */
    public MapCommands(ArenaManager arenaManager, DataManager dataManager) {
        this.arenaManager = Objects.requireNonNull(arenaManager);
        this.dataManager = Objects.requireNonNull(dataManager);
        this.wand = new SelectionWand();
    }
    
    /**
     * Execute a map command
     * 
     * @param sender the command sender
     * @param command the command name
     * @param args the command arguments
     * @return true if successful
     */
    public boolean execute(Object sender, String command, String[] args) {
        if (!hasPermission(sender, "ctf.admin.map")) {
            sendMessage(sender, MessageUtil.error("You don't have permission to use this command"));
            return false;
        }
        
        return switch (command) {
            case "wand" -> handleWand(sender);
            case "pos1" -> handlePos1(sender, args);
            case "pos2" -> handlePos2(sender, args);
            case "define" -> handleDefine(sender, args);
            case "snapshot" -> handleSnapshot(sender, args);
            case "reset" -> handleReset(sender, args);
            case "delete" -> handleDelete(sender, args);
            case "list" -> handleList(sender);
            case "info" -> handleInfo(sender, args);
            default -> {
                sendMessage(sender, MessageUtil.error("Unknown map command"));
                yield false;
            }
        };
    }
    
    /**
     * Give player the selection wand
     */
    private boolean handleWand(Object sender) {
        // TODO: Implement actual wand giving logic
        sendMessage(sender, MessageUtil.success("You received the selection wand!"));
        sendMessage(sender, MessageUtil.info("Left-click for pos1, right-click for pos2"));
        return true;
    }
    
    /**
     * Set first position of selection
     */
    private boolean handlePos1(Object sender, String[] args) {
        Location location = getPlayerLocation(sender);
        if (location == null) {
            sendMessage(sender, MessageUtil.error("Could not get your location"));
            return false;
        }
        
        wand.setPos1(getPlayerName(sender), location);
        sendMessage(sender, MessageUtil.success(String.format(
            "Position 1 set to: §f(%d, %d, %d)", 
            (int) location.x(), (int) location.y(), (int) location.z()
        )));
        return true;
    }
    
    /**
     * Set second position of selection
     */
    private boolean handlePos2(Object sender, String[] args) {
        Location location = getPlayerLocation(sender);
        if (location == null) {
            sendMessage(sender, MessageUtil.error("Could not get your location"));
            return false;
        }
        
        wand.setPos2(getPlayerName(sender), location);
        sendMessage(sender, MessageUtil.success(String.format(
            "Position 2 set to: §f(%d, %d, %d)", 
            (int) location.x(), (int) location.y(), (int) location.z()
        )));
        
        // Show selection info
        Location pos1 = wand.getPos1(getPlayerName(sender));
        if (pos1 != null) {
            int volume = calculateVolume(pos1, location);
            sendMessage(sender, MessageUtil.info(String.format(
                "Selection: §f%d blocks", volume
            )));
        }
        
        return true;
    }
    
    /**
     * Define a new arena from selection
     */
    private boolean handleDefine(Object sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf define <name>"));
            return false;
        }
        
        String arenaName = args[0];
        String playerName = getPlayerName(sender);
        
        Location pos1 = wand.getPos1(playerName);
        Location pos2 = wand.getPos2(playerName);
        
        if (pos1 == null || pos2 == null) {
            sendMessage(sender, MessageUtil.error("You must select both positions first!"));
            sendMessage(sender, MessageUtil.info("Use /ctf pos1 and /ctf pos2 or the wand"));
            return false;
        }
        
        if (arenaManager.hasArena(arenaName)) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' already exists!"));
            return false;
        }
        
        // Create temporary spawn points (to be set later)
        Location tempSpawn = new Location(pos1.world(), 
            (pos1.x() + pos2.x()) / 2, 
            (pos1.y() + pos2.y()) / 2, 
            (pos1.z() + pos2.z()) / 2);
        
        Region region = new Region(pos1, pos2);
        Arena arena = new Arena(
            arenaName, region,
            tempSpawn, tempSpawn, // Red/Blue spawns
            tempSpawn, tempSpawn, // Red/Blue flags
            tempSpawn              // Lobby
        );
        
        if (arenaManager.createArena(arena)) {
            dataManager.saveArena(arena);
            sendMessage(sender, MessageUtil.arenaCreated(arenaName));
            sendMessage(sender, MessageUtil.info("Don't forget to set spawns and flags!"));
            return true;
        } else {
            sendMessage(sender, MessageUtil.error("Failed to create arena"));
            return false;
        }
    }
    
    /**
     * Create a snapshot of an arena
     */
    private boolean handleSnapshot(Object sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf snapshot <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        sendMessage(sender, MessageUtil.info("Creating snapshot... This may take a moment."));
        
        // Create snapshot (simplified - would need actual block data provider)
        Snapshot snapshot = Snapshot.empty(arenaName);
        dataManager.saveSnapshot(snapshot);
        
        sendMessage(sender, MessageUtil.snapshotSaved(arenaName));
        return true;
    }
    
    /**
     * Reset an arena to its snapshot
     */
    private boolean handleReset(Object sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf reset <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        Snapshot snapshot = dataManager.loadSnapshot(arenaName);
        if (snapshot == null) {
            sendMessage(sender, MessageUtil.error("No snapshot found for '" + arenaName + "'!"));
            sendMessage(sender, MessageUtil.info("Create one with /ctf snapshot " + arenaName));
            return false;
        }
        
        sendMessage(sender, MessageUtil.info("Resetting arena... This may take a moment."));
        
        // Restore snapshot (simplified - would need actual block setter)
        snapshot.restore((loc, data) -> {
            // TODO: Actually set blocks
        });
        
        sendMessage(sender, MessageUtil.success("Arena '" + arenaName + "' has been reset!"));
        return true;
    }
    
    /**
     * Delete an arena
     */
    private boolean handleDelete(Object sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf delete <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        
        if (!arenaManager.hasArena(arenaName)) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        arenaManager.deleteArena(arenaName);
        dataManager.deleteArena(arenaName);
        dataManager.deleteSnapshot(arenaName);
        
        sendMessage(sender, MessageUtil.success("Arena '" + arenaName + "' deleted!"));
        return true;
    }
    
    /**
     * List all arenas
     */
    private boolean handleList(Object sender) {
        var arenas = arenaManager.listArenas();
        
        if (arenas.isEmpty()) {
            sendMessage(sender, MessageUtil.info("No arenas found!"));
            sendMessage(sender, MessageUtil.info("Create one with /ctf define <name>"));
            return true;
        }
        
        sendMessage(sender, MessageUtil.format("§6§lArenas (" + arenas.size() + "):"));
        for (String name : arenas) {
            Arena arena = arenaManager.getArena(name);
            boolean hasSnapshot = dataManager.loadSnapshot(name) != null;
            String status = hasSnapshot ? "§a✓" : "§c✗";
            sendMessage(sender, String.format("  %s §f%s", status, name));
        }
        sendMessage(sender, "");
        sendMessage(sender, MessageUtil.info("§a✓ = Has snapshot  §c✗ = No snapshot"));
        
        return true;
    }
    
    /**
     * Show arena information
     */
    private boolean handleInfo(Object sender, String[] args) {
        if (args.length == 0) {
            sendMessage(sender, MessageUtil.error("Usage: /ctf info <arena>"));
            return false;
        }
        
        String arenaName = args[0];
        Arena arena = arenaManager.getArena(arenaName);
        
        if (arena == null) {
            sendMessage(sender, MessageUtil.error("Arena '" + arenaName + "' not found!"));
            return false;
        }
        
        sendMessage(sender, MessageUtil.format("§6§lArena Info: §f" + arenaName));
        sendMessage(sender, String.format("  §7World: §f%s", arena.world()));
        sendMessage(sender, String.format("  §7Region: §f(%d, %d, %d) to (%d, %d, %d)",
            (int) arena.region().getMin().x(),
            (int) arena.region().getMin().y(),
            (int) arena.region().getMin().z(),
            (int) arena.region().getMax().x(),
            (int) arena.region().getMax().y(),
            (int) arena.region().getMax().z()
        ));
        sendMessage(sender, String.format("  §7Red Spawn: §f(%d, %d, %d)",
            (int) arena.redSpawn().x(),
            (int) arena.redSpawn().y(),
            (int) arena.redSpawn().z()
        ));
        sendMessage(sender, String.format("  §7Blue Spawn: §f(%d, %d, %d)",
            (int) arena.blueSpawn().x(),
            (int) arena.blueSpawn().y(),
            (int) arena.blueSpawn().z()
        ));
        
        boolean hasSnapshot = dataManager.loadSnapshot(arenaName) != null;
        sendMessage(sender, String.format("  §7Snapshot: %s", hasSnapshot ? "§aYes" : "§cNo"));
        
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
    
    private String getPlayerName(Object sender) {
        // TODO: Get actual player name
        return "Player";
    }
    
    private Location getPlayerLocation(Object sender) {
        // TODO: Get actual player location
        return new Location("world", 0, 64, 0);
    }
    
    private int calculateVolume(Location pos1, Location pos2) {
        int dx = (int) Math.abs(pos2.x() - pos1.x()) + 1;
        int dy = (int) Math.abs(pos2.y() - pos1.y()) + 1;
        int dz = (int) Math.abs(pos2.z() - pos1.z()) + 1;
        return dx * dy * dz;
    }
}
