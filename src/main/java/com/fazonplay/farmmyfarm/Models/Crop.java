
package com.fazonplay.farmmyfarm.Models;

import java.time.LocalDateTime;

public class Crop {
    private String name;
    private double seedCost;
    private double sellPrice;
    private int growthTime; // in minutes
    private LocalDateTime plantedTime;
    private CropState state;

    public enum CropState {
        EMPTY,       // No crop planted
        SEEDS,       // Just planted
        GROWING,     // In growth process
        MATURE,      // Ready to harvest
        WITHERED     // Crop has gone bad
    }

    public Crop(String name, double seedCost, double sellPrice, int growthTime) {
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
        if (state != CropState.SEEDS) return false;

        LocalDateTime currentTime = LocalDateTime.now();
        return plantedTime.plusMinutes(growthTime).isBefore(currentTime);
    }

    // Getters and setters
    public String getName() { return name; }
    public double getSeedCost() { return seedCost; }
    public double getSellPrice() { return sellPrice; }
    public CropState getState() { return state; }
    public void setState(CropState state) { this.state = state; }
}