package com.hytale.ctf.game;

import com.hytale.ctf.arena.Arena;
import com.hytale.ctf.arena.ArenaManager;
import com.hytale.ctf.flag.FlagManager;
import com.hytale.ctf.player.PlayerManager;
import com.hytale.ctf.structure.StructureManager;
import com.hytale.ctf.structure.StructureType;
import com.hytale.ctf.team.TeamManager;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main controller for CTF game logic and state management.
 * Coordinates all game subsystems and manages the game lifecycle.
 * 
 * <p>Designed for Java 25 virtual threads for efficient async game timer operations.
 * Uses modern concurrency patterns with daemon threads for state management.
 * When running on Java 21+, replace thread factory with Thread.ofVirtual() for virtual threads.</p>
 * 
 * @since 1.0
 */
public class CTFGame {
    
    private final AtomicReference<GameState> currentState;
    private final AtomicReference<GameSettings> settings;
    private final Map<CTFTeam, AtomicInteger> scores;
    
    private final ArenaManager arenaManager;
    private final FlagManager flagManager;
    private final PlayerManager playerManager;
    private final TeamManager teamManager;
    private final StructureManager structureManager;
    
    private final ScheduledExecutorService gameTimer;
    private ScheduledFuture<?> countdownTask;
    private ScheduledFuture<?> gameLimitTask;
    
    private Arena currentArena;
    private final AtomicInteger elapsedSeconds;
    private final AtomicInteger countdownSeconds;
    
    /**
     * Creates a new CTF game instance with all required managers.
     * 
     * @param arenaManager the arena manager
     * @param flagManager the flag manager
     * @param playerManager the player manager
     * @param teamManager the team manager
     * @param structureManager the structure manager
     * @throws IllegalArgumentException if any manager is null
     */
    public CTFGame(
            ArenaManager arenaManager,
            FlagManager flagManager,
            PlayerManager playerManager,
            TeamManager teamManager,
            StructureManager structureManager) {
        
        this.arenaManager = Objects.requireNonNull(arenaManager, "ArenaManager cannot be null");
        this.flagManager = Objects.requireNonNull(flagManager, "FlagManager cannot be null");
        this.playerManager = Objects.requireNonNull(playerManager, "PlayerManager cannot be null");
        this.teamManager = Objects.requireNonNull(teamManager, "TeamManager cannot be null");
        this.structureManager = Objects.requireNonNull(structureManager, "StructureManager cannot be null");
        
        this.currentState = new AtomicReference<>(new GameState.Waiting());
        this.settings = new AtomicReference<>(GameSettings.defaults());
        this.scores = new EnumMap<>(CTFTeam.class);
        this.scores.put(CTFTeam.RED, new AtomicInteger(0));
        this.scores.put(CTFTeam.BLUE, new AtomicInteger(0));
        
        this.gameTimer = Executors.newScheduledThreadPool(
            2,
            runnable -> {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.setName("ctf-game-timer-" + thread.getId());
                return thread;
            }
        );
        this.elapsedSeconds = new AtomicInteger(0);
        this.countdownSeconds = new AtomicInteger(0);
    }
    
    /**
     * Starts the game in the specified arena.
     * Initializes all game systems and begins countdown.
     * 
     * @param arena the arena to play in
     * @throws IllegalArgumentException if arena is null or invalid
     * @throws IllegalStateException if game is already running
     */
    public void start(Arena arena) {
        Objects.requireNonNull(arena, "Arena cannot be null");
        
        if (!arena.isValid()) {
            throw new IllegalArgumentException("Arena is not valid");
        }
        
        GameState state = currentState.get();
        if (state instanceof GameState.Active || state instanceof GameState.Starting) {
            throw new IllegalStateException("Game is already running");
        }
        
        this.currentArena = arena;
        resetScores();
        elapsedSeconds.set(0);
        
        spawnFlags(arena);
        
        startCountdown();
    }
    
