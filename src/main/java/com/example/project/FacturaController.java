package com.example.project;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.control.Separator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.transform.Scale;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.collections.ObservableList;
import javafx.stage.Popup;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.geometry.Bounds;
// duplicate import removed

public class FacturaController {
    @javafx.fxml.FXML
    private ImageView btn_cancelar;
    @javafx.fxml.FXML
    private Button btn_anular;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, String> col_codigo;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, String> col_cantidad;
    @javafx.fxml.FXML
    private AnchorPane datapane_factura;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, String> col_total;
    @javafx.fxml.FXML
    private ImageView btn_grabar;
    @javafx.fxml.FXML
    private Button btn_imprimir;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, String> col_pvp;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, String> col_descripcion;
    @FXML
    private TableColumn<DetalleFactura, Boolean> col_aplicaIva;
    @FXML
    private TableColumn<DetalleFactura, String> col_iva;

    @FXML
    private TableView<DetalleFactura> tabla_detalle;

    @FXML
    private TableColumn<DetalleFactura, String> col_acciones;

    @FXML
    private TextField txt_buscar_producto;
    @FXML
    private Button btn_buscar_producto;
    @FXML
    private TextField txt_cantidad;
    @FXML
    private TextField txt_subtotal1;
    @FXML
    private TextField txt_iva;
    @FXML
    private TextField txt_descuento;
    @FXML
    private TextField txt_total;
    @FXML
    private Button btn_agregar;

    private final javafx.collections.ObservableList<DetalleFactura> detalles = FXCollections.observableArrayList();
    // Autocompletado de clientes
    private Popup clienteSuggestionsPopup;
    private ListView<Cliente> clienteSuggestionsList;
    
    @FXML private TextField txt_numFactura;
    @FXML private TextField txt_fecha;
    @FXML private TextField txt_ci;
    @FXML private TextField txt_nombres;
    @FXML private TextField txt_apellidos;
    @FXML private TextField txt_telefono;
    @FXML private TextField txt_correo;
    @FXML private TextField txt_direccion;

