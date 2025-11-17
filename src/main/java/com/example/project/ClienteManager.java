package com.example.project;

import com.example.project.entities.ClienteEntity;
import com.example.project.repositories.ClienteRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manager de clientes usando JPA
 * Refactorizado para usar JPA en lugar de JDBC
 */
public class ClienteManager {
    private static ClienteManager instance;
    private ObservableList<Cliente> clientes;
    private final ClienteRepository repository;

    private ClienteManager() {
        clientes = FXCollections.observableArrayList();
        // Inicializar JPA explícitamente antes de crear el repositorio
        try {
            System.out.println("=== Inicializando ClienteManager con JPA ===");
            com.example.project.jpa.JPAUtil.getEntityManagerFactory();
            System.out.println("✓ JPA inicializado correctamente");
        } catch (Exception e) {
            System.err.println("✗ ERROR CRÍTICO: No se pudo inicializar JPA");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
        }
        repository = new ClienteRepository();
        cargarClientesDesdeDB();
    }

    public static ClienteManager getInstance() {
        if (instance == null) {
            instance = new ClienteManager();
        }
        return instance;
    }

    /**
     * Carga todos los clientes desde la base de datos usando JPA
     */
    private void cargarClientesDesdeDB() {
        clientes.clear();
        try {
            System.out.println("=== Iniciando carga de clientes desde BD (JPA) ===");
            List<ClienteEntity> entidades = repository.findAll();
            System.out.println("Entidades encontradas: " + entidades.size());
            
            for (ClienteEntity entity : entidades) {
                Cliente cliente = entity.toCliente();
                clientes.add(cliente);
                System.out.println("  - Cliente cargado: " + cliente.getNombres() + " " + cliente.getApellidos() + " (ID: " + cliente.getId_cliente() + ")");
            }
            System.out.println("✓ Total clientes cargados desde BD (JPA): " + clientes.size());
        } catch (Exception e) {
            System.err.println("✗ Error al cargar clientes desde BD (JPA): " + e.getMessage());
            e.printStackTrace();
            // Si hay error, cargar datos de prueba en memoria
            System.out.println("Cargando datos de prueba como fallback...");
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
     * Agrega un cliente a la base de datos usando JPA
     */
    public boolean agregarCliente(Cliente cliente) {
        return agregarCliente(cliente, "A");  // Estado por defecto: 'A' (Activo)
    }

    /**
     * Agrega un cliente a la base de datos usando JPA con estado específico
     */
    public boolean agregarCliente(Cliente cliente, String estado) {
        try {
            ClienteEntity entity = ClienteEntity.fromCliente(cliente, estado);
            ClienteEntity saved = repository.save(entity);
            
            // Actualizar el ID del cliente
            cliente.setId_cliente(saved.getId());
            
            // Agregar a la lista observable
            clientes.add(cliente);
            System.out.println("Cliente agregado exitosamente (JPA). ID: " + saved.getId() + ", Estado: " + saved.getEstado());
            return true;
        } catch (Exception e) {
            System.err.println("Error al agregar cliente (JPA): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza un cliente en la base de datos usando JPA
     */
    public boolean actualizarCliente(Cliente cliente) {
        try {
            ClienteEntity entity = ClienteEntity.fromCliente(cliente);
            repository.save(entity);
            recargar();
            System.out.println("Cliente actualizado exitosamente (JPA)");
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar cliente (JPA): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina (inactiva) un cliente de la base de datos usando JPA
     */
    public boolean eliminarCliente(int clienteId) {
        try {
            boolean eliminado = repository.delete(clienteId);
            if (eliminado) {
                recargar();
                System.out.println("Cliente inactivado exitosamente (JPA)");
            }
            return eliminado;
        } catch (Exception e) {
            System.err.println("Error al eliminar cliente (JPA): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Busca un cliente por cédula exacta usando JPA
     */
    public Cliente buscarClientePorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return null;
        
        try {
            return repository.findByCedula(cedula.trim())
                    .map(ClienteEntity::toCliente)
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error al buscar cliente por cédula (JPA): " + e.getMessage());
            // Fallback: buscar en memoria
            return buscarEnMemoriaPorCedula(cedula);
        }
    }

    /**
     * Busca clientes por cédula parcial usando JPA (búsqueda que contiene el texto)
     */
    public ObservableList<Cliente> buscarPorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return FXCollections.observableArrayList();
        }
        
        try {
            List<ClienteEntity> entidades = repository.findByCedulaPartial(cedula);
            return entidades.stream()
                    .map(ClienteEntity::toCliente)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        } catch (Exception e) {
            System.err.println("Error al buscar por cédula parcial (JPA): " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    /**
     * Busca clientes por criterio (cédula o nombre) usando JPA
     */
    public ObservableList<Cliente> buscarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            return FXCollections.observableArrayList();
        }
        
        try {
            List<ClienteEntity> entidades = repository.search(criterio);
            return entidades.stream()
                    .map(ClienteEntity::toCliente)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        } catch (Exception e) {
            System.err.println("Error al buscar clientes (JPA): " + e.getMessage());
            // Fallback: buscar en memoria
            return buscarEnMemoria(criterio);
        }
    }
    
    /**
     * Busca clientes por apellidos usando JPA
     */
    public ObservableList<Cliente> buscarPorApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            return FXCollections.observableArrayList();
        }
        
        try {
            List<ClienteEntity> entidades = repository.findByApellidos(apellidos);
            return entidades.stream()
                    .map(ClienteEntity::toCliente)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        } catch (Exception e) {
            System.err.println("Error al buscar por apellidos (JPA): " + e.getMessage());
            return FXCollections.observableArrayList();
        }
    }
    
    /**
     * Busca clientes por nombres usando JPA
     */
    public ObservableList<Cliente> buscarPorNombres(String nombres) {
        if (nombres == null || nombres.trim().isEmpty()) {
            return FXCollections.observableArrayList();
        }
        
        try {
            List<ClienteEntity> entidades = repository.findByNombres(nombres);
            return entidades.stream()
                    .map(ClienteEntity::toCliente)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
        } catch (Exception e) {
            System.err.println("Error al buscar por nombres (JPA): " + e.getMessage());
            return FXCollections.observableArrayList();
        }
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

