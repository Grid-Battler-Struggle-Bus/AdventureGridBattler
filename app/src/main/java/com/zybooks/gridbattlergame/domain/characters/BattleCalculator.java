package com.zybooks.gridbattlergame.domain.characters;

public final class BattleCalculator {
    private BattleCalculator() {}

    //Deal Damage
    public static int basicDamage(CharacterUnit attacker, CharacterUnit defender) {
        int damageDealt = ((attacker.unitStats.atk) - (defender.unitStats.def));
        return Math.max(1, damageDealt);
    }

    //Special Rules
    public static int backstabDamage(CharacterUnit attacker, CharacterUnit defender) {
        int damageDealt = ((attacker.unitStats.atk) - (defender.unitStats.def));
        return (damageDealt + 7);
    }

    //Healing&Buffs
    public static int heal() {
        return 10;
    }
}