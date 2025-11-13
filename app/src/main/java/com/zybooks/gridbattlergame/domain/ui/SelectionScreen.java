package com.zybooks.gridbattlergame.domain.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.gridbattlergame.R;
import com.zybooks.gridbattlergame.domain.characters.CharacterClass;
import com.zybooks.gridbattlergame.domain.characters.CharacterUnit;
import com.zybooks.gridbattlergame.domain.characters.ClassFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SelectionScreen extends AppCompatActivity {
    private MediaPlayer selectionMusic;
    LinkedHashSet<CharacterClass> selectedClasses = new LinkedHashSet<>();
    final int MAX_SELECTIONS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_screen);

        selectionMusic = MediaPlayer.create(this, R.raw.selection_screen_loop);
        selectionMusic.setLooping(true);
        selectionMusic.setVolume(0.4f, 0.4f);
        selectionMusic.start();

        /// Grabbing References
        LinearLayout infoPanel = findViewById(R.id.details_panel);
        TextView infoName   = findViewById(R.id.info_name);
        TextView infoClass  = findViewById(R.id.info_class);
        TextView infoStats  = findViewById(R.id.info_stats);
        TextView infoAbility= findViewById(R.id.info_ability);

        /// Define each button
        //Confirm Button (Disabled by default)
        Button confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setEnabled(false);
        confirmButton.setAlpha(0.5f);
        //Fighter Button
        Button fighter_button = findViewById(R.id.fighter_button);
        fighter_button.setTag(CharacterClass.FIGHTER);
        //Mage Button
        Button mage_button = findViewById(R.id.mage_button);
        mage_button.setTag(CharacterClass.MAGE);
        //Ranger Button
        Button ranger_button = findViewById(R.id.ranger_button);
        ranger_button.setTag(CharacterClass.RANGER);
        //Rogue Button
        Button rogue_button = findViewById(R.id.rogue_button);
        rogue_button.setTag(CharacterClass.ROGUE);
        //Cleric Button
        Button cleric_button = findViewById(R.id.cleric_button);
        cleric_button.setTag(CharacterClass.CLERIC);
        //On Click Listener&LinkedHashList util
        View.OnClickListener characterClickListener = v -> {
            CharacterClass classId = (CharacterClass) v.getTag();

            showClassInfo(classId, infoPanel, infoName, infoClass, infoStats, infoAbility);

            if (selectedClasses.contains(classId)) {
                selectedClasses.remove(classId);
                v.setSelected(false);
                v.setAlpha(1f); // unselected look
            } else {
                /// Make sure that only 3 buttons can be selected
                if (selectedClasses.size() < MAX_SELECTIONS) {
                    selectedClasses.add(classId);
                    v.setSelected(true);
                    v.setAlpha(0.5f); // selected look
                }
            }
            /// Make confirm button working
            //Enable confirm button at 3 selections
            confirmButton.setEnabled(selectedClasses.size() == MAX_SELECTIONS);
            //Confirm button opaque when enabled and transparent when not enabled
            if (confirmButton.isEnabled() == true) {
                confirmButton.setAlpha(1f);
            } else {
                confirmButton.setAlpha(0.5f);
            }
        };
        /// Make listeners for each button
        fighter_button.setOnClickListener(characterClickListener);
        mage_button.setOnClickListener(characterClickListener);
        ranger_button.setOnClickListener(characterClickListener);
        rogue_button.setOnClickListener(characterClickListener);
        cleric_button.setOnClickListener(characterClickListener);

        /// Push character class
        confirmButton.setOnClickListener(v -> {
            ArrayList<CharacterClass> chosenUnits = new ArrayList<>(selectedClasses);
            Intent intent = new Intent();
            for (int i = 0; i < chosenUnits.size(); i++) {
                intent.putExtra("char"+ i +"Name", chosenUnits.get(i).toString());
                intent.putExtra("char"+ i + "Class", chosenUnits.get(i).toString());
            }
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void showClassInfo(CharacterClass cls,
                               LinearLayout panel,
                               TextView nameV, TextView classV,
                               TextView statsV, TextView abilityV) {

        // pull data from your factories
        var stats   = ClassFactory.statsFor(cls);
        var ability = ClassFactory.defaultAbility(cls);

        // fill text
        nameV.setText(cls.name().charAt(0) + cls.name().substring(1).toLowerCase()); // quick titlecase
        classV.setText("Class: " + cls);
        statsV.setText("HP " + stats.maxHp + "  ATK " + stats.atk + "  DEF " + stats.def + "  Move " + stats.moveRange);
        abilityV.setText("Ability: " + ability.name + " (" + ability.type + ", " + ability.abRangeMin + "-" + ability.abRangeMax + ")");

        // ensure visible
        panel.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectionMusic != null && selectionMusic.isPlaying()) {
            selectionMusic.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectionMusic != null && !selectionMusic.isPlaying()) {
            selectionMusic.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selectionMusic != null) {
            selectionMusic.release();
            selectionMusic = null;
        }
    }

}
