package com.hytale.api.event;

/**
 * Manages event registration and firing.
 */
public interface EventManager {
    
    /**
     * Register an event listener
     */
    <T extends HytaleEvent> void registerListener(Class<T> eventClass, EventListener<T> listener);
    
    /**
     * Unregister an event listener
     */
    <T extends HytaleEvent> void unregisterListener(Class<T> eventClass, EventListener<T> listener);
    
    /**
     * Fire an event to all registered listeners
     */
    <T extends HytaleEvent> void fireEvent(T event);
}
