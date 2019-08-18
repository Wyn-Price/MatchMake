package com.wynprice.matchmake.game;

import lombok.Setter;

public class GameTimer {
    private double elapsedTicks;
    private long lastMilliTicks;
    @Setter private int ticksPerSecond = 100;

    public void update() {
        long timeMillis = System.currentTimeMillis();
        this.elapsedTicks += (timeMillis - this.lastMilliTicks) / 1000D * this.ticksPerSecond;
        this.lastMilliTicks = timeMillis;
    }

    public int getTicks() {
        int ticks = (int) this.elapsedTicks;
        this.elapsedTicks -= ticks;
        return ticks;
    }

}
