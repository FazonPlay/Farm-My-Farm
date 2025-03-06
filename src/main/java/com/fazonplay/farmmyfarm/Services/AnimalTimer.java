package com.fazonplay.farmmyfarm.Services;

import com.fazonplay.farmmyfarm.Models.Animal;
import javafx.application.Platform;
import javafx.scene.control.Button;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AnimalTimer {
    private final ScheduledExecutorService scheduler;
    private final Map<Button, Animal> animals;

    public AnimalTimer(Map<Button, Animal> animals) {
        this.animals = animals;
        this.scheduler = Executors.newScheduledThreadPool(1);

        // Check animal production every 3 seconds
        scheduler.scheduleAtFixedRate(this::checkAnimals, 3, 3, TimeUnit.SECONDS);
    }

    public void checkAnimals() {
        Platform.runLater(() -> {
            for (Map.Entry<Button, Animal> entry : animals.entrySet()) {
                Button penButton = entry.getKey();
                Animal animal = entry.getValue();

                if (animal != null && animal.isReadyToCollect()) {
                    // Animal is ready to produce
                    animal.setState(Animal.AnimalState.READY);
                    // Update UI
                    penButton.setStyle("-fx-background-color: #7CFC00; -fx-border-color: #999999;");
                }
            }
        });
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}