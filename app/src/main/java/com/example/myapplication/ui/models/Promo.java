package com.example.myapplication.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Promo {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("tipoPromo")
    @Expose
    private String tipoPromo;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("idGarage")
    @Expose
    private Integer idGarage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipoPromo() {
        return tipoPromo;
    }

    public void setTipoPromo(String tipoPromo) {
        this.tipoPromo = tipoPromo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(Integer idGarage) {
        this.idGarage = idGarage;
    }
}
