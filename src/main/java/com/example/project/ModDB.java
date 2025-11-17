/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.project;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.util.function.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Clase para gestión de conexiones y operaciones con base de datos
 * Soporta tanto MariaDB/MySQL como SQL Server
 * @author Jaime
 */
public class ModDB {

    private Connection conexion;
    private Statement sentenciaSQL;
    private ResultSet resulSet;

    public ModDB() {
    }

    public Connection getConexion() {
        return conexion;
    }

    public void setConexion(Connection conexion) {
        this.conexion = conexion;
    }

    public Statement getSentenciaSQL() {
        return sentenciaSQL;
    }

    public void setSentenciaSQL(Statement sentenciaSQL) {
        this.sentenciaSQL = sentenciaSQL;
    }

    public ResultSet getResulSet() {
        return resulSet;
    }

    public void setResulSet(ResultSet resulSet) {
        this.resulSet = resulSet;
    }

    //función para conectar con la base de datos
    public boolean conectarBD() {
        String servidor, basedatos, usuario, clave, classNombre, cadenaConexion;
        if (Mod_general.gestorBD == 1) {// se conecta con mysql
            servidor = "localhost";
            basedatos = "base20251";
            usuario = "dianapc";
            clave = "1234";
            Mod_general.str_nombreBD = "Maria DB";
            //cadena de conección
            classNombre = "org.mariadb.jdbc.Driver";
            cadenaConexion = "jdbc:mariadb://"
                    + servidor + ":3306/"
                    + basedatos + "?"
                    + "user=" + usuario
                    + "&" + "password=" + clave;
        } else {
            //conecta con SQLServer
            servidor = "localhost";
            basedatos = "bdFactura";
            usuario = "dianapc";
            clave = "1234";
            Mod_general.str_nombreBD = "SQL Server";
            classNombre = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            cadenaConexion = "jdbc:sqlserver://"
                    + servidor + ":1433;"
                    + "databaseName=" + basedatos + ";"
                    + "user=" + usuario + ";"
                    + "password=" + clave + ";"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=30;";

        }
        try {
            Class.forName(classNombre);
            System.out.println("✓ Driver cargado: " + classNombre);
            Connection conexion = DriverManager.getConnection(cadenaConexion);
            this.setConexion(conexion);
            System.out.println("✓ Conexión establecida exitosamente");
            System.out.println("  Base de datos: " + basedatos);
            System.out.println("  Servidor: " + servidor);
            System.out.println("  Usuario: " + usuario);
            System.out.println("  Gestor BD: " + Mod_general.str_nombreBD);
            return true;
        } catch (Exception e) {
            System.err.println("✗ Error al conectar con la base de datos:");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void desconectarBD() {
        try {
            this.conexion.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void iniciarTransaccion() {
        try {
            this.conexion.setAutoCommit(false);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void commit() {
        try {
            this.conexion.commit();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void rollback() {
        try {
            this.conexion.rollback();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //crear una función que me permita ejecutar sentencias sql
    // select
    public void ejecutarConsultaSql(String cadenaSQL) {
        try {
            this.sentenciaSQL = this.conexion.createStatement();
            this.resulSet = this.sentenciaSQL.executeQuery(cadenaSQL);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    //crear una función para Insertar - Update - Delete
    public int ejecutarSQL(String cadenaSQL) {
        int filas = 0;
        try {
            this.sentenciaSQL = this.conexion.createStatement();
            filas = this.sentenciaSQL.executeUpdate(cadenaSQL);
            this.commit();
        } catch (Exception e) {
            System.out.println(e);
        }
        return filas;
    }

    public <T> ObservableList<T> getListaConsulta(String cadenaSQL, Function<ResultSet, T> mapper) {
        ObservableList<T> obsListAux = FXCollections.observableArrayList();
        try {
            conectarBD(); // conectar a la BD
            ejecutarConsultaSql(cadenaSQL); // ejecutar la consulta
            ResultSet rs = getResulSet(); // obtener el resultado
            while (rs.next()) {
                T obj = mapper.apply(rs); // mapear la fila
                obsListAux.add(obj);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("ERROR en getListaConsulta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            desconectarBD();
        }
        return obsListAux;
    }

    public boolean fun_ejecutar(String cadenaSQL) {
        try {
            this.conectarBD();
            int filas = this.ejecutarSQL(cadenaSQL);
            
            if (filas > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }finally{
            this.desconectarBD();
        }
    }
    
    /**
     * Método de prueba para verificar la conexión a la base de datos
     * Muestra información detallada sobre la conexión
     */
    public static void probarConexion() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  PRUEBA DE CONEXIÓN - ModDB");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        ModDB modDB = new ModDB();
        
        System.out.println("Configuración actual:");
        System.out.println("  Gestor BD: " + (Mod_general.gestorBD == 1 ? "MariaDB/MySQL" : "SQL Server"));
        System.out.println("  Nombre BD: " + Mod_general.str_nombreBD);
        System.out.println();
        
        boolean conectado = modDB.conectarBD();
        
        if (conectado) {
            try {
                Connection conn = modDB.getConexion();
                if (conn != null && !conn.isClosed()) {
                    System.out.println("\n✓ CONEXIÓN EXITOSA");
                    System.out.println("  URL: " + conn.getMetaData().getURL());
                    System.out.println("  Usuario: " + conn.getMetaData().getUserName());
                    System.out.println("  Driver: " + conn.getMetaData().getDriverName());
                    System.out.println("  Versión Driver: " + conn.getMetaData().getDriverVersion());
                    System.out.println("  Base de datos: " + conn.getCatalog());
                    
                    // Prueba simple: ejecutar una consulta SELECT 1
                    System.out.println("\n  Probando consulta simple...");
                    modDB.ejecutarConsultaSql("SELECT 1 AS prueba");
                    ResultSet rs = modDB.getResulSet();
                    if (rs.next()) {
                        System.out.println("  ✓ Consulta ejecutada correctamente: " + rs.getInt("prueba"));
                    }
                    rs.close();
                }
            } catch (Exception e) {
                System.err.println("  ✗ Error al obtener información de la conexión: " + e.getMessage());
            } finally {
                modDB.desconectarBD();
                System.out.println("\n  Conexión cerrada");
            }
        } else {
            System.err.println("\n✗ NO SE PUDO ESTABLECER LA CONEXIÓN");
            System.err.println("\nVerifica:");
            System.err.println("1. Que el servidor de base de datos esté corriendo");
            System.err.println("2. Que las credenciales sean correctas (usuario: dianapc, contraseña: 1234)");
            System.err.println("3. Que la base de datos exista");
            System.err.println("4. Que el puerto esté disponible (1433 para SQL Server, 3306 para MariaDB)");
        }
        
        System.out.println("\n═══════════════════════════════════════════════════\n");
    }
    
    /**
     * Método main para probar la conexión desde la línea de comandos
     */
    public static void main(String[] args) {
        probarConexion();
    }

}//fin de la clase