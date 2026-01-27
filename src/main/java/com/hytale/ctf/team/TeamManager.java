package com.hytale.ctf.team;

import com.hytale.ctf.game.CTFTeam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages team assignments and rosters.
 * Tracks which players are on which teams.
 */
public class TeamManager {
    
    private final Map<CTFTeam, Set<String>> teamRosters;
    
    /**
     * Creates a new team manager
     */
    public TeamManager() {
        this.teamRosters = new EnumMap<>(CTFTeam.class);
        for (CTFTeam team : CTFTeam.values()) {
            teamRosters.put(team, ConcurrentHashMap.newKeySet());
        }
    }
    
    /**
     * Assigns a player to a team
     * 
     * @param playerName the player to assign
     * @param team the team to assign to
     */
    public void assignTeam(String playerName, CTFTeam team) {
        removeFromTeam(playerName);
        teamRosters.get(team).add(playerName);
    }
    
    /**
     * Removes a player from their current team
     * 
     * @param playerName the player to remove
     * @return the team the player was on, or null if not on a team
     */
    public CTFTeam removeFromTeam(String playerName) {
        for (var entry : teamRosters.entrySet()) {
            if (entry.getValue().remove(playerName)) {
                return entry.getKey();
            }
        }
        return null;
    }
    
    /**
     * Gets the size of a team
     * 
     * @param team the team to check
     * @return the number of players on the team
     */
    public int getTeamSize(CTFTeam team) {
        return teamRosters.get(team).size();
    }
    
    /**
     * Gets all players on a team
     * 
     * @param team the team to get players for
     * @return unmodifiable set of player names
     */
    public Set<String> getTeamPlayers(CTFTeam team) {
        return Collections.unmodifiableSet(teamRosters.get(team));
    }
    
    /**
     * Gets the team a player is on
     * 
     * @param playerName the player to check
     * @return optional containing the player's team
     */
    public Optional<CTFTeam> getPlayerTeam(String playerName) {
        return teamRosters.entrySet().stream()
            .filter(entry -> entry.getValue().contains(playerName))
            .map(Map.Entry::getKey)
            .findFirst();
    }
    
    /**
     * Balances teams by moving players from larger to smaller team
     * Uses the TeamSorter algorithm
     */
    public void balanceTeams() {
        int redSize = getTeamSize(CTFTeam.RED);
        int blueSize = getTeamSize(CTFTeam.BLUE);
        
        while (Math.abs(redSize - blueSize) > 1) {
            CTFTeam largerTeam = redSize > blueSize ? CTFTeam.RED : CTFTeam.BLUE;
            CTFTeam smallerTeam = largerTeam.opposite();
            
            String playerToMove = teamRosters.get(largerTeam).iterator().next();
            assignTeam(playerToMove, smallerTeam);
            
            redSize = getTeamSize(CTFTeam.RED);
            blueSize = getTeamSize(CTFTeam.BLUE);
        }
    }
    
    /**
     * Clears all team rosters
     */
    public void clearTeams() {
        teamRosters.values().forEach(Set::clear);
    }
    
    /**
     * Gets a copy of all team rosters
     * 
     * @return map of teams to player sets
     */
    public Map<CTFTeam, Set<String>> getTeamRosters() {
        Map<CTFTeam, Set<String>> copy = new EnumMap<>(CTFTeam.class);
        teamRosters.forEach((team, players) -> 
            copy.put(team, new HashSet<>(players))
        );
        return copy;
    }
}
