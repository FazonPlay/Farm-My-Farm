
package com.fazonplay.farmmyfarm.Models;

import java.time.LocalDateTime;

public class Crop {
    private String name;
    private double seedCost;
    private double sellPrice;
    private double growthTime; // in minutes
    private LocalDateTime plantedTime;
    private CropState state;

    public double getGrowthTime() {
        return growthTime;
    }

    public enum CropState {
        EMPTY,
        SEEDS,
        GROWING,
        MATURE,
    }

    public Crop(String name, double seedCost, double sellPrice, double growthTime) {
        this.name = name;
        this.seedCost = seedCost;
        this.sellPrice = sellPrice;
        this.growthTime = growthTime;
        this.state = CropState.EMPTY;
    }
    public void plant() {
        this.plantedTime = LocalDateTime.now();
        this.state = CropState.SEEDS;
    }

    public boolean isReadyToHarvest() {
        if (plantedTime == null) return false;

        LocalDateTime currentTime = LocalDateTime.now();
        return plantedTime.plusSeconds((long)(growthTime * 60)).isBefore(currentTime);
    }

    // Getters and setters
    public String getName() { return name; }
    public double getSeedCost() { return seedCost; }
    public double getSellPrice() { return sellPrice; }
    public CropState getState() { return state; }
    public void setState(CropState state) { this.state = state; }
}