package com.example.myapplication.ui.api;

import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Mapa;
import com.example.myapplication.ui.models.Reservacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {
    Retrofit retrofit;
    String url = "http://192.168.0.76/rest/";
    APIService APIService;

    private void crearRetrofit(String url){
        retrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService = retrofit.create(APIService.class);
    }

    public Call<Conductor> findConductor(Integer id){
        crearRetrofit(url);
        Call<Conductor> call = APIService.findConductor(id);
        return call;
    }

    public Call<Conductor> loginConductor(String email, String contrasena){
        crearRetrofit(url);
        Call<Conductor> call = APIService.findConductorLogin(email,contrasena);
        return call;
    }

    public Call<List<Conductor>> findAllConductor(){
        crearRetrofit(url);
        Call<List<Conductor>> call = APIService.findAllConductor();
        return call;
    }

    public Call<List<Garage>> findAllGarage(){
        crearRetrofit(url);
        Call<List<Garage>> call = APIService.findAllGarage();
        return call;
    }

    public Call<Garage> findGarage(Integer id){
        crearRetrofit(url);
        Call<Garage> call = APIService.findGarage(id);
        return call;
    }

    public Call<Estadia> findEstadia(Integer id){
        crearRetrofit(url);
        Call<Estadia> call = APIService.findEstadia(id);
        return call;
    }

    public Call<List<Estadia>> findAllEstadia(){
        crearRetrofit(url);
        Call<List<Estadia>> call = APIService.findAllEstadia();
        return call;
    }

    public Call<List<Estadia>> findAllFilterEstadia(){
        crearRetrofit(url);
        Call<List<Estadia>> call = APIService.findAllFilterEstadia("Si");
        return call;
    }

    public Call<Mapa> findMapa(Integer id){
        crearRetrofit(url);
        Call<Mapa> call = APIService.findMapa(id);
        return call;
    }
    public Call<List<Mapa>> findAllMapa(){
        crearRetrofit(url);
        Call<List<Mapa>> call = APIService.findAllMapa();
        return call;
    }

    public Call<Reservacion> findReservacion(Integer ID){
        crearRetrofit(url);
        Call<Reservacion> call = APIService.findReservacion(ID);
        return call;
    }

    public Call<List<Reservacion>> findAllReservacion(){
        crearRetrofit(url);
        Call<List<Reservacion>> call = APIService.findAllReservacion();
        return call;
    }

    public Call<Reservacion> insertReserva(Reservacion reservacion){
        crearRetrofit(url);
        Call<Reservacion> call = APIService.insertReserva(reservacion.getPrecio(), reservacion.getEstadia(), reservacion.getCantidad(), reservacion.getFecha_inicio(), reservacion.getFecha_final(), String.valueOf(reservacion.getEstado()), reservacion.getID_Conductor(), reservacion.getID_Garage());
        return call;
    }


}

