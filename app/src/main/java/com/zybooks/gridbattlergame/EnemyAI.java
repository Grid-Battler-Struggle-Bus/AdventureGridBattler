public class EnemyAI {
    private BattleGrid battleGrid;
    private int enemyIndex; // The grid index where this enemy is located
    private String enemyId; // e.g., "enemy0"
    private int movementRange = 1; // How many tiles the enemy can move
    private int attackRange = 1; // Attack range (1 = adjacent only)
    
    public EnemyAI(BattleGrid battleGrid, String enemyId) {
        this.battleGrid = battleGrid;
        this.enemyId = enemyId;
        this.enemyIndex = findEnemyIndex();
    }
    
    // Find where this enemy currently is on the grid
    private int findEnemyIndex() {
        for (int i = 0; i < BattleGrid.GRID_HEIGHT * BattleGrid.GRID_WIDTH; i++) {
            if (battleGrid.getContent(i).equals(enemyId)) {
                return i;
            }
        }
        return -1;
    }
    
    public void executeTurn() {
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
        
        // Update position after moving
        enemyIndex = findEnemyIndex();
        
        // Then check if we're now in attack range and attack
        int distance = calculateDistance(enemyIndex, nearestTargetIndex);
        
        if (distance <= attackRange) {
            attack(nearestTargetIndex);
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
        battleGrid.setContent(newIndex, enemyId);
        battleGrid.setContent(enemyIndex, "empty");
    }
    
    private void attack(int targetIndex) {
        // Get which character is being attacked
        String targetContent = battleGrid.getContent(targetIndex);
        
        // Extract character number (e.g., "character0" -> 0)
        int characterNum = Integer.parseInt(targetContent.replace("character", ""));
        
        // Deal damage to the character
        battleGrid.PCs[characterNum].health -= 10; // Adjust damage as needed
        
        Log.d("EnemyAI", enemyId + " attacked " + targetContent);
        
        // Check if character died
        if (battleGrid.PCs[characterNum].health <= 0) {
            battleGrid.setContent(targetIndex, "empty");
            Log.d("EnemyAI", targetContent + " was defeated!");
        }
    }
}