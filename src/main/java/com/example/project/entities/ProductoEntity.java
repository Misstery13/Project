package com.example.project.entities;

import jakarta.persistence.*;

/**
 * Entidad JPA para la tabla producto
 */
@Entity
@Table(name = "producto")
public class ProductoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prod_id")
    private Integer id;
    
    @Column(name = "prod_cod", length = 80)
    private String codigo;
    
    @Column(name = "prod_nombre", length = 80)
    private String nombre;
    
    @Column(name = "prod_precioCompra")
    private Double precioCompra;
    
    @Column(name = "prod_pvpxmenor")  // Precio venta por menor
    private Double pvpXMenor;
    
    @Column(name = "prod_pvpxmayor")  // Precio venta por mayor
    private Double pvpXMayor;
    
    @Column(name = "prod_stock")
    private Integer stock = 0;
    
    @Column(name = "prod_aplicaIva")  // Aplica IVA (bit)
    private Boolean aplicaIva = false;
    
    @Column(name = "prod_estado", length = 1)
    private String estado = "A";  // 'A' (Activo) o 'I' (Inactivo)
    
    @Column(name = "prod_imagen", columnDefinition = "VARBINARY(MAX)")
    @Lob
    private byte[] imagen;

    // Constructores
    public ProductoEntity() {
        // Constructor vacío para JPA
    }

    public ProductoEntity(String codigo, String nombre, Double precioCompra, Double pvpXMenor, Double pvpXMayor, Integer stock, Boolean aplicaIva) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.pvpXMenor = pvpXMenor;
        this.pvpXMayor = pvpXMayor;
        this.stock = stock;
        this.aplicaIva = aplicaIva;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public Double getPvpXMenor() {
        return pvpXMenor;
    }

    public void setPvpXMenor(Double pvpXMenor) {
        this.pvpXMenor = pvpXMenor;
    }

    public Double getPvpXMayor() {
        return pvpXMayor;
    }

    public void setPvpXMayor(Double pvpXMayor) {
        this.pvpXMayor = pvpXMayor;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getAplicaIva() {
        return aplicaIva;
    }

    public void setAplicaIva(Boolean aplicaIva) {
        this.aplicaIva = aplicaIva;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Convierte esta entidad JPA a la clase Producto
     * Convierte el estado de la BD ('A'/'I') a formato legible ("Activo"/"Inactivo")
     */
    public com.example.project.Producto toProducto() {
        com.example.project.Producto producto = new com.example.project.Producto();
        producto.setProd_id(id != null ? id : 0);
        producto.setProd_cod(codigo != null ? codigo : "");
        producto.setProd_nombre(nombre != null ? nombre : "");
        producto.setProd_precioCompra(precioCompra != null ? precioCompra.floatValue() : 0.0f);
        producto.setProd_pvpxmenor(pvpXMenor != null ? pvpXMenor.floatValue() : 0.0f);
        producto.setProd_pvpxmayor(pvpXMayor != null ? pvpXMayor.floatValue() : 0.0f);
        producto.setProd_stock(stock != null ? stock.intValue() : 0);
        producto.setProd_aplicalva(aplicaIva != null ? aplicaIva : false);
        // Convertir estado de BD ('A'/'I') a formato legible ("Activo"/"Inactivo")
        String estadoProducto = estado != null ? estado : "A";
        if ("A".equals(estadoProducto)) {
            estadoProducto = "Activo";
        } else if ("I".equals(estadoProducto)) {
            estadoProducto = "Inactivo";
        }
        producto.setProd_estado(estadoProducto);
        return producto;
    }

    /**
     * Crea una entidad desde un Producto
     * Convierte el estado del formato legible ("Activo"/"Inactivo") a formato BD ('A'/'I')
     */
    public static ProductoEntity fromProducto(com.example.project.Producto producto) {
        ProductoEntity entity = new ProductoEntity();
        if (producto.getProd_id() > 0) {
            entity.setId(producto.getProd_id());
        }
        entity.setCodigo(producto.getProd_cod());
        entity.setNombre(producto.getProd_nombre());
        entity.setPrecioCompra((double) producto.getProd_precioCompra());
        entity.setPvpXMenor((double) producto.getProd_pvpxmenor());
        entity.setPvpXMayor((double) producto.getProd_pvpxmayor());
        entity.setStock(producto.getProd_stock());
        entity.setAplicaIva(producto.getProd_aplicalva());
        // Convertir estado: "Activo" -> "A", "Inactivo" -> "I", o aceptar "A"/"I" directamente
        String estadoProducto = producto.getProd_estado();
        if (estadoProducto != null && !estadoProducto.trim().isEmpty()) {
            estadoProducto = estadoProducto.trim();
            if ("Activo".equalsIgnoreCase(estadoProducto)) {
                estadoProducto = "A";
            } else if ("Inactivo".equalsIgnoreCase(estadoProducto)) {
                estadoProducto = "I";
            } else if (!"A".equals(estadoProducto) && !"I".equals(estadoProducto)) {
                // Si no es ninguno de los formatos conocidos, usar "A" por defecto
                estadoProducto = "A";
            }
            // Si ya es "A" o "I", mantenerlo así
        } else {
            estadoProducto = "A";  // Por defecto: Activo
        }
        entity.setEstado(estadoProducto);
        return entity;
    }

    @Override
    public String toString() {
        return "ProductoEntity{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precioCompra=" + precioCompra +
                ", pvpXMenor=" + pvpXMenor +
                ", pvpXMayor=" + pvpXMayor +
                ", stock=" + stock +
                ", aplicaIva=" + aplicaIva +
                ", estado='" + estado + '\'' +
                '}';
    }
}

