package com.calculatorgui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getClassLoader().getResource("com/calculatorgui/main.fxml"));
        Parent root = fxmlLoader.load();
        root.getStylesheets().add("calculator_style.css");

        primaryStage.setTitle("Calculator");
        primaryStage.setScene(new Scene(root, 320, 470));
        primaryStage.show();
    }

}
