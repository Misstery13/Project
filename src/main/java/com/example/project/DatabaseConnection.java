package com.example.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para manejar la conexión a la base de datos SQL Server
 * Configurado para autenticación integrada de Windows (sin usuario ni contraseña)
 */
public class DatabaseConnection {
    // Configuración de conexión con autenticación integrada de Windows
    // Para SQL Server con autenticación de Windows:
    // 1. URL: jdbc:sqlserver://localhost:1433;databaseName=bdFactura;integratedSecurity=true
    //    - localhost: servidor local (o el nombre de tu instancia SQL Server)
    //    - 1433: puerto por defecto de SQL Server
    //    - databaseName: nombre de tu base de datos
    //    - integratedSecurity=true: usa tu credencial de Windows
    // 2. No necesitas USER ni PASSWORD cuando usas autenticación integrada
    //
    // Si tu instancia de SQL Server tiene un nombre específico, usa:
    // jdbc:sqlserver://localhost\\NOMBRE_INSTANCIA:1433;databaseName=bdFactura;integratedSecurity=true
    
    // Configuración básica para SQL Server local con autenticación de Windows
    // IMPORTANTE: Para instancias con nombre, SQL Server Browser debe estar corriendo
    // O habilitar TCP/IP con un puerto fijo en SQL Server Configuration Manager
    // Ver instrucciones en: HABILITAR_TCP_IP_SQL_SERVER.md
    
    // OPCIÓN 1: Instancia predeterminada (MSSQLSERVER) - Prueba esta primera
    // Si no sabes qué puerto usa, deja que JDBC lo encuentre automáticamente
    // Añadimos trustServerCertificate=true para evitar error SSL en entorno local
    private static final String URL = "jdbc:sqlserver://localhost;databaseName=bdFactura;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
    
    // OPCIÓN 2: Instancia predeterminada con puerto 1433 específico
    // private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=bdFactura;integratedSecurity=true";
    
    // OPCIÓN 3: Instancia con nombre DIANAMAIN (requiere Browser o puerto específico)
    // private static final String URL = "jdbc:sqlserver://localhost\\DIANAMAIN;databaseName=bdFactura;integratedSecurity=true";
    
    // OPCIÓN 4: Instancia con nombre y puerto específico (si conoces el puerto)
    // Primero habilita TCP/IP y configura un puerto, luego usa:
    // private static final String URL = "jdbc:sqlserver://localhost:PUERTO;databaseName=bdFactura;integratedSecurity=true;instanceName=DIANAMAIN";
    
    // NO se necesitan USER ni PASSWORD con autenticación integrada de Windows
    // Para SQL Server: la URL contiene integratedSecurity=true 
    
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
                // Con autenticación integrada de Windows, no se pasan usuario ni contraseña
                connection = DriverManager.getConnection(URL);
                System.out.println("Conexión a SQL Server establecida exitosamente con autenticación de Windows");
            } catch (SQLException e) {
                System.err.println("Error al conectar con SQL Server: " + e.getMessage());
                System.err.println("Verifica que:");
                System.err.println("1. SQL Server esté corriendo (verifica en Servicios de Windows)");
                System.err.println("2. La instancia de SQL Server esté configurada (localhost o localhost\\NOMBRE_INSTANCIA)");
                System.err.println("3. La base de datos 'bdFactura' exista");
                System.err.println("4. Tu usuario de Windows tenga permisos en SQL Server");
                System.err.println("5. El puerto 1433 esté disponible (o ajusta el puerto en la URL)");
                System.err.println("\nNOTA: Para autenticación integrada, necesitas sqljdbc_auth.dll en tu PATH");
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
        System.out.println("Autenticación: Windows (integratedSecurity=true)");
        System.out.println("Usuario:    (se usa tu usuario de Windows)");
        System.out.println("Contraseña: (no se requiere)");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("NOTA: Con autenticación integrada de Windows,");
        System.out.println("      SQL Server usa tus credenciales de Windows.");
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
            System.err.println("5. Asegúrate de tener permisos en SQL Server con tu usuario de Windows");
            System.err.println("6. Descarga sqljdbc_auth.dll si recibes error de autenticación");
            System.err.println("7. Prueba conectarte desde SQL Server Management Studio (SSMS)");
            e.printStackTrace();
        }
    }
    
    /**
     * Crea las tablas necesarias si no existen (SQL Server)
     */
    public void crearTablasNecesarias() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Crear tabla de clientes (SQL Server)
            String sqlClientes = """
                IF OBJECT_ID('clientes', 'U') IS NULL
                CREATE TABLE clientes (
                    cli_id INT PRIMARY KEY IDENTITY(1,1),
                    cli_cedula VARCHAR(20) UNIQUE NOT NULL,
                    cli_apellidos VARCHAR(100) NOT NULL,
                    cli_nombres VARCHAR(100) NOT NULL,
                    cli_direccion VARCHAR(200),
                    cli_telefono VARCHAR(20),
                    cli_correo VARCHAR(100),
                    cli_estado VARCHAR(20) DEFAULT 'Activo',
                    cli_fecha_registro DATETIME DEFAULT GETDATE()
                );
            """;
            stmt.execute(sqlClientes);
            System.out.println("✓ Tabla 'clientes' verificada/creada");
            
            // Crear tabla de productos (SQL Server)
            String sqlProductos = """
                IF OBJECT_ID('productos', 'U') IS NULL
                CREATE TABLE productos (
                    prod_id INT PRIMARY KEY IDENTITY(1,1),
                    prod_cod VARCHAR(50) UNIQUE NOT NULL,
                    prod_nombre VARCHAR(200) NOT NULL,
                    prod_pvp DECIMAL(10,2) NOT NULL,
                    prod_stock INT DEFAULT 0,
                    prod_estado VARCHAR(20) DEFAULT 'Activo',
                    prod_fecha_registro DATETIME DEFAULT GETDATE()
                );
            """;
            stmt.execute(sqlProductos);
            System.out.println("✓ Tabla 'productos' verificada/creada");
            
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
                    FOREIGN KEY (fac_cliente_id) REFERENCES clientes(cli_id)
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
                    FOREIGN KEY (det_producto_id) REFERENCES productos(prod_id)
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


