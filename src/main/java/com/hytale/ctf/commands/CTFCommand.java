package com.hytale.ctf.commands;

import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.flag.FlagManager;
import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.player.PlayerManager;
import com.hytale.ctf.storage.DataManager;
import com.hytale.ctf.team.TeamManager;
import com.hytale.ctf.util.MessageUtil;

import java.util.Arrays;
import java.util.Objects;

/**
 * Main CTF command router using Java 25 pattern matching.
 * Delegates to specialized command handlers based on subcommand.
 * 
 * <p>Usage: /ctf {@code <subcommand> <args...>}</p>
 * 
 * <p>This router uses Java 25 pattern matching for cleaner argument parsing
 * and type-safe command dispatching.</p>
 */
public class CTFCommand {
    
    private final ArenaManager arenaManager;
    private final FlagManager flagManager;
    private final PlayerManager playerManager;
    private final TeamManager teamManager;
    private final CTFGame game;
    private final DataManager dataManager;
    
    private final MapCommands mapCommands;
    private final FlagCommands flagCommands;
    private final TeamCommands teamCommands;
    private final GameCommands gameCommands;
    private final SettingsCommands settingsCommands;
    private final PlayerCommands playerCommands;
    
    /**
     * Create a new CTF command router
     * 
     * @param arenaManager the arena manager
     * @param flagManager the flag manager
     * @param playerManager the player manager
     * @param teamManager the team manager
     * @param game the CTF game instance
     * @param dataManager the data manager
     */
    public CTFCommand(
        ArenaManager arenaManager,
        FlagManager flagManager,
        PlayerManager playerManager,
        TeamManager teamManager,
        CTFGame game,
        DataManager dataManager
    ) {
        this.arenaManager = Objects.requireNonNull(arenaManager);
        this.flagManager = Objects.requireNonNull(flagManager);
        this.playerManager = Objects.requireNonNull(playerManager);
        this.teamManager = Objects.requireNonNull(teamManager);
        this.game = Objects.requireNonNull(game);
        this.dataManager = Objects.requireNonNull(dataManager);
        
        // Initialize command handlers
        this.mapCommands = new MapCommands(arenaManager, dataManager);
        this.flagCommands = new FlagCommands(flagManager, arenaManager);
        this.teamCommands = new TeamCommands(teamManager, arenaManager);
        this.gameCommands = new GameCommands(game, arenaManager);
        this.settingsCommands = new SettingsCommands(game, arenaManager);
        this.playerCommands = new PlayerCommands(playerManager, teamManager, game, dataManager);
    }
    
    /**
     * Execute a CTF command
     * 
     * @param sender the command sender
     * @param args the command arguments
     * @return true if command executed successfully
     */
    public boolean execute(Object sender, String[] args) {
        Objects.requireNonNull(sender, "Sender cannot be null");
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        
        // Pattern matching for command routing (Java 25)
        return switch (subcommand) {
            // Map management commands
            case "wand", "pos1", "pos2", "define", "snapshot", "reset", "delete", "list", "info" ->
                mapCommands.execute(sender, subcommand, subArgs);
            
            // Flag setup commands
            case "setflag", "removeflag", "moveflag", "flagreturn" ->
                flagCommands.execute(sender, subcommand, subArgs);
            
            // Team/spawn setup commands
            case "setspawn", "setlobby", "setportal", "sethub" ->
                teamCommands.execute(sender, subcommand, subArgs);
            
            // Game control commands
            case "start", "stop", "pause", "resume", "restart" ->
                gameCommands.execute(sender, subcommand, subArgs);
            
            // Settings commands
            case "setlimit", "settime", "setbuildzone", "setnobuild", 
                 "togglepvp", "togglebuild", "togglebreak" ->
                settingsCommands.execute(sender, subcommand, subArgs);
            
            // Player commands
            case "join", "leave", "team", "stats", "top" ->
                playerCommands.execute(sender, subcommand, subArgs);
            
            // Help command
            case "help" -> {
                sendHelp(sender);
                yield true;
            }
            
            // Unknown command
            default -> {
                sendMessage(sender, MessageUtil.error("Unknown command: " + subcommand));
                sendMessage(sender, MessageUtil.info("Use /ctf help for a list of commands"));
                yield false;
            }
        };
    }
    
