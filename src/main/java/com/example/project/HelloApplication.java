package com.example.project;
//AUTOR: DIANA MELENA
// SOFTWARE

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializar el sistema de idiomas
        Idiomas.inicializar();
        
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("FXMLLogin.fxml"));
        Pane ventana = fxmlLoader.load();
        Scene scene = new Scene(ventana);
        stage.setScene(scene);
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/ICONS/shop-svgrepo-com.png")));
        stage.setTitle(Idiomas.obtenerMensaje("window.title"));
        stage.setMaximized(false);
        stage.show();
    }
}
