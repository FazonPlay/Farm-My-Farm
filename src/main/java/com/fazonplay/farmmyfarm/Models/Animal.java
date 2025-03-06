package com.fazonplay.farmmyfarm.Models;

import javafx.scene.control.Button;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Animal {
    private String type;
    private double purchaseCost;
    private double productValue;
    private double productionTime; // in minutes
    private LocalDateTime lastProduced;
    private AnimalState state;

    public enum AnimalState {
        IDLE,       // Normal state
        HUNGRY,     // Needs feeding
        PRODUCING,  // In production process
        READY       // Product ready to collect
    }

    public Animal(String type, double purchaseCost, double productValue, double productionTime) {
        this.type = type;
        this.purchaseCost = purchaseCost;
        this.productValue = productValue;
        this.productionTime = productionTime;
        this.state = AnimalState.IDLE;
        this.lastProduced = LocalDateTime.now();
    }

    public boolean isReadyToCollect() {
        if (lastProduced == null) return false;

        LocalDateTime currentTime = LocalDateTime.now();
        return lastProduced.plusSeconds((long)(productionTime * 60)).isBefore(currentTime);
    }

    public void collect() {
        lastProduced = LocalDateTime.now();
        state = AnimalState.IDLE;
    }

    // Getters
    public String getType() { return type; }
    public double getPurchaseCost() { return purchaseCost; }
    public double getProductValue() { return productValue; }
    public AnimalState getState() { return state; }
    public void setState(AnimalState state) { this.state = state; }
}