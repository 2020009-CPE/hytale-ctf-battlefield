package com.hytale.ctf;

import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.commands.CTFCommand;
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

import java.io.File;
import java.util.logging.Logger;

/**
 * Main plugin class for Hytale CTF Battlefield.
 * Entry point for the plugin using Java 25 features.
 */
public class CTFPlugin {
    
    private static CTFPlugin instance;
    private final Logger logger;
    
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
    
    // Configuration
    private File dataFolder;
    
    public CTFPlugin() {
        instance = this;
        this.logger = Logger.getLogger("CTF-Battlefield");
    }
    
    /**
     * Plugin enable - called when plugin starts
     */
    public void onEnable() {
        logger.info("Enabling Hytale CTF Battlefield (Java 25)");
        
        // Create data folder
        dataFolder = new File("plugins/CTF-Battlefield");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
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
        
        logger.info("CTF Battlefield enabled successfully!");
        logger.info("Using Java " + System.getProperty("java.version"));
    }
    
    /**
     * Plugin disable - called when plugin stops
     */
    public void onDisable() {
        logger.info("Disabling Hytale CTF Battlefield");
        
        // Save all data
        if (dataManager != null) {
            dataManager.saveAll();
        }
        
        // Stop any running games
        if (game != null && game.isActive()) {
            game.stop();
        }
        
        logger.info("CTF Battlefield disabled successfully!");
    }
    
    /**
     * Load configuration from config.yml
     */
    private void loadConfiguration() {
        // In a real implementation, this would load from config.yml
        // For now, we'll use defaults
        MessageUtil.setPrefix("&8[&bCTF&8]&r ");
        logger.info("Configuration loaded");
    }
    
    /**
     * Initialize all managers
     */
    private void initializeManagers() {
        logger.info("Initializing managers...");
        
        dataManager = new DataManager(dataFolder);
        arenaManager = new ArenaManager(dataManager);
        structureManager = new StructureManager();
        teamManager = new TeamManager();
        playerManager = new PlayerManager(dataManager);
        flagManager = new FlagManager(structureManager);
        alertManager = new AlertManager();
        hudManager = new HUDManager();
        game = new CTFGame(
            arenaManager,
            flagManager,
            playerManager,
            teamManager,
            alertManager,
            hudManager
        );
        
        logger.info("Managers initialized");
    }
    
    /**
     * Register command handlers
     */
    private void registerCommands() {
        logger.info("Registering commands...");
        
        // In a real implementation, this would register with the server
        new CTFCommand(
            arenaManager,
            flagManager,
            playerManager,
            teamManager,
            game
        );
        
        logger.info("Commands registered");
    }
    
    /**
     * Register event handlers
     */
    private void registerEvents() {
        logger.info("Registering event handlers...");
        
        blockEvents = new BlockEvents(game, arenaManager);
        flagEvents = new FlagEvents(game, flagManager, playerManager);
        gameEvents = new GameEvents(game);
        playerEvents = new PlayerEvents(game, playerManager, teamManager);
        
        // In a real implementation, these would be registered with the event system
        
        logger.info("Event handlers registered");
    }
    
    /**
     * Load persistent data
     */
    private void loadData() {
        logger.info("Loading data...");
        
        dataManager.loadAll();
        
        logger.info("Data loaded");
    }
    
    // Getters
    
    public static CTFPlugin getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
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
    
    public File getDataFolder() {
        return dataFolder;
    }
    
    /**
     * Main entry point for standalone testing
     */
    public static void main(String[] args) {
        CTFPlugin plugin = new CTFPlugin();
        plugin.onEnable();
        
        // Keep running for testing
        System.out.println("CTF Plugin is running. Press Ctrl+C to stop.");
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(plugin::onDisable));
        
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
