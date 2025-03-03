package com.fazonplay.farmmyfarm.Services;

public class GrowthTimer {
    private int growthTime;
    private int growthStage;
    private int growthRate;
    private int growthCounter;

    public GrowthTimer(int growthTime, int growthStage, int growthRate) {
        this.growthTime = growthTime;
        this.growthStage = growthStage;
        this.growthRate = growthRate;
        this.growthCounter = 0;
    }

    public void updateGrowth() {
        if (growthCounter < growthTime) {
            growthCounter += growthRate;
        } else {
            growthCounter = 0;
            growthStage++;
        }
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public int getGrowthCounter() {
        return growthCounter;
    }
}
