package com.fazonplay.farmmyfarm.Models;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String, Integer> crops;
    private Map<String, Integer> seeds;

    public Inventory() {
        crops = new HashMap<>();
        seeds = new HashMap<>();
    }

    public void addCrop(String cropName, int quantity) {
        crops.put(cropName, crops.getOrDefault(cropName, 0) + quantity);
    }

    public void removeCrop(String cropName, int quantity) {
        int currentQuantity = crops.getOrDefault(cropName, 0);
        if (currentQuantity >= quantity) {
            crops.put(cropName, currentQuantity - quantity);
        }
    }

    public void addSeed(String seedName, int quantity) {
        seeds.put(seedName, seeds.getOrDefault(seedName, 0) + quantity);
    }

    public void removeSeed(String seedName, int quantity) {
        int currentQuantity = seeds.getOrDefault(seedName, 0);
        if (currentQuantity >= quantity) {
            seeds.put(seedName, currentQuantity - quantity);
        }
    }

    public Map<String, Integer> getCrops() {
        return crops;
    }

    public Map<String, Integer> getSeeds() {
        return seeds;
    }
}