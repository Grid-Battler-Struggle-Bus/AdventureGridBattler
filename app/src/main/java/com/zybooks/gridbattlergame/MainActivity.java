package com.zybooks.gridbattlergame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private BattleGrid mBattleGrid;
    private GridLayout mButtonGrid;
    private GridLayout mSpriteGrid;
    private Button mContinueButton;
    //private int selectedSquare = -1;
    private Characters char1;
    private String phase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonGrid = findViewById(R.id.button_grid);
        mSpriteGrid = findViewById(R.id.sprite_grid);
        mContinueButton = findViewById(R.id.continue_button);
        mBattleGrid = new BattleGrid();
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
                if (){

                }
                else{
                    phase = "attack";
                    break;
                }
            case "attack":
                if() {

                }
                else {
                    phase = "end";
                    break;
                }
            case "end":
                if (currTurn = friendly){
                    currTurn = enemy;
                    Toast.makeText(this,R.string.enemyTurn,Toast.LENGTH_SHORT).show();
                }
                else{
                    currTurn = friendly;
                    Toast.makeText(this,R.string.playerTurn,Toast.LENGTH_SHORT).show();
                }
                phase = "movement";
                break;
        }
    }

    private void onGridButtonClick(View view) {
        int buttonIndex = mButtonGrid.indexOfChild(view);
        switch (phase){
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
                mBattleGrid.damagecalulator();

        }
    }

    private void updateSprites() {
        for (int i = 0; i < mSpriteGrid.getChildCount(); i++) {
            TextView gridSprite = (TextView) mSpriteGrid.getChildAt(i);
            if (mBattleGrid.getContent(i) == "empty"){
                gridSprite.setText("");
            } else {
                gridSprite.setText(mBattleGrid.getContent(i));
            }
        }
    }

}