    @FXML
    public void initialize() {
        if (txt_fecha != null) {
            txt_fecha.setText(java.time.LocalDate.now().toString());
        }
        if (txt_numFactura != null) {
            txt_numFactura.setText(generarNumeroFactura());
        }
        // Configurar búsqueda de productos
        if (btn_buscar_producto != null) {
            btn_buscar_producto.setOnAction(e -> buscarProducto());
        }
        
        // Permitir búsqueda al presionar Enter en el campo de búsqueda de productos
        if (txt_buscar_producto != null) {
            txt_buscar_producto.setOnAction(e -> buscarProducto());
        }

        if (tabla_detalle != null) {
            tabla_detalle.setItems(detalles);
        }
        
        // Configurar autocompletado de clientes en el campo de cédula
        configurarAutocompletadoCliente();

        // Configurar columnas simples si existen
        if (col_codigo != null) { col_codigo.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getProd_cod()))); }
        if (col_descripcion != null) { col_descripcion.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProd_nombre())); }
        if (col_cantidad != null) { col_cantidad.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCantidad()))); }
        if (col_pvp != null) { col_pvp.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getProd_pvp()))); }
        if (col_total != null) { col_total.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotal()))); }

        // Columna aplica IVA con ChoiceBox por fila
        if (col_aplicaIva != null) {
            col_aplicaIva.setCellFactory(col -> new TableCell<DetalleFactura, Boolean>() {
                private final javafx.scene.control.ChoiceBox<String> choice = new javafx.scene.control.ChoiceBox<>(FXCollections.observableArrayList("SI", "NO"));
                {
                    choice.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
                        DetalleFactura d = getTableView().getItems().get(getIndex());
                        boolean aplica = "SI".equals(n);
                        d.setAplicaIva(aplica);
                        // Recalcular IVA y total de la fila
                        float base = d.getCantidad() * d.getProd_pvp();
                        float iva = aplica ? base * 0.15f : 0f;
                        d.setTotal(base + iva);
                        getTableView().refresh();
                        recalcularTotales();
                    });
                }

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        DetalleFactura d = getTableView().getItems().get(getIndex());
                        choice.getSelectionModel().select(d.isAplicaIva() ? "SI" : "NO");
                        setGraphic(choice);
                    }
                }
            });
        }

        if (col_iva != null) {
            col_iva.setCellValueFactory(data -> {
                DetalleFactura d = data.getValue();
                float base = d.getCantidad() * d.getProd_pvp();
                float descuentoItem = base * d.getDescuento();
                float baseConDesc = base - descuentoItem;
                float iva = d.isAplicaIva() ? baseConDesc * 0.15f : 0f;
                return new SimpleStringProperty(String.format("%.2f", iva));
            });
        }

        if (col_acciones != null) {
            col_acciones.setSortable(false);
            col_acciones.setText("APLICAR DESCUENTO");
            col_acciones.setCellFactory(col -> new TableCell<DetalleFactura, String>() {
                private final javafx.scene.control.ChoiceBox<String> choice = new javafx.scene.control.ChoiceBox<>(
                        FXCollections.observableArrayList("0%", "5%", "10%", "15%", "20%")
                );

                {
                    choice.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                        if (newV == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) return;
                        DetalleFactura d = getTableView().getItems().get(getIndex());
                        float pct = switch (newV) {
                            case "5%" -> 0.05f;
                            case "10%" -> 0.10f;
                            case "15%" -> 0.15f;
                            case "20%" -> 0.20f;
                            default -> 0f;
                        };
                        d.setDescuento(pct);
                        float base = d.getCantidad() * d.getProd_pvp();
                        float descuentoItem = base * d.getDescuento();
                        float baseConDesc = base - descuentoItem;
                        float iva = d.isAplicaIva() ? baseConDesc * 0.15f : 0f;
                        d.setTotal(baseConDesc + iva);
                        getTableView().refresh();
                        recalcularTotales();
                    });
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        DetalleFactura d = getTableView().getItems().get(getIndex());
                        String sel = switch ((int)Math.round(d.getDescuento() * 100)) {
                            case 5 -> "5%";
                            case 10 -> "10%";
                            case 15 -> "15%";
                            case 20 -> "20%";
                            default -> "0%";
                        };
                        choice.getSelectionModel().select(sel);
                        setGraphic(choice);
                    }
                }
            });
        }

        if (btn_agregar != null) {
            btn_agregar.setOnAction(e -> agregarProductoADetalle());
        }
    }

    private String generarNumeroFactura() {

        String prefijo = "FAC";
        String fecha = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String hora = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HHmmss"));
        return prefijo + "-" + fecha + "-" + hora;
    }

    private Producto productoSeleccionado = null;
    
    // Constantes para impresión térmica de 80mm
    private static final int THERMAL_WIDTH = 42; // Ancho máximo recomendado para 80mm
    private static final String ESC = "\u001B"; // Carácter de escape
    private static final String LF = "\n"; // Line feed
    private static final String CR = "\r"; // Carriage return
    private static final String FF = "\f"; // Form feed
    private static final String GS = "\u001D"; // Group separator
    private static final String CAN = "\u0018"; // Cancel
    
    private void agregarProductoADetalle() {
        if (productoSeleccionado == null) { 
            mostrarAlerta(AlertType.WARNING, "Producto requerido", "Busque y seleccione un producto primero"); 
            return; 
        }
        
        float cant = 1f;
        try { if (txt_cantidad != null) cant = Float.parseFloat(txt_cantidad.getText()); } catch (NumberFormatException ignored) {}

        DetalleFactura d = new DetalleFactura();
        d.setPro_id(productoSeleccionado.getProd_id());
        d.setProd_cod(productoSeleccionado.getProd_cod());
        d.setProd_nombre(productoSeleccionado.getProd_nombre());
        d.setCantidad(cant);
        d.setProd_pvp(productoSeleccionado.getProd_pvp());
        d.setAplicaIva(false);
        float base = cant * productoSeleccionado.getProd_pvp();
        float iva = d.isAplicaIva() ? base * 0.15f : 0f;
        d.setTotal(base + iva);
        detalles.add(d);

        // Limpiar campos después de agregar
        if (txt_buscar_producto != null) txt_buscar_producto.clear();
        if (txt_cantidad != null) txt_cantidad.setText("1");
        productoSeleccionado = null;

        recalcularTotales();
    }

    private void recalcularTotales() {
        float subtotalSinIva = 0f;
        float totalIva = 0f;
        float totalDescuento = 0f;
        
        for (DetalleFactura d : detalles) {
            float base = d.getCantidad() * d.getProd_pvp();
            float descuentoItem = base * d.getDescuento(); // Calcular descuento del item
            float baseConDesc = base - descuentoItem; // Base después del descuento
            float iva = d.isAplicaIva() ? baseConDesc * 0.15f : 0f; // IVA sobre base con descuento
            
            subtotalSinIva += baseConDesc;
            totalIva += iva;
            totalDescuento += descuentoItem; // Sumar descuento del item al total
        }
        
        float total = subtotalSinIva + totalIva;

        if (txt_subtotal1 != null) txt_subtotal1.setText(String.format("%.2f", subtotalSinIva));
        if (txt_iva != null) txt_iva.setText(String.format("%.2f", totalIva));
        if (txt_descuento != null) txt_descuento.setText(String.format("%.2f", totalDescuento));
        if (txt_total != null) txt_total.setText(String.format("%.2f", total));
    }

    @FXML
    private void acc_grabarFactura() {
        // Verificar que se haya seleccionado un cliente (campos llenos)
        if (txt_ci == null || txt_ci.getText() == null || txt_ci.getText().isBlank()) { 
            mostrarAlerta(AlertType.WARNING, "Cliente requerido", "Busque y seleccione un cliente primero"); 
            return; 
        }
        if (detalles.isEmpty()) { mostrarAlerta(AlertType.WARNING, "Detalle vacío", "Agregue al menos un producto"); return; }
        if (txt_numFactura != null && (txt_numFactura.getText() == null || txt_numFactura.getText().isBlank())) { mostrarAlerta(AlertType.WARNING, "Número de factura", "Ingrese un número de factura"); return; }

        mostrarAlerta(AlertType.INFORMATION, "Factura guardada", "La factura se ha guardado correctamente");
    }

    @FXML
    private void acc_imprimirFactura() {
        // Mostrar diálogo para elegir tipo de impresión
        ChoiceDialog<String> printDialog = new ChoiceDialog<>("Térmica (80mm)", 
            "Térmica (80mm)", "Estándar (A4)");
        printDialog.setTitle("Tipo de Impresión");
        printDialog.setHeaderText("Seleccione el tipo de impresión");
        printDialog.setContentText("Tipo:");
        
        printDialog.showAndWait().ifPresent(tipo -> {
            if ("Térmica (80mm)".equals(tipo)) {
                imprimirFacturaTermica();
            } else {
                imprimirFacturaEstandar();
            }
        });
    }
    
    private void imprimirFacturaEstandar() {
        Node printable = crearContenidoImprimible();
        Printer printer = Printer.getDefaultPrinter();
        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job == null) return;
        if (!job.showPrintDialog(datapane_factura.getScene().getWindow())) return;

        PageLayout pageLayout = job.getJobSettings().getPageLayout();
        double printableWidth = pageLayout.getPrintableWidth();
        double printableHeight = pageLayout.getPrintableHeight();

        // Calcular escala para ajustar al ancho de página
        printable.applyCss();
        if (printable instanceof javafx.scene.Parent p) {
            p.layout();
        }
        double scaleX = printableWidth / printable.prefWidth(-1);
        double scaleY = printableHeight / Math.max(printable.prefHeight(-1), 1);
        double scale = Math.min(scaleX, scaleY);
        Scale transform = new Scale(scale, scale);
        printable.getTransforms().add(transform);

        boolean success = job.printPage(printable);
        if (success) job.endJob();
        printable.getTransforms().remove(transform);
    }
    
    private void imprimirFacturaTermica() {
        try {
            String contenidoTermico = generarContenidoTermico();
            mostrarPrevisualizacionTermica(contenidoTermico);
                
        } catch (Exception e) {
            mostrarAlerta(AlertType.ERROR, "Error de Impresión", 
                "Error al generar contenido térmico: " + e.getMessage());
        }
    }
    
    private void mostrarPrevisualizacionTermica(String contenido) {
        // Crear diálogo de previsualización
        Dialog<Void> previewDialog = new Dialog<>();
        previewDialog.setTitle("Previsualización - Impresión Térmica (80mm)");
        previewDialog.setHeaderText("Vista previa del formato para impresora térmica");
        
        // Crear área de texto con scroll
        TextArea textArea = new TextArea(contenido);
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setFont(Font.font("Courier New", 12)); // Fuente monospace
        textArea.setPrefRowCount(25);
        textArea.setPrefColumnCount(50);
        
        // Crear scroll pane
        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setPrefSize(600, 500);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        // Crear layout
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new javafx.scene.control.Label("Formato optimizado para impresora térmica de 80mm:"),
            scrollPane
        );
        content.setPadding(new Insets(10));
        
        previewDialog.getDialogPane().setContent(content);
        
        // Agregar botones
        ButtonType imprimirButton = new ButtonType("Imprimir", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelarButton = new ButtonType("Cancelar", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        previewDialog.getDialogPane().getButtonTypes().addAll(imprimirButton, cancelarButton);
        
        // Configurar botón imprimir
        Button imprimirBtn = (Button) previewDialog.getDialogPane().lookupButton(imprimirButton);
        imprimirBtn.setOnAction(e -> {
            // Aquí puedes agregar la lógica real de impresión
            // Por ahora solo mostramos un mensaje
            mostrarAlerta(AlertType.INFORMATION, "Impresión", 
                "Enviando a impresora térmica...\n\n" +
                "Para implementación real, conecta la impresora térmica\n" +
                "y envía el contenido por puerto serie/USB.");
            previewDialog.close();
        });
        
        // Mostrar diálogo
        previewDialog.showAndWait();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
     // IMPRESION DE FACTURA
    private Node crearContenidoImprimible() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(16));

        // Encabezado con logo y título
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        try {
            Image logo = new Image(getClass().getResourceAsStream("/com/example/project/logo.png"));
            ImageView iv = new ImageView(logo);
            iv.setFitHeight(48);
            iv.setPreserveRatio(true);
            header.getChildren().add(iv);
        } catch (Exception ignored) {}
        VBox headerText = new VBox(2);
        Label titulo = new Label("FACTURA");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label numFecha = new Label("N°: " + safeText(txt_numFactura) + "    Fecha: " + safeText(txt_fecha));
        headerText.getChildren().addAll(titulo, numFecha);
        header.getChildren().add(headerText);

        // Datos de cliente
        GridPane gpCliente = new GridPane();
        gpCliente.setHgap(12);
        gpCliente.setVgap(6);
        gpCliente.addRow(0, new Label("CI/RUC:"), new Label(safeText(txt_ci)), new Label("Teléfono:"), new Label(safeText(txt_telefono)));
        gpCliente.addRow(1, new Label("Nombres:"), new Label(safeText(txt_nombres)), new Label("Apellidos:"), new Label(safeText(txt_apellidos)));
        gpCliente.addRow(2, new Label("Correo:"), new Label(safeText(txt_correo)));
        gpCliente.addRow(3, new Label("Dirección:"), new Label(safeText(txt_direccion)));

        // Tabla simple de items (grid)
        GridPane gpItems = new GridPane();
        gpItems.setHgap(8);
        gpItems.setVgap(4);
        gpItems.addRow(0,
                bold("CODIGO"), bold("DESCRIPCION"), bold("CANT"), bold("PVP"), bold("IVA"), bold("TOTAL")
        );
        int r = 1;
        for (DetalleFactura d : detalles) {
            String ivaStr = String.format("%.2f", (d.isAplicaIva() ? d.getCantidad()*d.getProd_pvp()*0.15f : 0f));
            gpItems.addRow(r++,
                    new Label(nullToEmpty(d.getProd_cod())),
                    new Label(nullToEmpty(d.getProd_nombre())),
                    new Label(String.valueOf(d.getCantidad())),
                    new Label(String.format("%.2f", d.getProd_pvp())),
                    new Label(ivaStr),
                    new Label(String.format("%.2f", d.getTotal()))
            );
        }

        // Totales
        VBox footer = new VBox(4);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.getChildren().addAll(
                new Label("Subtotal: " + safeText(txt_subtotal1)),
                new Label("IVA: " + safeText(txt_iva)),
                new Label("Descuento: " + safeText(txt_descuento)),
                bold("Total: " + safeText(txt_total))
        );

        root.getChildren().addAll(header, new Separator(), gpCliente, new Separator(), gpItems, new Separator(), footer);

        
        root.setPrefWidth(595); // A4 width in px @ ~72-96dpi approx
        return root;
    }

    private String safeText(TextField tf) { return tf == null ? "" : nullToEmpty(tf.getText()); }
    private Label bold(String s) { Label l = new Label(s); l.setStyle("-fx-font-weight: bold;"); return l; }
    private String nullToEmpty(String s) { return s == null ? "" : s; }
    
    private String generarContenidoTermico() {
        StringBuilder sb = new StringBuilder();
        
        // Inicializar impresora
        sb.append(ESC).append("@"); // Reset printer
        sb.append(ESC).append("a").append((char)1); // Centrar texto
        
        // Encabezado de la empresa
        sb.append(centrarTexto("FACTURA")).append(LF);
        sb.append(centrarTexto("SISTEMA DE VENTAS")).append(LF);
        sb.append(centrarTexto("================================")).append(LF);
        sb.append(LF);
        
        // Datos de la factura
        sb.append(ESC).append("a").append((char)0); // Alinear izquierda
        sb.append("Factura: ").append(safeText(txt_numFactura)).append(LF);
        sb.append("Fecha: ").append(safeText(txt_fecha)).append(LF);
        sb.append("================================").append(LF);
        sb.append(LF);
        
        // Datos del cliente
        sb.append("CLIENTE:").append(LF);
        sb.append("CI/RUC: ").append(safeText(txt_ci)).append(LF);
        sb.append("Nombre: ").append(safeText(txt_nombres)).append(" ").append(safeText(txt_apellidos)).append(LF);
        sb.append("Tel: ").append(safeText(txt_telefono)).append(LF);
        sb.append("Email: ").append(safeText(txt_correo)).append(LF);
        sb.append("Dir: ").append(truncarTexto(safeText(txt_direccion), THERMAL_WIDTH - 5)).append(LF);
        sb.append("================================").append(LF);
        sb.append(LF);
        
        // Encabezado de productos
        sb.append(ESC).append("E"); // Texto en negrita
        sb.append(String.format("%-8s %-12s %4s %8s %8s", "COD", "DESC", "CANT", "PVP", "TOTAL")).append(LF);
        sb.append(ESC).append("F"); // Texto normal
        sb.append("----------------------------------------").append(LF);
        
        // Detalles de productos
        for (DetalleFactura d : detalles) {
            String codigo = truncarTexto(d.getProd_cod(), 8);
            String descripcion = truncarTexto(d.getProd_nombre(), 12);
            String cantidad = String.format("%.0f", d.getCantidad());
            String pvp = String.format("%.2f", d.getProd_pvp());
            String total = String.format("%.2f", d.getTotal());
            
            sb.append(String.format("%-8s %-12s %4s %8s %8s", codigo, descripcion, cantidad, pvp, total)).append(LF);
            
            // Mostrar descuento si aplica
            if (d.getDescuento() > 0) {
                String descuentoPct = String.format("%.0f%%", d.getDescuento() * 100);
                String descuentoVal = String.format("%.2f", d.getCantidad() * d.getProd_pvp() * d.getDescuento());
                sb.append("  Descuento ").append(descuentoPct).append(": $").append(descuentoVal).append(LF);
            }
            
            // Mostrar IVA si aplica
            if (d.isAplicaIva()) {
                float iva = d.getCantidad() * d.getProd_pvp() * (1 - d.getDescuento()) * 0.15f;
                sb.append("  IVA 15%: $").append(String.format("%.2f", iva)).append(LF);
            }
        }
        
        sb.append("----------------------------------------").append(LF);
        
        // Totales
        sb.append(ESC).append("E"); // Texto en negrita
        float subtotal = parseFloatSafe(safeText(txt_subtotal1));
        float iva = parseFloatSafe(safeText(txt_iva));
        float descuento = parseFloatSafe(safeText(txt_descuento));
        float total = parseFloatSafe(safeText(txt_total));
        
        sb.append(String.format("%-20s $%8.2f", "Subtotal:", subtotal)).append(LF);
        sb.append(String.format("%-20s $%8.2f", "Descuento:", descuento)).append(LF);
        sb.append(String.format("%-20s $%8.2f", "IVA:", iva)).append(LF);
        sb.append("================================").append(LF);
        sb.append(String.format("%-20s $%8.2f", "TOTAL:", total)).append(LF);
        sb.append(ESC).append("F"); // Texto normal
        
        sb.append(LF).append(LF);
        sb.append(centrarTexto("¡Gracias por su compra!")).append(LF);
        sb.append(centrarTexto("Sistema de Ventas v1.0")).append(LF);
        sb.append(LF).append(LF).append(LF);
        
        // Cortar papel
        sb.append(GS).append("V").append((char)0); // Full cut
        
        return sb.toString();
    }
    
    private String centrarTexto(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        int espacios = (THERMAL_WIDTH - texto.length()) / 2;
        if (espacios < 0) espacios = 0;
        return " ".repeat(espacios) + texto;
    }
    
    private String truncarTexto(String texto, int maxLength) {
        if (texto == null) return "";
        if (texto.length() <= maxLength) return texto;
        return texto.substring(0, maxLength - 3) + "...";
    }
    
    private float parseFloatSafe(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }
    
    private void configurarAutocompletadoCliente() {
        if (txt_ci == null) return;
        
        // Inicializar el popup y la lista de sugerencias
        clienteSuggestionsPopup = new Popup();
        clienteSuggestionsList = new ListView<>();
        clienteSuggestionsList.setPrefWidth(500);
        clienteSuggestionsList.setPrefHeight(200);
        clienteSuggestionsList.setStyle("-fx-background-color: white; -fx-border-color: #32a852; -fx-border-width: 2;");
        
        // Configurar cómo se muestra cada cliente en la lista
        clienteSuggestionsList.setCellFactory(lv -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setText(null);
                } else {
                    setText(cliente.getCedula() + " - " + cliente.getNombres() + " " + cliente.getApellidos());
                }
            }
        });
        
        // Al hacer clic en una sugerencia, llenar los datos
        clienteSuggestionsList.setOnMouseClicked(event -> {
            Cliente clienteSeleccionado = clienteSuggestionsList.getSelectionModel().getSelectedItem();
            if (clienteSeleccionado != null) {
                llenarDatosCliente(clienteSeleccionado);
                clienteSuggestionsPopup.hide();
            }
        });
        
        // También permitir selección con Enter
        clienteSuggestionsList.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                Cliente clienteSeleccionado = clienteSuggestionsList.getSelectionModel().getSelectedItem();
                if (clienteSeleccionado != null) {
                    llenarDatosCliente(clienteSeleccionado);
                    clienteSuggestionsPopup.hide();
                }
            } else if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                clienteSuggestionsPopup.hide();
            }
        });
        
        clienteSuggestionsPopup.getContent().add(clienteSuggestionsList);
        
        // Listener para cuando el usuario escribe en el campo de cédula
        txt_ci.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                clienteSuggestionsPopup.hide();
                return;
            }
            
            String criterio = newValue.trim();
            ClienteManager clienteManager = ClienteManager.getInstance();
            
            // Buscar clientes que coincidan con el criterio (por cédula o nombre)
            ObservableList<Cliente> resultados = FXCollections.observableArrayList();
            
            // Buscar por cédula
            Cliente porCedula = clienteManager.buscarClientePorCedula(criterio);
            if (porCedula != null) {
                resultados.add(porCedula);
            }
            
            // También buscar por nombre (en caso de que el usuario escriba parte del nombre)
            ObservableList<Cliente> porNombre = clienteManager.buscarClientes(criterio);
            for (Cliente c : porNombre) {
                if (!resultados.contains(c)) {
                    resultados.add(c);
                }
            }
            
            if (!resultados.isEmpty()) {
                clienteSuggestionsList.setItems(resultados);
                
                // Posicionar el popup debajo del campo de texto
                if (!clienteSuggestionsPopup.isShowing()) {
                    Bounds bounds = txt_ci.localToScreen(txt_ci.getBoundsInLocal());
                    if (bounds != null) {
                        clienteSuggestionsPopup.show(txt_ci, bounds.getMinX(), bounds.getMaxY());
                    }
                }
            } else {
                clienteSuggestionsPopup.hide();
            }
        });
        
        // Ocultar el popup cuando el campo de cédula pierde el foco
        txt_ci.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Pequeño delay para permitir hacer clic en la lista
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    clienteSuggestionsPopup.hide();
                });
            }
        });
    }
    
    private void llenarDatosCliente(Cliente cliente) {
        // Llenar los campos del cliente
        txt_ci.setText(cliente.getCedula());
        txt_nombres.setText(cliente.getNombres());
        txt_apellidos.setText(cliente.getApellidos());
        txt_telefono.setText(cliente.getTelefono());
        txt_correo.setText(cliente.getCorreo());
        txt_direccion.setText(cliente.getDireccion());
    }
    
    @FXML
    private void buscarProducto() {
        if (txt_buscar_producto == null || txt_buscar_producto.getText() == null || txt_buscar_producto.getText().trim().isEmpty()) {
            mostrarAlerta(AlertType.WARNING, "Búsqueda de producto", "Ingrese código o nombre del producto");
            return;
        }
        
        String criterio = txt_buscar_producto.getText().trim();
        ProductManager productManager = ProductManager.getInstance();
        
        // Primero intentar buscar por código exacto (si es código, debe ser único)
        Producto producto = productManager.buscarProductoPorCodigo(criterio);
        
        if (producto != null) {
            // Si se encuentra por código, es único, seleccionar directamente
            seleccionarProducto(producto);
        } else {
            // Buscar por nombre (puede haber múltiples resultados)
            ObservableList<Producto> resultados = productManager.buscarProductos(criterio);
            
            if (resultados.isEmpty()) {
                mostrarAlerta(AlertType.WARNING, "Producto no encontrado", 
                    "No se encontró un producto con el criterio: " + criterio);
            } else if (resultados.size() == 1) {
                // Solo un resultado, seleccionar directamente
                seleccionarProducto(resultados.get(0));
            } else {
                // Múltiples resultados, mostrar diálogo de selección
                mostrarDialogoSeleccionProducto(resultados);
            }
        }
    }
    
    private void seleccionarProducto(Producto producto) {
        // Seleccionar el producto
        productoSeleccionado = producto;
        
        // Mostrar información del producto encontrado
        mostrarAlerta(AlertType.INFORMATION, "Producto encontrado", 
            "Producto: " + producto.getProd_cod() + " - " + producto.getProd_nombre() + 
            "\nPrecio: $" + producto.getProd_pvp());
    }
    
    private void mostrarDialogoSeleccionProducto(ObservableList<Producto> productos) {
        // Crear lista de opciones para el diálogo
        ObservableList<String> opciones = FXCollections.observableArrayList();
        for (Producto producto : productos) {
            opciones.add(producto.getProd_cod() + " - " + producto.getProd_nombre() + " ($" + producto.getProd_pvp() + ")");
        }
        
        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Múltiples productos encontrados");
        dialog.setHeaderText("Se encontraron " + productos.size() + " productos con el criterio de búsqueda");
        dialog.setContentText("Seleccione el producto correcto:");
        
        dialog.showAndWait().ifPresent(seleccion -> {
            // Encontrar el producto seleccionado
            for (Producto producto : productos) {
                String opcion = producto.getProd_cod() + " - " + producto.getProd_nombre() + " ($" + producto.getProd_pvp() + ")";
                if (opcion.equals(seleccion)) {
                    seleccionarProducto(producto);
                    break;
                }
            }
        });
    }
}
