package com.example.myapplication.ui.models;

import android.content.Intent;

import com.example.myapplication.ui.api.Api;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Conductor {

    private Integer ID;
    private String Nombre;
    private String Contrasena;
    private String Email;
    private String TipoVehiculo;
    private Integer Propietario;

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

    public String getContrasena() {
        return Contrasena;
    }

    public void setContrasena(String contrasena) {
        Contrasena = contrasena;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getTipoVehiculo() {
        return TipoVehiculo;
    }

    public void setTipoVehiculo(String tipoVehiculo) {
        TipoVehiculo = tipoVehiculo;
    }

    public Integer getPropietario() {
        return Propietario;
    }

    public void setPropietario(Integer propietario) {
        Propietario = propietario;
    }
}