    /**
     * Send help message showing all available commands
     * 
     * @param sender the command sender
     */
    private void sendHelp(Object sender) {
        sendMessage(sender, MessageUtil.format("§6§l=== CTF Commands ==="));
        sendMessage(sender, "");
        
        sendMessage(sender, "§e§lMap Management:");
        sendMessage(sender, "  §7/ctf wand §f- Get the selection wand");
        sendMessage(sender, "  §7/ctf pos1 §f- Set first position");
        sendMessage(sender, "  §7/ctf pos2 §f- Set second position");
        sendMessage(sender, "  §7/ctf define <name> §f- Create arena from selection");
        sendMessage(sender, "  §7/ctf snapshot <arena> §f- Save arena snapshot");
        sendMessage(sender, "  §7/ctf reset <arena> §f- Reset arena to snapshot");
        sendMessage(sender, "  §7/ctf delete <arena> §f- Delete an arena");
        sendMessage(sender, "  §7/ctf list §f- List all arenas");
        sendMessage(sender, "  §7/ctf info <arena> §f- Show arena information");
        sendMessage(sender, "");
        
        sendMessage(sender, "§e§lFlag Setup:");
        sendMessage(sender, "  §7/ctf setflag <arena> <red|blue> §f- Set flag location");
        sendMessage(sender, "  §7/ctf removeflag <arena> <red|blue> §f- Remove flag");
        sendMessage(sender, "  §7/ctf moveflag <arena> <red|blue> §f- Move flag to your location");
        sendMessage(sender, "  §7/ctf flagreturn <arena> §f- Set flag return time");
        sendMessage(sender, "");
        
        sendMessage(sender, "§e§lTeam Setup:");
        sendMessage(sender, "  §7/ctf setspawn <arena> <red|blue> §f- Set team spawn");
        sendMessage(sender, "  §7/ctf setlobby <arena> §f- Set lobby spawn");
        sendMessage(sender, "  §7/ctf setportal <arena> §f- Set portal location");
        sendMessage(sender, "  §7/ctf sethub §f- Set hub spawn");
        sendMessage(sender, "");
        
        sendMessage(sender, "§e§lGame Control:");
        sendMessage(sender, "  §7/ctf start <arena> §f- Start a game");
        sendMessage(sender, "  §7/ctf stop §f- Stop current game");
        sendMessage(sender, "  §7/ctf pause §f- Pause current game");
        sendMessage(sender, "  §7/ctf resume §f- Resume paused game");
        sendMessage(sender, "  §7/ctf restart §f- Restart current game");
        sendMessage(sender, "");
        
        sendMessage(sender, "§e§lGame Settings:");
        sendMessage(sender, "  §7/ctf setlimit <captures> §f- Set capture limit");
        sendMessage(sender, "  §7/ctf settime <minutes> §f- Set time limit");
        sendMessage(sender, "  §7/ctf setbuildzone <arena> §f- Set build zone");
        sendMessage(sender, "  §7/ctf setnobuild <arena> §f- Set no-build zone");
        sendMessage(sender, "  §7/ctf togglepvp §f- Toggle PvP");
        sendMessage(sender, "  §7/ctf togglebuild §f- Toggle building");
        sendMessage(sender, "  §7/ctf togglebreak §f- Toggle block breaking");
        sendMessage(sender, "");
        
        sendMessage(sender, "§e§lPlayer Commands:");
        sendMessage(sender, "  §7/ctf join [arena] §f- Join a game");
        sendMessage(sender, "  §7/ctf leave §f- Leave current game");
        sendMessage(sender, "  §7/ctf team <red|blue> §f- Choose a team");
        sendMessage(sender, "  §7/ctf stats [player] §f- View player statistics");
        sendMessage(sender, "  §7/ctf top [stat] §f- View leaderboard");
    }
    
    /**
     * Send a message to the command sender
     * 
     * @param sender the sender
     * @param message the message
     */
    private void sendMessage(Object sender, String message) {
        // This would use the actual API's message sending
        // For now, we'll just use System.out
        System.out.println(message);
    }
}
