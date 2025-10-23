package com.example.gridbattlergame.domain.characters;

public final class CharacterUnit {

    public final charName;
    public final CharacterClass unitClass;
    public final Stats unitStats;
    private Ability equippedAbility;
    private int currentHp;
    public  int location[];
    public boolean deployed;

    public CharacterUnit(String charname, CharacterClass unitClass) {
        this.charName = charName;
        this.unitClass = unitClass;
        this.unitStats = ClassFactory.statsFor(unitClass);
        this.currentHp = unitStats.maxHp;
        this.equippedAbility = ClassFactory.defaultAbility(unitClass);
        this.location = new int[2];
        this.deployed = false;
    }

    //Apply Damage
    public static void applyDamage(int damage) {
        currentHp = Math.max(0, (currentHp-damage));
    }
    //Apply Heal
    public static void applyHeal(int healAmount) {
        currentHp = Math.min(unitStats.maxHp, (currentHp + healAmount));
    }
    //HpGetter
    public static int getCurrentHp() {
        return currentHp;
    }
    //ClassGetter
    public static CharacterClass getUnitClass() {
        return unitClass;
    }
}