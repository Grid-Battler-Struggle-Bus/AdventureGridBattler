package com.zybooks.gridbattlergame.domain.characters;

public final class Ability {
    public final String name;
    public final AbilityType type;
    public final int abRangeMin, abRangeMax;

    public Ability(String name, AbilityType type, int abRangeMin, int abRangeMax) {
        this.name = name;
        this.type = type;
        this.abRangeMin = abRangeMin;
        this.abRangeMax = abRangeMax;
    }
}