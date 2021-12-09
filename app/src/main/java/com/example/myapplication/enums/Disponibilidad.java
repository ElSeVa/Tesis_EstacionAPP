package com.example.myapplication.enums;

public enum Disponibilidad {

    ABIERTO("Abierto"),
    CERRADO("Cerrado"),
    COMPLETO("Completo"),
    PROMOCION("Promocion");

    private String estado;

    private Disponibilidad(String estado){
        this.estado = estado;
    }

    public String getEstado(){
        return estado;
    }
}
