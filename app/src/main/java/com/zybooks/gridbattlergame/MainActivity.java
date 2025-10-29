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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.gridbattlergame.domain.characters.AbilityType;
import com.zybooks.gridbattlergame.domain.characters.CharacterClass;
import com.zybooks.gridbattlergame.domain.characters.CharacterUnit;
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
    private CharacterUnit[] Enemies = new CharacterUnit[]{new CharacterUnit("Goblin1", CharacterClass.GOBLIN, false), new CharacterUnit("Goblin2", CharacterClass.GOBLIN, false), new CharacterUnit("Goblin3", CharacterClass.GOBLIN, false)};
    private String phase;
    private AbilityType AbilityType;
    private int currTurn = 0;

    private int currentTarget = -1;
    int[] targets;


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
        updateSprites();
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
                }
                break;
            case "movement":
                phase = "attack";
                break;
            case "attack":
                phase = "end";
                break;
            case "end":
                if (friendly) {
                    friendly = false;
                    Toast.makeText(this, R.string.enemyTurn, Toast.LENGTH_SHORT).show();
                    currTurn++;
                } else {
                    friendly = true;
                    Toast.makeText(this, R.string.playerTurn, Toast.LENGTH_SHORT).show();
                    currTurn++;
                }
                phase = "movement";
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
                mBattleGrid.manageMovement(buttonIndex);
                updateSprites();
                break;
            case "attack":
                performAttack(buttonIndex); // Example method name
                break;
        }
    }


    private void updateSprites() {
        for (int i = 0; i < mSpriteGrid.getChildCount(); i++) {
            TextView gridSprite = (TextView) mSpriteGrid.getChildAt(i);
            if (mBattleGrid.getContent(i) == "empty") {
                gridSprite.setText("");
            } else {
                gridSprite.setText(mBattleGrid.getContent(i));
            }
        }
    }
    public void manageAttack(int index) {
        if (currentTarget == -1 && mBattleGrid.getContent(index).contains("character")) {
            Log.d("TAG", "manageMovement: Start Attack");
            startAttack(index);
        } else if (currentTarget == index) {
            Log.d("TAG", "manageMovement: End Attack");
            endAttack(index);
        } else if (Arrays.asList(targets).contains(index)){
            Log.d("TAG", "manageMovement: Attack");
            performAttack(index);
        }
    }
    public void startAttack(int index) {
        currentTarget = index;
        List<Integer> tempList;
        int [] tempArray;
        switch (mBattleGrid.getCharacter(index).equippedAbility.type) {
            case MELEE:
                tempArray = mBattleGrid.getSpecialAdjacent(currentTarget, "enemy");
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
            case RANGED:
                tempArray = mBattleGrid.getSpecialLine(currentTarget, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, 'E', "Enemy");
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
            case MAGIC:
                tempArray = mBattleGrid.getSpecialRadius(currentTarget, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, "Enemy");
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
            case EXPLOSIVE:
                tempArray = mBattleGrid.getSpecialRadius(currentTarget, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, "Enemy");
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
        }
    }

    public void endAttack(int index) {
        currentTarget = -1;
        targets = new int[0];
    }

    //perform an attack
    public void performAttack(int index) {
        
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
                                    }
                                }
                            }
                        }

                );

        }