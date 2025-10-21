package com.example.gridbattlergame.domain.characters;

public final class Stats {
    public final int maxHp, atk, def, moveRange;

    public Stats(int maxHp, atk, def, moveRange) {
        this.maxHp = maxHp;
        this.atk = atk;
        this.def = def;
        this.moveRange = moveRange;
    }
}