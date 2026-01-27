package com.hytale.ctf.team;

import com.hytale.ctf.game.CTFTeam;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Handles automatic team balancing logic.
 * Assigns players to teams to maintain balance.
 */
public class TeamSorter {
    
    private static final Random RANDOM = new Random();
    
    /**
     * Private constructor to prevent instantiation
     */
    private TeamSorter() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Assigns a player to a balanced team.
     * Favors the smaller team, or randomly assigns if teams are equal.
     * 
     * @param playerName the player to assign
     * @param teamRosters the current team rosters
     * @return the team the player was assigned to
     */
    public static CTFTeam assignPlayerToBalancedTeam(
            String playerName, 
            Map<CTFTeam, Set<String>> teamRosters) {
        
        int redSize = teamRosters.get(CTFTeam.RED).size();
        int blueSize = teamRosters.get(CTFTeam.BLUE).size();
        
        CTFTeam assignedTeam;
        
        if (redSize < blueSize) {
            assignedTeam = CTFTeam.RED;
        } else if (blueSize < redSize) {
            assignedTeam = CTFTeam.BLUE;
        } else {
            assignedTeam = RANDOM.nextBoolean() ? CTFTeam.RED : CTFTeam.BLUE;
        }
        
        teamRosters.get(assignedTeam).add(playerName);
        return assignedTeam;
    }
    
    /**
     * Gets the team with fewer players
     * 
     * @param teamRosters the current team rosters
     * @return the smaller team, or random if equal
     */
    public static CTFTeam getSmallerTeam(Map<CTFTeam, Set<String>> teamRosters) {
        int redSize = teamRosters.get(CTFTeam.RED).size();
        int blueSize = teamRosters.get(CTFTeam.BLUE).size();
        
        if (redSize < blueSize) {
            return CTFTeam.RED;
        } else if (blueSize < redSize) {
            return CTFTeam.BLUE;
        } else {
            return RANDOM.nextBoolean() ? CTFTeam.RED : CTFTeam.BLUE;
        }
    }
    
    /**
     * Checks if teams are balanced (difference of at most 1)
     * 
     * @param teamRosters the current team rosters
     * @return true if teams are balanced
     */
    public static boolean areTeamsBalanced(Map<CTFTeam, Set<String>> teamRosters) {
        int redSize = teamRosters.get(CTFTeam.RED).size();
        int blueSize = teamRosters.get(CTFTeam.BLUE).size();
        return Math.abs(redSize - blueSize) <= 1;
    }
}
