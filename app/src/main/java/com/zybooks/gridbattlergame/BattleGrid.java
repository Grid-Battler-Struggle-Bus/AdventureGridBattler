package com.zybooks.gridbattlergame;

import android.util.Log;

import com.zybooks.gridbattlergame.domain.characters.Ability;
import com.zybooks.gridbattlergame.domain.characters.AbilityType;
import com.zybooks.gridbattlergame.domain.characters.CharacterUnit;

import java.util.ArrayList;
import java.util.List;

public class BattleGrid {
    public static final int GRID_HEIGHT = 3;
    public static final int GRID_WIDTH = 8;
    public String[][] battleGrid;
    public int deploymentCount = 0;
    public int currentTarget = -1;
    public CharacterUnit[] PCs;
    public CharacterUnit[] Enemies;

    public BattleGrid(CharacterUnit[] incomingFriends, CharacterUnit[] incomingFoes) {
        battleGrid = new String[GRID_HEIGHT][GRID_WIDTH];
        PCs = incomingFriends;
        Enemies = incomingFoes;
        for (int row = 0; row < GRID_HEIGHT; row++){
            for (int col = 0; col < GRID_WIDTH; col++){
                battleGrid[row][col] = "empty";
            }
        }
    }
    public String getContent(int index) {
        int row = index / GRID_WIDTH;
        int col = index % GRID_WIDTH;
        return battleGrid[row][col];
    }

    public CharacterUnit getCharacter(int index) {
        if (getContent(index).contains("character")){
            return  PCs[Integer.parseInt(getContent(index).replaceAll("[^0-9]", ""))];
        } else {
            return  Enemies[Integer.parseInt(getContent(index).replaceAll("[^0-9]", ""))];
        }

    }

    public void setContent(int index, String content){
        int row = index / GRID_WIDTH;
        int col = index % GRID_WIDTH;
        battleGrid[row][col] = content;
    }
    public void deployEnemies(){
        Enemies[0].location = 4;
        Enemies[0].deployed = true;
        setContent (4, "enemy0");
        Enemies[1].location = 13;
        Enemies[1].deployed = true;
        setContent (13, "enemy1");
        Enemies[2].location = 21;
        Enemies[2].deployed = true;
        setContent (21, "enemy2");
    }
    public void deployCharacter(int index){
        int row = index / GRID_WIDTH;
        int col = index % GRID_WIDTH;
        //only let character deploy on friendly side
        if (col > 3) return;
        //remove character from previous position if deployed before
        if(PCs[deploymentCount].deployed){
            battleGrid[PCs[deploymentCount].location/8][PCs[deploymentCount].location%8] = "empty";
        //set that character has been deployed now
        } else {
            PCs[deploymentCount].deployed = true;
        }
        battleGrid[row][col] = "character" + deploymentCount;
        //TODO: put deploy sound here
        PCs[deploymentCount].location = (row * 8) + col;
    }

    //Return integer array of adjacent tiles index's
    public int[] getAdjacent(int index){
        List<Integer> adjacentTilesList = new ArrayList<>();
        int row = index / GRID_WIDTH;
        int col = index % GRID_WIDTH;
        if (row > 0){
            adjacentTilesList.add(index - GRID_WIDTH);
        }
        if (row < GRID_HEIGHT - 1){
            adjacentTilesList.add(index + GRID_WIDTH);
        }
        if (col > 0){
            adjacentTilesList.add(index - 1);
        }
        if (col < GRID_WIDTH - 1){
            adjacentTilesList.add(index + 1);
        }
        int[] adjacentTiles = new int[adjacentTilesList.size()];
        for(int i = 0; i < adjacentTiles.length; i++){
            adjacentTiles[i] = adjacentTilesList.get(i);
        }
        return adjacentTiles;
    }

    //Return integer array of adjacent tiles with specified keyword
    public int [] getSpecialAdjacent(int index, String keyword){
        int[] adjacentTilesTemp = getAdjacent(index);
        List<Integer> adjacentTilesList = new ArrayList<>();
        for(int i = 0; i < adjacentTilesTemp.length; i++){
            if(getContent(adjacentTilesTemp[i]).contains(keyword)){
                adjacentTilesList.add(adjacentTilesTemp[i]);
            }
        }
        int[] adjacentTiles = new int[adjacentTilesList.size()];
        for(int i = 0; i < adjacentTiles.length; i++){
            adjacentTiles[i] = adjacentTilesList.get(i);
        }
        return adjacentTiles;
    }

