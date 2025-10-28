package com.zybooks.gridbattlergame.domain.characters;

public final class Stats {
    public final int maxHp, atk, def, moveRange;

    public Stats(int maxHp, int atk, int def, int moveRange) {
        this.maxHp = maxHp;
        this.atk = atk;
        this.def = def;
        this.moveRange = moveRange;
    }
}