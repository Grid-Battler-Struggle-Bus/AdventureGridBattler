package com.zybooks.gridbattlergame.domain.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.zybooks.gridbattlergame.R;
import com.zybooks.gridbattlergame.domain.characters.CharacterClass;
import com.zybooks.gridbattlergame.domain.characters.CharacterUnit;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SelectionScreen extends AppCompatActivity {
    LinkedHashSet<CharacterClass> selectedClasses = new LinkedHashSet<>();
    final int MAX_SELECTIONS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selection_screen);

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

            if (selectedClasses.contains(classId)) {
                selectedClasses.remove(classId);
                v.setAlpha(1f); // unselected look
            } else {
                /// Make sure that only 3 buttons can be selected
                if (selectedClasses.size() < MAX_SELECTIONS) {
                    selectedClasses.add(classId);
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
}
