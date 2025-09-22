package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductManager {
    private static ProductManager instance;
    private final ObservableList<Producto> productos;

    private ProductManager() {
        this.productos = FXCollections.observableArrayList();
        
        // Agregar productos de prueba para probar la funcionalidad de búsqueda
        Producto p1 = new Producto();
        p1.setProd_id(1);
        p1.setProd_cod("LAP001");
        p1.setProd_nombre("Laptop HP");
        p1.setProd_pvp(899.99f);
        p1.setProd_stock(50);
        p1.setProd_estado("Activo");
        productos.add(p1);
        
        Producto p2 = new Producto();
        p2.setProd_id(2);
        p2.setProd_cod("LAP002");
        p2.setProd_nombre("Laptop Dell");
        p2.setProd_pvp(799.99f);
        p2.setProd_stock(30);
        p2.setProd_estado("Activo");
        productos.add(p2);
        
        Producto p3 = new Producto();
        p3.setProd_id(3);
        p3.setProd_cod("LAP003");
        p3.setProd_nombre("Laptop Lenovo");
        p3.setProd_pvp(699.99f);
        p3.setProd_stock(25);
        p3.setProd_estado("Activo");
        productos.add(p3);
        
        Producto p4 = new Producto();
        p4.setProd_id(4);
        p4.setProd_cod("MON001");
        p4.setProd_nombre("Monitor Samsung");
        p4.setProd_pvp(299.99f);
        p4.setProd_stock(40);
        p4.setProd_estado("Activo");
        productos.add(p4);
        
        Producto p5 = new Producto();
        p5.setProd_id(5);
        p5.setProd_cod("MON002");
        p5.setProd_nombre("Monitor LG");
        p5.setProd_pvp(249.99f);
        p5.setProd_stock(35);
        p5.setProd_estado("Activo");
        productos.add(p5);
        
        Producto p6 = new Producto();
        p6.setProd_id(6);
        p6.setProd_cod("TEC001");
        p6.setProd_nombre("Teclado Logitech");
        p6.setProd_pvp(49.99f);
        p6.setProd_stock(100);
        p6.setProd_estado("Activo");
        productos.add(p6);
        
        Producto p7 = new Producto();
        p7.setProd_id(7);
        p7.setProd_cod("TEC002");
        p7.setProd_nombre("Teclado Razer");
        p7.setProd_pvp(89.99f);
        p7.setProd_stock(60);
        p7.setProd_estado("Activo");
        productos.add(p7);
        
        Producto p8 = new Producto();
        p8.setProd_id(8);
        p8.setProd_cod("MOU001");
        p8.setProd_nombre("Mouse Inalámbrico");
        p8.setProd_pvp(29.99f);
        p8.setProd_stock(80);
        p8.setProd_estado("Activo");
        productos.add(p8);
        
        Producto p9 = new Producto();
        p9.setProd_id(9);
        p9.setProd_cod("MOU002");
        p9.setProd_nombre("Mouse Gaming");
        p9.setProd_pvp(59.99f);
        p9.setProd_stock(45);
        p9.setProd_estado("Activo");
        productos.add(p9);
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

    public Producto buscarProductoPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return null;
        String codigoBusqueda = codigo.trim().toLowerCase();
        for (Producto producto : productos) {
            if (producto.getProd_cod() != null && producto.getProd_cod().trim().toLowerCase().equals(codigoBusqueda)) {
                return producto;
            }
        }
        return null;
    }

    public Producto buscarProductoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        String nombreBusqueda = nombre.trim().toLowerCase();
        for (Producto producto : productos) {
            if (producto.getProd_nombre() != null && producto.getProd_nombre().toLowerCase().contains(nombreBusqueda)) {
                return producto;
            }
        }
        return null;
    }

    public ObservableList<Producto> buscarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) return FXCollections.observableArrayList();
        
        ObservableList<Producto> resultados = FXCollections.observableArrayList();
        String criterioBusqueda = criterio.trim().toLowerCase();
        
        for (Producto producto : productos) {
            boolean coincide = false;
            
            // Buscar por código
            if (producto.getProd_cod() != null && producto.getProd_cod().toLowerCase().contains(criterioBusqueda)) {
                coincide = true;
            }
            
            // Buscar por nombre
            if (!coincide && producto.getProd_nombre() != null && producto.getProd_nombre().toLowerCase().contains(criterioBusqueda)) {
                coincide = true;
            }
            
            if (coincide) {
                resultados.add(producto);
            }
        }
        
        return resultados;
    }
}

