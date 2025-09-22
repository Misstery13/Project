package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClienteManager {
    private static ClienteManager instance;
    private ObservableList<Cliente> clientes;

    private ClienteManager() {
        clientes = FXCollections.observableArrayList();
        // Seed de clientes por defecto para pruebas de interfaz
        clientes.add(new Cliente(
                1,
                "2450128257",
                "Melena",
                "Diana",
                "Santa Elena",
                "0963610580",
                "diana.melena25@gmail.com"
        ));
        
        // Agregar más clientes para probar la funcionalidad de múltiples resultados
        clientes.add(new Cliente(
                2,
                "1234567890",
                "García",
                "Diana",
                "Guayaquil",
                "0987654321",
                "diana.garcia@email.com"
        ));
        
        clientes.add(new Cliente(
                3,
                "0987654321",
                "López",
                "Diana",
                "Quito",
                "0912345678",
                "diana.lopez@email.com"
        ));
        
        clientes.add(new Cliente(
                4,
                "1122334455",
                "Martínez",
                "Carlos",
                "Cuenca",
                "0956789012",
                "carlos.martinez@email.com"
        ));
    }

    public static ClienteManager getInstance() {
        if (instance == null) {
            instance = new ClienteManager();
        }
        return instance;
    }

    public ObservableList<Cliente> getClientes() {
        return clientes;
    }

    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
        System.out.println("Cliente agregado. Total de clientes: " + clientes.size());
        System.out.println("Último cliente: " + cliente.getNombres() + " " + cliente.getApellidos());
    }

    public Cliente buscarClientePorCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) return null;
        String cedulaBusqueda = cedula.trim().toLowerCase();
        for (Cliente cliente : clientes) {
            if (cliente.getCedula() != null && cliente.getCedula().trim().toLowerCase().equals(cedulaBusqueda)) {
                return cliente;
            }
        }
        return null;
    }

    public Cliente buscarClientePorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        String nombreBusqueda = nombre.trim().toLowerCase();
        for (Cliente cliente : clientes) {
            String nombreCompleto = (cliente.getNombres() + " " + cliente.getApellidos()).toLowerCase();
            if (nombreCompleto.contains(nombreBusqueda) || 
                (cliente.getNombres() != null && cliente.getNombres().toLowerCase().contains(nombreBusqueda)) ||
                (cliente.getApellidos() != null && cliente.getApellidos().toLowerCase().contains(nombreBusqueda))) {
                return cliente;
            }
        }
        return null;
    }

    public ObservableList<Cliente> buscarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) return FXCollections.observableArrayList();
        
        ObservableList<Cliente> resultados = FXCollections.observableArrayList();
        String criterioBusqueda = criterio.trim().toLowerCase();
        
        for (Cliente cliente : clientes) {
            boolean coincide = false;
            
            // Buscar por cédula
            if (cliente.getCedula() != null && cliente.getCedula().toLowerCase().contains(criterioBusqueda)) {
                coincide = true;
            }
            
            // Buscar por nombre completo
            if (!coincide) {
                String nombreCompleto = (cliente.getNombres() + " " + cliente.getApellidos()).toLowerCase();
                if (nombreCompleto.contains(criterioBusqueda)) {
                    coincide = true;
                }
            }
            
            // Buscar por nombres o apellidos individualmente
            if (!coincide) {
                if ((cliente.getNombres() != null && cliente.getNombres().toLowerCase().contains(criterioBusqueda)) ||
                    (cliente.getApellidos() != null && cliente.getApellidos().toLowerCase().contains(criterioBusqueda))) {
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