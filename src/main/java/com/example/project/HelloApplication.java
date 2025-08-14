package com.example.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Pane ventana = (Pane)loader.load();
        Scene scene=new Scene(ventana);
        stage.setScene(scene);
//        stage.setResizable(true);
        stage.getIcons().add(new Image("/ICONS/diskette.png"));
        stage.setTitle("Sistema -Programacion Visual");
        stage.setMaximized(true);
        stage.show();
    }
}
