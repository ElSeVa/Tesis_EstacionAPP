package com.example.myapplication.ui.api;

import android.content.Intent;

import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Imagenes;
import com.example.myapplication.ui.models.Item_Promocion;
import com.example.myapplication.ui.models.Item_Reservacion;
import com.example.myapplication.ui.models.Mapa;
import com.example.myapplication.ui.models.Resena;
import com.example.myapplication.ui.models.Reservacion;

import java.util.List;
import java.util.Map;
import java.util.Observable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface APIService {
/*
*   Getters
*
*   1. Conductor
*/


    @Headers("Content-type: multipart/form-data")
    @GET("Conductor.php")
    Call<Conductor> findConductor(@Query(value="id", encoded=true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("Conductor.php")
    Call<Conductor> findConductorEmail(@Query(value="email", encoded=true) String email);

    @Headers("Content-type: multipart/form-data")
    @GET("Conductor.php")
    Call<List<Conductor>> findAllConductor();

    @Headers("Content-type: multipart/form-data")
    @GET("Conductor.php")
    Call<Conductor> findConductorLogin(@Query(value="email", encoded = true) String email, @Query(value="contrasena", encoded = true) String contrasena);

/*
*   2. Garage
*/
    @Headers("Content-type: multipart/form-data")
    @GET("Garage.php")
    Call<Garage> findGarage(@Query(value="id", encoded=true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("Garage.php")
    Call<Garage> findIDGarage(@Query(value="idConductor", encoded=true) int idConductor);

    @Headers("Content-type: multipart/form-data")
    @GET("Garage.php")
    Call<List<Garage>> findAllGarage();

/*
*   3. Estadia
*/
    @Headers("Content-type: multipart/form-data")
    @GET("Estadia.php")
    Call<Estadia> findEstadia(@Query(value="id", encoded=true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("Estadia.php")
    Call<List<Estadia>> findEstadiaID_Garage(@Query(value="idGarage", encoded=true) int idGarage);

    @Headers("Content-type: multipart/form-data")
    @GET("Estadia.php")
    Call<List<Estadia>> findEstadiaTipos(@Query("idGarage") Integer idGarage, @Query("tipoVehiculo") String tipoVehiculo, @Query("estadia") String estadia);

    @Headers("Content-type: multipart/form-data")
    @GET("Estadia.php")
    Call<List<Estadia>> findAllEstadia();

    @Headers("Content-type: multipart/form-data")
    @GET("Estadia.php")
    Call<List<Estadia>> findAllEstadiaPrecio(@Query(value="precio", encoded = true) String precio);

    @Headers("Content-type: multipart/form-data")
    @GET("Estadia.php")
    Call<List<Estadia>> findAllFilterEstadiaID(@Query(value="idGarage", encoded = true) Integer idGarage, @Query(value="filter", encoded = true) String filter);

    @Headers("Content-type: multipart/form-data")
    @GET("Estadia.php")
    Call<List<Estadia>> findAllFilterEstadia(@Query(value="filter", encoded = true) String filter);

/*
*   4. Mapa
*/
    @Headers("Content-type: multipart/form-data")
    @GET("Mapa.php")
    Call<Mapa> findMapa(@Query(value="id", encoded=true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("Mapa.php")
    Call<List<Mapa>> findAllMapa();

/*
*   5. Imagenes
*/
    @Headers("Content-type: multipart/form-data")
    @GET("Imagenes.php")
    Call<List<Imagenes>> findImagenes(@Query(value = "idGarage", encoded = true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("Imagenes.php")
    Call<List<Imagenes>> findAllImagenes();

/*
*   6. Reseña
*/
    @Headers("Content-type: multipart/form-data")
    @GET("Resenas.php")
    Call<List<Resena>> findResena(@Query(value = "id", encoded = true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("Resenas.php")
    Call<List<Resena>> findResenaID_Garage(@Query(value = "idGarage", encoded = true) int idGarage);

    @Headers("Content-type: multipart/form-data")
    @GET("Resenas.php")
    Call<List<Resena>> findAllResena();

/*
*   7. Reservaciones
*/
    @Headers("Content-type: multipart/form-data")
    @GET("Reservacion.php")
    Call<Reservacion> findReservacion(@Query(value = "id", encoded = true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("Reservacion.php")
    Call<List<Item_Promocion>> findReservacionGarage(@Query(value = "idGarage", encoded = true) int idGarage);

    @Headers("Content-type: multipart/form-data")
    @GET("Reservacion.php")
    Call<List<Item_Reservacion>> findReservacionConductor(@Query(value = "idConductor", encoded = true) int idConductor);

    @Headers("Content-type: multipart/form-data")
    @GET("Reservacion.php")
    Call<List<Reservacion>> findAllReservacion();

/*
*   Post
*
*   1. Conductor
*/
    @FormUrlEncoded
    @POST("Conductor.php")
    Call<Conductor> insertConductor(@Field("Nombre") String nombre,
                                           @Field("Contrasena") String contrasena,
                                           @Field("Email") String email,
                                           @Field("TipoVehiculo") String tipoVehiculo,
                                           @Field("Propietario") String propietario);

/*
*   2. Reservaciones
*/
    @FormUrlEncoded
    @POST("Reservacion.php")
    Call<Reservacion> insertReserva(@Field("Precio") int precio,
                                           @Field("Estadia") String estadia,
                                           @Field("Cantidad") int cantidad,
                                           @Field("Fecha_inicio") String fecha_inicio,
                                           @Field("Fecha_final") String Fecha_final,
                                           @Field("Estado") String Estado,
                                           @Field("ID_Conductor") int id_conductor,
                                           @Field("ID_Garage") int id_garage);
    @FormUrlEncoded
    @POST("Reservacion.php")
    Call<Reservacion> insertsReserva(@FieldMap Map<String, String> fields);
/*
*   3. Resenas
*/
    @FormUrlEncoded
    @POST("Resenas.php")
    Call<Resena> insertResena(@Field("Usuario") String usuario,
                                @Field("Texto") String texto,
                                @Field("Valoracion") int valoracion,
                                @Field("ID_Garage") int id_garage);
/*
*   Update
*
*   1. Garage
*/
    @Headers("Content-type: multipart/form-data")
    @PUT("Garage.php")
    Call<Garage> updateDisponibilidad( @Query("Disponibilidad") String disponibilidad, @Query("ID") int id);
}
