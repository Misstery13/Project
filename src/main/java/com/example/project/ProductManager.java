package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductManager {
    private static ProductManager instance;
    private final ObservableList<Producto> productos;

    private ProductManager() {
        this.productos = FXCollections.observableArrayList();
    }

    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    public ObservableList<Producto> getProductos() {
        return productos;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
        System.out.println("Producto agregado. Total: " + productos.size());
    }

    public boolean existeCodigo(String codigo) {
        if (codigo == null) return false;
        String cod = codigo.trim().toLowerCase();
        for (Producto p : productos) {
            if (p.getProd_cod() != null && p.getProd_cod().trim().toLowerCase().equals(cod)) {
                return true;
            }
        }
        return false;
    }

    public boolean existeNombre(String nombre) {
        if (nombre == null) return false;
        String nom = nombre.trim().toLowerCase();
        for (Producto p : productos) {
            if (p.getProd_nombre() != null && p.getProd_nombre().trim().toLowerCase().equals(nom)) {
                return true;
            }
        }
        return false;
    }
}

