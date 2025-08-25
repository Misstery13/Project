package com.example.project;

import javafx.animation.KeyValue;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public class Mod_general {
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
