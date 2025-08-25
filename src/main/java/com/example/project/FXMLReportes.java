package com.example.project;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    @javafx.fxml.FXML
    private ChoiceBox chbox;
    @javafx.fxml.FXML
    private TextField txt_cliente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        col_cedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        col_apellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        col_nombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        col_direccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        col_telefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        col_correo.setCellValueFactory(new PropertyValueFactory<>("correo"));


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


        tabla_clientes.refresh();

        // Doble clic para editar en pantalla1
        tabla_clientes.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                Cliente seleccionado = tabla_clientes.getSelectionModel().getSelectedItem();
                if (seleccionado != null)
                    System.out.println("click");
                {
                    try {
                        AnchorPane pantalla1 = FXMLLoader.load(getClass().getResource("/com/example/project/FXMLpantalla1.fxml"));
                        // Obtener el controlador para cargar el cliente
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/project/FXMLpantalla1.fxml"));
                        AnchorPane pane = loader.load();
                        FXMLPantalla1 controlador = loader.getController();
                        controlador.cargarClienteParaEdicion(seleccionado);


                        Scene scene = ((Node) event.getSource()).getScene();
                        HelloController controllerRoot = (HelloController) scene.getProperties().get("rootController");
                        if (controllerRoot != null) {
                            controllerRoot.setDataPane(pane);
                        } else {

                            ((AnchorPane) tabla_clientes.getParent()).getChildren().setAll(pane);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Stage newStage=new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setResizable(false);


    }
}