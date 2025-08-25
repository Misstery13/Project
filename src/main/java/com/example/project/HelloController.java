package com.example.project;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController {

    @FXML
    private Label lbl_nombreUsuario;
    @FXML
    private MenuItem menu_Pantalla2;
    @FXML
    private Button btn_pantalla2;
    @FXML
    private VBox dataPane;
    @FXML
    private Label lbl_version;
    @FXML
    private Label lbl_fecha;
    @FXML
    private MenuItem menu_pantalla1;
    @FXML
    private Button btn_pantalla1;
    @FXML
    private Label lbl_horaActual;


    @FXML
    public void acc_menuPantalla1(ActionEvent actionEvent) {
        String pantalla = "/com/example/project/FXMLpantalla1.fxml";
        try {
            AnchorPane a=fun_Animacion(pantalla);
            setDataPane(a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void acc_btnpantalla2(ActionEvent actionEvent) throws IOException {
        String pantalla = "/com/example/project/FXMLpantalla2.fxml";
        AnchorPane a = fun_Animacion(pantalla);
        setDataPane(a);

    }

    @FXML
    public void acc_btnPantalla1(ActionEvent actionEvent) {
        String pantalla="/com/example/project/FXMLpantalla1.fxml";
        try {
            AnchorPane a=fun_Animacion(pantalla);
            setDataPane(a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    public void acc_menuPantalla2(ActionEvent actionEvent) throws  IOException {
        String pantalla = "/com/example/project/FXMLpantalla2.fxml";
        AnchorPane a = fun_Animacion(pantalla);
        setDataPane(a);
    }

    public AnchorPane fun_Animacion(String url) throws IOException {
        AnchorPane anchorPane= FXMLLoader.load(getClass().getResource(url));
        FadeTransition ft=new FadeTransition(Duration.millis(1500));
        ft.setNode(anchorPane);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.play();
        return anchorPane;
    }

    public void setDataPane(Node node) {
        dataPane.getChildren().setAll(node);
        dataPane.setPadding(new Insets(100,300,100,300));
    }
}
