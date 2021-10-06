package com.example.myapplication.ui.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Estadia {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("precio")
    @Expose
    private Integer precio;
    @SerializedName("horario")
    @Expose
    private String horario;
    @SerializedName("vehiculoPermitido")
    @Expose
    private String vehiculoPermitido;
    @SerializedName("idGarage")
    @Expose
    private Integer idGarage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrecio() {
        return precio;
    }

    public void setPrecio(Integer precio) {
        this.precio = precio;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getVehiculoPermitido() {
        return vehiculoPermitido;
    }

    public void setVehiculoPermitido(String vehiculoPermitido) {
        this.vehiculoPermitido = vehiculoPermitido;
    }

    public Integer getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(Integer idGarage) {
        this.idGarage = idGarage;
    }
}
