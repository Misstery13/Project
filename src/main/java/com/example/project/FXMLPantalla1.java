package com.example.project;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLPantalla1 {
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

    public void initialize(URL url, ResourceBundle rb) {
        this.txt_cedula.setOnKeyPressed(evento->{
            if(evento.getCode()== KeyCode.ENTER){
                System.out.println("tecla enter en txt_cedula");
                //fun_validar cedula
                this.txt_apellidos.requestFocus();
                evento.consume();
            }
        });
        this.txt_apellidos.setOnKeyPressed(evento->{
            if(evento.getCode()== KeyCode.ENTER || evento.getCode() == KeyCode.TAB){
                System.out.println("tecla enter en txt_cedula");
                //fun_validar cedula
                this.txt_nombres.requestFocus();
                evento.consume();
            }
            //evento cuando pierde el focus
        this.txt_cedula.focusedProperty().addListener((obs,oldVal,newVal) ->{
            if (!newVal) {
                System.out.println("Validar desde el focus");

            }

                this.txt_apellidos.setOnKeyPressed(evento->{
                    if(evento.getCode()== KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                        System.out.println("tecla enter en txt_apellidos");
                        //fun_validar cedula
                        this.txt_direccion.requestFocus();
                        evento.consume();
                    }

                    this.txt_direccion.setOnKeyPressed(evento->{
                        if(evento.getCode()== KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                            System.out.println("tecla enter en txt_apellidos");
                            //fun_validar cedula
                            this.txt_telefono.requestFocus();
                            evento.consume();
                        }
                        this.txt_telefono.setOnKeyPressed(evento->{
                            if(evento.getCode()== KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                                System.out.println("tecla enter en txt_apellidos");
                                //fun_validar cedula
                                this.txt_correo.requestFocus();
                                evento.consume();
                            }

                        });
                        this.txt_correo.setOnKeyPressed(evento->{
                            if(evento.getCode()== KeyCode.ENTER || evento.getCode() == KeyCode.TAB) {
                                System.out.println("tecla enter en txt_apellidos");
                                //fun_validar cedula
                                this.btn_grabar.requestFocus();
                                evento.consume();
                            }

                        });
                        this.txt_cedula.requestFocus();
                        Mod_general.fun_detectarTecla(txt_cedula, KeyCode.ENTER,txt_apellidos);
                        Mod_general.fun_detectarTecla(txt_apellidos,KeyCode.ENTER,txt_nombres);
                        Mod_general.fun_detectarTecla(txt_nombres,KeyCode.ENTER,txt_direccion);
                        Mod_general.fun_detectarTecla(this.txt_direccion,KeyCode.ENTER,this.txt_telefono);
                        Mod_general.fun_detectarTecla(txt_telefono,KeyCode.ENTER,this.txt_correo);
                        Mod_general.fun_detectarTecla(txt_correo,  KeyCode.ENTER,this.btn_grabar);


//    @javafx.fxml.FXML
//    public void acc_btncancelar(ActionEvent actionEvent) {
//    }
//
//    @javafx.fxml.FXML
//    public void acc_txtapellidos(ActionEvent actionEvent) {
//    }
//
//    @javafx.fxml.FXML
//    public void acc_txtcedula(ActionEvent actionEvent) {
//    }
//
//    @javafx.fxml.FXML
//    public void acc_txtcorreo(ActionEvent actionEvent) {
//    }
//
//    @javafx.fxml.FXML
//    public void acc_txtnombres(ActionEvent actionEvent) {
//    }
//
//    @javafx.fxml.FXML
//    public void acc_txtelefono(ActionEvent actionEvent) {
//    }
//
//    @javafx.fxml.FXML
//    public void acc_btngrabar(ActionEvent actionEvent) {
//    }


    @javafx.fxml.FXML
    public void acc_txtapellidos(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void acc_txtcedula(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void acc_btngrabar(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void acc_txtcorreo(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void acc_txtnombres(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void acc_btncancelar(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void acc_txtelefono(ActionEvent actionEvent) {
    }