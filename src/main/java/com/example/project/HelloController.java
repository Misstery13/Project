package com.example.project;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//import java.util.stream.Node;

public class HelloController {

    @FXML
    private Label lbl_nombreUsuario;
    @FXML
    private MenuItem menu_Pantalla2;
    @FXML
    private Button btn_pantalla2;
    @FXML
    private Label lbl_version;
    @FXML
    private Label lbl_fecha;
    @FXML
    private MenuItem menu_pantalla1;
    @FXML
    private Button btn_pantalla1;
    @FXML
    private Label lbl_horaActual;
    @FXML
    private Button btn_tabla;
    @FXML
    private StackPane dataPane;
    @FXML
    private Menu menu_language;
    @FXML
    private MenuItem menu_spanish;
    @FXML
    private MenuItem menu_english;

    public void initialize() {

        Idiomas.inicializar();
        

        dataPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getProperties().put("rootController", this);
            }
        });
        actualizarFechaHora();
        //hora dinamica
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> actualizarFechaHora()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void actualizarFechaHora() {
        LocalDateTime ahora = LocalDateTime.now();

        // Formatear fecha (dd/MM/yy)
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yy");
        lbl_fecha.setText(ahora.format(formatoFecha));

        // Formatear hora (HH:mm:ss)
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");
        lbl_horaActual.setText(ahora.format(formatoHora));
    }


    @FXML
    public void acc_menuPantalla1(ActionEvent actionEvent) {
        String pantalla = "/com/example/project/FXMLpantalla1.fxml";
        try {
            AnchorPane a=fun_Animacion(pantalla);
            setDataPane(a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void acc_btnpantalla2(ActionEvent actionEvent) throws IOException {
        String pantalla = "/com/example/project/FXMLpantalla2.fxml";
        AnchorPane a = fun_Animacion(pantalla);
        setDataPane(a);

    }

    @FXML
    public void acc_btnPantalla1(ActionEvent actionEvent) {
        String pantalla="/com/example/project/FXMLpantalla1.fxml";
        try {
            AnchorPane a=fun_Animacion(pantalla);
            setDataPane(a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    public void acc_menuPantalla2(ActionEvent actionEvent) throws  IOException {
        String pantalla = "/com/example/project/FXMLpantalla2.fxml";
        AnchorPane a = fun_Animacion(pantalla);
        setDataPane(a);
    }

    public AnchorPane fun_Animacion(String url) throws IOException {
        AnchorPane anchorPane= FXMLLoader.load(getClass().getResource(url));
        FadeTransition ft=new FadeTransition(Duration.millis(1500));
        ft.setNode(anchorPane);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setAutoReverse(false);
        ft.play();
        return anchorPane;
    }

    public void setDataPane(Node node) {
        dataPane.getChildren().setAll(node);

        // Configurar el StackPane para centrar
        dataPane.setAlignment(javafx.geometry.Pos.CENTER);

        if (node instanceof AnchorPane) {
            AnchorPane anchorPane = (AnchorPane) node;

            // Establecer tamaño fijo y centrar
            anchorPane.setPrefSize(600, 400);
            anchorPane.setMaxSize(600, 400);
            anchorPane.setMinSize(600, 400);

            // Limpiar cualquier anclaje que pueda interferir
            AnchorPane.clearConstraints(anchorPane);
            
            // Aplicar traducciones a la nueva pantalla
            Idiomas.aplicarIdiomaAPantallaSecundaria(anchorPane);
        }
    }

    
    @FXML
    public void acc_btntabla(ActionEvent actionEvent) {
        String pantalla="/com/example/project/FXMLReportes.fxml";
        try {
            AnchorPane a=fun_Animacion(pantalla);
            setDataPane(a);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    // Métodos para cambiar idioma
    @FXML
    public void acc_changeToSpanish(ActionEvent actionEvent) {
        cambiarIdioma("es");
    }
    
    @FXML
    public void acc_changeToEnglish(ActionEvent actionEvent) {
        cambiarIdioma("en");
    }
    
    private void cambiarIdioma(String idioma) {
        // Cambiar el idioma
        Idiomas.cambiarIdioma(idioma);
        
        // Obtener el BorderPane raíz
        BorderPane panelRaiz = (BorderPane) dataPane.getScene().getRoot();
        
        // Aplicar el nuevo idioma a toda la interfaz
        Idiomas.aplicarIdiomaAInterfaz(panelRaiz);
        
        // Aplicar idioma a la pantalla secundaria actual si existe
        aplicarIdiomaAPantallaSecundariaActual();
        
        // Actualizar el título de la ventana
        Stage stage = (Stage) dataPane.getScene().getWindow();
        stage.setTitle(Idiomas.obtenerMensaje("window.title"));
    }
    
    private void aplicarIdiomaAPantallaSecundariaActual() {
        // Verificar si hay una pantalla secundaria cargada
        if (!dataPane.getChildren().isEmpty()) {
            Node nodoActual = dataPane.getChildren().get(0);
            if (nodoActual instanceof AnchorPane) {
                AnchorPane pantallaActual = (AnchorPane) nodoActual;
                Idiomas.aplicarIdiomaAPantallaSecundaria(pantallaActual);
            }
        }
    }
}
