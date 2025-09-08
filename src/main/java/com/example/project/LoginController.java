package com.example.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<Session.Role> cbRol;
    @FXML
    private Button btnIngresar;

    public void initialize() {
        cbRol.getItems().setAll(Session.Role.values());
        cbRol.getSelectionModel().select(Session.Role.BODEGUERO);
    }

    @FXML
    public void acc_ingresar(ActionEvent e) throws IOException {
        String usuario = txtUsuario.getText() == null ? "" : txtUsuario.getText().trim();
        Session.Role rol = cbRol.getValue();

        if (usuario.isEmpty() || rol == null) {
            return;
        }

        Session.start(usuario, rol);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Pane ventana = fxmlLoader.load();

        Stage stage = (Stage) btnIngresar.getScene().getWindow();
        stage.setScene(new Scene(ventana));
        stage.setTitle(Idiomas.obtenerMensaje("window.title"));
        stage.show();
    }
}


