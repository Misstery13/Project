package com.example.project;

import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLReportes implements Initializable {
    @javafx.fxml.FXML
    private TableView<Cliente> tabla_clientes;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> col_apellidos;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> col_nombres;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> col_telefono;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> col_correo;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> col_cedula;
    @javafx.fxml.FXML
    private TableColumn<Cliente, String> col_direccion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar las columnas
        col_cedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        col_apellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        col_nombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correo"));

        // Obtener la instancia del manager y vincular los datos
        ClienteManager manager = ClienteManager.getInstance();
        tabla_clientes.setItems(manager.getClientes());

        // Debug mejorado
        System.out.println("=== INICIALIZANDO TABLA ===");
        System.out.println("Manager instance: " + manager);
        System.out.println("Lista de clientes: " + manager.getClientes());
        System.out.println("Número de clientes: " + manager.getClientes().size());

        if (manager.getClientes().size() > 0) {
            System.out.println("Primer cliente: " + manager.getClientes().get(0).getNombres());
        }

        // Forzar actualización
        tabla_clientes.refresh();
    }
}