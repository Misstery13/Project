package com.example.project;

public class Producto {
    private int prod_id;
    private String prod_cod;
    private String prod_nombre;
    private float prod_precioCompra;
    private float prod_pvp;
    private float prod_stock;
    private float prod_aplicaIva;
    private String prod_estado;

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

    public float getProd_pvp() {
        return prod_pvp;
    }

    public void setProd_pvp(float prod_pvp) {
        this.prod_pvp = prod_pvp;
    }

    public float getProd_stock() {
        return prod_stock;
    }

    public void setProd_stock(float prod_stock) {
        this.prod_stock = prod_stock;
    }

    public float getProd_aplicaIva() {
        return prod_aplicaIva;
    }

    public void setProd_aplicaIva(float prod_aplicaIva) {
        this.prod_aplicaIva = prod_aplicaIva;
    }

    public String getProd_estado() {
        return prod_estado;
    }

    public void setProd_estado(String prod_estado) {
        this.prod_estado = prod_estado;
    }
}
