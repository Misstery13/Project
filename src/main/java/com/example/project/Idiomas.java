package com.example.project;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase para manejar la internacionalización de la aplicación
 * Autor: Diana Melena
 */
public class Idiomas {
    
    private static ResourceBundle mensajes;
    private static Locale idiomaActual = new Locale("es"); // Español por defecto
    
    // Inicializar el sistema de idiomas
    public static void inicializar() {
        cargarIdioma(idiomaActual);
    }
    
    // Cargar idioma específico
    public static void cargarIdioma(Locale locale) {
        idiomaActual = locale;
        mensajes = ResourceBundle.getBundle("messages", locale);
    }
    
    // Obtener mensaje traducido
    public static String obtenerMensaje(String clave) {
        if (mensajes == null) {
            inicializar();
        }
        try {
            return mensajes.getString(clave);
        } catch (Exception e) {
            return clave; // Retornar la clave si no se encuentra la traducción
        }
    }
    
    // Cambiar idioma
    public static void cambiarIdioma(String idioma) {
        Locale nuevoLocale;
        switch (idioma.toLowerCase()) {
            case "en":
            case "english":
                nuevoLocale = new Locale("en");
                break;
            case "es":
            case "spanish":
            default:
                nuevoLocale = new Locale("es");
                break;
        }
        cargarIdioma(nuevoLocale);
    }
    
    // Obtener idioma actual
    public static String obtenerIdiomaActual() {
        return idiomaActual.getLanguage();
    }
    
    // Aplicar idioma a toda la interfaz
    public static void aplicarIdiomaAInterfaz(BorderPane panelRaiz) {
        if (panelRaiz == null) return;
        
        // Aplicar al menú
        MenuBar barraMenu = (MenuBar) panelRaiz.getTop();
        if (barraMenu != null) {
            aplicarIdiomaAMenu(barraMenu);
        }
        
        // Aplicar al panel izquierdo (Accordion)
        Node nodoIzquierdo = panelRaiz.getLeft();
        if (nodoIzquierdo instanceof Accordion) {
            aplicarIdiomaAAccordion((Accordion) nodoIzquierdo);
        }
        
        // Aplicar a la barra inferior
        Node nodoInferior = panelRaiz.getBottom();
        if (nodoInferior instanceof Pane) {
            aplicarIdiomaABarraInferior((Pane) nodoInferior);
        }
    }
    
