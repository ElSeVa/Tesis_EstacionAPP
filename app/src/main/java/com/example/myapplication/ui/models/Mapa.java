package com.example.myapplication.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mapa {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("latitud")
    @Expose
    private String latitud;
    @SerializedName("longitud")
    @Expose
    private String longitud;
    @SerializedName("idGarage")
    @Expose
    private Integer idGarage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(Integer idGarage) {
        this.idGarage = idGarage;
    }
}
