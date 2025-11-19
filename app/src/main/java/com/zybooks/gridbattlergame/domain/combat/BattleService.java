package com.zybooks.gridbattlergame.domain.combat;

import android.util.Log;

import com.zybooks.gridbattlergame.domain.characters.*;

public final class BattleService {
    private void BattlerService() {}

    //Perform Basic Attack
    public static void dealBasicDamage(CharacterUnit attacker, CharacterUnit defender) {
        int dmg = BattleCalculator.basicDamage(attacker, defender);
        Log.d("TAG", "dealBasicDamage: " + attacker.charName + " to " + defender.charName + ": " +dmg);
        defender.applyDamage(dmg);
        if(defender.getCurrentHp() == 0){
            defender.location = -1;
        }
    }
    //Perform Backstab
    public static void dealBackstabDamage(CharacterUnit attacker, CharacterUnit defender) {
        int dmg = BattleCalculator.backstabDamage(attacker, defender);
        defender.applyDamage(dmg);
        if(defender.getCurrentHp() == 0){
            defender.location = -1;
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
    }
}