package com.example.myapplication.ui.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.InputStream;
import java.sql.Blob;

public class Imagenes {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("tipo")
    @Expose
    private String tipo;
    @SerializedName("imagen")
    @Expose
    private String imagen;
    @SerializedName("idGarage")
    @Expose
    private Integer idGarage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(Integer idGarage) {
        this.idGarage = idGarage;
    }
}
