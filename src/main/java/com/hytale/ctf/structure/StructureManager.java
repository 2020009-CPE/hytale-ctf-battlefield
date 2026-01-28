package com.hytale.ctf.structure;

import com.hytale.ctf.arena.Location;
import com.hytale.ctf.game.CTFTeam;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Manager for registering and retrieving flag structures.
 * Handles structure lifecycle and provides factory methods for structure creation.
 * 
 * @since 1.0
 */
public class StructureManager {
    
    private final Map<StructureType, FlagStructure> registeredStructures;
    
    /**
     * Creates a new structure manager with default structures registered.
     */
    public StructureManager() {
        this.registeredStructures = new EnumMap<>(StructureType.class);
        registerDefaultStructures();
    }
    
    /**
     * Registers all default structure implementations.
     */
    private void registerDefaultStructures() {
        registerStructure(StructureType.PEDESTAL, new PedestalStructure());
        registerStructure(StructureType.TOWER, new TowerStructure());
        registerStructure(StructureType.FORTRESS, new FortressStructure());
        registerStructure(StructureType.SHRINE, new ShrineStructure());
        registerStructure(StructureType.PILLAR, new PillarStructure());
    }
    
    /**
     * Registers a structure implementation for a given type.
     * 
     * @param type the structure type
     * @param structure the structure implementation
     * @throws IllegalArgumentException if type or structure is null
     */
    public void registerStructure(StructureType type, FlagStructure structure) {
        Objects.requireNonNull(type, "Structure type cannot be null");
        Objects.requireNonNull(structure, "Structure cannot be null");
        registeredStructures.put(type, structure);
    }
    
    /**
     * Gets a structure by type.
     * 
     * @param type the structure type to retrieve
     * @return optional containing the structure if registered
     */
    public Optional<FlagStructure> getStructure(StructureType type) {
        return Optional.ofNullable(registeredStructures.get(type));
    }
    
    /**
     * Builds a structure at the specified location.
     * 
     * @param type the type of structure to build
     * @param location the location to build at
     * @param team the team owning the structure
     * @return true if structure was built, false if type not registered
     */
    public boolean buildStructure(StructureType type, Location location, CTFTeam team) {
        Objects.requireNonNull(type, "Structure type cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        Objects.requireNonNull(team, "Team cannot be null");
        
        Optional<FlagStructure> structure = getStructure(type);
        structure.ifPresent(s -> s.build(location, team));
        return structure.isPresent();
    }
    
    /**
     * Removes a structure at the specified location.
     * 
     * @param type the type of structure to remove
     * @param location the location to remove from
     * @return true if structure was removed, false if type not registered
     */
    public boolean removeStructure(StructureType type, Location location) {
        Objects.requireNonNull(type, "Structure type cannot be null");
        Objects.requireNonNull(location, "Location cannot be null");
        
        Optional<FlagStructure> structure = getStructure(type);
        structure.ifPresent(s -> s.remove(location));
        return structure.isPresent();
    }
    
    /**
     * Checks if a structure type is registered.
     * 
     * @param type the structure type to check
     * @return true if the type is registered
     */
    public boolean isRegistered(StructureType type) {
        return registeredStructures.containsKey(type);
    }
    
    /**
     * Gets the number of registered structures.
     * 
     * @return the count of registered structures
     */
    public int getRegisteredCount() {
        return registeredStructures.size();
    }
}
