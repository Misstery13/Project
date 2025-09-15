package com.example.project;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;

public class FacturaController {
    @javafx.fxml.FXML
    private ImageView btn_cancelar;
    @javafx.fxml.FXML
    private Button btn_anular;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, ?> col_codigo;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, ?> col_cantidad;
    @javafx.fxml.FXML
    private AnchorPane datapane_factura;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, ?> col_total;
    @javafx.fxml.FXML
    private ImageView btn_grabar;
    @javafx.fxml.FXML
    private Button btn_imprimir;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, ?> col_pvp;
    @javafx.fxml.FXML
    private TableColumn<DetalleFactura, ?> col_descripcion;

    @FXML
    private TableView<DetalleFactura> tabla_detalle;

    @FXML
    private TableColumn<DetalleFactura, Void> col_acciones;

    @FXML
    public void initialize() {
        if (col_acciones != null) {
            col_acciones.setSortable(false);
            col_acciones.setCellFactory(col -> new TableCell<DetalleFactura, Void>() {
                private final Button actionButton = new Button("Imprimir");

                {
                    actionButton.setOnAction(e -> {
                        TableView<DetalleFactura> tv = getTableView();
                        if (tv != null && tv.getFocusModel() != null) {
                            TablePosition<DetalleFactura, ?> focused = tv.getFocusModel().getFocusedCell();
                            TableColumn<DetalleFactura, ?> selectedColumn = focused != null ? focused.getTableColumn() : null;
                            String columnName = selectedColumn != null ? String.valueOf(selectedColumn.getText()) : "(sin columna)";
                            System.out.println(columnName);
                        } else {
                            System.out.println("(sin columna)");
                        }
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : actionButton);
                }
            });
        }
    }
}
