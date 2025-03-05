package com.fazonplay.farmmyfarm.Controllers;

import com.fazonplay.farmmyfarm.Models.*;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class GameController {
    private static final int GRID_SIZE = 5; // 5x5 grid

    @FXML
    private GridPane farmGrid;

    @FXML
    private Label balanceLabel;

    private FinanceManager financeManager;
    private Inventory inventory;
    private Store store;
    private Crop[][] gridCrops;

    @FXML
    public void initialize() {
        // Initialize game components
        financeManager = new FinanceManager(500); // Starting balance
        inventory = new Inventory();
        store = new Store(financeManager);

        // Setup grid
        setupFarmGrid();
        updateBalanceDisplay();
    }

    private void setupFarmGrid() {
        gridCrops = new Crop[GRID_SIZE][GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                StackPane gridCell = createGridCell(row, col);
                farmGrid.add(gridCell, col, row);
            }
        }
    }

    private StackPane createGridCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.getStyleClass().add("farm-grid-cell");

        Button cellButton = new Button();
        cellButton.getStyleClass().add("farm-cell-button");

        cell.getChildren().add(cellButton);

        cellButton.setOnAction(event -> handleCellClick(row, col));

        return cell;
    }

    private void handleCellClick(int row, int col) {
        Crop currentCrop = gridCrops[row][col];

        if (currentCrop == null || currentCrop.getState() == Crop.CropState.EMPTY) {
            // Attempt to plant a seed
            plantSeed(row, col, "Wheat"); // Default to wheat, could be chosen dynamically
        } else if (currentCrop.isReadyToHarvest()) {
            // Harvest the crop
            harvestCrop(row, col);
        }
    }

    private void plantSeed(int row, int col, String seedName) {
        if (inventory.getSeeds().getOrDefault(seedName, 0) > 0) {
            Crop newCrop = new Crop(seedName, 10, 25, 5); // Using wheat as default
            newCrop.plant();
            gridCrops[row][col] = newCrop;
            inventory.removeSeed(seedName, 1);
            // TODO: Update grid cell visual to show planted seed
        }
    }

    private void harvestCrop(int row, int col) {
        Crop crop = gridCrops[row][col];
        if (crop != null && crop.isReadyToHarvest()) {
            inventory.addCrop(crop.getName(), 1);
            gridCrops[row][col] = null;
            // TODO: Update grid cell visual to show empty plot
        }
    }

    private void updateBalanceDisplay() {
        balanceLabel.setText(String.format("Balance: $%.2f", financeManager.getBalance()));
    }

    // Methods for store interactions
    @FXML
    public void openSeedStore() {
        // TODO: Implement seed store UI
    }

    @FXML
    public void openCropSellStore() {
        // TODO: Implement crop sell store UI
    }
}