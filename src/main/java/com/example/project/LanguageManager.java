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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase para manejar la internacionalización de la aplicación
 * Autor: Diana Melena
 */
public class LanguageManager {
    
    private static ResourceBundle messages;
    private static Locale currentLocale = new Locale("es"); // Español por defecto
    
    // Inicializar el sistema de idiomas
    public static void initialize() {
        loadLanguage(currentLocale);
    }
    
    // Cargar idioma específico
    public static void loadLanguage(Locale locale) {
        currentLocale = locale;
        messages = ResourceBundle.getBundle("messages", locale);
    }
    
    // Obtener mensaje traducido
    public static String getMessage(String key) {
        if (messages == null) {
            initialize();
        }
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key; // Retornar la clave si no se encuentra la traducción
        }
    }
    
    // Cambiar idioma
    public static void changeLanguage(String language) {
        Locale newLocale;
        switch (language.toLowerCase()) {
            case "en":
            case "english":
                newLocale = new Locale("en");
                break;
            case "es":
            case "spanish":
            default:
                newLocale = new Locale("es");
                break;
        }
        loadLanguage(newLocale);
    }
    
    // Obtener idioma actual
    public static String getCurrentLanguage() {
        return currentLocale.getLanguage();
    }
    
    // Aplicar idioma a toda la interfaz
    public static void applyLanguageToUI(BorderPane rootPane) {
        if (rootPane == null) return;
        
        // Aplicar al menú
        MenuBar menuBar = (MenuBar) rootPane.getTop();
        if (menuBar != null) {
            applyLanguageToMenuBar(menuBar);
        }
        
        // Aplicar al panel izquierdo (Accordion)
        Node leftNode = rootPane.getLeft();
        if (leftNode instanceof Accordion) {
            applyLanguageToAccordion((Accordion) leftNode);
        }
        
        // Aplicar a la barra inferior
        Node bottomNode = rootPane.getBottom();
        if (bottomNode instanceof Pane) {
            applyLanguageToBottomBar((Pane) bottomNode);
        }
    }
    
    // Aplicar idioma al menú
    private static void applyLanguageToMenuBar(MenuBar menuBar) {
        for (Menu menu : menuBar.getMenus()) {
            switch (menu.getText()) {
                case "Administracion":
                case "Administration":
                    menu.setText(getMessage("menu.administration"));
                    break;
                case "Proceso":
                case "Process":
                    menu.setText(getMessage("menu.process"));
                    break;
                case "Reportes":
                case "Reports":
                    menu.setText(getMessage("menu.reports"));
                    break;
            }
            
            // Aplicar a los elementos del menú
            for (MenuItem item : menu.getItems()) {
                switch (item.getText()) {
                    case "Pantalla 1":
                    case "Screen 1":
                        item.setText(getMessage("menu.pantalla1"));
                        break;
                    case "Pantalla 2":
                    case "Screen 2":
                        item.setText(getMessage("menu.pantalla2"));
                        break;
                    case "Delete":
                    case "Eliminar":
                        item.setText(getMessage("menu.delete"));
                        break;
                    case "About":
                    case "Acerca de":
                        item.setText(getMessage("menu.about"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma al Accordion
    private static void applyLanguageToAccordion(Accordion accordion) {
        for (TitledPane pane : accordion.getPanes()) {
            switch (pane.getText()) {
                case "Administracion":
                case "Administration":
                    pane.setText(getMessage("panel.administration"));
                    break;
                case "Proceso":
                case "Process":
                    pane.setText(getMessage("panel.process"));
                    break;
                case "Reportes":
                case "Reports":
                    pane.setText(getMessage("panel.reports"));
                    break;
            }
            
            // Aplicar a los botones dentro del panel
            Node content = pane.getContent();
            if (content instanceof AnchorPane) {
                applyLanguageToAnchorPane((AnchorPane) content);
            }
        }
    }
    
    // Aplicar idioma a AnchorPane (contenedor de botones)
    private static void applyLanguageToAnchorPane(AnchorPane anchorPane) {
        for (Node node : anchorPane.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                switch (button.getText()) {
                    case "Clientes":
                    case "Clients":
                        button.setText(getMessage("btn.clientes"));
                        break;
                    case "Pantalla 2":
                    case "Screen 2":
                        button.setText(getMessage("btn.pantalla2"));
                        break;
                    case "Tabla":
                    case "Table":
                        button.setText(getMessage("btn.tabla"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma a la barra inferior
    private static void applyLanguageToBottomBar(Pane bottomPane) {
        for (Node node : bottomPane.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                switch (label.getText()) {
                    case "Usuario:":
                    case "User:":
                        label.setText(getMessage("label.user"));
                        break;
                    case "Fecha:":
                    case "Date:":
                        label.setText(getMessage("label.date"));
                        break;
                    case "Version:":
                    case "Versión:":
                        label.setText(getMessage("label.version"));
                        break;
                    case "Hora:":
                    case "Time:":
                        label.setText(getMessage("label.time"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma a pantallas secundarias
    public static void applyLanguageToSecondaryScreen(AnchorPane screen) {
        if (screen == null) return;
        
        // Detectar qué tipo de pantalla es y aplicar traducciones correspondientes
        String screenId = screen.getId();
        if (screenId != null) {
            switch (screenId) {
                case "p1":
                    applyLanguageToPantalla1(screen);
                    break;
                case "pr":
                    applyLanguageToReportes(screen);
                    break;
            }
        }
    }
    
    // Aplicar idioma a Pantalla 1 (Registro de Clientes)
    private static void applyLanguageToPantalla1(AnchorPane screen) {
        for (Node node : screen.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                switch (label.getText()) {
                    case "REGISTRO DE CLIENTES":
                    case "CLIENT REGISTRATION":
                        label.setText(getMessage("pantalla1.title"));
                        break;
                    case "Cedula:":
                    case "ID:":
                        label.setText(getMessage("pantalla1.cedula"));
                        break;
                    case "Apellidos":
                    case "Last Names":
                        label.setText(getMessage("pantalla1.apellidos"));
                        break;
                    case "Nombres:":
                    case "First Names:":
                        label.setText(getMessage("pantalla1.nombres"));
                        break;
                    case "Direccion:":
                    case "Address:":
                        label.setText(getMessage("pantalla1.direccion"));
                        break;
                    case "Telefono:":
                    case "Phone:":
                        label.setText(getMessage("pantalla1.telefono"));
                        break;
                    case "Correo:":
                    case "Email:":
                        label.setText(getMessage("pantalla1.correo"));
                        break;
                }
            } else if (node instanceof Button) {
                Button button = (Button) node;
                switch (button.getText()) {
                    case "Cancelar":
                    case "Cancel":
                        button.setText(getMessage("pantalla1.btn.cancelar"));
                        break;
                    case "Grabar":
                    case "Save":
                        button.setText(getMessage("pantalla1.btn.grabar"));
                        break;
                }
            }
        }
    }
    
    // Aplicar idioma a Pantalla Reportes
    private static void applyLanguageToReportes(AnchorPane screen) {
        for (Node node : screen.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                switch (label.getText()) {
                    case "BUSCAR CLIENTE":
                    case "SEARCH CLIENT":
                        label.setText(getMessage("reportes.title"));
                        break;
                    case "BUSQUEDA POR:":
                    case "SEARCH BY:":
                        label.setText(getMessage("reportes.busqueda"));
                        break;
                }
            } else if (node instanceof TextField) {
                TextField textField = (TextField) node;
                if ("Ingrese el dato:".equals(textField.getPromptText()) || 
                    "Enter data:".equals(textField.getPromptText())) {
                    textField.setPromptText(getMessage("reportes.prompt"));
                }
            } else if (node instanceof TableView) {
                TableView tableView = (TableView) node;
                applyLanguageToTableView(tableView);
            }
        }
    }
    
    // Aplicar idioma a TableView
    private static void applyLanguageToTableView(TableView tableView) {
        for (Object column : tableView.getColumns()) {
            if (column instanceof TableColumn) {
                TableColumn tableColumn = (TableColumn) column;
                String columnText = tableColumn.getText();
                switch (columnText) {
                    case "Cédula":
                    case "ID":
                        tableColumn.setText(getMessage("reportes.col.cedula"));
                        break;
                    case "Apellidos":
                    case "Last Names":
                        tableColumn.setText(getMessage("reportes.col.apellidos"));
                        break;
                    case "Nombres":
                    case "First Names":
                        tableColumn.setText(getMessage("reportes.col.nombres"));
                        break;
                    case "Dirección":
                    case "Address":
                        tableColumn.setText(getMessage("reportes.col.direccion"));
                        break;
                    case "Teléfono":
                    case "Phone":
                        tableColumn.setText(getMessage("reportes.col.telefono"));
                        break;
                    case "Correo":
                    case "Email":
                        tableColumn.setText(getMessage("reportes.col.correo"));
                        break;
                }
            }
        }
    }
}
