package com.example.myapplication.ui.models;

public class Mapa {
    private Integer ID;
    private String latitud;
    private String longitud;
    private Integer ID_Garage;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public Integer getID_Garage() {
        return ID_Garage;
    }

    public void setID_Garage(Integer ID_Garage) {
        this.ID_Garage = ID_Garage;
    }
}
