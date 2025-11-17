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
        // Verificar si la columna prod_aplicaIva existe antes de incluirla en el SELECT
        boolean columnaAplicaIvaExiste = verificarColumnaExiste("producto", "prod_aplicaIva");
        
        String sql;
        if (columnaAplicaIvaExiste) {
            sql = "SELECT * FROM producto WHERE (prod_estado = 'A' OR prod_estado = 'Activo' OR prod_estado IS NULL) ORDER BY prod_nombre";
        } else {
            sql = "SELECT prod_id, prod_cod, prod_nombre, prod_precioCompra, prod_pvpxmenor, prod_pvpxmayor, prod_stock, prod_estado " +
                  "FROM producto WHERE (prod_estado = 'A' OR prod_estado = 'Activo' OR prod_estado IS NULL) ORDER BY prod_nombre";
        }
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProd_id(rs.getInt("prod_id"));
                producto.setProd_cod(rs.getString("prod_cod"));
                producto.setProd_nombre(rs.getString("prod_nombre"));
                
                // Leer precio de compra
                Object precioCompraObj = rs.getObject("prod_precioCompra");
                producto.setProd_precioCompra(precioCompraObj != null ? rs.getFloat("prod_precioCompra") : 0.0f);
                
                // Leer precio venta por menor
                Object pvpXMenorObj = rs.getObject("prod_pvpxmenor");
                producto.setProd_pvpxmenor(pvpXMenorObj != null ? rs.getFloat("prod_pvpxmenor") : 0.0f);
                
                // Leer precio venta por mayor
                Object pvpXMayorObj = rs.getObject("prod_pvpxmayor");
                producto.setProd_pvpxmayor(pvpXMayorObj != null ? rs.getFloat("prod_pvpxmayor") : 0.0f);
                
                // Leer stock (entero)
                Object stockObj = rs.getObject("prod_stock");
                producto.setProd_stock(stockObj != null ? rs.getInt("prod_stock") : 0);
                
                // Leer aplica IVA (bit) - solo si la columna existe
                if (columnaAplicaIvaExiste) {
                    try {
                        Object aplicaIvaObj = rs.getObject("prod_aplicaIva");
                        if (aplicaIvaObj != null) {
                            if (aplicaIvaObj instanceof Boolean) {
                                producto.setProd_aplicalva((Boolean) aplicaIvaObj);
                            } else {
                                producto.setProd_aplicalva(rs.getInt("prod_aplicaIva") > 0);
                            }
                        } else {
                            producto.setProd_aplicalva(false);
                        }
                    } catch (SQLException e) {
                        // Si hay error, usar false por defecto
                        producto.setProd_aplicalva(false);
                    }
                } else {
                    // Si la columna no existe, usar false por defecto
                    producto.setProd_aplicalva(false);
                }
                
                // Leer estado y convertir de BD ('A'/'I') a formato legible ("Activo"/"Inactivo")
                String estadoBD = rs.getString("prod_estado");
                String estadoLegible = "A";  // Por defecto: Activo
                if (estadoBD != null) {
                    if ("A".equals(estadoBD.trim())) {
                        estadoLegible = "Activo";
                    } else if ("I".equals(estadoBD.trim())) {
                        estadoLegible = "Inactivo";
                    } else {
                        estadoLegible = estadoBD;  // Mantener el valor si no es 'A' ni 'I'
                    }
                }
                producto.setProd_estado(estadoLegible);
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
        p1.setProd_stock(50);  // int
        p1.setProd_estado("Activo");
        productos.add(p1);
        
        Producto p2 = new Producto();
        p2.setProd_id(2);
        p2.setProd_cod("LAP002");
        p2.setProd_nombre("Laptop Dell");
        p2.setProd_pvp(799.99f);
        p2.setProd_stock(30);  // int
        p2.setProd_estado("Activo");
        productos.add(p2);
        
        Producto p3 = new Producto();
        p3.setProd_id(3);
        p3.setProd_cod("MON001");
        p3.setProd_nombre("Monitor Samsung");
        p3.setProd_pvp(299.99f);
        p3.setProd_stock(40);  // int
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
        // Verificar si la columna prod_aplicaIva existe antes de incluirla
        boolean columnaAplicaIvaExiste = verificarColumnaExiste("producto", "prod_aplicaIva");
        
        String sql;
        if (columnaAplicaIvaExiste) {
            sql = "INSERT INTO producto (prod_cod, prod_nombre, prod_precioCompra, prod_pvpxmenor, prod_pvpxmayor, prod_stock, prod_aplicaIva, prod_estado) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO producto (prod_cod, prod_nombre, prod_precioCompra, prod_pvpxmenor, prod_pvpxmayor, prod_stock, prod_estado) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, producto.getProd_cod());
            pstmt.setString(2, producto.getProd_nombre());
            pstmt.setFloat(3, producto.getProd_precioCompra());
            pstmt.setFloat(4, producto.getProd_pvpxmenor());
            pstmt.setFloat(5, producto.getProd_pvpxmayor());
            pstmt.setInt(6, producto.getProd_stock());
            
            // Convertir 'Activo' a 'A' o 'Inactivo' a 'I'
            String estado = producto.getProd_estado() != null ? producto.getProd_estado() : "A";
            if ("Activo".equals(estado)) estado = "A";
            if ("Inactivo".equals(estado)) estado = "I";
            
            if (columnaAplicaIvaExiste) {
                // Guardar como 1 o 0 (BIT en SQL Server) en lugar de boolean
                int aplicaIvaValue = producto.getProd_aplicalva() ? 1 : 0;
                System.out.println("  [DEBUG] Guardando aplicaIva: " + aplicaIvaValue + " (checkbox seleccionado: " + producto.getProd_aplicalva() + ")");
                pstmt.setInt(7, aplicaIvaValue);
                pstmt.setString(8, estado);
            } else {
                System.out.println("  [DEBUG] Columna prod_aplicaIva no existe, omitiendo...");
                pstmt.setString(7, estado);
            }
            
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
        // Verificar si la columna prod_aplicaIva existe antes de incluirla
        boolean columnaAplicaIvaExiste = verificarColumnaExiste("producto", "prod_aplicaIva");
        
        String sql;
        if (columnaAplicaIvaExiste) {
            sql = "UPDATE producto SET prod_cod = ?, prod_nombre = ?, prod_precioCompra = ?, " +
                  "prod_pvpxmenor = ?, prod_pvpxmayor = ?, prod_stock = ?, prod_aplicaIva = ?, prod_estado = ? WHERE prod_id = ?";
        } else {
            sql = "UPDATE producto SET prod_cod = ?, prod_nombre = ?, prod_precioCompra = ?, " +
                  "prod_pvpxmenor = ?, prod_pvpxmayor = ?, prod_stock = ?, prod_estado = ? WHERE prod_id = ?";
        }
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, producto.getProd_cod());
            pstmt.setString(2, producto.getProd_nombre());
            pstmt.setFloat(3, producto.getProd_precioCompra());
            pstmt.setFloat(4, producto.getProd_pvpxmenor());
            pstmt.setFloat(5, producto.getProd_pvpxmayor());
            pstmt.setInt(6, producto.getProd_stock());
            
            // Convertir 'Activo' a 'A' o 'Inactivo' a 'I'
            String estado = producto.getProd_estado() != null ? producto.getProd_estado() : "A";
            if ("Activo".equals(estado)) estado = "A";
            if ("Inactivo".equals(estado)) estado = "I";
            
            if (columnaAplicaIvaExiste) {
                // Guardar como 1 o 0 (BIT en SQL Server) en lugar de boolean
                int aplicaIvaValue = producto.getProd_aplicalva() ? 1 : 0;
                pstmt.setInt(7, aplicaIvaValue);
                pstmt.setString(8, estado);
                pstmt.setInt(9, producto.getProd_id());
            } else {
                pstmt.setString(7, estado);
                pstmt.setInt(8, producto.getProd_id());
            }
            
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
        String sql = "UPDATE producto SET prod_stock = ? WHERE prod_id = ?";
        
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
        String sql = "UPDATE producto SET prod_estado = 'I' WHERE prod_id = ?";
        
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
        
        String sql = "SELECT COUNT(*) FROM producto WHERE prod_cod = ? AND (prod_estado = 'A' OR prod_estado = 'Activo' OR prod_estado IS NULL)";
        
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
        
        String sql = "SELECT COUNT(*) FROM producto WHERE prod_nombre = ? AND (prod_estado = 'A' OR prod_estado = 'Activo' OR prod_estado IS NULL)";
        
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
        
        String sql = "SELECT * FROM producto WHERE prod_cod = ? AND (prod_estado = 'A' OR prod_estado = 'Activo' OR prod_estado IS NULL)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, codigo.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Producto producto = new Producto();
                producto.setProd_id(rs.getInt("prod_id"));
                producto.setProd_cod(rs.getString("prod_cod"));
                producto.setProd_nombre(rs.getString("prod_nombre"));
                
                Object precioCompraObj = rs.getObject("prod_precioCompra");
                producto.setProd_precioCompra(precioCompraObj != null ? rs.getFloat("prod_precioCompra") : 0.0f);
                
                Object pvpXMenorObj = rs.getObject("prod_pvpxmenor");
                producto.setProd_pvpxmenor(pvpXMenorObj != null ? rs.getFloat("prod_pvpxmenor") : 0.0f);
                
                Object pvpXMayorObj = rs.getObject("prod_pvpxmayor");
                producto.setProd_pvpxmayor(pvpXMayorObj != null ? rs.getFloat("prod_pvpxmayor") : 0.0f);
                
                Object stockObj = rs.getObject("prod_stock");
                producto.setProd_stock(stockObj != null ? rs.getInt("prod_stock") : 0);
                
                        Object aplicaIvaObj = rs.getObject("prod_aplicaIva");
                        if (aplicaIvaObj != null) {
                            if (aplicaIvaObj instanceof Boolean) {
                                producto.setProd_aplicalva((Boolean) aplicaIvaObj);
                            } else {
                                producto.setProd_aplicalva(rs.getInt("prod_aplicaIva") > 0);
                            }
                } else {
                    producto.setProd_aplicalva(false);
                }
                
                // Leer estado y convertir de BD ('A'/'I') a formato legible ("Activo"/"Inactivo")
                String estadoBD = rs.getString("prod_estado");
                String estadoLegible = "A";  // Por defecto: Activo
                if (estadoBD != null) {
                    if ("A".equals(estadoBD.trim())) {
                        estadoLegible = "Activo";
                    } else if ("I".equals(estadoBD.trim())) {
                        estadoLegible = "Inactivo";
                    } else {
                        estadoLegible = estadoBD;  // Mantener el valor si no es 'A' ni 'I'
                    }
                }
                producto.setProd_estado(estadoLegible);
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
        
        String sql = "SELECT TOP 1 * FROM producto WHERE prod_nombre LIKE ? AND (prod_estado = 'A' OR prod_estado = 'Activo' OR prod_estado IS NULL)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + nombre.trim() + "%");
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Producto producto = new Producto();
                producto.setProd_id(rs.getInt("prod_id"));
                producto.setProd_cod(rs.getString("prod_cod"));
                producto.setProd_nombre(rs.getString("prod_nombre"));
                
                Object precioCompraObj = rs.getObject("prod_precioCompra");
                producto.setProd_precioCompra(precioCompraObj != null ? rs.getFloat("prod_precioCompra") : 0.0f);
                
                Object pvpXMenorObj = rs.getObject("prod_pvpxmenor");
                producto.setProd_pvpxmenor(pvpXMenorObj != null ? rs.getFloat("prod_pvpxmenor") : 0.0f);
                
                Object pvpXMayorObj = rs.getObject("prod_pvpxmayor");
                producto.setProd_pvpxmayor(pvpXMayorObj != null ? rs.getFloat("prod_pvpxmayor") : 0.0f);
                
                Object stockObj = rs.getObject("prod_stock");
                producto.setProd_stock(stockObj != null ? rs.getInt("prod_stock") : 0);
                
                        Object aplicaIvaObj = rs.getObject("prod_aplicaIva");
                        if (aplicaIvaObj != null) {
                            if (aplicaIvaObj instanceof Boolean) {
                                producto.setProd_aplicalva((Boolean) aplicaIvaObj);
                            } else {
                                producto.setProd_aplicalva(rs.getInt("prod_aplicaIva") > 0);
                            }
                } else {
                    producto.setProd_aplicalva(false);
                }
                
                // Leer estado y convertir de BD ('A'/'I') a formato legible ("Activo"/"Inactivo")
                String estadoBD = rs.getString("prod_estado");
                String estadoLegible = "A";  // Por defecto: Activo
                if (estadoBD != null) {
                    if ("A".equals(estadoBD.trim())) {
                        estadoLegible = "Activo";
                    } else if ("I".equals(estadoBD.trim())) {
                        estadoLegible = "Inactivo";
                    } else {
                        estadoLegible = estadoBD;  // Mantener el valor si no es 'A' ni 'I'
                    }
                }
                producto.setProd_estado(estadoLegible);
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
        String sql = "SELECT * FROM producto WHERE " +
                     "(prod_cod LIKE ? OR prod_nombre LIKE ?) " +
                     "AND (prod_estado = 'A' OR prod_estado = 'Activo' OR prod_estado IS NULL) " +
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
                
                Object precioCompraObj = rs.getObject("prod_precioCompra");
                producto.setProd_precioCompra(precioCompraObj != null ? rs.getFloat("prod_precioCompra") : 0.0f);
                
                Object pvpXMenorObj = rs.getObject("prod_pvpxmenor");
                producto.setProd_pvpxmenor(pvpXMenorObj != null ? rs.getFloat("prod_pvpxmenor") : 0.0f);
                
                Object pvpXMayorObj = rs.getObject("prod_pvpxmayor");
                producto.setProd_pvpxmayor(pvpXMayorObj != null ? rs.getFloat("prod_pvpxmayor") : 0.0f);
                
                Object stockObj = rs.getObject("prod_stock");
                producto.setProd_stock(stockObj != null ? rs.getInt("prod_stock") : 0);
                
                        Object aplicaIvaObj = rs.getObject("prod_aplicaIva");
                        if (aplicaIvaObj != null) {
                            if (aplicaIvaObj instanceof Boolean) {
                                producto.setProd_aplicalva((Boolean) aplicaIvaObj);
                            } else {
                                producto.setProd_aplicalva(rs.getInt("prod_aplicaIva") > 0);
                            }
                } else {
                    producto.setProd_aplicalva(false);
                }
                
                // Leer estado y convertir de BD ('A'/'I') a formato legible ("Activo"/"Inactivo")
                String estadoBD = rs.getString("prod_estado");
                String estadoLegible = "A";  // Por defecto: Activo
                if (estadoBD != null) {
                    if ("A".equals(estadoBD.trim())) {
                        estadoLegible = "Activo";
                    } else if ("I".equals(estadoBD.trim())) {
                        estadoLegible = "Inactivo";
                    } else {
                        estadoLegible = estadoBD;  // Mantener el valor si no es 'A' ni 'I'
                    }
                }
                producto.setProd_estado(estadoLegible);
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
    
    /**
     * Verifica si una columna existe en una tabla
     */
    private boolean verificarColumnaExiste(String tabla, String columna) {
        // Intentar con diferentes variaciones del nombre de tabla
        String[] variacionesTabla = {tabla, tabla.toLowerCase(), tabla.toUpperCase()};
        
        for (String nombreTabla : variacionesTabla) {
            String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                         "WHERE TABLE_NAME = ? AND COLUMN_NAME = ?";
            
            try (Connection conn = DatabaseConnection.getInstance().getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, nombreTabla);
                pstmt.setString(2, columna);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        boolean existe = rs.getInt(1) > 0;
                        if (existe) {
                            System.out.println("  [DEBUG] Columna " + nombreTabla + "." + columna + " existe: " + existe);
                            return true;
                        }
                    }
                }
            } catch (SQLException e) {
                // Continuar con la siguiente variación
                continue;
            }
        }
        
        // Si ninguna variación funcionó, intentar consulta directa sin parámetros
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                         "WHERE TABLE_NAME = 'producto' AND COLUMN_NAME = 'prod_aplicaIva'";
            try (ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    boolean existe = rs.getInt(1) > 0;
                    System.out.println("  [DEBUG] Columna producto.prod_aplicaIva existe (consulta directa): " + existe);
                    return existe;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar si la columna existe: " + e.getMessage());
        }
        
        System.out.println("  [DEBUG] Columna producto.prod_aplicaIva no encontrada");
        return false;
    }
}
