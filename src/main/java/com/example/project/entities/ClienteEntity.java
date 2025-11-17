package com.example.project.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA para la tabla cliente
 */
@Entity
@Table(name = "cliente")
public class ClienteEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cli_id")
    private Integer id;
    
    @Column(name = "cli_cedula", unique = true, nullable = false, length = 20)
    private String cedula;
    
    @Column(name = "cli_apellidos", nullable = false, length = 100)
    private String apellidos;
    
    @Column(name = "cli_nombres", nullable = false, length = 100)
    private String nombres;
    
    @Column(name = "cli_direccion", length = 200)
    private String direccion;
    
    @Column(name = "cli_telefono", length = 20)
    private String telefono;
    
    @Column(name = "cli_correo", length = 100)
    private String correo;
    
    @Column(name = "cli_estado", length = 1)
    private String estado = "A";  // Estado por defecto: 'A' (Activo) o 'I' (Inactivo)
    
    // Campo no mapeado a la BD (la columna cli_fecha_registro no existe en la tabla)
    @Transient
    private LocalDateTime fechaRegistro;

    // Constructores
    public ClienteEntity() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public ClienteEntity(String cedula, String apellidos, String nombres, 
                        String direccion, String telefono, String correo) {
        this();
        this.cedula = cedula;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Convierte esta entidad JPA a la clase Cliente de JavaFX
     */
    public com.example.project.Cliente toCliente() {
        return new com.example.project.Cliente(
            id != null ? id : 0,
            cedula != null ? cedula : "",
            apellidos != null ? apellidos : "",
            nombres != null ? nombres : "",
            direccion != null ? direccion : "",
            telefono != null ? telefono : "",
            correo != null ? correo : ""
        );
    }

    /**
     * Crea una entidad desde un Cliente de JavaFX
     */
    public static ClienteEntity fromCliente(com.example.project.Cliente cliente) {
        return fromCliente(cliente, "A");  // Estado por defecto: 'A' (Activo)
    }

    /**
     * Crea una entidad desde un Cliente de JavaFX con estado específico
     */
    public static ClienteEntity fromCliente(com.example.project.Cliente cliente, String estado) {
        ClienteEntity entity = new ClienteEntity();
        if (cliente.getId_cliente() > 0) {
            entity.setId(cliente.getId_cliente());
        }
        entity.setCedula(cliente.getCedula());
        entity.setApellidos(cliente.getApellidos());
        entity.setNombres(cliente.getNombres());
        entity.setDireccion(cliente.getDireccion());
        entity.setTelefono(cliente.getTelefono());
        entity.setCorreo(cliente.getCorreo());
        entity.setEstado(estado != null && !estado.trim().isEmpty() ? estado : "A");
        return entity;
    }

    @Override
    public String toString() {
        return "ClienteEntity{" +
                "id=" + id +
                ", cedula='" + cedula + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                '}';
    }
}

