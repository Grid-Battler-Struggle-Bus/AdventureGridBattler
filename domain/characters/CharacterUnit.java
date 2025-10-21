package com.example.gridbattlergame.domain.characters;

public final class CharacterUnit {

    public final CharacterClass unitClass;
    public final Stats unitStats;
    private Ability equippedAbility;
    private int currentHp;

    public CharacterUnit(CharacterClass unitClass) {
        this(unitClass, ClassFactory.defaultAbility(unit_class)
    }

    public CharacterUnit(CharacterClass unitClass, Ability chosenAbility) {
        this.unitClass = unitClass;
        this.unitStats = C;

    }
}