    //Return integer array of all tiles in a radius
    public int[] getRadius(int index, int radius){
        List<Integer> radiusTilesList = new ArrayList<>();
        int row = index / GRID_WIDTH;
        int col = index % GRID_WIDTH;
        int vertDisplacement;
        int horDisplacement;
        int totalDisplacement;
        //loop through a grid the same size as diameter
        for (int i = 0; i <= radius * 2; i++){
            for (int j = 0; j <= radius * 2; j++){
                vertDisplacement = i - radius;
                horDisplacement = j - radius;
                totalDisplacement = Math.abs(vertDisplacement) + Math.abs(horDisplacement);
                //check that the tile is within the range and isn't the origin
                if(totalDisplacement <= radius && totalDisplacement != 0){
                    //check that tile's position relative to the index would exist on the battle grid
                    if(0 <= row + vertDisplacement && row + vertDisplacement < GRID_HEIGHT) {
                        if(0 <= col + horDisplacement && col + horDisplacement < GRID_WIDTH){
                            radiusTilesList.add(index + (vertDisplacement * GRID_WIDTH) + (horDisplacement));
                        }
                    }
                }
            }
        }
        int[] radiusTiles = new int[radiusTilesList.size()];
        for(int i = 0; i < radiusTiles.length; i++){
            radiusTiles[i] = radiusTilesList.get(i);
        }
        return radiusTiles;
    }

    //Return integer array of all tiles in a radius with specified keyword
    public int[] getSpecialRadius(int index, int radius, String keyword){
        int[] radiusTilesTemp = getRadius(index, radius);
        List<Integer> radiusTilesList = new ArrayList<>();
        for(int i = 0; i < radiusTilesTemp.length; i++){
            if(getContent(radiusTilesTemp[i]).contains(keyword)){
                radiusTilesList.add(radiusTilesTemp[i]);
            }
        }
        int[] radiusTiles = new int[radiusTilesList.size()];
        for(int i = 0; i < radiusTiles.length; i++){
            radiusTiles[i] = radiusTilesList.get(i);
        }
        return radiusTiles;
    }

    //Return integer array of all tiles in a line facing a cardinal direction
    public int[] getLine(int index, int range, char direction){
        List<Integer> lineTilesList = new ArrayList<>();
        switch(direction) {
            case('N'):
                for(int i = 1; i <= range; i++){
                    int row = index / GRID_WIDTH;
                    if(row - i >= 0){
                        lineTilesList.add(index - (i * GRID_WIDTH));
                    }
                }
                break;
            case('E'):
                for(int i = 1; i <= range; i++){
                    int col = index % GRID_WIDTH;
                    if(col + i < GRID_WIDTH){
                        lineTilesList.add(index + i);
                    }
                }
                break;
            case('S'):
                for(int i = 1; i <= range; i++){
                    int row = index / GRID_WIDTH;
                    if(row + i < GRID_HEIGHT){
                        lineTilesList.add(index + (i * GRID_WIDTH));
                    }
                }
                break;
            case('W'):
                for(int i = 1; i <= range; i++){
                    int col = index % GRID_WIDTH;
                    if(col + i >= 0){
                        lineTilesList.add(index - i);
                    }
                }
                break;
        }
        int[] lineTiles = new int[lineTilesList.size()];
        for(int i = 0; i < lineTiles.length; i++){
            lineTiles[i] = lineTilesList.get(i);
        }
        return lineTiles;
    }


    //Return integer array of all tiles in a line facing a cardinal direction with a specified keyword
    public int[] getSpecialLine(int index, int range, char direction, String keyword){
        int[] lineTilesTemp = getLine(index, range, direction);
        List<Integer> lineTilesList = new ArrayList<>();
        for(int i = 0; i < lineTilesTemp.length; i++){
            if(getContent(lineTilesTemp[i]).contains(keyword)){
                lineTilesList.add(lineTilesTemp[i]);
            }
        }
        int[] lineTiles = new int[lineTilesList.size()];
        for(int i = 0; i < lineTiles.length; i++){
            lineTiles[i] = lineTilesList.get(i);
        }
        return lineTiles;
    }
}
