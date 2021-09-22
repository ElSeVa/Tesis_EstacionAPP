package com.example.myapplication.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Resena {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("usuario")
    @Expose
    private String usuario;
    @SerializedName("texto")
    @Expose
    private String texto;
    @SerializedName("valoracion")
    @Expose
    private int valoracion;
    @SerializedName("idGarage")
    @Expose
    private int idGarage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getValoracion() {
        return valoracion;
    }

    public void setValoracion(int valoracion) {
        this.valoracion = valoracion;
    }

    public int getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(int idGarage) {
        this.idGarage = idGarage;
    }
}
