package com.example.project;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FXMLPantalla2 {
    @javafx.fxml.FXML
    private Button btn_cancelar;
    @javafx.fxml.FXML
    private AnchorPane ap_pantalla2;
    @javafx.fxml.FXML
    private Button btn_grabar;
    @javafx.fxml.FXML
    private TextField txt_codigo;
    @javafx.fxml.FXML
    private TextField txt_nombre;
    @javafx.fxml.FXML
    private TextField txt_pvp;

    public void initialize(java.net.URL url, java.util.ResourceBundle resourceBundle) {
        ap_pantalla2.sceneProperty().addListener(new javafx.beans.value.ChangeListener<>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends javafx.scene.Scene> obs, javafx.scene.Scene oldScene, javafx.scene.Scene newScene) {
                if (newScene != null) {
                    newScene.setOnKeyPressed(event -> {
                        if (event.isControlDown() && event.getCode() == javafx.scene.input.KeyCode.G) {
                            btn_grabar.fire();

                        }
                    });
                }
            }
        });
    }

    @javafx.fxml.FXML
    public void acc_btncancelar(ActionEvent actionEvent) {
        try {
            Stage myStage=(Stage) this.btn_cancelar.getScene().getWindow();
            myStage.close();
        } catch (Exception e) {
            System.out.println(""+e.getMessage());
        }
    }

    @javafx.fxml.FXML
    public void acc_btngrabar(ActionEvent actionEvent) {
        String cod = txt_codigo.getText() == null ? "" : txt_codigo.getText().trim();
        String nom = txt_nombre.getText() == null ? "" : txt_nombre.getText().trim();
        String pvpStr = txt_pvp.getText() == null ? "" : txt_pvp.getText().trim();

        if (cod.isEmpty() || nom.isEmpty() || pvpStr.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campos requeridos", "Ingrese código, nombre y PVP");
            return;
        }
        if (ProductManager.getInstance().existeCodigo(cod)) {
            mostrarAlerta(AlertType.ERROR, "Código duplicado", "Ya existe un producto con el mismo código");
            return;
        }
        if (ProductManager.getInstance().existeNombre(nom)) {
            mostrarAlerta(AlertType.ERROR, "Nombre duplicado", "Ya existe un producto con el mismo nombre");
            return;
        }
        float pvp;
        try { pvp = Float.parseFloat(pvpStr); }
        catch (NumberFormatException ex) { mostrarAlerta(AlertType.ERROR, "PVP inválido", "Ingrese un número válido"); return; }

        Producto p = new Producto();
        p.setProd_cod(cod);
        p.setProd_nombre(nom);
        p.setProd_pvp(pvp);
        ProductManager.getInstance().agregarProducto(p);

        mostrarAlerta(AlertType.INFORMATION, "Registro exitoso", "Producto registrado correctamente");
        txt_codigo.clear();
        txt_nombre.clear();
        txt_pvp.clear();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
