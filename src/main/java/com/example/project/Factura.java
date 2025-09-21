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
    private String fe_clave;
    private String fe_fecha_aut;
    private int usr_id_crea;
    private int usr_id_modifica;
    private String mac_crea;
    private String mac_modifica;
    private String fecha_creacion;
    private String hora_creacion;
    private String fecha_modificacion;
    private String hora_modificacion;
    private String fac_estado;

}
