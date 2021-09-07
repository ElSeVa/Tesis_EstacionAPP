package com.example.myapplication.ui.models;

public class Resena {
    private int ID;
    private String Usuario;
    private String Texto;
    private int Valoracion;
    private int ID_Garage;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getTexto() {
        return Texto;
    }

    public void setTexto(String texto) {
        Texto = texto;
    }

    public Integer getValoracion() {
        return Valoracion;
    }

    public void setValoracion(Integer valoracion) {
        Valoracion = valoracion;
    }

    public Integer getID_Garage() {
        return ID_Garage;
    }

    public void setID_Garage(Integer ID_Garage) {
        this.ID_Garage = ID_Garage;
    }
}
