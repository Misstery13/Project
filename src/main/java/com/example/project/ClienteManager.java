package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class ClienteManager {
    private static ClienteManager instance;
    private ObservableList<Cliente> clientes;

    private ClienteManager() {
        clientes = FXCollections.observableArrayList();
        cargarClientesDesdeDB();
    }

    public static ClienteManager getInstance() {
        if (instance == null) {
            instance = new ClienteManager();
        }
        return instance;
    }

    /**
     * Carga todos los clientes desde la base de datos
     */
    private void cargarClientesDesdeDB() {
        clientes.clear();
        String sql = "SELECT * FROM clientes WHERE cli_estado = 'Activo' ORDER BY cli_nombres";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("cli_id"),
                    rs.getString("cli_cedula"),
                    rs.getString("cli_apellidos"),
                    rs.getString("cli_nombres"),
                    rs.getString("cli_direccion"),
                    rs.getString("cli_telefono"),
                    rs.getString("cli_correo")
                );
                clientes.add(cliente);
            }
            System.out.println("Clientes cargados desde BD: " + clientes.size());
            
        } catch (SQLException e) {
            System.err.println("Error al cargar clientes desde BD: " + e.getMessage());
            // Si hay error de conexión, cargar datos de prueba en memoria
            cargarDatosPrueba();
        }
    }

    /**
     * Carga datos de prueba en memoria (fallback si no hay BD)
     */
    private void cargarDatosPrueba() {
        System.out.println("Cargando datos de prueba en memoria...");
        clientes.add(new Cliente(1, "2450128257", "Melena", "Diana", "Santa Elena", "0963610580", "diana.melena25@gmail.com"));
        clientes.add(new Cliente(2, "1234567890", "García", "Diana", "Guayaquil", "0987654321", "diana.garcia@email.com"));
        clientes.add(new Cliente(3, "0987654321", "López", "Diana", "Quito", "0912345678", "diana.lopez@email.com"));
        clientes.add(new Cliente(4, "1122334455", "Martínez", "Carlos", "Cuenca", "0956789012", "carlos.martinez@email.com"));
    }

    /**
     * Recarga los clientes desde la base de datos
     */
    public void recargar() {
        cargarClientesDesdeDB();
    }

    public ObservableList<Cliente> getClientes() {
        return clientes;
    }

    /**
     * Agrega un cliente a la base de datos
     */
    public boolean agregarCliente(Cliente cliente) {
        String sql = "INSERT INTO clientes (cli_cedula, cli_apellidos, cli_nombres, cli_direccion, cli_telefono, cli_correo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getNombres());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setString(6, cliente.getCorreo());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Obtener el ID generado
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cliente.setId_cliente(generatedKeys.getInt(1));
                    }
                }
                
        clientes.add(cliente);
                System.out.println("Cliente agregado exitosamente. ID: " + cliente.getId_cliente());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al agregar cliente: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("La cédula ya existe en la base de datos");
            }
        }
        
        return false;
    }

    /**
     * Actualiza un cliente en la base de datos
     */
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET cli_cedula = ?, cli_apellidos = ?, cli_nombres = ?, " +
                     "cli_direccion = ?, cli_telefono = ?, cli_correo = ? WHERE cli_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cliente.getCedula());
            pstmt.setString(2, cliente.getApellidos());
            pstmt.setString(3, cliente.getNombres());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getTelefono());
            pstmt.setString(6, cliente.getCorreo());
            pstmt.setInt(7, cliente.getId_cliente());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                recargar();
                System.out.println("Cliente actualizado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Elimina (inactiva) un cliente de la base de datos
     */
    public boolean eliminarCliente(int clienteId) {
        String sql = "UPDATE clientes SET cli_estado = 'Inactivo' WHERE cli_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, clienteId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                recargar();
                System.out.println("Cliente inactivado exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar cliente: " + e.getMessage());
        }
        
        return false;
    }

    /**
     * Busca un cliente por cédula exacta
     */
    public Cliente buscarClientePorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return null;
        
        String sql = "SELECT * FROM clientes WHERE cli_cedula = ? AND cli_estado = 'Activo'";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cedula.trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Cliente(
                    rs.getInt("cli_id"),
                    rs.getString("cli_cedula"),
                    rs.getString("cli_apellidos"),
                    rs.getString("cli_nombres"),
                    rs.getString("cli_direccion"),
                    rs.getString("cli_telefono"),
                    rs.getString("cli_correo")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar cliente por cédula: " + e.getMessage());
            // Fallback: buscar en memoria
            return buscarEnMemoriaPorCedula(cedula);
        }
        
        return null;
    }

    /**
     * Busca clientes por criterio (cédula o nombre)
     */
    public ObservableList<Cliente> buscarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return FXCollections.observableArrayList();
        }
        
        ObservableList<Cliente> resultados = FXCollections.observableArrayList();
        String sql = "SELECT * FROM clientes WHERE " +
                     "(cli_cedula LIKE ? OR cli_nombres LIKE ? OR cli_apellidos LIKE ? " +
                     "OR CONCAT(cli_nombres, ' ', cli_apellidos) LIKE ?) " +
                     "AND cli_estado = 'Activo' " +
                     "ORDER BY cli_nombres";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + criterio.trim() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getInt("cli_id"),
                    rs.getString("cli_cedula"),
                    rs.getString("cli_apellidos"),
                    rs.getString("cli_nombres"),
                    rs.getString("cli_direccion"),
                    rs.getString("cli_telefono"),
                    rs.getString("cli_correo")
                );
                resultados.add(cliente);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar clientes: " + e.getMessage());
            // Fallback: buscar en memoria
            return buscarEnMemoria(criterio);
        }
        
        return resultados;
    }

    // Métodos auxiliares para fallback en memoria
    
    private Cliente buscarEnMemoriaPorCedula(String cedula) {
        String cedulaBusqueda = cedula.trim().toLowerCase();
        for (Cliente cliente : clientes) {
            if (cliente.getCedula() != null && 
                cliente.getCedula().trim().toLowerCase().equals(cedulaBusqueda)) {
                return cliente;
            }
        }
        return null;
    }

    private ObservableList<Cliente> buscarEnMemoria(String criterio) {
        ObservableList<Cliente> resultados = FXCollections.observableArrayList();
        String criterioBusqueda = criterio.trim().toLowerCase();
        
        for (Cliente cliente : clientes) {
            boolean coincide = false;
            
            if (cliente.getCedula() != null && 
                cliente.getCedula().toLowerCase().contains(criterioBusqueda)) {
                coincide = true;
            }
            
            if (!coincide) {
                String nombreCompleto = (cliente.getNombres() + " " + cliente.getApellidos()).toLowerCase();
                if (nombreCompleto.contains(criterioBusqueda)) {
                    coincide = true;
                }
            }
            
            if (coincide) {
                resultados.add(cliente);
            }
        }
        
        return resultados;
    }
}

