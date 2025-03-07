package com.fazonplay.farmmyfarm.Controllers;

import com.fazonplay.farmmyfarm.Models.*;
import com.fazonplay.farmmyfarm.Services.AnimalTimer;
import com.fazonplay.farmmyfarm.Services.GrowthTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.geometry.Insets;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameController {
    private static final int GRID_SIZE = 5; // 5x5 grid

    @FXML
    private GridPane farmGrid;

    @FXML
    private Label balanceLabel;

    private FinanceManager financeManager;
    private Inventory inventory;
    private Store store;
    private Button[][] gridButtons;
    private Map<Button, PlotState> plotStates;
    private GrowthTimer growthTimer;
    private Map<Button, Crop> growingCrops;

    private class PlotState {
        Crop crop;
        int row;
        int col;

        PlotState(int row, int col) {
            this.row = row;
            this.col = col;
            this.crop = null;
        }
    }

    @FXML
    private GridPane animalPensGrid;
    private Button[] animalPens;
    private AnimalTimer animalTimer;

    @FXML
    public void initialize() {
        // Initialize game components
        financeManager = new FinanceManager(500); // Starting balance
        inventory = new Inventory();
        store = new Store(financeManager);
        gridButtons = new Button[GRID_SIZE][GRID_SIZE];
        plotStates = new HashMap<>();
        growingCrops = new HashMap<>();

        // Add some seeds to inventory for testing
        inventory.addSeed("Wheat", 5);

        // Setup grid
        setupFarmGrid();
        updateBalanceDisplay();
        setupAnimalPens();
        animalTimer = new AnimalTimer(inventory.getOwnedAnimals());

        growthTimer = new GrowthTimer(growingCrops);
    }


    private void setupFarmGrid() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Button plotButton = new Button();
                plotButton.setPrefSize(60, 60);
                plotButton.setStyle("-fx-background-color: #cccccc; -fx-border-color: #999999;");

                final int finalRow = row;
                final int finalCol = col;
                plotButton.setOnAction(event -> handlePlotClick(plotButton, finalRow, finalCol));

                farmGrid.add(plotButton, col, row);
                gridButtons[row][col] = plotButton;

                PlotState plotState = new PlotState(row, col);
                plotStates.put(plotButton, plotState);
            }
        }
    }

    private void handlePlotClick(Button plotButton, int row, int col) {
        PlotState state = plotStates.get(plotButton);

        if (state.crop == null) {
            // Empty plot - show seed planting options
            showSeedPlantingOptions(plotButton, row, col);
        } else if (state.crop.isReadyToHarvest()) {
            // Ready to harvest
            harvestCrop(plotButton, row, col);
        } else {
            // Growing
            showMessage("Growing", "This crop is still growing. Please check back later.");
        }
    }

    private void showSeedPlantingOptions(Button plotButton, int row, int col) {
        // Only show options if player has seeds
        Map<String, Integer> availableSeeds = inventory.getSeeds();
        if (availableSeeds.isEmpty()) {
            showMessage("No Seeds", "You don't have any seeds to plant. Visit the store to buy some.");
            return;
        }

        // Create a dialog with buttons for each seed type
        Stage dialog = new Stage();
        dialog.setTitle("Plant Seeds");
        dialog.initModality(Modality.APPLICATION_MODAL);

        GridPane seedOptions = new GridPane();
        seedOptions.setHgap(10);
        seedOptions.setVgap(10);
        seedOptions.setPadding(new Insets(10));

        int seedRow = 0;
        for (Map.Entry<String, Integer> entry : availableSeeds.entrySet()) {
            String seedName = entry.getKey();
            Integer quantity = entry.getValue();

            if (quantity > 0) {
                seedOptions.add(new Label(seedName + " (" + quantity + ")"), 0, seedRow);
                Button plantButton = new Button("Plant");
                plantButton.setOnAction(e -> {
                    plantSeed(plotButton, row, col, seedName);
                    dialog.close();
                });
                seedOptions.add(plantButton, 1, seedRow);
                seedRow++;
            }
        }

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());
        seedOptions.add(cancelButton, 0, seedRow, 2, 1);

        Scene scene = new Scene(seedOptions);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void plantSeed(Button plotButton, int row, int col, String seedName) {
        if (inventory.getSeeds().getOrDefault(seedName, 0) > 0) {
            Crop seedCrop = store.getAvailableSeeds().get(seedName);
            Crop newCrop = new Crop(seedName, seedCrop.getSeedCost(), seedCrop.getSellPrice(), seedCrop.getGrowthTime());
            newCrop.plant();

            PlotState state = plotStates.get(plotButton);
            state.crop = newCrop;

            growingCrops.put(plotButton, newCrop);

            inventory.removeSeed(seedName, 1);
            plotButton.setStyle("-fx-background-color: #8B4513; -fx-border-color: #999999;"); // Brown for planted


            showMessage("Planted", "You planted a " + seedName + " seed!");
        }
    }

    public void harvestCrop(Button plotButton, int row, int col) {
        PlotState state = plotStates.get(plotButton);
        if (state.crop != null && state.crop.getState() == Crop.CropState.MATURE) {
            inventory.addCrop(state.crop.getName(), 1);
            plotButton.setStyle("-fx-background-color: #cccccc; -fx-border-color: #999999;"); // Reset to empty

            // Remove from growing crops map
            growingCrops.remove(plotButton);

            showMessage("Harvested", "You harvested a " + state.crop.getName() + "!");
            state.crop = null;
        }
    }

    private void updateBalanceDisplay() {
        balanceLabel.setText(String.format("$%.2f", financeManager.getBalance()));
    }

    @FXML
    private void setupAnimalPens() {
        animalPens = new Button[3]; // Initial set of 3 pens

        for (int i = 0; i < animalPens.length; i++) {
            Button penButton = createAnimalPenButton("Empty Pen");
            animalPensGrid.add(penButton, i, 0);
            animalPens[i] = penButton;
        }

        // Add button to buy more pens
        Button buyPenButton = new Button("Buy New Pen");
        buyPenButton.setPrefSize(100, 100);
        buyPenButton.setStyle("-fx-background-color: #FFCC99; -fx-border-color: #999999;");
        buyPenButton.setOnAction(event -> buyNewPen());
        animalPensGrid.add(buyPenButton, animalPens.length, 0);
    }

    private Button createAnimalPenButton(String text) {
        Button penButton = new Button(text);
        penButton.setPrefSize(100, 100);
        penButton.setStyle("-fx-background-color: #DDDDDD; -fx-border-color: #999999;");
        penButton.setOnAction(event -> handleAnimalPenClick(penButton));
        return penButton;
    }

    private void buyNewPen() {
        double penCost = 100.0; // Cost to buy a new pen

        if (financeManager.canAfford(penCost)) {
            financeManager.deductMoney(penCost);

            // Create new array with one more slot
            Button[] newPens = new Button[animalPens.length + 1];
            System.arraycopy(animalPens, 0, newPens, 0, animalPens.length);

            // Create and add new pen
            Button newPen = createAnimalPenButton("Empty Pen");
            newPens[animalPens.length] = newPen;

            // Remove the buy button first
            animalPensGrid.getChildren().remove(animalPensGrid.lookup("Button[text=\"Buy New Pen\"]"));

            // Add the new pen
            animalPensGrid.add(newPen, animalPens.length, 0);

            // Create a fresh buy button and add it
            Button buyPenButton = new Button("Buy New Pen");
            buyPenButton.setPrefSize(100, 100);
            buyPenButton.setStyle("-fx-background-color: #FFCC99; -fx-border-color: #999999;");
            buyPenButton.setOnAction(event -> buyNewPen());
            animalPensGrid.add(buyPenButton, animalPens.length + 1, 0);

            animalPens = newPens;
            updateBalanceDisplay();
            showMessage("Purchase Successful", "You bought a new animal pen!");
        } else {
            showMessage("Insufficient Funds", "You need $" + penCost + " to buy a new animal pen.");
        }
    }
    private void handleAnimalPenClick(Button penButton) {
        Animal animal = inventory.getAnimal(penButton);

        if (animal == null) {
            showMessage("Empty Pen", "This pen is empty. Visit the animal shop to buy animals.");
        } else {
            // Create options dialog
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(animal.getType() + " Options");

            VBox options = new VBox(10);
            options.setPadding(new Insets(20));

            Button collectButton = new Button("Collect Products");
            collectButton.setDisable(!animal.isReadyToCollect());
            collectButton.setOnAction(e -> {
                if (animal.isReadyToCollect()) {
                    collectFromAnimal(penButton, animal);
                }
                dialog.close();
            });

            Button sellButton = new Button("Sell Animal");
            sellButton.setOnAction(e -> {
                sellAnimal(penButton, animal);
                dialog.close();
            });

            Label statusLabel = new Label("Status: " +
                    (animal.isReadyToCollect() ? "Ready to collect" : "Not ready yet"));

            options.getChildren().addAll(statusLabel, collectButton, sellButton);

            Scene scene = new Scene(options);
            dialog.setScene(scene);
            dialog.showAndWait();
        }
    }

    private void collectFromAnimal(Button penButton, Animal animal) {
        animal.collect();

        String productName = productNameForAnimal(animal.getType());
        inventory.addAnimalProduct(productName, 1);

        showMessage("Collected Product", "You collected " + productName + " from your " + animal.getType() + "!");

        // Update button color
        updateAnimalPenColor(penButton, animal);
    }

    private void sellAnimal(Button penButton, Animal animal) {
        // Animal resale value is 80% of purchase price
        double sellValue = animal.getPurchaseCost() * 0.8;

        financeManager.addMoney(sellValue);
        inventory.removeAnimal(penButton);

        penButton.setText("Empty Pen");
        penButton.setStyle("-fx-background-color: #DDDDDD; -fx-border-color: #999999;");

        updateBalanceDisplay();
        showMessage("Animal Sold", "You sold your " + animal.getType() + " for $" + String.format("%.2f", sellValue));
    }

    private String productNameForAnimal(String animalType) {
        switch(animalType) {
            case "Chicken": return "Egg";
            case "Cow": return "Milk";
            case "Sheep": return "Wool";
            default: return "Product";
        }
    }

    public void addAnimalToPen(String animalType) {
        // Find empty pen
        for (Button pen : animalPens) {
            if (inventory.getAnimal(pen) == null) {
                Animal animal = new Animal(animalType, 0, 0, 0); // Values don't matter, just for display
                switch (animalType) {
                    case "Chicken":
                        animal = new Animal("Chicken", 50, 8, 0.5);
                        break;
                    case "Cow":
                        animal = new Animal("Cow", 200, 25, 1.0);
                        break;
                    case "Sheep":
                        animal = new Animal("Sheep", 150, 15, 0.75);
                        break;
                }

                inventory.addAnimal(pen, animal);
                pen.setText(animalType);
                pen.setStyle("-fx-background-color: #90EE90; -fx-border-color: #999999;"); // Green for idle
                return;
            }
        }
        showMessage("No Space", "All your animal pens are full!");
    }

    private void updateAnimalPenColor(Button penButton, Animal animal) {
        if (animal.isReadyToCollect()) {
            penButton.setStyle("-fx-background-color: #7CFC00; -fx-border-color: #999999;"); // Bright green for ready
        } else {
            penButton.setStyle("-fx-background-color: #90EE90; -fx-border-color: #999999;"); // Light green for idle
        }
    }

    @FXML
    public void openAnimalShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fazonplay/farmmyfarm/animalShop.fxml"));
            Stage animalShopStage = new Stage();
            animalShopStage.setTitle("Animal Shop");
            animalShopStage.setScene(new Scene(loader.load(), 400, 300));

            AnimalController animalShopController = loader.getController();
            animalShopController.initData(store, inventory, financeManager, this);

            animalShopStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error", "Could not open the animal shop: " + e.getMessage());
        }
    }
    @FXML
    public void openSeedStore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fazonplay/farmmyfarm/shop.fxml"));
            Stage storeStage = new Stage();
            storeStage.setTitle("Seed Store");
            storeStage.setScene(new Scene(loader.load(), 400, 300));

            StoreController storeController = loader.getController();
            storeController.initData(store, inventory, financeManager, this);

            storeStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error", "Could not open the store: " + e.getMessage());
        }
    }

    @FXML
    public void openCropSellStore() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fazonplay/farmmyfarm/inventory.fxml"));
            Stage inventoryStage = new Stage();
            inventoryStage.setTitle("Inventory");
            inventoryStage.setScene(new Scene(loader.load(), 400, 300));

            InventoryController inventoryController = loader.getController();
            inventoryController.initData(inventory, store, financeManager, this);

            inventoryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error", "Could not open the inventory: " + e.getMessage());
        }
    }

    // Method to be called from other controllers (like StoreController)
    public void refreshGameState() {
        updateBalanceDisplay();

        // Could add more refresh logic here as game expands
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
//    public void shutdown() {
//        if (growthTimer != null) {
//            growthTimer.shutdown();
//        }
//    }
}