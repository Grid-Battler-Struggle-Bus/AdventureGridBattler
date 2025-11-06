package com.zybooks.gridbattlergame;

import static com.zybooks.gridbattlergame.domain.characters.AbilityType.EXPLOSIVE;
import static com.zybooks.gridbattlergame.domain.characters.AbilityType.MAGIC;
import static com.zybooks.gridbattlergame.domain.characters.AbilityType.MELEE;
import static com.zybooks.gridbattlergame.domain.characters.AbilityType.RANGED;
import static com.zybooks.gridbattlergame.domain.characters.CharacterUnit.friendly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.gridbattlergame.domain.characters.AbilityType;
import com.zybooks.gridbattlergame.domain.characters.BattleCalculator;
import com.zybooks.gridbattlergame.domain.characters.CharacterClass;
import com.zybooks.gridbattlergame.domain.characters.CharacterUnit;
import com.zybooks.gridbattlergame.domain.combat.BattleService;
import com.zybooks.gridbattlergame.domain.ui.SelectionScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BattleGrid mBattleGrid;
    private GridLayout mButtonGrid;
    private GridLayout mSpriteGrid;
    private Button mContinueButton;
    private CharacterUnit[] PCs = new CharacterUnit[3];
    private CharacterUnit[] Enemies = new CharacterUnit[]{new CharacterUnit("Goblin0", CharacterClass.GOBLIN, false), new CharacterUnit("Goblin1", CharacterClass.GOBLIN, false), new CharacterUnit("Goblin2", CharacterClass.GOBLIN, false)};
    private EnemyAI AI0;
    private EnemyAI AI1;
    private EnemyAI AI2;
    private String phase;
    private int currTurn = 0;
    private int enemyTurns = 0;
    private int selectedTile = -1;
    int[] targets = new int[0];
    int[] openMoves = new int[0];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SelectionScreen.class);
        intent.putExtra("PC array", PCs);
        characterSelectLauncher.launch(intent);
        setContentView(R.layout.activity_main);

        mButtonGrid = findViewById(R.id.button_grid);
        mSpriteGrid = findViewById(R.id.sprite_grid);
        mContinueButton = findViewById(R.id.continue_button);
        mBattleGrid = new BattleGrid(PCs, Enemies);
        mBattleGrid.deployEnemies();
        AI0 = new EnemyAI(mBattleGrid, Enemies[0]);
        AI1 = new EnemyAI(mBattleGrid, Enemies[1]);
        AI2 = new EnemyAI(mBattleGrid, Enemies[2]);
        phase = "deploySquad";
        for (int i = 0; i < mButtonGrid.getChildCount(); i++) {
            Button gridButton = (Button) mButtonGrid.getChildAt(i);
            gridButton.setOnClickListener(this::onGridButtonClick);
        }
        mContinueButton.setOnClickListener(this::onContinueButtonClick);

    }

    private void onContinueButtonClick(View view) {
        switch (phase) {
            case "deploySquad":
                if (mBattleGrid.deploymentCount <= 1) {
                    if (mBattleGrid.PCs[mBattleGrid.deploymentCount].deployed) {
                        mBattleGrid.deploymentCount++;
                    }
                } else {
                    phase = "movement";
                    Log.d("TAG", "ContinueButton: Go to Move");
                }
                break;
            case "movement":
                Log.d("TAG", "ContinueButton: Go to Attack");
                phase = "attack";
                break;
            case "attack":
                end();
                break;
            case "enemy_turn":
                Log.d("TAG", "ContinueButton: Go to Enemy Turn");
                if(enemyTurns == 0) {
                    AI0.executeTurn();
                    enemyTurns++;
                } else if (enemyTurns == 1) {
                    AI1.executeTurn();
                    enemyTurns++;
                } else if (enemyTurns == 2) {
                    AI2.executeTurn();
                    enemyTurns++;
                } else {
                    end();
                }
                updateSprites();
                break;

        }
    }

    private void onGridButtonClick(View view) {
        int buttonIndex = mButtonGrid.indexOfChild(view);
        switch (phase) {
            case "deploySquad":
                if (mBattleGrid.getContent(buttonIndex) == "empty") {
                    mBattleGrid.deployCharacter(buttonIndex);
                    updateSprites();
                }
                break;
            case "movement":
                manageMovement(buttonIndex);
                updateSprites();
                break;
            case "attack":
                manageAttack(buttonIndex);
                updateSprites();
                break;
        }
    }

    private void updateSprites() {
        for (int i = 0; i < mSpriteGrid.getChildCount(); i++) {
            ImageView gridSprite = (ImageView) mSpriteGrid.getChildAt(i);
            gridSprite.setImageResource(0);
        }
        for (int i = 0; i < PCs.length; i++) {
            if (PCs[i].location != -1) {
                ImageView gridSprite = (ImageView) mSpriteGrid.getChildAt(PCs[i].location);
                gridSprite.setImageResource(PCs[i].spriteId);
            }
        }
        for (int i = 0; i < Enemies.length; i++){
            if (Enemies[i].location != -1) {
                ImageView gridSprite = (ImageView) mSpriteGrid.getChildAt(Enemies[i].location);
                gridSprite.setImageResource(Enemies[i].spriteId);
            }
        }
        for (int i = 0; i < openMoves.length; i++){
            ImageView gridSprite = (ImageView) mSpriteGrid.getChildAt(openMoves[i]);
            gridSprite.setImageResource(R.drawable.board_movement_highlight);
        }
    }

    public void manageMovement(int index){
        //did player click a character and is a move already started
        if (selectedTile == -1 && mBattleGrid.getContent(index).contains("character")){
            startMovement(index);
            //let player click on character again to cancel move
        } else if (selectedTile == index){
            endMovement(index);
            //Move character to a different square and reset board for next move
        } else if (checkContent(openMoves, index)){
            performMove(index);
        }
    }

    //Sets tiles around target as available to move
    public void startMovement(int index){
        String currentChar = mBattleGrid.getContent(index);
        if (PCs[Integer.parseInt(currentChar.replaceAll("[^0-9]", ""))].currentMove < PCs[Integer.parseInt(currentChar.replaceAll("[^0-9]", ""))].unitStats.moveRange) {
            selectedTile = index;
            openMoves = mBattleGrid.getSpecialAdjacent(index, "empty");
        }
    }

    //Move character to an available adjacent tile
    public void performMove(int index){
        String currentChar = mBattleGrid.getContent(selectedTile);
        mBattleGrid.setContent(index, currentChar);
        PCs[Integer.parseInt(currentChar.replaceAll("[^0-9]", ""))].location = index;
        PCs[Integer.parseInt(currentChar.replaceAll("[^0-9]", ""))].currentMove += 1;
        mBattleGrid.setContent(selectedTile, "empty");
        selectedTile = - 1;
        openMoves = new int[0];
    }

    //Undo start movement
    public void endMovement(int index){
        openMoves = new int[0];
        selectedTile = -1;
    }
    public void manageAttack(int index) {
        if (selectedTile == -1 && mBattleGrid.getContent(index).contains("character")) {
            Log.d("TAG", "manageAttack: Start Attack");
            startAttack(index);
        } else if (selectedTile == index) {
            Log.d("TAG", "manageAttack: End Attack");
            endAttack(index);
        } else if (checkContent(targets, index)){
            Log.d("TAG", "manageAttack: Attack");
            performAttack(index);
        }
    }
    public void startAttack(int index) {
        selectedTile = index;
        List<Integer> tempList;
        int [] tempArray;
        switch (mBattleGrid.getCharacter(index).equippedAbility.type) {
            case MELEE:
                tempArray = mBattleGrid.getSpecialAdjacent(selectedTile, "enemy");
                tempList = new ArrayList<>();
                for(int i = 0; i < tempArray.length; i++){
                    if(mBattleGrid.getContent(tempArray[i]).contains("enemy")){
                        tempList.add(tempArray[i]);
                    }
                }
                targets = new int[tempList.size()];
                for(int i = 0; i < targets.length; i++){
                    targets[i] = tempList.get(i);

                }
                Log.d("TAG", "Attack: Targets" + Arrays.toString(targets));
                break;
            case RANGED:
                tempArray = mBattleGrid.getSpecialLine(selectedTile, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, 'E', "enemy");
                tempList = new ArrayList<>();
                for(int i = 0; i < tempArray.length; i++){
                    if(mBattleGrid.getContent(tempArray[i]).contains("enemy")){
                        tempList.add(tempArray[i]);
                    }
                }

                targets = new int[tempList.size()];
                for(int i = 0; i < targets.length; i++){
                    targets[i] = tempList.get(i);
                }
                Log.d("TAG", "Attack: Targets" + Arrays.toString(targets));
                break;
            case MAGIC:
                tempArray = mBattleGrid.getSpecialRadius(selectedTile, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, "enemy");
                Log.d("TAG", "Attack: Targets" + Arrays.toString(tempArray));
                tempList = new ArrayList<>();
                for(int i = 0; i < tempArray.length; i++){
                    if(mBattleGrid.getContent(tempArray[i]).contains("enemy")){
                        tempList.add(tempArray[i]);
                    }
                }

                targets = new int[tempList.size()];
                for(int i = 0; i < targets.length; i++){
                    targets[i] = tempList.get(i);
                }
                Log.d("TAG", "Attack: Targets" + Arrays.toString(targets));
                break;
            case EXPLOSIVE:
                tempArray = mBattleGrid.getSpecialRadius(selectedTile, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, "enemy");
                tempList = new ArrayList<>();
                for(int i = 0; i < tempArray.length; i++){
                    if(mBattleGrid.getContent(tempArray[i]).contains("enemy")){
                        tempList.add(tempArray[i]);
                    }
                }

                targets = new int[tempList.size()];
                for(int i = 0; i < targets.length; i++){
                    targets[i] = tempList.get(i);
                }
                break;
        }
    }

    public void endAttack(int index) {
        selectedTile = -1;
        targets = new int[0];
    }

    //perform an attack
    public void performAttack(int index) {
       switch (mBattleGrid.getCharacter(selectedTile).unitClass) {
           case FIGHTER:
               BattleService.dealBasicDamage(mBattleGrid.getCharacter(selectedTile), mBattleGrid.getCharacter(index));
               break;
           case RANGER:
               BattleService.dealBasicDamage(mBattleGrid.getCharacter(selectedTile), mBattleGrid.getCharacter(index));
               break;
           case MAGE:
               BattleService.dealBasicDamage(mBattleGrid.getCharacter(selectedTile), mBattleGrid.getCharacter(index));
               break;
           case ROGUE:
               BattleService.dealBackstabDamage(mBattleGrid.getCharacter(selectedTile), mBattleGrid.getCharacter(index));
               break;
           case CLERIC:
               BattleService.healUnit(mBattleGrid.getCharacter(index));
               break;
       }
        selectedTile = -1;
        targets = new int[0];
    }
  
  private void end(){
        if (friendly) {
            friendly = false;
            Toast.makeText(this, R.string.enemyTurn, Toast.LENGTH_SHORT).show();
            currTurn++;
            for (int i = 0; i < mButtonGrid.getChildCount(); i++) {
                Button gridButton = (Button) mButtonGrid.getChildAt(i);
                gridButton.setEnabled(false);
            }
            phase = "enemy_turn";
        } else {
            friendly = true;
            Toast.makeText(this, R.string.playerTurn, Toast.LENGTH_SHORT).show();
            currTurn++;
            for (int i = 0; i < mButtonGrid.getChildCount(); i++) {
                Button gridButton = (Button) mButtonGrid.getChildAt(i);
                gridButton.setEnabled(true);
            }
            enemyTurns = 0;
            phase = "movement";
        }

    }
    public boolean checkContent (int[] array, int index){
        for (int i = 0; i < array.length; i++){
            if (index == array[i]) return true;
        }
        return false;
    }

    ActivityResultLauncher<Intent> characterSelectLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
               @Override
               public void onActivityResult(ActivityResult result) {
                   if (result.getResultCode() == Activity.RESULT_OK) {
                       Intent data = result.getData();
                       if (data != null) {
                           Log.d("TAG", "onActivityResult: results extracted");
                           String char0Name = data.getStringExtra("char0Name");
                           String char0Class = data.getStringExtra("char0Class");
                           Log.d("TAG", "onActivityResult: char1 strings extracted" + char0Class + char0Name);
                           PCs[0] = new CharacterUnit(char0Name, CharacterClass.valueOf(char0Class), true);
                           String char1Name = data.getStringExtra("char1Name");
                           String char1Class = data.getStringExtra("char1Class");
                           PCs[1] = new CharacterUnit(char1Name, CharacterClass.valueOf(char1Class), true);
                           String char2Name = data.getStringExtra("char2Name");
                           String char2Class = data.getStringExtra("char2Class");
                           PCs[2] = new CharacterUnit(char2Name, CharacterClass.valueOf(char2Class), true);
                           updateSprites();
                       }
                   }
               }
            }
    );
}
