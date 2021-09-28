package com.example.myapplication.preferencias;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Switch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Preferencias implements ItemPreferencias{

    private final String PREFS_KEY;

    public Preferencias(String PREFS_KEY) {
        this.PREFS_KEY = PREFS_KEY;
    }

    public void removePref(Context context, String valor){
        SharedPreferences settings = context.getSharedPreferences(this.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(valor);
        editor.apply();
    }

    public String getPrefString(Context context, String valor, String porDefecto) {
        SharedPreferences Preferencias = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return Preferencias.getString(valor,porDefecto);
    }

    public Integer getPrefInteger(Context context, String valor,Integer porDefecto) {
        SharedPreferences Preferencias = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return Preferencias.getInt(valor,porDefecto);
    }

    public Long getPrefLong(Context context, String valor, Long porDefecto) {
        SharedPreferences Preferencias = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return Preferencias.getLong(valor,porDefecto);
    }

    public Boolean getPrefBoolean(Context context, String valor, Boolean porDefecto) {
        SharedPreferences Preferencias = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        return Preferencias.getBoolean(valor,porDefecto);
    }

    @Override
    public void setPrefTiempos(Context context, Map<String, String> value) {
        SharedPreferences settings = context.getSharedPreferences(this.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        for(Map.Entry<String, String> entry : value.entrySet()){
            if(entry.getKey().equalsIgnoreCase("tiempoRestante")){
                editor.putLong("tiempoRestante",Long.parseLong(entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("seEstaEjecutando")){
                editor.putBoolean("seEstaEjecutando",Boolean.parseBoolean(entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("tiempoFinal")){
                editor.putLong("tiempoRestante",Long.parseLong(entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("idReservacion")){
                editor.putInt("idReservacion",Integer.parseInt(entry.getValue()));
            }
        }
        editor.apply();
    }

    @Override
    public void setPrefLogin(Context context, Map<String, String> value) {
        SharedPreferences settings = context.getSharedPreferences(this.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        for(Map.Entry<String, String> entry : value.entrySet()){
            if(entry.getKey().equalsIgnoreCase("idConductor")){
                editor.putInt("idConductor",Integer.parseInt(entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("idGarage")){
                editor.putInt("idGarage",Integer.parseInt(entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("Vehiculo")){
                editor.putString("Vehiculo",entry.getValue());
            }
        }
        editor.apply();
    }

    @Override
    public void setPrefCuenta(Context context, Map<String, String> value) {
        SharedPreferences settings = context.getSharedPreferences(this.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        for(Map.Entry<String, String> entry : value.entrySet()){
            if(entry.getKey().equalsIgnoreCase("idConductor")){
                editor.putInt("idConductor",Integer.parseInt(entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("Nombre")){
                editor.putString("Nombre",entry.getValue());
            }
            if(entry.getKey().equalsIgnoreCase("Uri")){
                editor.putString("Uri",entry.getValue());
            }
        }
        editor.apply();
    }

    @Override
    public void setPrefMantener(Context context, Map<String, String> value) {
        SharedPreferences settings = context.getSharedPreferences(this.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        for(Map.Entry<String, String> entry : value.entrySet()){
            if(entry.getKey().equalsIgnoreCase("Check")){
                editor.putBoolean("Check",Boolean.parseBoolean(entry.getValue()));
            }
            if(entry.getKey().equalsIgnoreCase("Usuario")){
                editor.putString("Usuario",entry.getValue());
            }
            if(entry.getKey().equalsIgnoreCase("Password")){
                editor.putString("Password",entry.getValue());
            }
        }
        editor.apply();
    }

    @Override
    public void setPrefFiltros(Context context, Map<String, String> value) {
        SharedPreferences settings = context.getSharedPreferences(this.PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        for(Map.Entry<String, String> entry : value.entrySet()){
            if(entry.getKey().equalsIgnoreCase("vehiculo")){
                editor.putString("vehiculo",entry.getValue());
            }
            if(entry.getKey().equalsIgnoreCase("horario")){
                editor.putString("horario",entry.getValue());
            }
            if(entry.getKey().equalsIgnoreCase("precio")){
                editor.putString("precio",entry.getValue());
            }
            if(entry.getKey().equalsIgnoreCase("filtro")){
                editor.putString("filtro",entry.getValue());
            }
        }
        editor.apply();
    }
}
