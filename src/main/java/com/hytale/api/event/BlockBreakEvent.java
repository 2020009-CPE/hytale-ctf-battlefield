package com.hytale.api.event;

import com.hytale.api.player.HytalePlayer;
import com.hytale.api.world.HytaleLocation;
import com.hytale.api.block.BlockType;

/**
 * Event fired when a player breaks a block.
 */
public class BlockBreakEvent extends HytaleEvent {
    
    private final HytalePlayer player;
    private final HytaleLocation location;
    private final BlockType blockType;
    
    public BlockBreakEvent(HytalePlayer player, HytaleLocation location, BlockType blockType) {
        this.player = player;
        this.location = location;
        this.blockType = blockType;
    }
    
    @Override
    public boolean isCancellable() {
        return true;
    }
    
    public HytalePlayer getPlayer() {
        return player;
    }
    
    public HytaleLocation getLocation() {
        return location;
    }
    
    public BlockType getBlockType() {
        return blockType;
    }
}
