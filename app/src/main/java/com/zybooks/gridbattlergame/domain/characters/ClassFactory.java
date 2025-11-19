package com.zybooks.gridbattlergame.domain.characters;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.zybooks.gridbattlergame.R;

public final class ClassFactory extends AppCompatActivity {
    public static Stats statsFor(CharacterClass char_class) {
        switch (char_class) {
            //Reminder: Stats(int maxHp, atk, def, moveRange)
            case FIGHTER: return new Stats(34, 10, 4, 1);
            case MAGE: return new Stats(14, 12, 2, 1);
            case RANGER: return new Stats(28, 8, 3, 2);
            case ROGUE: return new Stats(24, 10, 3, 2);
            case CLERIC: return new Stats(30, 0, 4, 1);
            case GOBLIN: return new Stats(18, 8, 3, 1);
            //If Non-existent class is entered, throw an error
            default: throw new IllegalArgumentException("Unknown Class Entered: " + char_class);
        }
    }
    public static Ability defaultAbility(CharacterClass char_class) {
        switch (char_class) {
            //Reminder: Ability(String name, AbilityType type, int abRangeMin, abRangeMax)
            case FIGHTER: return new Ability("Slash", AbilityType.MELEE, 1, 1);
            case MAGE: return new Ability("Firebolt", AbilityType.MAGIC, 2, 3);
            case RANGER: return new Ability("Bow Shot", AbilityType.RANGED, 2, 3);
            case ROGUE: return new Ability("Backstab", AbilityType.MELEE, 1, 1);
            case CLERIC: return new Ability("Heal", AbilityType.HEAL, 1, 2);
            case GOBLIN: return new Ability("GoblinTime", AbilityType.EXPLOSIVE, 1, 1);
            //If Non-existent class is entered, throw an error
            default: throw new IllegalArgumentException("Unknown Class Entered: " + char_class);
        }
    }

    // IDLE sprite (what you use most of the time)
    public static int idleSpriteFor(CharacterClass char_class) {
        switch (char_class) {
            case FIGHTER: return R.drawable.fighter_idle;
            case MAGE:    return R.drawable.mage_idle;
            case RANGER:  return R.drawable.ranger_idle;
            case ROGUE:   return R.drawable.rogue_idle;
            case CLERIC:  return R.drawable.cleric_idle;
            case GOBLIN:  return R.drawable.skeleton_idle;
            default: throw new IllegalArgumentException("Unknown Class Entered: " + char_class);
        }
    }

    // ATTACK sprite (used briefly when they attack)
    public static int attackSpriteFor(CharacterClass char_class) {
        switch (char_class) {
            case FIGHTER: return R.drawable.fighter_attack;
            case MAGE:    return R.drawable.mage_attack;
            case RANGER:  return R.drawable.ranger_attack;
            case ROGUE:   return R.drawable.rogue_attack;
            case CLERIC:  return R.drawable.cleric_attack;
            case GOBLIN:  return R.drawable.skeleton_attack;
            default: throw new IllegalArgumentException("Unknown Class Entered: " + char_class);
        }
    }
}