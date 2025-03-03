package com.fazonplay.farmmyfarm.Application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class Game extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Game.class.getResource
                ("/com/fazonplay/farmmyfarm/grid.fxml"));
        stage.setTitle("I like broly");
        stage.setWidth(300);
        stage.setHeight(250);


        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }
}