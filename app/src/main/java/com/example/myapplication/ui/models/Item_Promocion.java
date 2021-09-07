package com.example.myapplication.ui.models;

public class Item_Promocion {
    private Integer ID;
    private String Nombre;
    private String TipoVehiculo;
    private Integer Frecuencia;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTipoVehiculo() {
        return TipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        TipoVehiculo = tipoVehiculo;
    }

    public Integer getFrecuencia() {
        return Frecuencia;
    }

    public void setFrecuencia(Integer frecuencia) {
        Frecuencia = frecuencia;
    }
}
