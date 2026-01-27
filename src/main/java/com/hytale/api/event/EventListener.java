package com.hytale.api.event;

/**
 * Listener interface for handling events.
 */
@FunctionalInterface
public interface EventListener<T extends HytaleEvent> {
    
    /**
     * Handle the event
     */
    void onEvent(T event);
}
