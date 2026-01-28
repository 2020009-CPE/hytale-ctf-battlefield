package com.hytale.api.event;

/**
 * Base class for all Hytale events.
 */
public abstract class HytaleEvent {
    
    private boolean cancelled = false;
    
    /**
     * Check if this event is cancellable
     */
    public boolean isCancellable() {
        return false;
    }
    
    /**
     * Check if this event is cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * Set the cancelled state of this event
     */
    public void setCancelled(boolean cancelled) {
        if (!isCancellable()) {
            throw new UnsupportedOperationException("Event is not cancellable");
        }
        this.cancelled = cancelled;
    }
}
