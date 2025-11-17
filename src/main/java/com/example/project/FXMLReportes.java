package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
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
    private ChoiceBox<String> chbox;
    @javafx.fxml.FXML
    private TextField txt_cliente;

    // Lista observable para los resultados de búsqueda (debe ser una variable de instancia)
    private ObservableList<Cliente> clientesObservable = FXCollections.observableArrayList();
    private SortedList<Cliente> ordenados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Usar lambda expressions para acceder directamente a las propiedades
        // Esto es más seguro que PropertyValueFactory y funciona mejor con JavaFX
        col_cedula.setCellValueFactory(cellData -> cellData.getValue().cedulaProperty());
        col_apellidos.setCellValueFactory(cellData -> cellData.getValue().apellidosProperty());
        col_nombres.setCellValueFactory(cellData -> cellData.getValue().nombresProperty());
        col_direccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());
        col_telefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        col_correo.setCellValueFactory(cellData -> cellData.getValue().correoProperty());

        ClienteManager manager = ClienteManager.getInstance();
        chbox.getItems().setAll("Cédula", "Apellidos", "Nombres", "Todos");
        chbox.getSelectionModel().selectLast(); // Seleccionar "Todos" por defecto
        
        // Cargar todos los clientes inicialmente
        System.out.println("=== INICIALIZANDO TABLA ===");
        manager.recargar(); // Asegurar que los datos estén cargados
        
        // Vincular la lista observable a la tabla (usando SortedList para permitir ordenamiento)
        // IMPORTANTE: Crear el SortedList ANTES de cargar los datos
        ordenados = new SortedList<>(clientesObservable);
        ordenados.comparatorProperty().bind(tabla_clientes.comparatorProperty());
        tabla_clientes.setItems(ordenados);
        
        // Ahora cargar los datos en la lista observable
        clientesObservable.setAll(manager.getClientes());
        System.out.println("Clientes cargados inicialmente: " + clientesObservable.size());
        System.out.println("Items en observable: " + clientesObservable.size());
        System.out.println("Items en SortedList: " + (ordenados != null ? ordenados.size() : 0));
        
        // Verificar que la tabla tenga los items
        int itemsEnTabla = tabla_clientes.getItems() != null ? tabla_clientes.getItems().size() : 0;
        System.out.println("Items en tabla (inicial): " + itemsEnTabla);
        
        // Listener para búsqueda en tiempo real desde la BD
        txt_cliente.textProperty().addListener((observable, oldValue, newValue) -> {
            String campoSeleccionado = chbox.getValue();
            if (campoSeleccionado == null) {
                campoSeleccionado = "Todos"; // Valor por defecto
            }
            System.out.println("Texto cambiado: '" + newValue + "', Campo: '" + campoSeleccionado + "'");
            buscarEnBaseDatos(newValue, campoSeleccionado);
        });
        
        chbox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String textoBusqueda = txt_cliente.getText();
            if (newValue == null) {
                newValue = "Todos"; // Valor por defecto
            }
            System.out.println("Campo cambiado: '" + newValue + "', Texto: '" + textoBusqueda + "'");
            buscarEnBaseDatos(textoBusqueda, newValue);
        });

        // Debug mejorado
        System.out.println("Manager instance: " + manager);
        System.out.println("Número de clientes en manager: " + manager.getClientes().size());
        System.out.println("Número de clientes en observable: " + clientesObservable.size());

        if (clientesObservable.size() > 0) {
            Cliente primerCliente = clientesObservable.get(0);
            System.out.println("Primer cliente: " + primerCliente.getNombres() + " " + primerCliente.getApellidos());
            System.out.println("  - Cédula: " + primerCliente.getCedula());
            System.out.println("  - Dirección: " + primerCliente.getDireccion());
        }

        // Forzar actualización de la tabla varias veces para asegurar que se muestre
        tabla_clientes.refresh();
        
        // Verificar nuevamente después del refresh
        itemsEnTabla = tabla_clientes.getItems() != null ? tabla_clientes.getItems().size() : 0;
        System.out.println("Items en tabla (después de refresh): " + itemsEnTabla);
        
        // Si aún no hay items pero debería haberlos, forzar actualización manual
        if (itemsEnTabla == 0 && !clientesObservable.isEmpty()) {
            System.out.println("⚠ ADVERTENCIA: La tabla está vacía pero debería tener " + clientesObservable.size() + " clientes");
            System.out.println("  Forzando actualización manual...");
            // Recrear el SortedList
            ordenados = new SortedList<>(clientesObservable);
            ordenados.comparatorProperty().bind(tabla_clientes.comparatorProperty());
            tabla_clientes.setItems(ordenados);
            tabla_clientes.refresh();
            
            itemsEnTabla = tabla_clientes.getItems() != null ? tabla_clientes.getItems().size() : 0;
            System.out.println("  Items después de forzar: " + itemsEnTabla);
        }
        
        if (itemsEnTabla > 0) {
            System.out.println("✓ Tabla inicializada correctamente con " + itemsEnTabla + " clientes");
        } else {
            System.out.println("⚠ Tabla vacía después de la inicialización");
        }

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
//        Stage newStage=new Stage();
//        newStage.initModality(Modality.APPLICATION_MODAL);
//        newStage.setResizable(false);


    }

    /**
     * Busca clientes en la base de datos según el criterio y campo seleccionado
     */
    private void buscarEnBaseDatos(String texto, String campo) {
        System.out.println("\n=== INICIANDO BÚSQUEDA ===");
        System.out.println("Texto recibido: '" + texto + "'");
        System.out.println("Campo recibido: '" + campo + "'");
        
        ClienteManager manager = ClienteManager.getInstance();
        ObservableList<Cliente> resultados = FXCollections.observableArrayList();
        
        String criterio = texto == null ? "" : texto.trim();
        String campoBusqueda = campo == null ? "Todos" : campo;
        
        // Si el campo de búsqueda está vacío, mostrar todos los clientes
        if (criterio.isEmpty()) {
            System.out.println("Criterio vacío - Cargando todos los clientes...");
            manager.recargar(); // Recargar desde BD
            resultados = manager.getClientes();
            System.out.println("✓ Todos los clientes cargados: " + resultados.size());
        } else {
            // Buscar según el campo seleccionado
            System.out.println("Buscando con criterio: '" + criterio + "', Campo: '" + campoBusqueda + "'");
            
            try {
                if ("Cédula".equals(campoBusqueda)) {
                    // Buscar por cédula parcial (contiene el texto)
                    System.out.println("Buscando por cédula (parcial)...");
                    resultados = manager.buscarPorCedula(criterio);
                } else if ("Apellidos".equals(campoBusqueda)) {
                    System.out.println("Buscando por apellidos...");
                    resultados = manager.buscarPorApellidos(criterio);
                } else if ("Nombres".equals(campoBusqueda)) {
                    System.out.println("Buscando por nombres...");
                    resultados = manager.buscarPorNombres(criterio);
                } else {
                    // "Todos" - buscar en todos los campos
                    System.out.println("Buscando en todos los campos...");
                    resultados = manager.buscarClientes(criterio);
                }
                
                System.out.println("✓ Búsqueda completada. Resultados encontrados: " + resultados.size());
                
                // Mostrar detalles de los resultados
                if (resultados.size() > 0) {
                    System.out.println("Primeros resultados:");
                    for (int i = 0; i < Math.min(3, resultados.size()); i++) {
                        Cliente c = resultados.get(i);
                        System.out.println("  " + (i+1) + ". " + c.getNombres() + " " + c.getApellidos() + " (Cédula: " + c.getCedula() + ")");
                    }
                } else {
                    System.out.println("⚠ No se encontraron resultados para: '" + criterio + "'");
                }
            } catch (Exception e) {
                System.err.println("✗ Error durante la búsqueda: " + e.getMessage());
                e.printStackTrace();
                resultados = FXCollections.observableArrayList();
            }
        }
        
        // Actualizar la lista observable (esto actualizará automáticamente la tabla)
        System.out.println("Actualizando tabla con " + resultados.size() + " clientes...");
        
        // Limpiar y agregar resultados de forma que notifique los cambios
        clientesObservable.clear();
        if (!resultados.isEmpty()) {
            clientesObservable.addAll(resultados);
            System.out.println("✓ " + resultados.size() + " clientes agregados a la lista observable");
        } else {
            System.out.println("⚠ Lista observable vaciada (sin resultados)");
        }
        
        // Verificar que el SortedList tenga los items (debería actualizarse automáticamente)
        if (ordenados != null) {
            System.out.println("Items en SortedList: " + ordenados.size());
        }
        
        // Verificar que la tabla tenga los items ANTES de refresh
        int itemsEnTabla = tabla_clientes.getItems() != null ? tabla_clientes.getItems().size() : 0;
        System.out.println("Items en tabla (antes de refresh): " + itemsEnTabla);
        
        // Forzar actualización visual de la tabla
        tabla_clientes.refresh();
        
        // Verificar que la tabla tenga los items DESPUÉS de refresh
        itemsEnTabla = tabla_clientes.getItems() != null ? tabla_clientes.getItems().size() : 0;
        System.out.println("Items en tabla (después de refresh): " + itemsEnTabla);
        System.out.println("Items en observable: " + clientesObservable.size());
        
        // Si la tabla no tiene items pero debería tenerlos, forzar actualización
        if (itemsEnTabla == 0 && !resultados.isEmpty()) {
            System.out.println("⚠ ADVERTENCIA: La tabla está vacía pero debería tener " + resultados.size() + " clientes");
            System.out.println("  Forzando actualización manual de la tabla...");
            // Recrear el SortedList y vincularlo de nuevo
            ordenados = new SortedList<>(clientesObservable);
            ordenados.comparatorProperty().bind(tabla_clientes.comparatorProperty());
            tabla_clientes.setItems(ordenados);
            tabla_clientes.refresh();
            
            // Verificar nuevamente
            itemsEnTabla = tabla_clientes.getItems() != null ? tabla_clientes.getItems().size() : 0;
            System.out.println("  Items después de forzar actualización: " + itemsEnTabla);
        }
        
        if (itemsEnTabla > 0) {
            System.out.println("✓ Tabla actualizada correctamente con " + itemsEnTabla + " clientes");
        } else if (resultados.isEmpty()) {
            System.out.println("⚠ No hay resultados para mostrar");
        }
        
        System.out.println("=== FIN BÚSQUEDA ===\n");
    }
    
    
}
