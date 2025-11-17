package com.example.project;

public class Producto {
    private int prod_id;
    private String prod_cod;
    private String prod_nombre;
    private float prod_precioCompra;
    private float prod_pvpxmenor;  // Precio venta por menor
    private float prod_pvpxmayor;  // Precio venta por mayor
    private int prod_stock;
    private boolean prod_aplicalva;  // Aplica IVA (bit en BD)
    private String prod_estado;
    
    // Método de compatibilidad - retorna prod_pvpxmenor como PVP
    public float getProd_pvp() {
        return prod_pvpxmenor;
    }
    
    public void setProd_pvp(float pvp) {
        this.prod_pvpxmenor = pvp;
    }

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getProd_cod() {
        return prod_cod;
    }

    public void setProd_cod(String prod_cod) {
        this.prod_cod = prod_cod;
    }

    public String getProd_nombre() {
        return prod_nombre;
    }

    public void setProd_nombre(String prod_nombre) {
        this.prod_nombre = prod_nombre;
    }

    public float getProd_precioCompra() {
        return prod_precioCompra;
    }

    public void setProd_precioCompra(float prod_precioCompra) {
        this.prod_precioCompra = prod_precioCompra;
    }

    public float getProd_pvpxmenor() {
        return prod_pvpxmenor;
    }

    public void setProd_pvpxmenor(float prod_pvpxmenor) {
        this.prod_pvpxmenor = prod_pvpxmenor;
    }

    public float getProd_pvpxmayor() {
        return prod_pvpxmayor;
    }

    public void setProd_pvpxmayor(float prod_pvpxmayor) {
        this.prod_pvpxmayor = prod_pvpxmayor;
    }

    public int getProd_stock() {
        return prod_stock;
    }

    public void setProd_stock(int prod_stock) {
        this.prod_stock = prod_stock;
    }

    public boolean getProd_aplicalva() {
        return prod_aplicalva;
    }

    public void setProd_aplicalva(boolean prod_aplicalva) {
        this.prod_aplicalva = prod_aplicalva;
    }
    
    // Método de compatibilidad para código existente
    public float getProd_aplicaIva() {
        return prod_aplicalva ? 1.0f : 0.0f;
    }

    public void setProd_aplicaIva(float prod_aplicaIva) {
        this.prod_aplicalva = prod_aplicaIva > 0;
    }

    public String getProd_estado() {
        return prod_estado;
    }

    public void setProd_estado(String prod_estado) {
        this.prod_estado = prod_estado;
    }
}
