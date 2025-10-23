package com.example.gridbattlergame.domain.combat;

import com.example.gridbattlergame.domain.characters.*;

public final class BattleService() {
    private BattlerService() {}

    //Perform Basic Attack
    public static void dealBasicDamage(CharacterUnit attacker, CharacterUnit defender) {
        int dmg = BattleCalculator.basicDamage(attacker, defender);
        defender.applyDamage(dmg);
    }
    //Perform Backstab
    public static void dealBackstabDamage(CharacterUnit attacker, CharacterUnit defender) {
        int dmg = BattleCalculator.backstabDamage(attacker, defender);
        defender.applyDamage(dmg);
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
    //AreTheyDead?
    public static boolean DeathCheck(CharacterUnit potentialCorpse) {
        if (potentialCorpse.getCurrentHp() == 0) {
            return true;
        } else {
            return false;
        }
    }
}