    // Aplicar idioma al menú
    private static void aplicarIdiomaAMenu(MenuBar barraMenu) {
        for (Menu menu : barraMenu.getMenus()) {
            switch (menu.getText()) {
                case "Administracion":
                case "Administration":
                    menu.setText(obtenerMensaje("menu.administration"));
                    break;
                case "Proceso":
                case "Process":
                    menu.setText(obtenerMensaje("menu.process"));
                    break;
                case "Reportes":
                case "Reports":
                    menu.setText(obtenerMensaje("menu.reports"));
                    break;
                case "Idioma":
                case "Language":
                    menu.setText(obtenerMensaje("menu.language"));
                    break;
            }
            
            // Aplicar a los elementos del menú
            for (MenuItem item : menu.getItems()) {
                switch (item.getText()) {
                    case "Pantalla 1":
                    case "Screen 1":
                        item.setText(obtenerMensaje("menu.pantalla1"));
                        break;
                    case "Pantalla 2":
                    case "Screen 2":
                        item.setText(obtenerMensaje("menu.pantalla2"));
                        break;
                    case "Delete":
                    case "Eliminar":
                        item.setText(obtenerMensaje("menu.delete"));
                        break;
                    case "About":
                    case "Acerca de":
                        item.setText(obtenerMensaje("menu.about"));
                        break;
                    case "Español":
                    case "Spanish":
                        item.setText(obtenerMensaje("menu.language.spanish"));
                        break;
                    case "English":
                    case "Inglés":
                        item.setText(obtenerMensaje("menu.language.english"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma al Accordion
    private static void aplicarIdiomaAAccordion(Accordion accordion) {
        for (TitledPane pane : accordion.getPanes()) {
            switch (pane.getText()) {
                case "Administracion":
                case "Administration":
                    pane.setText(obtenerMensaje("panel.administration"));
                    break;
                case "Proceso":
                case "Process":
                    pane.setText(obtenerMensaje("panel.process"));
                    break;
                case "Reportes":
                case "Reports":
                    pane.setText(obtenerMensaje("panel.reports"));
                    break;
            }
            
            // Aplicar a los botones dentro del panel
            Node contenido = pane.getContent();
            if (contenido instanceof AnchorPane) {
                aplicarIdiomaAAnchorPane((AnchorPane) contenido);
            }
        }
    }
    
    // Aplicar idioma a AnchorPane (contenedor de botones)
    private static void aplicarIdiomaAAnchorPane(AnchorPane anchorPane) {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                switch (button.getText()) {
                    case "Clientes":
                    case "Clients":
                        button.setText(obtenerMensaje("btn.clientes"));
                        break;
                    case "Pantalla 2":
                    case "Screen 2":
                        button.setText(obtenerMensaje("btn.pantalla2"));
                        break;
                    case "Tabla":
                    case "Table":
                        button.setText(obtenerMensaje("btn.tabla"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma a la barra inferior
    private static void aplicarIdiomaABarraInferior(Pane panelInferior) {
        for (Node node : panelInferior.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                switch (label.getText()) {
                    case "Usuario:":
                    case "User:":
                        label.setText(obtenerMensaje("label.user"));
                        break;
                    case "Fecha:":
                    case "Date:":
                        label.setText(obtenerMensaje("label.date"));
                        break;
                    case "Version:":
                    case "Versión:":
                        label.setText(obtenerMensaje("label.version"));
                        break;
                    case "Hora:":
                    case "Time:":
                        label.setText(obtenerMensaje("label.time"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma a pantallas secundarias
    public static void aplicarIdiomaAPantallaSecundaria(AnchorPane pantalla) {
        if (pantalla == null) return;
        
        // Detectar qué tipo de pantalla es y aplicar traducciones correspondientes
        String idPantalla = pantalla.getId();
        if (idPantalla != null) {
            switch (idPantalla) {
                case "p1":
                    aplicarIdiomaAPantalla1(pantalla);
                    break;
                case "pr":
                    aplicarIdiomaAReportes(pantalla);
                    break;
            }
        }
    }
    
    // Aplicar idioma a Pantalla 1 (Registro de Clientes)
    private static void aplicarIdiomaAPantalla1(AnchorPane pantalla) {
        for (Node node : pantalla.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                switch (label.getText()) {
                    case "REGISTRO DE CLIENTES":
                    case "CLIENT REGISTRATION":
                        label.setText(obtenerMensaje("pantalla1.title"));
                        break;
                    case "Cedula:":
                    case "ID:":
                        label.setText(obtenerMensaje("pantalla1.cedula"));
                        break;
                    case "Apellidos":
                    case "Last Names":
                        label.setText(obtenerMensaje("pantalla1.apellidos"));
                        break;
                    case "Nombres:":
                    case "First Names:":
                        label.setText(obtenerMensaje("pantalla1.nombres"));
                        break;
                    case "Direccion:":
                    case "Address:":
                        label.setText(obtenerMensaje("pantalla1.direccion"));
                        break;
                    case "Telefono:":
                    case "Phone:":
                        label.setText(obtenerMensaje("pantalla1.telefono"));
                        break;
                    case "Correo:":
                    case "Email:":
                        label.setText(obtenerMensaje("pantalla1.correo"));
                        break;
                }
            } else if (node instanceof Button) {
                Button button = (Button) node;
                switch (button.getText()) {
                    case "Cancelar":
                    case "Cancel":
                        button.setText(obtenerMensaje("pantalla1.btn.cancelar"));
                        break;
                    case "Grabar":
                    case "Save":
                        button.setText(obtenerMensaje("pantalla1.btn.grabar"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma a Pantalla Reportes
    private static void aplicarIdiomaAReportes(AnchorPane pantalla) {
        for (Node node : pantalla.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                switch (label.getText()) {
                    case "BUSCAR CLIENTE":
                    case "SEARCH CLIENT":
                        label.setText(obtenerMensaje("reportes.title"));
                        break;
                    case "BUSQUEDA POR:":
                    case "SEARCH BY:":
                        label.setText(obtenerMensaje("reportes.busqueda"));
                        break;
                }
            } else if (node instanceof TextField) {
                TextField campoTexto = (TextField) node;
                if ("Ingrese el dato:".equals(campoTexto.getPromptText()) || 
                    "Enter data:".equals(campoTexto.getPromptText())) {
                    campoTexto.setPromptText(obtenerMensaje("reportes.prompt"));
                }
            } else if (node instanceof TableView) {
                TableView tabla = (TableView) node;
                aplicarIdiomaATabla(tabla);
            } else if (node instanceof ChoiceBox) {
                ChoiceBox comboBox = (ChoiceBox) node;
                aplicarIdiomaAComboBox(comboBox);
            }
        }
    }
    
    // Aplicar idioma a TableView
    private static void aplicarIdiomaATabla(TableView tabla) {
        for (Object columna : tabla.getColumns()) {
            if (columna instanceof TableColumn) {
                TableColumn columnaTabla = (TableColumn) columna;
                String textoColumna = columnaTabla.getText();
                switch (textoColumna) {
                    case "Cédula":
                    case "ID":
                        columnaTabla.setText(obtenerMensaje("reportes.col.cedula"));
                        break;
                    case "Apellidos":
                    case "Last Names":
                        columnaTabla.setText(obtenerMensaje("reportes.col.apellidos"));
                        break;
                    case "Nombres":
                    case "First Names":
                        columnaTabla.setText(obtenerMensaje("reportes.col.nombres"));
                        break;
                    case "Dirección":
                    case "Address":
                        columnaTabla.setText(obtenerMensaje("reportes.col.direccion"));
                        break;
                    case "Teléfono":
                    case "Phone":
                        columnaTabla.setText(obtenerMensaje("reportes.col.telefono"));
                        break;
                    case "Correo":
                    case "Email":
                        columnaTabla.setText(obtenerMensaje("reportes.col.correo"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma a ChoiceBox (ComboBox)
    private static void aplicarIdiomaAComboBox(ChoiceBox comboBox) {
        // Limpiar y agregar opciones traducidas
        comboBox.getItems().clear();
        
        if (obtenerIdiomaActual().equals("es")) {
            comboBox.getItems().addAll(
                obtenerMensaje("reportes.combo.cedula"),
                obtenerMensaje("reportes.combo.apellidos"),
                obtenerMensaje("reportes.combo.nombres")
//                obtenerMensaje("reportes.combo.direccion"),
//                obtenerMensaje("reportes.combo.telefono"),
//                obtenerMensaje("reportes.combo.correo")
            );
        } else {
            comboBox.getItems().addAll(
                obtenerMensaje("reportes.combo.cedula"),
                obtenerMensaje("reportes.combo.apellidos"),
                obtenerMensaje("reportes.combo.nombres")
//                obtenerMensaje("reportes.combo.direccion"),
//                obtenerMensaje("reportes.combo.telefono"),
//                obtenerMensaje("reportes.combo.correo")
            );
        }
        
        // Seleccionar primera opción por defecto
        if (!comboBox.getItems().isEmpty()) {
            comboBox.setValue(comboBox.getItems().get(0));
        }
    }
    
    // Métodos de compatibilidad (alias en inglés para mantener compatibilidad)
    public static void initialize() { inicializar(); }
    public static void changeLanguage(String language) { cambiarIdioma(language); }
    public static String getMessage(String key) { return obtenerMensaje(key); }
    public static void applyLanguageToUI(BorderPane rootPane) { aplicarIdiomaAInterfaz(rootPane); }
    public static void applyLanguageToSecondaryScreen(AnchorPane screen) { aplicarIdiomaAPantallaSecundaria(screen); }
}
