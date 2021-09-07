package com.example.myapplication.ui.models;

public class Garage {
    private Integer ID;
    private String Nombre;
    private String Direccion;
    private String Disponibilidad;
    private String Telefono;
    private Integer ID_Conductor;

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

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getDisponibilidad() {
        return Disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        Disponibilidad = disponibilidad;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public Integer getID_Conductor() {
        return ID_Conductor;
    }

    public void setID_Conductor(Integer ID_Conductor) {
        this.ID_Conductor = ID_Conductor;
    }
}
