package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

// removed invalid static import that conflicted with the singleton field

public class ProductManager {
    private static ProductManager instance;
    private final ObservableList<Producto> productos;

    private ProductManager() {
        this.productos = FXCollections.observableArrayList();
        cargarProductosDesdeDB();
    }

    public static synchronized ProductManager getInstance() {
        if (instance == null) {
            instance = new ProductManager();
        }
        return instance;
    }

    /**
     * Carga todos los productos desde la base de datos
     */
    private void cargarProductosDesdeDB() {
        productos.clear();
        String sql = "SELECT * FROM productos WHERE prod_estado = 'Activo' ORDER BY prod_nombre";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProd_id(rs.getInt("prod_id"));
                producto.setProd_cod(rs.getString("prod_cod"));
                producto.setProd_nombre(rs.getString("prod_nombre"));
                producto.setProd_pvp(rs.getFloat("prod_pvp"));
                producto.setProd_stock(rs.getInt("prod_stock"));
                producto.setProd_estado(rs.getString("prod_estado"));
                productos.add(producto);
            }
            System.out.println("Productos cargados desde BD: " + productos.size());
            
        } catch (SQLException e) {
            System.err.println("Error al cargar productos desde BD: " + e.getMessage());
            // Si hay error de conexión, cargar datos de prueba en memoria
            cargarDatosPrueba();
        }
    }

    /**
     * Carga datos de prueba en memoria (fallback si no hay BD)
     */
    private void cargarDatosPrueba() {
        System.out.println("Cargando productos de prueba en memoria...");
        
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
        p3.setProd_cod("MON001");
        p3.setProd_nombre("Monitor Samsung");
        p3.setProd_pvp(299.99f);
        p3.setProd_stock(40);
        p3.setProd_estado("Activo");
        productos.add(p3);
    }

    /**
     * Recarga los productos desde la base de datos
     */
    public void recargar() {
        cargarProductosDesdeDB();
    }

    public ObservableList<Producto> getProductos() {
        return productos;
    }

    /**
     * Agrega un producto a la base de datos
     */
    public boolean agregarProducto(Producto producto) {
        String sql = "INSERT INTO productos (prod_cod, prod_nombre, prod_pvp, prod_stock, prod_estado) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, producto.getProd_cod());
            pstmt.setString(2, producto.getProd_nombre());
            pstmt.setFloat(3, producto.getProd_pvp());
            pstmt.setInt(4, (int) producto.getProd_stock());
            pstmt.setString(5, producto.getProd_estado() != null ? producto.getProd_estado() : "Activo");
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Obtener el ID generado
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setProd_id(generatedKeys.getInt(1));
                    }
                }
                
        productos.add(producto);
                System.out.println("Producto agregado exitosamente. ID: " + producto.getProd_id());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al agregar producto: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("El código de producto ya existe en la base de datos");
            }
        }
        
        return false;
    }

    /**
     * Actualiza un producto en la base de datos
     */
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET prod_cod = ?, prod_nombre = ?, prod_pvp = ?, " +
                     "prod_stock = ?, prod_estado = ? WHERE prod_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getProd_cod());
            pstmt.setString(2, producto.getProd_nombre());
            pstmt.setFloat(3, producto.getProd_pvp());
            pstmt.setInt(4, (int) producto.getProd_stock());
            pstmt.setString(5, producto.getProd_estado());
            pstmt.setInt(6, producto.getProd_id());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                recargar();
                System.out.println("Producto actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Actualiza el stock de un producto
     */
    public boolean actualizarStock(int productoId, int nuevoStock) {
        String sql = "UPDATE productos SET prod_stock = ? WHERE prod_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, productoId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                recargar();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Elimina (inactiva) un producto de la base de datos
     */
    public boolean eliminarProducto(int productoId) {
        String sql = "UPDATE productos SET prod_estado = 'Inactivo' WHERE prod_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productoId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                recargar();
                System.out.println("Producto inactivado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
        
        return false;
    }

    public boolean existeCodigo(String codigo) {
        if (codigo == null) return false;
        
        String sql = "SELECT COUNT(*) FROM productos WHERE prod_cod = ? AND prod_estado = 'Activo'";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar código: " + e.getMessage());
            // Fallback a búsqueda en memoria
            return existeCodigoEnMemoria(codigo);
        }
        
        return false;
    }

    public boolean existeNombre(String nombre) {
        if (nombre == null) return false;
        
        String sql = "SELECT COUNT(*) FROM productos WHERE prod_nombre = ? AND prod_estado = 'Activo'";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombre.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar nombre: " + e.getMessage());
            return existeNombreEnMemoria(nombre);
        }
        
        return false;
    }

    public Producto buscarProductoPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) return null;
        
        String sql = "SELECT * FROM productos WHERE prod_cod = ? AND prod_estado = 'Activo'";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Producto producto = new Producto();
                producto.setProd_id(rs.getInt("prod_id"));
                producto.setProd_cod(rs.getString("prod_cod"));
                producto.setProd_nombre(rs.getString("prod_nombre"));
                producto.setProd_pvp(rs.getFloat("prod_pvp"));
                producto.setProd_stock(rs.getInt("prod_stock"));
                producto.setProd_estado(rs.getString("prod_estado"));
                return producto;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por código: " + e.getMessage());
            return buscarEnMemoriaPorCodigo(codigo);
        }
        
        return null;
    }

    public Producto buscarProductoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        
        String sql = "SELECT * FROM productos WHERE prod_nombre LIKE ? AND prod_estado = 'Activo' LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre.trim() + "%");
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Producto producto = new Producto();
                producto.setProd_id(rs.getInt("prod_id"));
                producto.setProd_cod(rs.getString("prod_cod"));
                producto.setProd_nombre(rs.getString("prod_nombre"));
                producto.setProd_pvp(rs.getFloat("prod_pvp"));
                producto.setProd_stock(rs.getInt("prod_stock"));
                producto.setProd_estado(rs.getString("prod_estado"));
                return producto;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar producto por nombre: " + e.getMessage());
            return buscarEnMemoriaPorNombre(nombre);
        }
        
        return null;
    }

    public ObservableList<Producto> buscarProductos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return FXCollections.observableArrayList();
        }
        
        ObservableList<Producto> resultados = FXCollections.observableArrayList();
        String sql = "SELECT * FROM productos WHERE " +
                     "(prod_cod LIKE ? OR prod_nombre LIKE ?) " +
                     "AND prod_estado = 'Activo' " +
                     "ORDER BY prod_nombre";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + criterio.trim() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProd_id(rs.getInt("prod_id"));
                producto.setProd_cod(rs.getString("prod_cod"));
                producto.setProd_nombre(rs.getString("prod_nombre"));
                producto.setProd_pvp(rs.getFloat("prod_pvp"));
                producto.setProd_stock(rs.getInt("prod_stock"));
                producto.setProd_estado(rs.getString("prod_estado"));
                resultados.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
            return buscarEnMemoria(criterio);
        }
        
        return resultados;
    }

    // Métodos auxiliares para fallback en memoria
    
    private boolean existeCodigoEnMemoria(String codigo) {
        String cod = codigo.trim().toLowerCase();
        for (Producto p : productos) {
            if (p.getProd_cod() != null && p.getProd_cod().trim().toLowerCase().equals(cod)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeNombreEnMemoria(String nombre) {
        String nom = nombre.trim().toLowerCase();
        for (Producto p : productos) {
            if (p.getProd_nombre() != null && p.getProd_nombre().trim().toLowerCase().equals(nom)) {
                return true;
            }
        }
        return false;
    }

    private Producto buscarEnMemoriaPorCodigo(String codigo) {
        String codigoBusqueda = codigo.trim().toLowerCase();
        for (Producto producto : productos) {
            if (producto.getProd_cod() != null && 
                producto.getProd_cod().trim().toLowerCase().equals(codigoBusqueda)) {
                return producto;
            }
        }
        return null;
    }

    private Producto buscarEnMemoriaPorNombre(String nombre) {
        String nombreBusqueda = nombre.trim().toLowerCase();
        for (Producto producto : productos) {
            if (producto.getProd_nombre() != null && 
                producto.getProd_nombre().toLowerCase().contains(nombreBusqueda)) {
                return producto;
            }
        }
        return null;
    }

    private ObservableList<Producto> buscarEnMemoria(String criterio) {
        ObservableList<Producto> resultados = FXCollections.observableArrayList();
        String criterioBusqueda = criterio.trim().toLowerCase();
        
        for (Producto producto : productos) {
            boolean coincide = false;
            
            if (producto.getProd_cod() != null && 
                producto.getProd_cod().toLowerCase().contains(criterioBusqueda)) {
                coincide = true;
            }
            
            if (!coincide && producto.getProd_nombre() != null && 
                producto.getProd_nombre().toLowerCase().contains(criterioBusqueda)) {
                coincide = true;
            }
            
            if (coincide) {
                resultados.add(producto);
            }
        }
        
        return resultados;
    }
}
