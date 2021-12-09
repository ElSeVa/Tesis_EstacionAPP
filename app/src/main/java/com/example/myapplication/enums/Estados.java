package com.example.myapplication.enums;

public enum Estados {
    ACEPTADO("Aceptado"),
    ESPERANDO("Esperando"),
    CANCELADO("Cancelado");

    private String estados;

    Estados(String estados){
        this.estados = estados;
    }

    public String getEstados(){
        return estados;
    }
}
