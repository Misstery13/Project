package com.example.project;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLPantalla1 implements javafx.fxml.Initializable {
    @javafx.fxml.FXML
    private AnchorPane ap_pantalla1;
    @javafx.fxml.FXML
    private Button btn_cancelar;
    @javafx.fxml.FXML
    private TextField txt_correo;
    @javafx.fxml.FXML
    private TextField txt_telefono;
    @javafx.fxml.FXML
    private TextField txt_cedula;
    @javafx.fxml.FXML
    private TextField txt_apellidos;
    @javafx.fxml.FXML
    private TextField txt_nombres;
    @javafx.fxml.FXML
    private TextArea txt_direccion;
    @javafx.fxml.FXML
    private Button btn_grabar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ap_pantalla1.sceneProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> obs, javafx.scene.Scene oldScene, javafx.scene.Scene newScene) {
                if (newScene != null) {
                    newScene.setOnKeyPressed(event -> {
                        if (event.isControlDown() && event.getCode() == KeyCode.G) {
                            btn_grabar.fire();
                        }
                    });
                }
            }
        });
        txt_cedula.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER) {
                txt_apellidos.requestFocus();
                evento.consume();
            }
        });


        txt_apellidos.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                txt_nombres.requestFocus();
                evento.consume();
            }
        });

        txt_nombres.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                txt_direccion.requestFocus();
                evento.consume();
            }
        });

        txt_direccion.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                txt_telefono.requestFocus();
                evento.consume();
            }
        });

        txt_telefono.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                txt_correo.requestFocus();
                evento.consume();
            }
        });

        txt_correo.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                btn_grabar.requestFocus();
                evento.consume();
            }
        });

        txt_cedula.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                System.out.println("Validar desde el focus");
            }
        });

        // Si fun_detectarTecla solo acepta dos argumentos:
        Mod_general.fun_detectarTecla(txt_cedula, KeyCode.ENTER, txt_apellidos);
        Mod_general.fun_detectarTecla(txt_apellidos, KeyCode.ENTER, txt_nombres);
        Mod_general.fun_detectarTecla(txt_nombres, KeyCode.ENTER, txt_direccion);
        Mod_general.fun_detectarTecla(txt_direccion,KeyCode.ENTER, txt_telefono);
        Mod_general.fun_detectarTecla(txt_telefono,KeyCode.ENTER, txt_correo);
        Mod_general.fun_detectarTecla(txt_correo,KeyCode.ENTER, btn_grabar);
    }

    @javafx.fxml.FXML
    public void acc_btncancelar(ActionEvent actionEvent) {}

    @javafx.fxml.FXML
    public void acc_txtapellidos(ActionEvent actionEvent) {}

    @javafx.fxml.FXML
    public void acc_txtcedula(ActionEvent actionEvent) {}

    @javafx.fxml.FXML
    public void acc_txtcorreo(ActionEvent actionEvent) {}

    @javafx.fxml.FXML
    public void acc_txtnombres(ActionEvent actionEvent) {}

    @javafx.fxml.FXML
    public void acc_txtelefono(ActionEvent actionEvent) {}

    @javafx.fxml.FXML
    public void acc_btngrabar(ActionEvent actionEvent) {
        if (txt_cedula.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo requerido");
            alert.setHeaderText(null);
            alert.setContentText("La cédula es obligatoria.");
            alert.showAndWait();
            txt_cedula.requestFocus();
            return;
        }

        if (txt_nombres.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo requerido");
            alert.setHeaderText(null);
            alert.setContentText("El nombre es obligatorio.");
            alert.showAndWait();
            txt_nombres.requestFocus();
            return;
        }

        if (txt_apellidos.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campo requerido");
            alert.setHeaderText(null);
            alert.setContentText("Los apellidos son obligatorios.");
            alert.showAndWait();
            txt_apellidos.requestFocus();
            return;
        }

        // Crear el cliente con datos validados
        Cliente nuevoCliente = new Cliente(
                txt_cedula.getText().trim(),
                txt_apellidos.getText().trim(),
                txt_nombres.getText().trim(),
                txt_direccion.getText().trim(),
                txt_telefono.getText().trim(),
                txt_correo.getText().trim()
        );

        ClienteManager.getInstance().agregarCliente(nuevoCliente);

        // Debug para confirmar que se guardó
        System.out.println("=== CLIENTE GUARDADO CON CTRL+G ===");
        System.out.println("Cédula: " + nuevoCliente.getCedula());
        System.out.println("Nombres: " + nuevoCliente.getNombres());
        System.out.println("Apellidos: " + nuevoCliente.getApellidos());
        System.out.println("Total clientes: " + ClienteManager.getInstance().getClientes().size());

        // Limpiar los campos después de guardar
        txt_cedula.clear();
        txt_apellidos.clear();
        txt_nombres.clear();
        txt_direccion.clear();
        txt_telefono.clear();
        txt_correo.clear();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("Cliente agregado exitosamente.");
        alert.showAndWait();

        // Regresar el foco al primer campo
        txt_cedula.requestFocus();
    }
}