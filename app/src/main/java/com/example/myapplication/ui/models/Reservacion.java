package com.example.myapplication.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Observable;
import java.util.Observer;

public class Reservacion {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("precio")
    @Expose
    private int precio;
    @SerializedName("estadia")
    @Expose
    private String estadia;
    @SerializedName("cantidad")
    @Expose
    private int cantidad;
    @SerializedName("fechaInicio")
    @Expose
    private String fechaInicio;
    @SerializedName("fechaFinal")
    @Expose
    private String fechaFinal;
    @SerializedName("estado")
    @Expose
    private String estado;
    @SerializedName("idConductor")
    @Expose
    private int idConductor;
    @SerializedName("idGarage")
    @Expose
    private int idGarage;

    public Reservacion(int precio, String estadia, int cantidad, String fechaInicio, String fechaFinal, String estado, int idConductor, int idGarage) {
        this.precio = precio;
        this.estadia = estadia;
        this.cantidad = cantidad;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.estado = estado;
        this.idConductor = idConductor;
        this.idGarage = idGarage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getEstadia() {
        return estadia;
    }

    public void setEstadia(String estadia) {
        this.estadia = estadia;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(int idConductor) {
        this.idConductor = idConductor;
    }

    public int getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(int idGarage) {
        this.idGarage = idGarage;
    }
}
