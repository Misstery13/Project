package com.example.project;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLPantalla2 implements Initializable {
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
    private TextField txt_precioCompra;
    @javafx.fxml.FXML
    private TextField txt_pvpxmenor;
    @javafx.fxml.FXML
    private TextField txt_pvpxmayor;
    @javafx.fxml.FXML
    private TextField txt_stock;
    @javafx.fxml.FXML
    private javafx.scene.control.CheckBox chk_aplicaIva;
    @javafx.fxml.FXML
    private javafx.scene.control.ChoiceBox<String> chbox_estado;

    private Producto productoEnEdicion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar ChoiceBox de estado
        if (chbox_estado != null) {
            chbox_estado.getItems().setAll("Activo", "Inactivo");
            chbox_estado.setValue("Activo"); // Valor por defecto
        }
        
        if (chk_aplicaIva != null) {
            chk_aplicaIva.setSelected(false); // Valor por defecto
        }

        ap_pantalla2.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (event.isControlDown() && event.getCode() == KeyCode.G) {
                        btn_grabar.fire();
                    }
                });
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
        // Validar campos requeridos
        String cod = txt_codigo.getText() == null ? "" : txt_codigo.getText().trim();
        String nom = txt_nombre.getText() == null ? "" : txt_nombre.getText().trim();
        String precioCompraStr = txt_precioCompra != null && txt_precioCompra.getText() != null ? txt_precioCompra.getText().trim() : "0";
        String pvpXMenorStr = txt_pvpxmenor != null && txt_pvpxmenor.getText() != null ? txt_pvpxmenor.getText().trim() : "";
        String pvpXMayorStr = txt_pvpxmayor != null && txt_pvpxmayor.getText() != null ? txt_pvpxmayor.getText().trim() : "";
        String stockStr = txt_stock != null && txt_stock.getText() != null ? txt_stock.getText().trim() : "0";

        if (cod.isEmpty() || nom.isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Campos requeridos", "Ingrese código y nombre");
            return;
        }
        
        // Si estamos editando, verificar duplicados solo si el código o nombre cambió
        if (productoEnEdicion == null) {
            // Validar código duplicado solo para productos nuevos
            if (ProductManager.getInstance().existeCodigo(cod)) {
                mostrarAlerta(AlertType.ERROR, "Código duplicado", "Ya existe un producto con el mismo código");
                return;
            }
            
            // Validar nombre duplicado solo para productos nuevos
            if (ProductManager.getInstance().existeNombre(nom)) {
                mostrarAlerta(AlertType.ERROR, "Nombre duplicado", "Ya existe un producto con el mismo nombre");
                return;
            }
        } else {
            // Si estamos editando, verificar duplicados solo si cambió el código o nombre
            if (!cod.equals(productoEnEdicion.getProd_cod()) && ProductManager.getInstance().existeCodigo(cod)) {
                mostrarAlerta(AlertType.ERROR, "Código duplicado", "Ya existe un producto con el mismo código");
                return;
            }
            if (!nom.equals(productoEnEdicion.getProd_nombre()) && ProductManager.getInstance().existeNombre(nom)) {
                mostrarAlerta(AlertType.ERROR, "Nombre duplicado", "Ya existe un producto con el mismo nombre");
                return;
            }
        }
        
        // Validar y parsear valores numéricos
        float precioCompra = 0.0f;
        try { 
            if (!precioCompraStr.isEmpty()) precioCompra = Float.parseFloat(precioCompraStr); 
        } catch (NumberFormatException ex) { 
            mostrarAlerta(AlertType.ERROR, "Precio de compra inválido", "Ingrese un número válido"); 
            return; 
        }
        
        float pvpXMenor = 0.0f;
        try { 
            if (!pvpXMenorStr.isEmpty()) pvpXMenor = Float.parseFloat(pvpXMenorStr); 
            else {
                mostrarAlerta(AlertType.WARNING, "PVP por menor requerido", "Ingrese el precio de venta por menor");
                return;
            }
        } catch (NumberFormatException ex) { 
            mostrarAlerta(AlertType.ERROR, "PVP por menor inválido", "Ingrese un número válido"); 
            return; 
        }
        
        float pvpXMayor = 0.0f;
        try { 
            if (!pvpXMayorStr.isEmpty()) pvpXMayor = Float.parseFloat(pvpXMayorStr);
            else pvpXMayor = pvpXMenor;  // Si no se ingresa, usar el mismo que menor
        } catch (NumberFormatException ex) { 
            mostrarAlerta(AlertType.ERROR, "PVP por mayor inválido", "Ingrese un número válido"); 
            return; 
        }
        
        int stock = 0;
        try { 
            if (!stockStr.isEmpty()) stock = Integer.parseInt(stockStr); 
        } catch (NumberFormatException ex) { 
            mostrarAlerta(AlertType.ERROR, "Stock inválido", "Ingrese un número entero válido"); 
            return; 
        }
        
        // Obtener el estado del ComboBox y convertir a código de BD ('A' o 'I') (misma lógica que FXMLPantalla1)
        String estadoSeleccionado = (chbox_estado != null && chbox_estado.getValue() != null) 
                ? chbox_estado.getValue() 
                : "Activo";
        // Convertir "Activo" a "A" y "Inactivo" a "I"
        String estado = "Activo".equals(estadoSeleccionado) ? "A" : "I";
        
        // Obtener el valor de aplica IVA del checkbox
        boolean aplicaIva = chk_aplicaIva != null && chk_aplicaIva.isSelected();

        // Crear o actualizar el producto
        Producto p;
        if (productoEnEdicion != null) {
            // Estamos editando, usar el producto existente
            p = productoEnEdicion;
        } else {
            // Estamos creando uno nuevo
            p = new Producto();
        }
        
        p.setProd_cod(cod);
        p.setProd_nombre(nom);
        p.setProd_precioCompra(precioCompra);
        p.setProd_pvpxmenor(pvpXMenor);
        p.setProd_pvpxmayor(pvpXMayor);
        p.setProd_stock(stock);
        p.setProd_aplicalva(aplicaIva);
        p.setProd_estado(estado);
        
        // Guardar o actualizar el producto
        boolean exito;
        if (productoEnEdicion != null) {
            exito = ProductManager.getInstance().actualizarProducto(p);
            if (exito) {
                mostrarAlerta(AlertType.INFORMATION, "Actualización exitosa", "Producto actualizado correctamente");
            } else {
                mostrarAlerta(AlertType.ERROR, "Error", "No se pudo actualizar el producto");
            }
        } else {
            exito = ProductManager.getInstance().agregarProducto(p);
            if (exito) {
                mostrarAlerta(AlertType.INFORMATION, "Registro exitoso", "Producto registrado correctamente");
            } else {
                mostrarAlerta(AlertType.ERROR, "Error", "No se pudo guardar el producto");
            }
        }
        
        if (exito) {
            // Limpiar el formulario
            limpiarFormulario();
            productoEnEdicion = null;
        }
    }

    /**
     * Carga un producto para edición
     */
    public void cargarProductoParaEdicion(Producto producto) {
        this.productoEnEdicion = producto;
        if (producto != null) {
            txt_codigo.setText(producto.getProd_cod());
            txt_nombre.setText(producto.getProd_nombre());
            if (txt_precioCompra != null) {
                txt_precioCompra.setText(String.valueOf(producto.getProd_precioCompra()));
            }
            if (txt_pvpxmenor != null) {
                txt_pvpxmenor.setText(String.valueOf(producto.getProd_pvpxmenor()));
            }
            if (txt_pvpxmayor != null) {
                txt_pvpxmayor.setText(String.valueOf(producto.getProd_pvpxmayor()));
            }
            if (txt_stock != null) {
                txt_stock.setText(String.valueOf(producto.getProd_stock()));
            }
            if (chk_aplicaIva != null) {
                chk_aplicaIva.setSelected(producto.getProd_aplicalva());
            }
            if (chbox_estado != null) {
                // Convertir estado de BD ('A'/'I') a formato legible ("Activo"/"Inactivo")
                String estado = producto.getProd_estado();
                if ("A".equals(estado) || "Activo".equals(estado)) {
                    chbox_estado.setValue("Activo");
                } else if ("I".equals(estado) || "Inactivo".equals(estado)) {
                    chbox_estado.setValue("Inactivo");
                } else {
                    chbox_estado.setValue("Activo"); // Por defecto
                }
            }
        }
        txt_codigo.requestFocus();
    }
    
    /**
     * Limpia el formulario
     */
    private void limpiarFormulario() {
        txt_codigo.clear();
        txt_nombre.clear();
        if (txt_precioCompra != null) txt_precioCompra.clear();
        if (txt_pvpxmenor != null) txt_pvpxmenor.clear();
        if (txt_pvpxmayor != null) txt_pvpxmayor.clear();
        if (txt_stock != null) txt_stock.clear();
        if (chk_aplicaIva != null) chk_aplicaIva.setSelected(false);
        if (chbox_estado != null) chbox_estado.setValue("Activo");
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
