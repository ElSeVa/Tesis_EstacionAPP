package com.example.myapplication.ui.models;

public class Item_Reservacion {

    private String Nombre;
    private String Fecha_inicio;
    private String Fecha_final;
    private String Estado;

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getFecha_inicio() {
        return Fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        Fecha_inicio = fecha_inicio;
    }

    public String getFecha_final() {
        return Fecha_final;
    }

    public void setFecha_final(String fecha_final) {
        Fecha_final = fecha_final;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }
}
