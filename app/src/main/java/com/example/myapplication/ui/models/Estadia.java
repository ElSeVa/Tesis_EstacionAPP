package com.example.myapplication.ui.models;


public class Estadia {
    private Integer ID;
    private Integer Precio;
    private String Horario;
    private String VehiculoPermitido;
    private Integer ID_Garage;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getPrecio() {
        return Precio;
    }

    public void setPrecio(Integer Precio) {
        this.Precio = Precio;
    }

    public String getHorario() {
        return Horario;
    }

    public void setHorario(String Horario) {
        this.Horario = Horario;
    }

    public String getVehiculoPermitido() {
        return VehiculoPermitido;
    }

    public void setVehiculoPermitido(String VehiculoPermitido) {
        this.VehiculoPermitido = VehiculoPermitido;
    }

    public Integer getID_Garage() {
        return ID_Garage;
    }

    public void setID_Garage(Integer ID_Garage) {
        this.ID_Garage = ID_Garage;
    }
}
