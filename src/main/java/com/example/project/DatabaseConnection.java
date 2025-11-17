package com.example.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para manejar la conexión a la base de datos SQL Server
 * Configurado para autenticación con usuario y contraseña
 */
public class DatabaseConnection {
    // Configuración de conexión con autenticación SQL Server (usuario y contraseña)
    // Para SQL Server con autenticación SQL:
    // 1. URL: jdbc:sqlserver://localhost:1433;databaseName=bdFactura;encrypt=true;trustServerCertificate=true
    //    - localhost: servidor local (o el nombre de tu instancia SQL Server)
    //    - 1433: puerto por defecto de SQL Server
    //    - databaseName: nombre de tu base de datos
    //    - encrypt=true;trustServerCertificate=true: para evitar errores SSL en entorno local
    // 2. Se requiere USER y PASSWORD para autenticación SQL
    
    // URL de conexión (sin integratedSecurity para usar autenticación SQL)
    private static final String URL = "jdbc:sqlserver://localhost;databaseName=bdFactura;encrypt=true;trustServerCertificate=true";
    
    // Credenciales de autenticación SQL Server
    private static final String USER = "dianapc";
    private static final String PASSWORD = "1234";
    
    // OPCIÓN ALTERNATIVA: Si tu instancia tiene un puerto específico, usa:
    // private static final String URL = "jdbc:sqlserver://localhost:PUERTO;databaseName=bdFactura;encrypt=true;trustServerCertificate=true";
    
    // OPCIÓN ALTERNATIVA: Si tu instancia tiene un nombre específico, usa:
    // private static final String URL = "jdbc:sqlserver://localhost\\NOMBRE_INSTANCIA;databaseName=bdFactura;encrypt=true;trustServerCertificate=true"; 
    
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        try {
            // Cargar el driver de SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Driver SQL Server cargado correctamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver de SQL Server: " + e.getMessage());
            System.err.println("Asegúrate de que la dependencia mssql-jdbc esté en pom.xml");
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
                // Con autenticación SQL, se pasan usuario y contraseña
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión a SQL Server establecida exitosamente con usuario: " + USER);
            } catch (SQLException e) {
                System.err.println("Error al conectar con SQL Server: " + e.getMessage());
                System.err.println("Verifica que:");
                System.err.println("1. SQL Server esté corriendo (verifica en Servicios de Windows)");
                System.err.println("2. La instancia de SQL Server esté configurada (localhost o localhost\\NOMBRE_INSTANCIA)");
                System.err.println("3. La base de datos 'bdFactura' exista");
                System.err.println("4. El usuario '" + USER + "' exista y tenga permisos en SQL Server");
                System.err.println("5. La contraseña sea correcta");
                System.err.println("6. El puerto 1433 esté disponible (o ajusta el puerto en la URL)");
                System.err.println("7. SQL Server esté configurado para permitir autenticación SQL (modo mixto)");
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
     * Muestra la configuración actual de conexión
     */
    public static void mostrarConfiguracion() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("CONFIGURACIÓN ACTUAL DE CONEXIÓN:");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("Tipo BD:    SQL Server");
        System.out.println("URL:        " + URL);
        System.out.println("Autenticación: SQL Server (usuario y contraseña)");
        System.out.println("Usuario:    " + USER);
        System.out.println("Contraseña: " + (PASSWORD.length() > 0 ? "***" : "(vacía)"));
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("NOTA: Se usa autenticación SQL Server con usuario y contraseña.");
        System.out.println("      Asegúrate de que SQL Server esté en modo mixto de autenticación.");
        System.out.println("═══════════════════════════════════════════════════");
    }
    