    /**
     * Starts the game countdown before actual gameplay begins.
     */
    private void startCountdown() {
        countdownSeconds.set(10);
        currentState.set(new GameState.Starting(10));
        
        countdownTask = gameTimer.scheduleAtFixedRate(
            () -> {
                int remaining = countdownSeconds.decrementAndGet();
                currentState.set(new GameState.Starting(remaining));
                
                if (remaining <= 0) {
                    countdownTask.cancel(false);
                    activateGame();
                } else {
                    broadcastCountdown(remaining);
                }
            },
            0,
            1,
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Activates the game after countdown completes.
     */
    private void activateGame() {
        currentState.set(new GameState.Active());
        broadcastGameStart();
        
        GameSettings currentSettings = settings.get();
        if (currentSettings.timeLimit() > 0) {
            scheduleTimeLimit(currentSettings.timeLimit());
        }
        
        startGameTimer();
    }
    
    /**
     * Starts the main game timer for tracking elapsed time.
     */
    private void startGameTimer() {
        gameLimitTask = gameTimer.scheduleAtFixedRate(
            () -> {
                int elapsed = elapsedSeconds.incrementAndGet();
                
                GameSettings currentSettings = settings.get();
                if (currentSettings.timeLimit() > 0 && elapsed >= currentSettings.timeLimit()) {
                    endGameByTimeLimit();
                }
            },
            1,
            1,
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Schedules the time limit check.
     * 
     * @param timeLimitSeconds the time limit in seconds
     */
    private void scheduleTimeLimit(int timeLimitSeconds) {
        gameTimer.schedule(
            this::checkTimeLimit,
            timeLimitSeconds,
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Stops the game immediately.
     */
    public void stop() {
        cancelAllTimers();
        removeFlags();
        currentState.set(new GameState.Ending());
        
        gameTimer.schedule(
            () -> {
                currentState.set(new GameState.Waiting());
                currentArena = null;
            },
            3,
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Pauses the game temporarily.
     * 
     * @throws IllegalStateException if game is not active
     */
    public void pause() {
        GameState state = currentState.get();
        if (!(state instanceof GameState.Active)) {
            throw new IllegalStateException("Cannot pause: game is not active");
        }
        
        cancelAllTimers();
        currentState.set(new GameState.Paused());
        broadcastPause();
    }
    
    /**
     * Resumes a paused game.
     * 
     * @throws IllegalStateException if game is not paused
     */
    public void resume() {
        GameState state = currentState.get();
        if (!(state instanceof GameState.Paused)) {
            throw new IllegalStateException("Cannot resume: game is not paused");
        }
        
        currentState.set(new GameState.Active());
        startGameTimer();
        broadcastResume();
    }
    
    /**
     * Restarts the current game.
     * 
     * @throws IllegalStateException if no arena is loaded
     */
    public void restart() {
        if (currentArena == null) {
            throw new IllegalStateException("No arena to restart");
        }
        
        stop();
        
        gameTimer.schedule(
            () -> start(currentArena),
            2,
            TimeUnit.SECONDS
        );
    }
    
    /**
     * Handles a flag capture by a team.
     * 
     * @param capturingTeam the team that captured the flag
     */
    public void handleCapture(CTFTeam capturingTeam) {
        Objects.requireNonNull(capturingTeam, "Capturing team cannot be null");
        
        if (!isActive()) {
            return;
        }
        
        int newScore = scores.get(capturingTeam).incrementAndGet();
        broadcastCapture(capturingTeam, newScore);
        
        GameSettings currentSettings = settings.get();
        if (newScore >= currentSettings.captureLimit()) {
            endGameByCapture(capturingTeam);
        } else {
            checkMercyRule();
        }
    }
    
    /**
     * Handles a flag steal event.
     * 
     * @param stealingTeam the team stealing the flag
     * @param stolenTeam the team whose flag was stolen
     */
    public void handleFlagSteal(CTFTeam stealingTeam, CTFTeam stolenTeam) {
        Objects.requireNonNull(stealingTeam, "Stealing team cannot be null");
        Objects.requireNonNull(stolenTeam, "Stolen team cannot be null");
        
        if (!isActive()) {
            return;
        }
        
        broadcastFlagSteal(stealingTeam, stolenTeam);
    }
    
    /**
     * Checks if the game is currently active.
     * 
     * @return true if the game is in active state
     */
    public boolean isActive() {
        return currentState.get() instanceof GameState.Active;
    }
    
    /**
     * Gets the current game state.
     * 
     * @return the current state
     */
    public GameState getCurrentState() {
        return currentState.get();
    }
    
    /**
     * Gets the current game settings.
     * 
     * @return the current settings
     */
    public GameSettings getSettings() {
        return settings.get();
    }
    
    /**
     * Updates the game settings.
     * 
     * @param newSettings the new settings
     * @throws IllegalArgumentException if settings are null
     * @throws IllegalStateException if game is active
     */
    public void updateSettings(GameSettings newSettings) {
        Objects.requireNonNull(newSettings, "Settings cannot be null");
        
        if (isActive()) {
            throw new IllegalStateException("Cannot change settings while game is active");
        }
        
        settings.set(newSettings);
    }
    
    /**
     * Gets the score for a team.
     * 
     * @param team the team to get score for
     * @return the team's score
     */
    public int getScore(CTFTeam team) {
        return scores.get(team).get();
    }
    
    /**
     * Gets the elapsed game time in seconds.
     * 
     * @return elapsed seconds
     */
    public int getElapsedSeconds() {
        return elapsedSeconds.get();
    }
    
    /**
     * Gets the current arena.
     * 
     * @return the current arena, or null if no game is running
     */
    public Arena getCurrentArena() {
        return currentArena;
    }
    
    /**
     * Shuts down the game timer executor.
     * Call this when the game system is being disabled.
     */
    public void shutdown() {
        cancelAllTimers();
        gameTimer.shutdown();
        try {
            if (!gameTimer.awaitTermination(5, TimeUnit.SECONDS)) {
                gameTimer.shutdownNow();
            }
        } catch (InterruptedException e) {
            gameTimer.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private void spawnFlags(Arena arena) {
        flagManager.spawnFlag(CTFTeam.RED, arena.redFlag());
        flagManager.spawnFlag(CTFTeam.BLUE, arena.blueFlag());
        
        structureManager.buildStructure(StructureType.PEDESTAL, arena.redFlag(), CTFTeam.RED);
        structureManager.buildStructure(StructureType.PEDESTAL, arena.blueFlag(), CTFTeam.BLUE);
    }
    
    private void removeFlags() {
        if (currentArena != null) {
            structureManager.removeStructure(StructureType.PEDESTAL, currentArena.redFlag());
            structureManager.removeStructure(StructureType.PEDESTAL, currentArena.blueFlag());
        }
        flagManager.clearAllFlags();
    }
    
    private void resetScores() {
        scores.get(CTFTeam.RED).set(0);
        scores.get(CTFTeam.BLUE).set(0);
    }
    
    private void cancelAllTimers() {
        if (countdownTask != null && !countdownTask.isDone()) {
            countdownTask.cancel(false);
        }
        if (gameLimitTask != null && !gameLimitTask.isDone()) {
            gameLimitTask.cancel(false);
        }
    }
    
    private void checkTimeLimit() {
        if (isActive()) {
            GameSettings currentSettings = settings.get();
            if (elapsedSeconds.get() >= currentSettings.timeLimit()) {
                endGameByTimeLimit();
            }
        }
    }
    
    private void checkMercyRule() {
        GameSettings currentSettings = settings.get();
        int redScore = scores.get(CTFTeam.RED).get();
        int blueScore = scores.get(CTFTeam.BLUE).get();
        int difference = Math.abs(redScore - blueScore);
        
        if (difference >= currentSettings.mercyRuleDifference()) {
            CTFTeam winner = redScore > blueScore ? CTFTeam.RED : CTFTeam.BLUE;
            endGameByMercyRule(winner);
        }
    }
    
    private void endGameByCapture(CTFTeam winner) {
        broadcastVictory(winner, "Capture Limit Reached");
        stop();
    }
    
    private void endGameByTimeLimit() {
        int redScore = scores.get(CTFTeam.RED).get();
        int blueScore = scores.get(CTFTeam.BLUE).get();
        
        if (redScore == blueScore) {
            broadcastDraw();
        } else {
            CTFTeam winner = redScore > blueScore ? CTFTeam.RED : CTFTeam.BLUE;
            broadcastVictory(winner, "Time Limit Reached");
        }
        stop();
    }
    
    private void endGameByMercyRule(CTFTeam winner) {
        broadcastVictory(winner, "Mercy Rule");
        stop();
    }
    
    private void broadcastCountdown(int seconds) {
        System.out.println("Game starting in " + seconds + " seconds...");
    }
    
    private void broadcastGameStart() {
        System.out.println("Game has started!");
    }
    
    private void broadcastPause() {
        System.out.println("Game paused");
    }
    
    private void broadcastResume() {
        System.out.println("Game resumed");
    }
    
    private void broadcastCapture(CTFTeam team, int score) {
        System.out.println(team.getColoredName() + " captured the flag! Score: " + score);
    }
    
    private void broadcastFlagSteal(CTFTeam stealingTeam, CTFTeam stolenTeam) {
        System.out.println(stealingTeam.getColoredName() + " stole " + 
                          stolenTeam.getColoredName() + "'s flag!");
    }
    
    private void broadcastVictory(CTFTeam winner, String reason) {
        System.out.println(winner.getColoredName() + " wins! (" + reason + ")");
        System.out.println("Final Score - Red: " + scores.get(CTFTeam.RED).get() + 
                          " Blue: " + scores.get(CTFTeam.BLUE).get());
    }
    
    private void broadcastDraw() {
        System.out.println("Game ended in a draw!");
        System.out.println("Final Score - Red: " + scores.get(CTFTeam.RED).get() + 
                          " Blue: " + scores.get(CTFTeam.BLUE).get());
    }
}
