package com.example.project;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
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
    }
}
