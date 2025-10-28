package com.zybooks.gridbattlergame.domain.characters;

public final class CharacterUnit {

    public final String charName;
    public static CharacterClass unitClass = null;
    public static Stats unitStats = null;
    private Ability equippedAbility;
    private static int currentHp;
    public  int location;
    public boolean deployed;

    public CharacterUnit(String charName, CharacterClass unitClass) {
        this.charName = charName;
        this.unitClass = unitClass;
        this.unitStats = ClassFactory.statsFor(unitClass);
        this.currentHp = unitStats.maxHp;
        this.equippedAbility = ClassFactory.defaultAbility(unitClass);
        this.location = -1;
        this.deployed = false;
    }

    //Apply Damage
    public static void applyDamage(int damage) {
        currentHp = Math.max(0, (currentHp-damage));
    }
    //Apply Explosive Damage
    public static void applyExplosiveDamage(int damage, CharacterUnit defender){
        int defLocation = defender.location;

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