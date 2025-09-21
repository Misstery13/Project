package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClienteManager {
    private static ClienteManager instance;
    private ObservableList<Cliente> clientes;

    private ClienteManager() {
        clientes = FXCollections.observableArrayList();
        // Seed de cliente por defecto para pruebas de interfaz
        clientes.add(new Cliente(
                1,
                "2450128257",
                "Melena",
                "Diana",
                "Santa Elena",
                "0963610580",
                "diana.melena25@gmail.com"
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
}