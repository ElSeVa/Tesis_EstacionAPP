package com.example.myapplication.enums;

public enum Horario {
    HORA("Hora"),
    MEDIA_ESTADIA("Media Estadia"),
    ESTADIA("Estadia");

    private String horarios;

    private Horario(String horarios){
        this.horarios = horarios;
    }

    public String getHorarios(){
        return horarios;
    }
}
