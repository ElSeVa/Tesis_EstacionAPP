package com.example.myapplication.ui.models;

import java.util.Observable;
import java.util.Observer;

public class Reservacion {

    private int ID;
    private int Precio;
    private String Estadia;
    private int Cantidad;
    private String Fecha_inicio;
    private String Fecha_final;
    private String Estado;
    private int ID_Conductor;
    private int ID_Garage;

    public Reservacion(int Precio, String Estadia, int Cantidad, String Fecha_inicio, String Fecha_final, String Estado, int ID_Conductor, int ID_Garage) {
        this.Precio = Precio;
        this.Estadia = Estadia;
        this.Cantidad = Cantidad;
        this.Fecha_inicio = Fecha_inicio;
        this.Fecha_final = Fecha_final;
        this.Estado = Estado;
        this.ID_Conductor = ID_Conductor;
        this.ID_Garage = ID_Garage;
    }


    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPrecio() {
        return Precio;
    }

    public void setPrecio(int precio) {
        Precio = precio;
    }

    public String getEstadia() {
        return Estadia;
    }

    public void setEstadia(String estadia) {
        Estadia = estadia;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }

    public String getFecha_inicio() {
        return Fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        Fecha_inicio = fecha_inicio;
    }

    public String getFecha_final() {
        return Fecha_final;
    }

    public void setFecha_final(String fecha_final) {
        Fecha_final = fecha_final;
    }

    public int getID_Conductor() {
        return ID_Conductor;
    }

    public void setID_Conductor(int ID_Conductor) {
        this.ID_Conductor = ID_Conductor;
    }

    public int getID_Garage() {
        return ID_Garage;
    }

    public void setID_Garage(int ID_Garage) {
        this.ID_Garage = ID_Garage;
    }

}