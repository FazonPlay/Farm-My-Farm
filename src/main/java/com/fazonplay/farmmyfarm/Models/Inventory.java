package com.fazonplay.farmmyfarm.Models;

import javafx.scene.control.Button;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<String, Integer> crops;
    private Map<String, Integer> seeds;
    private Map<String, Integer> animalProducts;
    private Map<Button, Animal> ownedAnimals;

    public Inventory() {
        crops = new HashMap<>();
        seeds = new HashMap<>();
        animalProducts = new HashMap<>();
        ownedAnimals = new HashMap<>();
    }
    public void addAnimal(Button penButton, Animal animal) {
        ownedAnimals.put(penButton, animal);
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
    public void addAnimalProduct(String productName, int quantity) {
        animalProducts.put(productName, animalProducts.getOrDefault(productName, 0) + quantity);
    }

    public void removeSeed(String seedName, int quantity) {
        int currentQuantity = seeds.getOrDefault(seedName, 0);
        if (currentQuantity >= quantity) {
            seeds.put(seedName, currentQuantity - quantity);
        }
    }
    public void removeAnimal(Button penButton) {
        ownedAnimals.remove(penButton);
    }


    public Map<String, Integer> getCrops() {
        return crops;
    }

    public Animal getAnimal(Button penButton) {
        return ownedAnimals.get(penButton);
    }
    public Map<String, Integer> getSeeds() {
        return seeds;
    }
    public Map<String, Integer> getAnimalProducts() {
        return animalProducts;
    }

    public Map<Button, Animal> getOwnedAnimals() {
        return ownedAnimals;
    }
}
