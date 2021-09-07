package com.example.myapplication.ui.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;

import java.io.InputStream;
import java.sql.Blob;

public class Imagenes {
    private Integer ID;
    private String tipo;
    private String imagen;
    private Integer ID_Garage;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
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

    public void setImagen(String Imagen) {
        this.imagen = Imagen;
    }

    public Integer getID_Garage() {
        return ID_Garage;
    }

    public void setID_Garage(Integer ID_Garage) {
        this.ID_Garage = ID_Garage;
    }
}
