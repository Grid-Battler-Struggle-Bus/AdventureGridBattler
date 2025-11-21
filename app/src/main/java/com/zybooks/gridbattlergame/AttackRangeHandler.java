package com.zybooks.gridbattlergame;

import android.util.Log;

import com.zybooks.gridbattlergame.domain.characters.CharacterUnit;

import java.util.ArrayList;
import java.util.List;

public class AttackRangeHandler {
    private BattleGrid battleGrid;
    private CharacterUnit attacker;
    private static final String TAG = "AttackRangeHandler";

    public AttackRangeHandler(BattleGrid battleGrid) {
        this.battleGrid = battleGrid;
    }

    // Get all valid targets within attack range
    public int[] getValidTargets(int attackerLocation, int attackRange, String targetKeyword) {
        Log.d(TAG, "Finding targets for attacker at " + attackerLocation
                + " with range " + attackRange);

        // Get all tiles in attack range
        int[] tilesInRange = battleGrid.getRadius(attackerLocation, attackRange);

        // Filter for tiles containing the target keyword
        List<Integer> validTargets = new ArrayList<>();

        for (int tile : tilesInRange) {
            String tileContent = battleGrid.getContent(tile);
            if (tileContent.contains(targetKeyword)) {
                validTargets.add(tile);
                Log.d(TAG, "Valid target found at tile " + tile);
            }
        }

        // Convert list to array
        int[] targets = new int[validTargets.size()];
        for (int i = 0; i < validTargets.size(); i++) {
            targets[i] = validTargets.get(i);
        }

        Log.d(TAG, "Total valid targets found: " + targets.length);
        return targets;
    }

    // Check if a specific target is in range
    public boolean isTargetInRange(int attackerLocation, int targetLocation, int attackRange) {
        int distance = calculateDistance(attackerLocation, targetLocation);
        boolean inRange = distance <= attackRange && distance > 0;

        Log.d(TAG, "Target at " + targetLocation + " is " + distance
                + " tiles away. In range: " + inRange);

        return inRange;
    }

    // Get tiles in a line for linear attacks (useful for abilities like slash or beam)
    public int[] getLineTargets(int attackerLocation, int range, char direction, String targetKeyword) {
        Log.d(TAG, "Getting line targets in direction " + direction
                + " with range " + range);

        int[] tilesInLine = battleGrid.getSpecialLine(attackerLocation, range, targetKeyword);

        Log.d(TAG, "Targets in line: " + tilesInLine.length);
        return tilesInLine;
    }

    // Get tiles in a radius for area attacks
    public int[] getAreaTargets(int centerLocation, int radius, String targetKeyword) {
        Log.d(TAG, "Getting area targets centered at " + centerLocation
                + " with radius " + radius);

        int[] tilesInArea = battleGrid.getSpecialRadius(centerLocation, radius, targetKeyword);

        Log.d(TAG, "Targets in area: " + tilesInArea.length);
        return tilesInArea;
    }

    // Get only adjacent targets (melee attacks)
    public int[] getAdjacentTargets(int attackerLocation, String targetKeyword) {
        Log.d(TAG, "Getting adjacent targets for melee attack");

        int[] adjacentTiles = battleGrid.getSpecialAdjacent(attackerLocation, targetKeyword);

        Log.d(TAG, "Adjacent targets found: " + adjacentTiles.length);
        return adjacentTiles;
    }

    // Check if attacker has any valid targets in range
    public boolean hasValidTargets(int attackerLocation, int attackRange, String targetKeyword) {
        int[] validTargets = getValidTargets(attackerLocation, attackRange, targetKeyword);
        boolean hasTargets = validTargets.length > 0;

        Log.d(TAG, "Attacker has valid targets: " + hasTargets);
        return hasTargets;
    }

    // Get the closest target within range
    public int getClosestTargetInRange(int attackerLocation, int attackRange, String targetKeyword) {
        int[] validTargets = getValidTargets(attackerLocation, attackRange, targetKeyword);

        if (validTargets.length == 0) {
            Log.d(TAG, "No targets in range");
            return -1;
        }

        int closestTarget = validTargets[0];
        int closestDistance = calculateDistance(attackerLocation, closestTarget);

        for (int i = 1; i < validTargets.length; i++) {
            int distance = calculateDistance(attackerLocation, validTargets[i]);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTarget = validTargets[i];
            }
        }

        Log.d(TAG, "Closest target at " + closestTarget
                + " with distance " + closestDistance);

        return closestTarget;
    }

    // Calculate Manhattan distance between two positions
    private int calculateDistance(int position1, int position2) {
        int row1 = position1 / BattleGrid.GRID_WIDTH;
        int col1 = position1 % BattleGrid.GRID_WIDTH;
        int row2 = position2 / BattleGrid.GRID_WIDTH;
        int col2 = position2 % BattleGrid.GRID_WIDTH;

        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }

    // Highlight valid targets on the grid (useful for UI feedback)
    public void highlightValidTargets(int attackerLocation, int attackRange, String targetKeyword) {
        int[] validTargets = getValidTargets(attackerLocation, attackRange, targetKeyword);

        for (int target : validTargets) {
            String currentContent = battleGrid.getContent(target);
            battleGrid.setContent(target, currentContent + "_highlighted");
            Log.d(TAG, "Highlighted target at " + target);
        }
    }

    // Clear highlight from targets
    public void clearTargetHighlight(int attackerLocation, int attackRange, String targetKeyword) {
        int[] validTargets = getValidTargets(attackerLocation, attackRange, targetKeyword);

        for (int target : validTargets) {
            String currentContent = battleGrid.getContent(target);
            String cleaned = currentContent.replace("_highlighted", "");
            battleGrid.setContent(target, cleaned);
            Log.d(TAG, "Cleared highlight from " + target);
        }
    }
}