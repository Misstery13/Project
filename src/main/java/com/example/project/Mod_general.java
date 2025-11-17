package com.example.project;

import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public class Mod_general {
    // Variables estáticas para gestión de base de datos
    // gestorBD: 1 = MariaDB/MySQL, 0 = SQL Server
    public static int gestorBD = 0; // Por defecto SQL Server
    public static String str_nombreBD = "SQL Server";
    
    public static void fun_detectarTecla(Node nodoOrigen, KeyCode tecla, Node nodoAFocus) {
        nodoOrigen.setOnKeyPressed(evento->{
            if(evento.getCode()==tecla) {
                nodoAFocus.requestFocus();
            }
            if(tecla== KeyCode.TAB) {
                evento.consume();
            }
        });
    }
}
