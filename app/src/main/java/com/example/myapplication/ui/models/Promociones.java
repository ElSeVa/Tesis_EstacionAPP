package com.example.myapplication.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Promociones {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("idConductor")
    @Expose
    private Integer idConductor;
    @SerializedName("idPromo")
    @Expose
    private Integer idPromo;
    @SerializedName("idGarage")
    @Expose
    private Integer idGarage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(Integer idConductor) {
        this.idConductor = idConductor;
    }

    public Integer getIdPromo() {
        return idPromo;
    }

    public void setIdPromo(Integer idPromo) {
        this.idPromo = idPromo;
    }

    public Integer getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(Integer idGarage) {
        this.idGarage = idGarage;
    }
}