    /**
     * Método de prueba para verificar la conexión
     */
    public static void testConnection() {
        mostrarConfiguracion();
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            Connection conn = dbConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Conexión exitosa a la base de datos");
                System.out.println("  Base de datos: " + conn.getCatalog());
                System.out.println("  URL real: " + conn.getMetaData().getURL());
                System.out.println("  Usuario conectado: " + conn.getMetaData().getUserName());
                System.out.println("  Driver: " + conn.getMetaData().getDriverName());
                System.out.println("  Versión: " + conn.getMetaData().getDriverVersion());
            }
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión: " + e.getMessage());
            System.err.println("\nPOSIBLES SOLUCIONES:");
            System.err.println("1. Verifica que SQL Server esté corriendo (Servicios de Windows)");
            System.err.println("2. Verifica que la base de datos 'bdFactura' exista");
            System.err.println("3. Si usas una instancia con nombre, cambia localhost a localhost\\NOMBRE_INSTANCIA");
            System.err.println("4. Verifica el puerto (default: 1433, puede ser diferente en tu instancia)");
            System.err.println("5. Verifica que el usuario '" + USER + "' exista en SQL Server");
            System.err.println("6. Verifica que la contraseña sea correcta");
            System.err.println("7. Asegúrate de que SQL Server esté en modo mixto de autenticación");
            System.err.println("8. Prueba conectarte desde SQL Server Management Studio (SSMS) con las mismas credenciales");
            e.printStackTrace();
        }
    }
    
    /**
     * Crea las tablas necesarias si no existen (SQL Server)
     */
    public void crearTablasNecesarias() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla de clientes (SQL Server) - NOTA: usar nombre singular 'cliente' para consistencia con JPA
            String sqlClientes = """
                IF OBJECT_ID('cliente', 'U') IS NULL
                CREATE TABLE cliente (
                    cli_id INT PRIMARY KEY IDENTITY(1,1),
                    cli_cedula VARCHAR(20) UNIQUE NOT NULL,
                    cli_apellidos VARCHAR(100) NOT NULL,
                    cli_nombres VARCHAR(100) NOT NULL,
                    cli_direccion VARCHAR(200),
                    cli_telefono VARCHAR(20),
                    cli_correo VARCHAR(100),
                    cli_estado VARCHAR(1) DEFAULT 'A',
                    cli_fecha_registro DATETIME DEFAULT GETDATE()
                );
            """;
            stmt.execute(sqlClientes);
            System.out.println("✓ Tabla 'cliente' verificada/creada");
            
            // Crear tabla de productos (SQL Server) - NOTA: usar nombre singular 'producto' para consistencia con JPA
            // Estructura actualizada para coincidir con la tabla real en la BD
            String sqlProductos = """
                IF OBJECT_ID('producto', 'U') IS NULL
                CREATE TABLE producto (
                    prod_id INT PRIMARY KEY IDENTITY(1,1),
                    prod_cod VARCHAR(80),
                    prod_nombre VARCHAR(80),
                    prod_precioCompra FLOAT,
                    prod_pvpxmenor FLOAT,
                    prod_pvpxmayor FLOAT,
                    prod_stock FLOAT DEFAULT 0,
                    prod_aplicaIva BIT DEFAULT 0,
                    prod_imagen VARBINARY(MAX),
                    prod_estado VARCHAR(1) DEFAULT 'A'
                );
            """;
            stmt.execute(sqlProductos);
            System.out.println("✓ Tabla 'producto' verificada/creada");
            
            // Crear tabla de facturas (SQL Server)
            String sqlFacturas = """
                IF OBJECT_ID('facturas', 'U') IS NULL
                CREATE TABLE facturas (
                    fac_id INT PRIMARY KEY IDENTITY(1,1),
                    fac_numero VARCHAR(50) UNIQUE NOT NULL,
                    fac_fecha DATE NOT NULL,
                    fac_cliente_id INT NOT NULL,
                    fac_subtotal DECIMAL(10,2) DEFAULT 0,
                    fac_iva DECIMAL(10,2) DEFAULT 0,
                    fac_descuento DECIMAL(10,2) DEFAULT 0,
                    fac_total DECIMAL(10,2) NOT NULL,
                    fac_estado VARCHAR(20) DEFAULT 'Activa',
                    fac_fecha_registro DATETIME DEFAULT GETDATE(),
                    FOREIGN KEY (fac_cliente_id) REFERENCES cliente(cli_id)
                );
            """;
            stmt.execute(sqlFacturas);
            System.out.println("✓ Tabla 'facturas' verificada/creada");
            
            // Crear tabla de detalles de factura (SQL Server)
            String sqlDetalles = """
                IF OBJECT_ID('factura_detalles', 'U') IS NULL
                CREATE TABLE factura_detalles (
                    det_id INT PRIMARY KEY IDENTITY(1,1),
                    det_factura_id INT NOT NULL,
                    det_producto_id INT NOT NULL,
                    det_cantidad INT NOT NULL,
                    det_precio_unitario DECIMAL(10,2) NOT NULL,
                    det_aplica_iva BIT DEFAULT 1,
                    det_descuento DECIMAL(5,2) DEFAULT 0,
                    det_subtotal DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (det_factura_id) REFERENCES facturas(fac_id),
                    FOREIGN KEY (det_producto_id) REFERENCES producto(prod_id)
                );
            """;
            stmt.execute(sqlDetalles);
            System.out.println("✓ Tabla 'factura_detalles' verificada/creada");
            
            // Crear tabla de usuarios (SQL Server)
            String sqlUsuarios = """
                IF OBJECT_ID('usuarios', 'U') IS NULL
                CREATE TABLE usuarios (
                    usr_id INT PRIMARY KEY IDENTITY(1,1),
                    usr_username VARCHAR(50) UNIQUE NOT NULL,
                    usr_password VARCHAR(255) NOT NULL,
                    usr_nombre VARCHAR(100) NOT NULL,
                    usr_email VARCHAR(100),
                    usr_rol VARCHAR(20) DEFAULT 'Usuario',
                    usr_estado VARCHAR(20) DEFAULT 'Activo',
                    usr_fecha_registro DATETIME DEFAULT GETDATE()
                );
            """;
            stmt.execute(sqlUsuarios);
            System.out.println("✓ Tabla 'usuarios' verificada/creada");
            
            System.out.println("✓ Todas las tablas están listas");
            
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método principal para probar la conexión independientemente
     * Ejecuta este método desde la línea de comandos para verificar tu configuración
     */
    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  PRUEBA DE CONEXIÓN A BASE DE DATOS");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        testConnection();
        
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  Para ver solo la configuración, usa:");
        System.out.println("  DatabaseConnection.mostrarConfiguracion();");
        System.out.println("═══════════════════════════════════════════════════\n");
    }
}


