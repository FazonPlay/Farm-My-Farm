package com.fazonplay.farmmyfarm.Controllers;

import com.fazonplay.farmmyfarm.Models.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.Map;
public class StoreController {
    @FXML
    private Label balanceLabel;

    @FXML
    private Label seedInventoryLabel;

    private Store store;
    private Inventory inventory;
    private FinanceManager financeManager;
    private GameController gameController;

    public void initData(Store store, Inventory inventory, FinanceManager financeManager, GameController gameController) {
        this.store = store;
        this.inventory = inventory;
        this.financeManager = financeManager;
        this.gameController = gameController;

        updateDisplay();
    }

    private void updateDisplay() {
        balanceLabel.setText(String.format("$%.2f", financeManager.getBalance()));

        // Update inventory display
        StringBuilder seedsText = new StringBuilder();
        Map<String, Integer> seeds = inventory.getSeeds();

        if (seeds.isEmpty()) {
            seedsText.append("None");
        } else {
            boolean first = true;
            for (Map.Entry<String, Integer> entry : seeds.entrySet()) {
                if (!first) {
                    seedsText.append(", ");
                }
                seedsText.append(entry.getKey()).append(": ").append(entry.getValue());
                first = false;
            }
        }

        seedInventoryLabel.setText(seedsText.toString());
    }

    @FXML
    private void onBuyWheat() {
        buySeed("Wheat");
    }

    @FXML
    private void onBuyCorn() {
        buySeed("Corn");
    }

    @FXML
    private void onBuyCarrot() {
        buySeed("Carrot");
    }

    private void buySeed(String seedName) {
        boolean success = store.buySeed(seedName, inventory);

        if (success) {
            showMessage("Purchase Successful", "You bought " + seedName + " seeds!");
            updateDisplay();
            gameController.refreshGameState();
        } else {
            showMessage("Purchase Failed", "You don't have enough money to buy " + seedName + " seeds.");
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