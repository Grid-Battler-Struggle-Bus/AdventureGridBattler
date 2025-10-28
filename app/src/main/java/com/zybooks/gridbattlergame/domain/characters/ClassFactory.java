package com.zybooks.gridbattlergame.domain.characters;

public final class ClassFactory {
    public static Stats statsFor(CharacterClass char_class) {
        switch (char_class) {
            //Reminder: Stats(int maxHp, atk, def, moveRange)
            case FIGHTER: return new Stats(40, 10, 6, 1);
            case MAGE: return new Stats(14, 12, 2, 1);
            case RANGER: return new Stats(28, 8, 3, 2);
            case ROGUE: return new Stats(24, 10, 3, 2);
            case CLERIC: return new Stats(36, 0, 5, 1);
            case GOBLIN: return new Stats(18, 5, 3, 1);
            //If Non-existent class is entered, throw an error
            default: throw new IllegalArgumentException("Unknown Class Entered: " + char_class);
        }
    }
    public static Ability defaultAbility(CharacterClass char_class) {
        switch (char_class) {
            //Reminder: Ability(String name, AbilityType type, int abRangeMin, abRangeMax)
            case FIGHTER: return new Ability("Slash", AbilityType.MELEE, 1, 1);
            case MAGE: return new Ability("Firebolt", AbilityType.MAGIC, 2, 3);
            case RANGER: return new Ability("Bow Shot", AbilityType.RANGED, 2, 3);
            case ROGUE: return new Ability("Backstab", AbilityType.MELEE, 1, 1);
            case CLERIC: return new Ability("Heal", AbilityType.MAGIC, 1, 2);
            case GOBLIN: return new Ability("GoblinTime", AbilityType.EXPLOSIVE, 1, 1);
            //If Non-existent class is entered, throw an error
            default: throw new IllegalArgumentException("Unknown Class Entered: " + char_class);
        }
    }
}