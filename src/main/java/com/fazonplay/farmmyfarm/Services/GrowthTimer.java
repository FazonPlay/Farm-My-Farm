package com.fazonplay.farmmyfarm.Services;

import com.fazonplay.farmmyfarm.Models.Crop;
import javafx.application.Platform;
import javafx.scene.control.Button;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GrowthTimer {
    private final ScheduledExecutorService scheduler;
    private final Map<Button, Crop> growingCrops;

    public GrowthTimer(Map<Button, Crop> growingCrops) {
        this.growingCrops = growingCrops;
        this.scheduler = Executors.newScheduledThreadPool(1);

        // check crops growth status every 3 seconds
        scheduler.scheduleAtFixedRate(this::checkCrops, 3, 3, TimeUnit.SECONDS);

    }

    public void checkCrops() {
        Platform.runLater(() -> {
            for (Map.Entry<Button, Crop> entry : growingCrops.entrySet()) {
                Button plotButton = entry.getKey();
                Crop crop = entry.getValue();

                if (crop != null && (crop.getState() == Crop.CropState.SEEDS ||
                        crop.getState() == Crop.CropState.GROWING) &&
                        crop.isReadyToHarvest()) {
                    crop.setState(Crop.CropState.MATURE);
                    plotButton.setStyle("-fx-background-color: #7CFC00; -fx-border-color: #999999;");
                }
            }
        });
    }
}