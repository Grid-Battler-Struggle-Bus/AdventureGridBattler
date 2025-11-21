package com.zybooks.gridbattlergame;

import static com.zybooks.gridbattlergame.domain.characters.CharacterUnit.friendly;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
    private GridLayout mHighlightGrid;
    private CharacterUnit[] PCs = new CharacterUnit[3];
    private CharacterUnit[] Enemies = new CharacterUnit[]{
            new CharacterUnit("Goblin0", CharacterClass.GOBLIN, false),
            new CharacterUnit("Goblin1", CharacterClass.GOBLIN, false),
            new CharacterUnit("Goblin2", CharacterClass.GOBLIN, false),
            new CharacterUnit("Goblin3", CharacterClass.GOBLIN, false)};
    private EnemyAI AI0;
    private EnemyAI AI1;
    private EnemyAI AI2;
    private EnemyAI AI3;
    private String phase;
    private int currTurn = 0;
    private int enemyTurns = 0;
    private int selectedTile = -1;
    int[] targets = new int[0];

    int[] allys = new int[0];
    int[] openMoves = new int[0];
    private TextView PhaseText;
    private TextView TurnText;
    private FrameLayout turnOverlay;
    private TextView turnOverlayText;
    private float screenWidth;
    private ImageButton mContinueButton;
    private MediaPlayer battleIntro;
    private MediaPlayer battleLoop;
    private SoundPool soundPool;
    public static TextView battleLog;
    private int sfxBlade, sfxBow, sfxFire, sfxMove, sfxHit, sfxButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SelectionScreen.class);
        intent.putExtra("PC array", PCs);
        characterSelectLauncher.launch(intent);
        setContentView(R.layout.activity_main);

        initSoundEffects();

        turnOverlay = findViewById(R.id.turnOverlay);
        turnOverlayText = findViewById(R.id.turnOverlayText);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        PhaseText = findViewById(R.id.bannerText1);
        TurnText  = findViewById(R.id.bannerText2);
        battleLog = findViewById(R.id.combatLog);
        mButtonGrid = findViewById(R.id.button_grid);
        mSpriteGrid = findViewById(R.id.sprite_grid);
        mHighlightGrid = findViewById(R.id.highlight_grid);
        mContinueButton = findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(v -> {
            if (soundPool != null) soundPool.play(sfxButton, 1f, 1f, 1, 0, 1f);
            onContinueButtonClick(v);
        });
        mContinueButton.setOnClickListener(this::onContinueButtonClick);
        mBattleGrid = new BattleGrid(PCs, Enemies);
        mBattleGrid.deployEnemies();
        AI0 = new EnemyAI(mBattleGrid, Enemies[0], this::updateSprites, soundPool, sfxMove, sfxBow, sfxHit);
        AI1 = new EnemyAI(mBattleGrid, Enemies[1], this::updateSprites, soundPool, sfxMove, sfxBow, sfxHit);
        AI2 = new EnemyAI(mBattleGrid, Enemies[2], this::updateSprites, soundPool, sfxMove, sfxBow, sfxHit);
        AI3 = new EnemyAI(mBattleGrid, Enemies[3], this::updateSprites, soundPool, sfxMove, sfxBow, sfxHit);
        phase = "deploySquad";
        friendly = true;
        updateBanner();
        showTurnOverlay("Player Turn");
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
                    if (PCs[mBattleGrid.deploymentCount].deployed) {
                        mBattleGrid.deploymentCount++;
                        battleLog.append("\n" + PCs[mBattleGrid.deploymentCount-1].charName + "  deployed at ("
                                + ((PCs[mBattleGrid.deploymentCount-1].location%8) + 1) + "," + ((PCs[mBattleGrid.deploymentCount-1].location/8) + 1) + ")");
                    }
                } else {
                    phase = "movement";
                    Log.d("TAG", "ContinueButton: Go to Move");
                    updateBanner();
                    battleLog.append("\n" + PCs[mBattleGrid.deploymentCount].charName + "  deployed at ("
                            + ((PCs[mBattleGrid.deploymentCount].location%8) + 1) + "," + ((PCs[mBattleGrid.deploymentCount].location/8) + 1) + ")");
                }
                break;
            case "movement":
                Log.d("TAG", "ContinueButton: Go to Attack");
                phase = "attack";
                selectedTile = -1;
                openMoves = new int[0];
                updateSprites();
                updateBanner();
                break;
            case "attack":
                selectedTile = -1;
                targets = new int[0];
                updateSprites();
                end();
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
        int enemiesAlive = 4;
        int charactersAlive = 3;
        for (int i = 0; i < mSpriteGrid.getChildCount(); i++) {
            ImageView gridSprite = (ImageView) mSpriteGrid.getChildAt(i);
            gridSprite.setImageResource(0);
            ImageView gridHighlight = (ImageView) mHighlightGrid.getChildAt(i);
            gridHighlight.setImageResource(0);
        }
        for (int i = 0; i < PCs.length; i++) {
            if (PCs[i].location != -1) {
                ImageView gridSprite = (ImageView) mSpriteGrid.getChildAt(PCs[i].location);
                gridSprite.setImageResource(PCs[i].spriteId);

            } else {
                if (currTurn != 0 ){
                    charactersAlive--;
                }
            }
        }
        for (int i = 0; i < Enemies.length; i++){
            if (Enemies[i].location != -1) {
                ImageView gridSprite = (ImageView) mSpriteGrid.getChildAt(Enemies[i].location);
                gridSprite.setImageResource(Enemies[i].spriteId);
            } else {
                if (currTurn != 0 ){
                    enemiesAlive--;
                }
            }
        }

        for (int i = 0; i < openMoves.length; i++) {
            ImageView gridSprite = (ImageView) mHighlightGrid.getChildAt(openMoves[i]);
            gridSprite.setImageResource(R.drawable.board_movement_highlight);
        }

        for (int i = 0; i < targets.length; i++) {
            ImageView gridSprite = (ImageView) mHighlightGrid.getChildAt(targets[i]);
            gridSprite.setImageResource(R.drawable.board_enemy_highlight);
        }

        if (selectedTile != -1) {
            ImageView gridSprite = (ImageView) mHighlightGrid.getChildAt(selectedTile);
            gridSprite.setImageResource(R.drawable.board_character_highlight);
        }
        ProgressBar bar1 = findViewById(R.id.character_one_bar);
        bar1.setProgress((((int)(((float)PCs[0].getCurrentHp()/(float)PCs[0].unitStats.maxHp)*100))/5)*5);
        ProgressBar bar2 = findViewById(R.id.character_two_bar);
        bar2.setProgress((((int)(((float)PCs[1].getCurrentHp()/(float)PCs[1].unitStats.maxHp)*100))/5)*5);
        ProgressBar bar3 = findViewById((R.id.character_three_bar));
        bar3.setProgress((((int)(((float)PCs[2].getCurrentHp()/(float)PCs[2].unitStats.maxHp)*100))/5)*5);
        if(enemiesAlive==0){
            battleWin();
            return;
        }
        if(charactersAlive==0){
            battleLose();
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
        int characterNum = Integer.parseInt(currentChar.replaceAll("[^0-9]", ""));
        PCs[characterNum].location = index;
        PCs[characterNum].currentMove += 1;
        if (soundPool != null) soundPool.play(sfxMove, 1f, 1f, 1, 0, 1f);
        mBattleGrid.setContent(selectedTile, "empty");
        selectedTile = - 1;
        openMoves = new int[0];
        battleLog.append("\n" + PCs[characterNum].charName + "  moved to ("
                + ((PCs[characterNum].location%8) + 1) + "," + ((PCs[characterNum].location/8) + 1) + ")");
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
        } else if (checkContent(targets, index) || checkContent(allys, index)){
            Log.d("TAG", "manageAttack: Attack");
            performAttack(index);
        }
    }
    public void startAttack(int index) {
        if (mBattleGrid.getCharacter(index).hasAttacked) return;
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
                tempArray = mBattleGrid.getSpecialLine(selectedTile, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, "enemy");
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
            case HEAL:
                tempArray = mBattleGrid.getSpecialRadius(selectedTile, mBattleGrid.getCharacter(index).equippedAbility.abRangeMax, "character");
                Log.d("TAG", "Attack: Targets" + Arrays.toString(tempArray));
                tempList = new ArrayList<>();
                for(int i = 0; i < tempArray.length; i++){
                    if(mBattleGrid.getContent(tempArray[i]).contains("character")){
                        tempList.add(tempArray[i]);
                    }
                }

                targets = new int[tempList.size()];
                for(int i = 0; i < targets.length; i++){
                    targets[i] = tempList.get(i);
                }
                Log.d("TAG", "Attack: Targets" + Arrays.toString(targets));
                break;
        }
    }

    public void endAttack(int index) {
        selectedTile = -1;
        targets = new int[0];
    }

    public void performAttack(int index) {

        final int attackerTileIndex = selectedTile;
        final CharacterUnit attacker = mBattleGrid.getCharacter(attackerTileIndex);
        final CharacterUnit victim   = mBattleGrid.getCharacter(index);

        attacker.spriteId = attacker.attackSpriteId;
        updateSprites();

        switch (attacker.unitClass) {
            case FIGHTER:
                if (soundPool != null) soundPool.play(sfxBlade, 1f, 1f, 1, 0, 1f);
                BattleService.dealBasicDamage(attacker, victim);
                break;
            case RANGER:
                if (soundPool != null) soundPool.play(sfxBow, 1f, 1f, 1, 0, 1f);
                BattleService.dealBasicDamage(attacker, victim);
                break;
            case MAGE:
                if (soundPool != null) soundPool.play(sfxFire, 1f, 1f, 1, 0, 1f);
                BattleService.dealBasicDamage(attacker, victim);
                break;
            case ROGUE:
                if (soundPool != null) soundPool.play(sfxBlade, 1f, 1f, 1, 0, 1f);
                BattleService.dealBackstabDamage(attacker, victim);
                break;
            case CLERIC:
                BattleService.healUnit(victim);
                break;
        }

        if (soundPool != null && attacker.unitClass != CharacterClass.CLERIC) {
            soundPool.play(sfxHit, 1f, 1f, 1, 0, 1f);
        }

        attacker.hasAttacked = true;

        if (victim.getCurrentHp() <= 0) {
            mBattleGrid.setContent(index, "empty");
        }

        selectedTile = -1;
        targets = new int[0];

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            attacker.spriteId = attacker.idleSpriteId;
            updateSprites();
        }, 700);
    }
  
  private void end(){
            friendly = false;
            showTurnOverlay("Enemy Turn");
            currTurn++;
            for (int i = 0; i < mButtonGrid.getChildCount(); i++) {
                Button gridButton = (Button) mButtonGrid.getChildAt(i);
                gridButton.setEnabled(false);
            }
            mContinueButton.setEnabled(false);
            phase = "enemy_turn";
            updateBanner();
            CountDownTimer Timer = new CountDownTimer(5000, 1000){
                int count = 0;
                public void onFinish() {
                    count = 0;
                    mContinueButton.setEnabled(true);
                    for (int i = 0; i < mButtonGrid.getChildCount(); i++) {
                        Button gridButton = (Button) mButtonGrid.getChildAt(i);
                        gridButton.setEnabled(true);
                    }
                    for (int i = 0; i < PCs.length; i++) {
                        PCs[i].currentMove = 0;
                        PCs[i].hasAttacked = false;
                    }
                    friendly = true;
                    phase = "movement";
                    updateBanner();
                    showTurnOverlay("Player Turn");
                }
                public void onTick(long millisUntilFinished) {
                    if (count == 1){
                        AI0.executeTurn();
                        count++;
                    } else if(count == 2){
                        AI1.executeTurn();
                        count++;
                    } else if (count == 3) {
                        AI2.executeTurn();
                        count++;
                    } else if (count == 4) {
                        AI3.executeTurn();
                        count++;
                    } else {
                        count++;
                    }
                    updateSprites();
                }
            }.start();
    }
    public boolean checkContent (int[] array, int index){
        for (int i = 0; i < array.length; i++){
            if (index == array[i]) return true;
        }
        return false;
    }

    private void updateBanner() {
        // Turn banner
        if (friendly) {
            TurnText.setText("Player Turn");
        } else {
            TurnText.setText("Enemy Turn");
        }

        // Phase banner
        switch (phase) {
            case "deploySquad":
                PhaseText.setText("Deploy Squad");
                break;
            case "movement":
                PhaseText.setText("Movement");
                break;
            case "attack":
                PhaseText.setText("Attack");
                break;
            case "enemy_turn":
                PhaseText.setText("Enemy Phase");
                break;
            default:
                PhaseText.setText("");
                break;
        }
    }

    private void showTurnOverlay(String text) {
        turnOverlayText.setText(text);
        turnOverlay.setVisibility(View.VISIBLE);

        float startX = -screenWidth;
        float centerX = 0f;
        float driftX  = 80f;
        float endX    = screenWidth;

        turnOverlay.setTranslationX(startX);

        ObjectAnimator slideIn = ObjectAnimator.ofFloat(turnOverlay, "translationX",
                startX, centerX);
        slideIn.setDuration(500);

        ObjectAnimator drift = ObjectAnimator.ofFloat(turnOverlay, "translationX",
                centerX, driftX);
        drift.setDuration(1200);

        ObjectAnimator slideOut = ObjectAnimator.ofFloat(turnOverlay, "translationX",
                driftX, endX);
        slideOut.setDuration(350);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(slideIn, drift, slideOut);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                turnOverlay.setVisibility(View.GONE);
            }
        });

        set.start();
    }

    private void initMusic() {
        battleIntro = MediaPlayer.create(this, R.raw.battle_theme_intro);
        battleIntro.setVolume(0.4f, 0.4f);
        battleIntro.setOnCompletionListener(mp -> {
            mp.release();
            battleIntro = null;

            battleLoop = MediaPlayer.create(MainActivity.this, R.raw.battle_theme_loop);
            battleLoop.setLooping(true);
            battleLoop.setVolume(0.4f, 0.4f);
            battleLoop.start();
        });
        battleIntro.start();
    }

    private void initSoundEffects() {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .build();

        sfxBlade  = soundPool.load(this, R.raw.blade, 1);
        sfxBow    = soundPool.load(this, R.raw.bow, 1);
        sfxFire   = soundPool.load(this, R.raw.fire, 1);
        sfxMove   = soundPool.load(this, R.raw.move, 1);
        sfxHit    = soundPool.load(this, R.raw.hit, 1);
        sfxButton = soundPool.load(this, R.raw.button_press, 1);
    }

    public void battleWin() {
        showTurnOverlay("Victory");
        mContinueButton.setActivated(false);
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        CountDownTimer Timer = new CountDownTimer(3000, 1000) {
            public void onFinish() {
                startActivity(intent);
                finish();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    public void battleLose(){
        mContinueButton.setActivated(false);
        showTurnOverlay("Defeat");
        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        CountDownTimer Timer = new CountDownTimer(3000, 1000) {
            public void onFinish() {
                startActivity(intent);
                finish();
            }
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    ActivityResultLauncher<Intent> characterSelectLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
               @Override
               public void onActivityResult(ActivityResult result) {
                   if (result.getResultCode() == Activity.RESULT_OK) {
                       Intent data = result.getData();
                       if (data != null) {
                           String char0Name = data.getStringExtra("char0Name");
                           String char0Class = data.getStringExtra("char0Class");
                           PCs[0] = new CharacterUnit(char0Name, CharacterClass.valueOf(char0Class), true);
                           ImageView imageView1 = findViewById(R.id.character_one_card);
                           imageView1.setImageResource(PCs[0].spriteId);
                           String char1Name = data.getStringExtra("char1Name");
                           String char1Class = data.getStringExtra("char1Class");
                           PCs[1] = new CharacterUnit(char1Name, CharacterClass.valueOf(char1Class), true);
                           ImageView imageView2 = findViewById(R.id.character_two_card);
                           imageView2.setImageResource(PCs[1].spriteId);
                           String char2Name = data.getStringExtra("char2Name");
                           String char2Class = data.getStringExtra("char2Class");
                           PCs[2] = new CharacterUnit(char2Name, CharacterClass.valueOf(char2Class), true);
                           ImageView imageView3 = findViewById(R.id.character_three_card);
                           imageView3.setImageResource(PCs[2].spriteId);
                           updateSprites();
                            currTurn++;
                           initMusic();
                       }
                   }
               }
            }
    );

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (battleIntro != null) {
            battleIntro.release();
            battleIntro = null;
        }
        if (battleLoop != null) {
            battleLoop.release();
            battleLoop = null;
        }
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }

}
