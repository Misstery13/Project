package com.example.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para manejar la conexión a la base de datos MySQL
 */
public class DatabaseConnection {
    // Configuración de conexión - MODIFICA ESTOS VALORES SEGÚN TU CONFIGURACIÓN
    private static final String URL = "jdbc:mysql://localhost:3306/BD2024_1";
    private static final String USER = "DESKTOP-980N1BK\\MSQL2";
    private static final String PASSWORD = ""; // Cambia esto por tu contraseña 
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver de MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene la instancia única de DatabaseConnection (Singleton)
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Obtiene una conexión a la base de datos
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión a la base de datos establecida exitosamente");
            } catch (SQLException e) {
                System.err.println("Error al conectar con la base de datos: " + e.getMessage());
                System.err.println("Verifica que:");
                System.err.println("1. MySQL esté corriendo");
                System.err.println("2. La base de datos 'facturacion_db' exista");
                System.err.println("3. El usuario y contraseña sean correctos");
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión a la base de datos cerrada");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica si la conexión está activa
     */
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Método de prueba para verificar la conexión
     */
    public static void testConnection() {
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            Connection conn = dbConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Conexión exitosa a la base de datos");
                System.out.println("  Base de datos: " + conn.getCatalog());
                System.out.println("  URL: " + conn.getMetaData().getURL());
            }
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión: " + e.getMessage());
        }
    }
    
    /**
     * Crea las tablas necesarias si no existen
     */
    public void crearTablasNecesarias() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla de clientes
            String sqlClientes = """
                CREATE TABLE IF NOT EXISTS clientes (
                    cli_id INT PRIMARY KEY AUTO_INCREMENT,
                    cli_cedula VARCHAR(20) UNIQUE NOT NULL,
                    cli_apellidos VARCHAR(100) NOT NULL,
                    cli_nombres VARCHAR(100) NOT NULL,
                    cli_direccion VARCHAR(200),
                    cli_telefono VARCHAR(20),
                    cli_correo VARCHAR(100),
                    cli_estado VARCHAR(20) DEFAULT 'Activo',
                    cli_fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;
            stmt.execute(sqlClientes);
            System.out.println("✓ Tabla 'clientes' verificada/creada");
            
            // Crear tabla de productos
            String sqlProductos = """
                CREATE TABLE IF NOT EXISTS productos (
                    prod_id INT PRIMARY KEY AUTO_INCREMENT,
                    prod_cod VARCHAR(50) UNIQUE NOT NULL,
                    prod_nombre VARCHAR(200) NOT NULL,
                    prod_pvp DECIMAL(10,2) NOT NULL,
                    prod_stock INT DEFAULT 0,
                    prod_estado VARCHAR(20) DEFAULT 'Activo',
                    prod_fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;
            stmt.execute(sqlProductos);
            System.out.println("✓ Tabla 'productos' verificada/creada");
            
            // Crear tabla de facturas
            String sqlFacturas = """
                CREATE TABLE IF NOT EXISTS facturas (
                    fac_id INT PRIMARY KEY AUTO_INCREMENT,
                    fac_numero VARCHAR(50) UNIQUE NOT NULL,
                    fac_fecha DATE NOT NULL,
                    fac_cliente_id INT NOT NULL,
                    fac_subtotal DECIMAL(10,2) DEFAULT 0,
                    fac_iva DECIMAL(10,2) DEFAULT 0,
                    fac_descuento DECIMAL(10,2) DEFAULT 0,
                    fac_total DECIMAL(10,2) NOT NULL,
                    fac_estado VARCHAR(20) DEFAULT 'Activa',
                    fac_fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (fac_cliente_id) REFERENCES clientes(cli_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;
            stmt.execute(sqlFacturas);
            System.out.println("✓ Tabla 'facturas' verificada/creada");
            
            // Crear tabla de detalles de factura
            String sqlDetalles = """
                CREATE TABLE IF NOT EXISTS factura_detalles (
                    det_id INT PRIMARY KEY AUTO_INCREMENT,
                    det_factura_id INT NOT NULL,
                    det_producto_id INT NOT NULL,
                    det_cantidad INT NOT NULL,
                    det_precio_unitario DECIMAL(10,2) NOT NULL,
                    det_aplica_iva BOOLEAN DEFAULT TRUE,
                    det_descuento DECIMAL(5,2) DEFAULT 0,
                    det_subtotal DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (det_factura_id) REFERENCES facturas(fac_id),
                    FOREIGN KEY (det_producto_id) REFERENCES productos(prod_id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;
            stmt.execute(sqlDetalles);
            System.out.println("✓ Tabla 'factura_detalles' verificada/creada");
            
            // Crear tabla de usuarios (si no existe ya)
            String sqlUsuarios = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    usr_id INT PRIMARY KEY AUTO_INCREMENT,
                    usr_username VARCHAR(50) UNIQUE NOT NULL,
                    usr_password VARCHAR(255) NOT NULL,
                    usr_nombre VARCHAR(100) NOT NULL,
                    usr_email VARCHAR(100),
                    usr_rol VARCHAR(20) DEFAULT 'Usuario',
                    usr_estado VARCHAR(20) DEFAULT 'Activo',
                    usr_fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;
            stmt.execute(sqlUsuarios);
            System.out.println("✓ Tabla 'usuarios' verificada/creada");
            
            System.out.println("✓ Todas las tablas están listas");
            
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


