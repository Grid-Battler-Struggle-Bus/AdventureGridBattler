package com.example.gridbattlergame.domain.characters;

public final class CharacterUnit {

    public final charName;
    public final CharacterClass unitClass;
    public final Stats unitStats;
    private Ability equippedAbility;
    private int currentHp;

    public CharacterUnit(String charname, CharacterClass unitClass) {
        this.charName = charName;
        this.unitClass = unitClass;
        this.unitStats = ClassFactory.statsFor(unitClass);
        this.currentHp = unitStats.maxHp;
        this.equippedAbility = ClassFactory.defaultAbility(unitClass);
    }
}