package com.zybooks.gridbattlergame.domain.characters;

import java.util.ArrayList;
import java.util.List;

public final class Roster {
    private Roster() {}

    public static List<CharacterUnit> allPlayable() {
        List<CharacterUnit> charList = new ArrayList<>();
        //CharacterUnit(String charname, CharacterClass unitClass)
        charList.add(new CharacterUnit("Fighter", CharacterClass.FIGHTER));
        charList.add(new CharacterUnit("Mage", CharacterClass.MAGE));
        charList.add(new CharacterUnit("Ranger", CharacterClass.RANGER));
        charList.add(new CharacterUnit("Rogue", CharacterClass.ROGUE));
        charList.add(new CharacterUnit("Cleric", CharacterClass.CLERIC));

        return charList;
    }
}