package com.example.project.jpa;

import com.example.project.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Verifica los datos existentes en la base de datos usando JDBC directo
 */
public class VerificarDatosExistentes {
    
    public static void verificarDatosExistentes() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("  VERIFICANDO DATOS EXISTENTES EN BD (JDBC)");
        System.out.println("═══════════════════════════════════════════════════\n");
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            System.out.println("✓ Conexión establecida con JDBC\n");
            
            // 1. Verificar qué tablas existen
            System.out.println("1. Verificando tablas existentes...");
            String sqlTablas = """
                SELECT TABLE_NAME 
                FROM INFORMATION_SCHEMA.TABLES 
                WHERE TABLE_TYPE = 'BASE TABLE' 
                AND TABLE_NAME LIKE '%cliente%'
                ORDER BY TABLE_NAME
                """;
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlTablas)) {
                
                System.out.println("   Tablas encontradas con 'cliente' en el nombre:");
                boolean hayTablas = false;
                while (rs.next()) {
                    hayTablas = true;
                    String tabla = rs.getString("TABLE_NAME");
                    System.out.println("   - " + tabla);
                }
                if (!hayTablas) {
                    System.out.println("   ⚠ No se encontraron tablas con 'cliente' en el nombre");
                }
            }
            
            // 2. Verificar registros en la tabla 'clientes'
            System.out.println("\n2. Verificando registros en tabla 'clientes'...");
            String sqlCount = "SELECT COUNT(*) as total FROM cliente";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlCount)) {
                
                if (rs.next()) {
                    int total = rs.getInt("total");
                    System.out.println("   ✓ Total de registros en 'clientes': " + total);
                    
                    if (total > 0) {
                        // Mostrar algunos registros
                        System.out.println("\n3. Mostrando primeros 5 registros:");
                        String sqlSelect = """
                            SELECT TOP 5 
                                cli_id, cli_cedula, cli_nombres, cli_apellidos, 
                                cli_estado, cli_fecha_registro
                            FROM cliente
                            ORDER BY cli_id
                            """;
                        
                        try (Statement stmt2 = conn.createStatement();
                             ResultSet rs2 = stmt2.executeQuery(sqlSelect)) {
                            
                            System.out.println("   ID | Cédula      | Nombres          | Apellidos    | Estado | Fecha Registro");
                            System.out.println("   " + "-".repeat(90));
                            
                            while (rs2.next()) {
                                int id = rs2.getInt("cli_id");
                                String cedula = rs2.getString("cli_cedula");
                                String nombres = rs2.getString("cli_nombres");
                                String apellidos = rs2.getString("cli_apellidos");
                                String estado = rs2.getString("cli_estado");
                                Object fechaReg = rs2.getObject("cli_fecha_registro");
                                
                                System.out.printf("   %-3d | %-11s | %-15s | %-12s | %-6s | %s%n",
                                    id, cedula, nombres, apellidos, estado, fechaReg);
                            }
                        }
                    }
                }
            }
            
            // 3. Verificar esquema actual
            System.out.println("\n4. Verificando esquema actual...");
            String sqlEsquema = "SELECT SCHEMA_NAME() as esquema_actual";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlEsquema)) {
                
                if (rs.next()) {
                    String esquema = rs.getString("esquema_actual");
                    System.out.println("   ✓ Esquema actual: " + esquema);
                }
            }
            
            // 4. Verificar base de datos actual
            System.out.println("\n5. Verificando base de datos actual...");
            String sqlDB = "SELECT DB_NAME() as base_datos";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlDB)) {
                
                if (rs.next()) {
                    String db = rs.getString("base_datos");
                    System.out.println("   ✓ Base de datos actual: " + db);
                }
            }
            
            System.out.println("\n═══════════════════════════════════════════════════\n");
            
        } catch (Exception e) {
            System.err.println("\n✗ ERROR al verificar datos:");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        verificarDatosExistentes();
    }
}

