package com.fazonplay.farmmyfarm.Models;

import java.util.HashMap;
import java.util.Map;

public class Store {
    private Map<String, Crop> availableSeeds;
    private FinanceManager financeManager;

    public Store(FinanceManager financeManager) {
        this.financeManager = financeManager;
        initializeSeeds();
    }

    private void initializeSeeds() {
        availableSeeds = new HashMap<>();
        availableSeeds.put("Wheat", new Crop("Wheat", 10, 25, 0.25));
        availableSeeds.put("Corn", new Crop("Corn", 15, 35, 0.5));
        availableSeeds.put("Carrot", new Crop("Carrot", 8, 20, 0.1));
    }

    public boolean buySeed(String seedName, Inventory inventory) {
        Crop seed = availableSeeds.get(seedName);
        if (seed == null) return false;

        if (financeManager.canAfford(seed.getSeedCost())) {
            financeManager.deductMoney(seed.getSeedCost());
            inventory.addSeed(seedName, 1);
            return true;
        }
        return false;
    }

    public boolean sellCrop(String cropName, int quantity, Inventory inventory) {
        Crop crop = availableSeeds.get(cropName);
        if (crop == null) return false;

        if (inventory.getCrops().getOrDefault(cropName, 0) >= quantity) {
            double totalSellPrice = crop.getSellPrice() * quantity;
            financeManager.addMoney(totalSellPrice);
            inventory.removeCrop(cropName, quantity);
            return true;
        }
        return false;
    }

    public Map<String, Crop> getAvailableSeeds() { return availableSeeds; }
}