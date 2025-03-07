package com.fazonplay.farmmyfarm.Controllers;

import com.fazonplay.farmmyfarm.Models.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Map;

public class InventoryController {
    @FXML
    private Label balanceLabel;

    @FXML
    private GridPane seedsGrid;

    @FXML
    private GridPane cropsGrid;

    @FXML
    private GridPane animalProductsGrid;

    private Inventory inventory;
    private Store store;
    private FinanceManager financeManager;
    private GameController gameController;

    public void initData(Inventory inventory, Store store, FinanceManager financeManager, GameController gameController) {
        this.inventory = inventory;
        this.store = store;
        this.financeManager = financeManager;
        this.gameController = gameController;

        updateDisplay();
    }

    private void updateDisplay() {
        balanceLabel.setText(String.format("$%.2f", financeManager.getBalance()));

        clearGridRows(seedsGrid, 1);
        clearGridRows(cropsGrid, 1);
        clearGridRows(animalProductsGrid, 1);

        int seedRow = 1;
        for (Map.Entry<String, Integer> entry : inventory.getSeeds().entrySet()) {
            seedsGrid.add(new Label(entry.getKey()), 0, seedRow);
            seedsGrid.add(new Label(entry.getValue().toString()), 1, seedRow);
            seedRow++;
        }

        int cropRow = 1;
        for (Map.Entry<String, Integer> entry : inventory.getCrops().entrySet()) {
            String cropName = entry.getKey();
            int quantity = entry.getValue();

            Crop cropInfo = store.getAvailableSeeds().get(cropName);
            double sellPrice = cropInfo != null ? cropInfo.getSellPrice() : 0;

            cropsGrid.add(new Label(cropName), 0, cropRow);
            cropsGrid.add(new Label(Integer.toString(quantity)), 1, cropRow);
            cropsGrid.add(new Label(String.format("$%.2f", sellPrice)), 2, cropRow);

            Button sellButton = new Button("Sell");
            final String finalCropName = cropName;
            sellButton.setOnAction(event -> sellCrop(finalCropName));

            cropsGrid.add(sellButton, 3, cropRow);
            cropRow++;
        }

        int productRow = 1;
        for (Map.Entry<String, Integer> entry : inventory.getAnimalProducts().entrySet()) {
            String productName = entry.getKey();
            int quantity = entry.getValue();

            double sellPrice = getAnimalProductPrice(productName);

            animalProductsGrid.add(new Label(productName), 0, productRow);
            animalProductsGrid.add(new Label(Integer.toString(quantity)), 1, productRow);
            animalProductsGrid.add(new Label(String.format("$%.2f", sellPrice)), 2, productRow);

            Button sellButton = new Button("Sell");
            final String finalProductName = productName;
            sellButton.setOnAction(event -> sellAnimalProduct(finalProductName));

            animalProductsGrid.add(sellButton, 3, productRow);
            productRow++;
        }
    }

    private void sellAnimalProduct(String productName) {
        if (inventory.getAnimalProducts().getOrDefault(productName, 0) > 0) {
            inventory.getAnimalProducts().put(productName,
                    inventory.getAnimalProducts().get(productName) - 1);

            financeManager.addMoney(getAnimalProductPrice(productName));

            showMessage("Sale Successful", "You sold 1 " + productName + "!");
            updateDisplay();
            gameController.refreshGameState();
        } else {
            showMessage("Error", "You don't have any " + productName + " to sell!");
        }
    }
    private double getAnimalProductPrice(String productName) {
        switch (productName) {
            case "Egg": return 5.0;
            case "Milk": return 10.0;
            case "Wool": return 7.0;
            default: return 3.0;
        }
    }

    private void clearGridRows(GridPane grid, int startRow) {
        for (int i = startRow; i < grid.getRowCount(); i++) {
            grid.getChildren().removeIf(node ->
                    GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node) >= startRow);
        }
    }

    private void sellCrop(String cropName) {
        if (inventory.getCrops().getOrDefault(cropName, 0) > 0) {
            boolean success = store.sellCrop(cropName, 1, inventory);

            if (success) {
                showMessage("Sale Successful", "You sold 1 " + cropName + "!");
                updateDisplay();
                gameController.refreshGameState();
            } else {
                showMessage("Sale Failed", "Failed to sell " + cropName);
            }
        } else {
            showMessage("Error", "You don't have any " + cropName + " to sell!");
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) balanceLabel.getScene().getWindow();
        stage.close();
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}