package com.example.project;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
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
    private ComboBox<Producto> combo_producto;
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
    @FXML private ComboBox<Cliente> combo_cliente;
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
        // Cargar productos al iniciar (si se requiere mostrar en tabla de detalle)
        ObservableList<Producto> productos = ProductManager.getInstance().getProductos();
        System.out.println("Productos disponibles: " + productos.size());

        if (combo_producto != null) {
            combo_producto.setItems(productos);
            combo_producto.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Producto p) { return p == null ? "" : p.getProd_cod() + " - " + p.getProd_nombre(); }
                @Override
                public Producto fromString(String s) { return null; }
            });
        }

        if (tabla_detalle != null) {
            tabla_detalle.setItems(detalles);
        }
        // Cargar clientes
        ObservableList<Cliente> clientes = ClienteManager.getInstance().getClientes();
        if (combo_cliente != null) {
            combo_cliente.setItems(clientes);
            combo_cliente.setConverter(new javafx.util.StringConverter<>() {
                @Override public String toString(Cliente c) { return c == null ? "" : c.getCedula() + " - " + c.getApellidos() + " " + c.getNombres(); }
                @Override public Cliente fromString(String s) { return null; }
            });
            combo_cliente.getSelectionModel().selectedItemProperty().addListener((obs, o, c) -> {
                if (c != null) {
                    txt_ci.setText(c.getCedula());
                    txt_nombres.setText(c.getNombres());
                    txt_apellidos.setText(c.getApellidos());
                    txt_telefono.setText(c.getTelefono());
                    txt_correo.setText(c.getCorreo());
                    txt_direccion.setText(c.getDireccion());
                }
            });
        }

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
                        float iva = aplica ? base * 0.12f : 0f;
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
                float baseConDesc = base * (1 - d.getDescuento());
                float iva = d.isAplicaIva() ? baseConDesc * 0.12f : 0f;
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
                        float baseConDesc = base * (1 - d.getDescuento());
                        float iva = d.isAplicaIva() ? baseConDesc * 0.12f : 0f;
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

    private void agregarProductoADetalle() {
        Producto sel = combo_producto.getValue();
        if (sel == null) { System.out.println("Seleccione un producto"); return; }
        float cant = 1f;
        try { if (txt_cantidad != null) cant = Float.parseFloat(txt_cantidad.getText()); } catch (NumberFormatException ignored) {}

        DetalleFactura d = new DetalleFactura();
        d.setPro_id(sel.getProd_id());
        d.setProd_cod(sel.getProd_cod());
        d.setProd_nombre(sel.getProd_nombre());
        d.setCantidad(cant);
        d.setProd_pvp(sel.getProd_pvp());
        d.setAplicaIva(false);
        float base = cant * sel.getProd_pvp();
        float iva = d.isAplicaIva() ? base * 0.15f : 0f;
        d.setTotal(base + iva);
        detalles.add(d);

        recalcularTotales();
    }

    private void recalcularTotales() {
        float subtotalSinIva = 0f;
        float totalIva = 0f;
        for (DetalleFactura d : detalles) {
            float base = d.getCantidad() * d.getProd_pvp();
            float baseConDesc = base * (1 - d.getDescuento());
            float iva = d.isAplicaIva() ? baseConDesc * 0.15f : 0f;
            subtotalSinIva += baseConDesc;
            totalIva += iva;
        }
        float descuento = 0f;
        float total = subtotalSinIva + totalIva - descuento;

        if (txt_subtotal1 != null) txt_subtotal1.setText(String.format("%.2f", subtotalSinIva));
        if (txt_iva != null) txt_iva.setText(String.format("%.2f", totalIva));
        if (txt_descuento != null) txt_descuento.setText(String.format("%.2f", descuento));
        if (txt_total != null) txt_total.setText(String.format("%.2f", total));
    }

    @FXML
    private void acc_grabarFactura() {
        if (combo_cliente == null || combo_cliente.getValue() == null) { mostrarAlerta(AlertType.WARNING, "Cliente requerido", "Seleccione un cliente"); return; }
        if (detalles.isEmpty()) { mostrarAlerta(AlertType.WARNING, "Detalle vacío", "Agregue al menos un producto"); return; }
        if (txt_numFactura != null && (txt_numFactura.getText() == null || txt_numFactura.getText().isBlank())) { mostrarAlerta(AlertType.WARNING, "Número de factura", "Ingrese un número de factura"); return; }

        mostrarAlerta(AlertType.INFORMATION, "Factura guardada", "La factura se ha guardado correctamente");
    }

    @FXML
    private void acc_imprimirFactura() {
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
            String ivaStr = String.format("%.2f", (d.isAplicaIva() ? d.getCantidad()*d.getProd_pvp()*0.12f : 0f));
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
}
