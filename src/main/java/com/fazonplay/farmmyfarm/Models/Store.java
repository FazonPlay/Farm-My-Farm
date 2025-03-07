package com.fazonplay.farmmyfarm.Models;

import java.util.HashMap;
import java.util.Map;

public class Store {
    private Map<String, Crop> availableSeeds;
    private FinanceManager financeManager;
    private Map<String, Animal> availableAnimals;

    public Store(FinanceManager financeManager) {
        this.financeManager = financeManager;
        initializeSeeds();
        initializeAnimals();
    }

    private void initializeSeeds() {
        availableSeeds = new HashMap<>();
        availableSeeds.put("Wheat", new Crop("Wheat", 10, 25, 0.25));
        availableSeeds.put("Corn", new Crop("Corn", 15, 35, 0.5));
        availableSeeds.put("Carrot", new Crop("Carrot", 8, 20, 0.1));
    }

    private void initializeAnimals() {
        availableAnimals = new HashMap<>();
        availableAnimals.put("Chicken", new Animal("Chicken", 20, 5, 1));
        availableAnimals.put("Cow", new Animal("Cow", 50, 10, 2));
        availableAnimals.put("Sheep", new Animal("Sheep", 30, 7, 1.5));
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
    public boolean buyAnimal(String animalType) {
        Animal animal = availableAnimals.get(animalType);
        if (animal == null)
            return false;
        if(financeManager.canAfford(animal.getPurchaseCost())) {
            financeManager.deductMoney(animal.getPurchaseCost());
            return true; 
        }
        return false;
    }

    public boolean sellAnimalProduct(String animalType, Inventory inventory) {
        Animal animal = availableAnimals.get(animalType);
        if (animal == null) return false;

        double sellPrice = animal.getProductValue();
        financeManager.addMoney(sellPrice);
        return true;
    }

    public Map<String, Animal> getAvailableAnimals() {
        return availableAnimals;
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