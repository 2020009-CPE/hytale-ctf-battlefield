package com.hytale.ctf.game;

/**
 * Sealed interface representing the possible game states.
 * Uses Java 25 sealed types for type-safe state management.
 */
public sealed interface GameState permits 
    GameState.Waiting,
    GameState.Starting,
    GameState.Active,
    GameState.Paused,
    GameState.Ending {
    
    /**
     * Waiting for players to join
     */
    record Waiting() implements GameState {}
    
    /**
     * Countdown before game starts
     */
    record Starting(int countdown) implements GameState {}
    
    /**
     * Game is currently running
     */
    record Active() implements GameState {}
    
    /**
     * Game is paused
     */
    record Paused() implements GameState {}
    
    /**
     * Game is ending
     */
    record Ending() implements GameState {}
}
