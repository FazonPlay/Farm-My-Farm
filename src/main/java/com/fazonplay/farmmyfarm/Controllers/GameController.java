package com.fazonplay.farmmyfarm.Controllers;

import com.fazonplay.farmmyfarm.Models.*;
import com.fazonplay.farmmyfarm.Services.GrowthTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
    public void shutdown() {
        if (growthTimer != null) {
            growthTimer.shutdown();
        }
    }
}