package com.zybooks.gridbattlergame.domain.combat;

import android.util.Log;

import com.zybooks.gridbattlergame.MainActivity;
import com.zybooks.gridbattlergame.domain.characters.*;

public final class BattleService {
    private void BattlerService() {}

    //Perform Basic Attack
    public static void dealBasicDamage(CharacterUnit attacker, CharacterUnit defender) {
        int dmg = BattleCalculator.basicDamage(attacker, defender);
        Log.d("TAG",  attacker.charName + " attacks " + defender.charName + " for " + dmg + " damage");
        defender.applyDamage(dmg);
        MainActivity.battleLog.append("\n" + attacker.charName + " attacks " + defender.charName + " for " + dmg + " damage");
        if(defender.getCurrentHp() == 0){
            defender.location = -1;
            MainActivity.battleLog.append("\n" + defender.charName + " killed");
        }
    }
    //Perform Backstab
    public static void dealBackstabDamage(CharacterUnit attacker, CharacterUnit defender) {
        int dmg = BattleCalculator.backstabDamage(attacker, defender);
        defender.applyDamage(dmg);
        Log.d("TAG",  attacker.charName + " backstabs " + defender.charName + " for " + dmg + " damage");
        MainActivity.battleLog.append("\n" + attacker.charName + " backstabs " + defender.charName + " for " + dmg + " damage");
        if(defender.getCurrentHp() == 0){
            defender.location = -1;
            MainActivity.battleLog.append("\n" + defender.charName + " killed");
        }
    }
    //Perform Explosive Damage
    public static void dealExplosiveDamage(CharacterUnit attacker, CharacterUnit defender) {
        int dmg = BattleCalculator.basicDamage(attacker, defender);
        defender.applyExplosiveDamage(dmg, defender);
    }
    //Perform Heal
    public static void healUnit(CharacterUnit target) {
        target.applyHeal(BattleCalculator.heal());
        MainActivity.battleLog.append("\nCLERIC heals " + target.charName + " for " + BattleCalculator.heal() + " health");
    }
}