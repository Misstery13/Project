package com.example.project;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {
    private final StringProperty cedula;
    private final StringProperty apellidos;
    private final StringProperty nombres;
    private final StringProperty direccion;
    private final StringProperty telefono;
    private final StringProperty correo;

    public Cliente(String cedula, String apellidos, String nombres, String direccion, String telefono, String correo) {
        this.cedula = new SimpleStringProperty(cedula);
        this.apellidos = new SimpleStringProperty(apellidos);
        this.nombres = new SimpleStringProperty(nombres);
        this.direccion = new SimpleStringProperty(direccion);
        this.telefono = new SimpleStringProperty(telefono);
        this.correo = new SimpleStringProperty(correo);
    }

    // Getters para el TableView
    public String getCedula() { return cedula.get(); }
    public void setCedula(String cedula) { this.cedula.set(cedula); }
    public StringProperty cedulaProperty() { return cedula; }

    public String getApellidos() { return apellidos.get(); }
    public void setApellidos(String apellidos) { this.apellidos.set(apellidos); }
    public StringProperty apellidosProperty() { return apellidos; }

    public String getNombres() { return nombres.get(); }
    public void setNombres(String nombres) { this.nombres.set(nombres); }
    public StringProperty nombresProperty() { return nombres; }

    public String getDireccion() { return direccion.get(); }
    public void setDireccion(String direccion) { this.direccion.set(direccion); }
    public StringProperty direccionProperty() { return direccion; }

    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }
    public StringProperty telefonoProperty() { return telefono; }

    public String getCorreo() { return correo.get(); }
    public void setCorreo(String correo) { this.correo.set(correo); }
    public StringProperty correoProperty() { return correo; }
}