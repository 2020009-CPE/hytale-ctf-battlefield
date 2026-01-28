package com.hytale.ctf;

import com.hytale.api.HytaleMod;
import com.hytale.api.command.Command;
import com.hytale.api.event.*;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.commands.CTFCommandAdapter;
import com.hytale.ctf.events.BlockEvents;
import com.hytale.ctf.events.FlagEvents;
import com.hytale.ctf.events.GameEvents;
import com.hytale.ctf.events.PlayerEvents;
import com.hytale.ctf.flag.FlagManager;
import com.hytale.ctf.game.CTFGame;
import com.hytale.ctf.player.PlayerManager;
import com.hytale.ctf.storage.DataManager;
import com.hytale.ctf.structure.StructureManager;
import com.hytale.ctf.team.TeamManager;
import com.hytale.ctf.ui.AlertManager;
import com.hytale.ctf.ui.HUDManager;
import com.hytale.ctf.util.MessageUtil;

/**
 * Main plugin class for Hytale CTF Battlefield.
 * Entry point for the mod using Java 25 features and Hytale API.
 */
public class CTFPlugin extends HytaleMod {
    
    private static CTFPlugin instance;
    
    // Managers
    private ArenaManager arenaManager;
    private FlagManager flagManager;
    private PlayerManager playerManager;
    private TeamManager teamManager;
    private StructureManager structureManager;
    private DataManager dataManager;
    private CTFGame game;
    private AlertManager alertManager;
    private HUDManager hudManager;
    
    // Event handlers
    private BlockEvents blockEvents;
    private FlagEvents flagEvents;
    private GameEvents gameEvents;
    private PlayerEvents playerEvents;
    
    public CTFPlugin() {
        instance = this;
    }
    
    @Override
    public void onLoad() {
        getLogger().info("Loading Hytale CTF Battlefield");
    }
    
    /**
     * Mod enable - called when mod starts
     */
    @Override
    public void onEnable() {
        getLogger().info("Enabling Hytale CTF Battlefield (Java 25)");
        getLogger().info("Using Java " + System.getProperty("java.version"));
        
        // Initialize configuration
        loadConfiguration();
        
        // Initialize managers
        initializeManagers();
        
        // Register commands
        registerCommands();
        
        // Register event handlers
        registerEvents();
        
        // Load data
        loadData();
        
        getLogger().info("CTF Battlefield enabled successfully!");
    }
    
    /**
     * Mod disable - called when mod stops
     */
    @Override
    public void onDisable() {
        getLogger().info("Disabling Hytale CTF Battlefield");
        
        // Save all data
        if (dataManager != null) {
            dataManager.saveAll();
        }
        
        // Stop any running games
        if (game != null && game.isActive()) {
            game.stop();
        }
        
        getLogger().info("CTF Battlefield disabled successfully!");
    }
    
    /**
     * Load configuration from config.yml
     */
    private void loadConfiguration() {
        // In a real implementation, this would load from config.yml
        // For now, we'll use defaults
        MessageUtil.setPrefix("&8[&bCTF&8]&r ");
        getLogger().info("Configuration loaded");
    }
    
    /**
     * Initialize all managers
     */
    private void initializeManagers() {
        getLogger().info("Initializing managers...");
        
        dataManager = new DataManager(getDataFolder());
        arenaManager = new ArenaManager(dataManager);
        structureManager = new StructureManager();
        teamManager = new TeamManager();
        playerManager = new PlayerManager(dataManager);
        flagManager = new FlagManager(structureManager);
        // AlertManager constructor requires (MessageUtil, HytaleServer)
        // MessageUtil is a utility class with only static methods and private constructor
        alertManager = new AlertManager(null, getServer());
        hudManager = new HUDManager(getServer());
        // CTFGame constructor requires (ArenaManager, FlagManager, PlayerManager, TeamManager, StructureManager)
        game = new CTFGame(
            arenaManager,
            flagManager,
            playerManager,
            teamManager,
            structureManager
        );
        
        getLogger().info("Managers initialized");
    }
    
    /**
     * Register command handlers
     */
    private void registerCommands() {
        getLogger().info("Registering commands...");
        
        // Create and register the CTF command
        // CTFCommandAdapter wraps CTFCommand which requires DataManager
        Command ctfCommand = new CTFCommandAdapter(
            arenaManager,
            flagManager,
            playerManager,
            teamManager,
            game,
            dataManager
        );
        getCommandManager().registerCommand(ctfCommand);
        
        getLogger().info("Commands registered");
    }
    
    /**
     * Register event handlers
     */
    private void registerEvents() {
        getLogger().info("Registering event handlers...");
        
        blockEvents = new BlockEvents(game, arenaManager);
        // FlagEvents constructor requires (CTFGame, FlagManager, PlayerManager, AlertManager, HUDManager)
        flagEvents = new FlagEvents(game, flagManager, playerManager, alertManager, hudManager);
        // GameEvents constructor requires (CTFGame, AlertManager, HUDManager)
        gameEvents = new GameEvents(game, alertManager, hudManager);
        // PlayerEvents constructor requires (CTFGame, PlayerManager, TeamManager, FlagManager, AlertManager, HUDManager)
        playerEvents = new PlayerEvents(game, playerManager, teamManager, flagManager, alertManager, hudManager);
        
        // Register Hytale event listeners
        EventManager eventManager = getEventManager();
        eventManager.registerListener(BlockBreakEvent.class, blockEvents::onBlockBreak);
        eventManager.registerListener(BlockPlaceEvent.class, blockEvents::onBlockPlace);
        eventManager.registerListener(PlayerJoinEvent.class, playerEvents::onPlayerJoin);
        eventManager.registerListener(PlayerQuitEvent.class, playerEvents::onPlayerQuit);
        eventManager.registerListener(PlayerDeathEvent.class, playerEvents::onPlayerDeath);
        
        getLogger().info("Event handlers registered");
    }
    
    /**
     * Load persistent data
     */
    private void loadData() {
        getLogger().info("Loading data...");
        
        dataManager.loadAll();
        
        getLogger().info("Data loaded");
    }
    
    // Getters
    
    public static CTFPlugin getInstance() {
        return instance;
    }
    
    public ArenaManager getArenaManager() {
        return arenaManager;
    }
    
    public FlagManager getFlagManager() {
        return flagManager;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public TeamManager getTeamManager() {
        return teamManager;
    }
    
    public StructureManager getStructureManager() {
        return structureManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public CTFGame getGame() {
        return game;
    }
    
    public AlertManager getAlertManager() {
        return alertManager;
    }
    
    public HUDManager getHUDManager() {
        return hudManager;
    }
}
