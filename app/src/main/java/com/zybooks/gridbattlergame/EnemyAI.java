package com.zybooks.gridbattlergame;

import android.media.SoundPool;
import android.util.Log;

import com.zybooks.gridbattlergame.BattleGrid;
import com.zybooks.gridbattlergame.domain.characters.CharacterUnit;
import com.zybooks.gridbattlergame.domain.combat.BattleService;
import android.os.Handler;
import android.os.Looper;

public class EnemyAI {
    public interface SpriteUpdateCallback {
        void refreshSprites();
    }

    private CharacterUnit character;
    private BattleGrid battleGrid;
    private int enemyIndex; // The grid index where this enemy is located
    private String enemyId; // e.g., "enemy0"
    private int movementRange = 1; // How many tiles the enemy can move
    private int attackRange = 1; // Attack range (1 = adjacent only)
    private SpriteUpdateCallback spriteCallback;
    private SoundPool soundPool;
    private int sfxMove;
    private int sfxBow;
    private int sfxHit;

    public EnemyAI(BattleGrid battleGrid, CharacterUnit character,
                   SpriteUpdateCallback spriteCallback, SoundPool soundPool,
                   int sfxMove, int sfxBow, int sfxHit) {
        this.character = character;
        this.battleGrid = battleGrid;
        this.enemyId = character.charName;
        this.enemyIndex = character.location;
        this.spriteCallback = spriteCallback;
        this.soundPool = soundPool;
        this.sfxMove = sfxMove;
        this.sfxBow  = sfxBow;
        this.sfxHit  = sfxHit;
    }

    // Find where this enemy currently is on the grid
    private int findEnemyIndex() {
        return character.location;
    }

    public void executeTurn() {
        Log.d("TAG", "AI: AI called");
        // Update enemy position
        enemyIndex = findEnemyIndex();

        if (enemyIndex == -1) {
            return; // Enemy not found or dead
        }

        // Find nearest player character
        int nearestTargetIndex = findNearestTarget();

        if (nearestTargetIndex == -1) {
            return; // No valid targets
        }

        // First, move towards the target
        moveTowards(nearestTargetIndex);
        //TODO: play movement sound

        // Update position after moving
        enemyIndex = findEnemyIndex();

        // Then check if we're now in attack range and attack
        int distance = calculateDistance(enemyIndex, nearestTargetIndex);

        if (distance <= attackRange) {
            attack(nearestTargetIndex);
            //TODO: play attack sound
        }
    }

    private int findNearestTarget() {
        int nearestIndex = -1;
        int minDistance = Integer.MAX_VALUE;

        // Search for all characters on the grid
        for (int i = 0; i < BattleGrid.GRID_HEIGHT * BattleGrid.GRID_WIDTH; i++) {
            String content = battleGrid.getContent(i);
            if (content.contains("character")) {
                int distance = calculateDistance(enemyIndex, i);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestIndex = i;
                }
            }
        }

        return nearestIndex;
    }

    private int calculateDistance(int index1, int index2) {
        int row1 = index1 / BattleGrid.GRID_WIDTH;
        int col1 = index1 % BattleGrid.GRID_WIDTH;
        int row2 = index2 / BattleGrid.GRID_WIDTH;
        int col2 = index2 % BattleGrid.GRID_WIDTH;

        // Manhattan distance
        return Math.abs(row1 - row2) + Math.abs(col1 - col2);
    }

    private void moveTowards(int targetIndex) {
        int currentRow = enemyIndex / BattleGrid.GRID_WIDTH;
        int currentCol = enemyIndex % BattleGrid.GRID_WIDTH;
        int targetRow = targetIndex / BattleGrid.GRID_WIDTH;
        int targetCol = targetIndex % BattleGrid.GRID_WIDTH;

        // Calculate direction
        int dx = Integer.signum(targetCol - currentCol);
        int dy = Integer.signum(targetRow - currentRow);

        // Try to move diagonally first (if movement range allows)
        int newRow = currentRow + dy;
        int newCol = currentCol + dx;
        int newIndex = newRow * BattleGrid.GRID_WIDTH + newCol;

        // Check if new position is valid and empty
        if (isValidMove(newRow, newCol, newIndex)) {
            performEnemyMove(newIndex);
            return;
        }

        // Try moving just vertically
        newRow = currentRow + dy;
        newCol = currentCol;
        newIndex = newRow * BattleGrid.GRID_WIDTH + newCol;

        if (isValidMove(newRow, newCol, newIndex)) {
            performEnemyMove(newIndex);
            return;
        }

        // Try moving just horizontally
        newRow = currentRow;
        newCol = currentCol + dx;
        newIndex = newRow * BattleGrid.GRID_WIDTH + newCol;

        if (isValidMove(newRow, newCol, newIndex)) {
            performEnemyMove(newIndex);
        }

        // If all moves fail, enemy stays in place
    }

    private boolean isValidMove(int row, int col, int index) {
        // Check bounds
        if (row < 0 || row >= BattleGrid.GRID_HEIGHT) return false;
        if (col < 0 || col >= BattleGrid.GRID_WIDTH) return false;

        // Check if tile is empty
        return battleGrid.getContent(index).equals("empty");
    }

    private void performEnemyMove(int newIndex) {
        // Move enemy to new position
        battleGrid.setContent(newIndex, "enemy" + Integer.parseInt(character.charName.replaceAll("[^0-9]", "")));
        battleGrid.setContent(enemyIndex, "empty");
        character.location = newIndex;
        if (soundPool != null) soundPool.play(sfxMove, 1f, 1f, 1, 0, 1f);
    }

    private void attack(int targetIndex) {
        CharacterUnit victim = battleGrid.getCharacter(targetIndex);

        character.spriteId = character.attackSpriteId;
        if (spriteCallback != null) spriteCallback.refreshSprites();

        if (soundPool != null) {
            soundPool.play(sfxBow, 1f, 1f, 1, 0, 1f); // arrow launch
        }

        BattleService.dealBasicDamage(character, victim);

        if (soundPool != null) {
            soundPool.play(sfxHit, 1f, 1f, 1, 0, 1f);
        }

        Log.d("EnemyAI", character.charName + " attacked " + victim.charName);

        if (victim.getCurrentHp() <= 0) {
            battleGrid.setContent(targetIndex, "empty");
            Log.d("EnemyAI", victim.charName + " was defeated!");
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            character.spriteId = character.idleSpriteId;
            if (spriteCallback != null) spriteCallback.refreshSprites();
        }, 700);
    }
}