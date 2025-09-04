package com.example.project;

import java.util.ArrayList;

public class Factura {
    private int fac_id;
    private String fac_numero;
    private String fac_fecha;
    private Cliente fac_cliente;
    private ArrayList<DetalleFactura> fac_detalle;
    private float fac_subtotal;
    private float fac_subtotalcero;
    private float fac_iva;
    private float fac_descuento;
    private float fac_total;
    private int usr_id;
    private String fac_estado;

}
