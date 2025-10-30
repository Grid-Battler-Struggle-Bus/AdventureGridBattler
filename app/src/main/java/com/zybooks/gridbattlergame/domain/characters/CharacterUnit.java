package com.zybooks.gridbattlergame.domain.characters;

import android.util.Log;

public final class CharacterUnit {

    public final String charName;
    public CharacterClass unitClass;
    public Stats unitStats;
    public Ability equippedAbility;
    private int currentHp;
    public int location;
    public boolean deployed;

    public static boolean friendly;

    public CharacterUnit(String charName, CharacterClass unitClass, boolean team) {
        this.charName = charName;
        this.unitClass = unitClass;
        this.unitStats = ClassFactory.statsFor(unitClass);
        this.currentHp = unitStats.maxHp;
        this.equippedAbility = ClassFactory.defaultAbility(unitClass);
        this.location = -1;
        this.deployed = false;
        this.friendly = team;
    }

    //Apply Damage
    public void applyDamage(int damage) {
        currentHp = Math.max(0, (currentHp-damage));
        Log.d("TAG", "current health: " + currentHp + " " + charName);
    }
    //Apply Explosive Damage
    public static void applyExplosiveDamage(int damage, CharacterUnit defender){
        int defLocation = defender.location;

    }
    //Apply Heal
    public void applyHeal(int healAmount) {
        currentHp = Math.min(unitStats.maxHp, (currentHp + healAmount));
    }
    //HpGetter
    public int getCurrentHp() {
        return currentHp;
    }
    //ClassGetter
    public CharacterClass getUnitClass() {
        return unitClass;
    }
}