package com.fazonplay.farmmyfarm.Controllers;

import com.fazonplay.farmmyfarm.Models.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Map;

public class AnimalController {
    @FXML
    private Label balanceLabel;

    @FXML
    private GridPane animalsGrid;

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

        // Clear existing grid items
        animalsGrid.getChildren().clear();

        // Headers
        animalsGrid.add(new Label("Animal Type"), 0, 0);
        animalsGrid.add(new Label("Cost"), 1, 0);
        animalsGrid.add(new Label("Product Value"), 2, 0);
        animalsGrid.add(new Label("Actions"), 3, 0);

        int row = 1;
        for (Map.Entry<String, Animal> entry : store.getAvailableAnimals().entrySet()) {
            String animalType = entry.getKey();
            Animal animal = entry.getValue();

            animalsGrid.add(new Label(animalType), 0, row);
            animalsGrid.add(new Label(String.format("$%.2f", animal.getPurchaseCost())), 1, row);
            animalsGrid.add(new Label(String.format("$%.2f", animal.getProductValue())), 2, row);

            Button buyButton = new Button("Buy");
            buyButton.setOnAction(e -> buyAnimal(animalType));
            animalsGrid.add(buyButton, 3, row);

            row++;
        }
    }

    private void buyAnimal(String animalType) {
        boolean success = store.buyAnimal(animalType);

        if (success) {
            gameController.addAnimalToPen(animalType);
            updateDisplay();
            gameController.refreshGameState();
            showMessage("Purchase Successful", "You bought a " + animalType + "!");
        } else {
            showMessage("Purchase Failed", "You don't have enough money!");
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