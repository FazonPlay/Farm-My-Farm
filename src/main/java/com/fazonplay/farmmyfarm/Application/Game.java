package com.fazonplay.farmmyfarm.Application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Game extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource
                ("/com/fazonplay/farmmyfarm/farm.fxml"));

        stage.setTitle("I like broly");
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }
}
//