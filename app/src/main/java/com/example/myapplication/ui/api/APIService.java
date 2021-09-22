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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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


    @GET("/conductor/{id}")
    Call<Conductor> findConductor(@Path(value="id") Integer id);

    @GET("/conductor/query")
    Call<Conductor> findConductorEmail(@Query(value="email", encoded=true) String email);

    @Headers("Content-type: multipart/form-data")
    @GET("/conductor")
    Call<List<Conductor>> findAllConductor();


/*
*   2. Garage
*/
    @GET("/garage/{id}")
    Call<Garage> findGarage(@Path(value="id", encoded=true) int id);

    @GET("/garage/conductor/{idConductor}")
    Call<Garage> findIDGarage(@Path(value="idConductor", encoded=true) int idConductor);

    @GET("/garage")
    Call<List<Garage>> findAllGarage();

/*
*   3. Estadia
*/
    @GET("/estadia/{id}")
    Call<Estadia> findEstadia(@Path(value="id") int id);

    @GET("/estadia/garage/{id_garage}")
    Call<List<Estadia>> findEstadiaID_Garage(@Path(value="id_garage", encoded=true) int idGarage);

    @GET("/verificar/{idGarage}/{vehiculo}/{horario}")
    Call<List<Estadia>> findEstadiaTipos(@Path("idGarage") Integer idGarage, @Path("tipoVehiculo") String tipoVehiculo, @Path("estadia") String estadia);

    @GET("/estadia")
    Call<List<Estadia>> findAllEstadia();

    @GET("/estadia/ordenar")
    Call<List<Estadia>> findAllEstadiaPrecio(@Query(value="precio", encoded = true) String precio);

    @GET("/estadia/filter/{id_garage}")
    Call<List<Estadia>> findAllFilterEstadiaID(@Path(value="id_garage", encoded = true) Integer idGarage, @Query(value="filter", encoded = true) String filter);

    @GET("/estadia/filter")
    Call<List<Estadia>> findAllFilterEstadia(@Query(value="groupBy", encoded = true) String filter);

/*
*   4. Mapa
*/
    @GET("/mapa/{id}")
    Call<Mapa> findMapa(@Path(value="id") int id);

    @GET("/mapa")
    Call<List<Mapa>> findAllMapa();

/*
*   5. Imagenes
*/
    @GET("/imagenes/garage/{idGarage}")
    Call<List<Imagenes>> findImagenes(@Path(value = "idGarage") int idGarage);

    @GET("/imagenes")
    Call<List<Imagenes>> findAllImagenes();

/*
*   6. Reseña
*/
    @GET("/resena/{id}")
    Call<List<Resena>> findResena(@Path(value = "id") int id);

    @GET("/resena/garage/{idGarage}")
    Call<List<Resena>> findResenaID_Garage(@Path(value = "idGarage") int idGarage);

    @GET("/resena")
    Call<List<Resena>> findAllResena();

/*
*   7. Reservaciones
*/
    @GET("/reservacion/{id}")
    Call<Reservacion> findReservacion(@Path(value = "id", encoded = true) int id);

    @Headers("Content-type: multipart/form-data")
    @GET("/reservacion/promo")
    Call<List<Item_Promocion>> findReservacionGarage(@Query(value = "id_garage", encoded = true) int idGarage);

    @Headers("Content-type: multipart/form-data")
    @GET("/reservacion/query")
    Call<List<Item_Reservacion>> findReservacionConductor(@Query(value = "id_conductor", encoded = true) int idConductor);

    @GET("/reservacion")
    Call<List<Reservacion>> findAllReservacion();

/*
*   Post
*
*   1. Conductor
*/
    @POST("/conductor")
    Call<Conductor> insertConductor(@Body Conductor conductor);

    @POST("/conductor/login")
    Call<Conductor> findConductorLogin(@Field("email") String email, @Field("contrasena") String contrasena);

/*
*   2. Reservaciones
*/
    @FormUrlEncoded
    @POST("/reservacion")
    Call<Reservacion> insertReserva(@Field("Precio") int precio,
                                   @Field("Estadia") String estadia,
                                   @Field("Cantidad") int cantidad,
                                   @Field("Fecha_inicio") String fecha_inicio,
                                   @Field("Fecha_final") String Fecha_final,
                                   @Field("Estado") String Estado,
                                   @Field("ID_Conductor") int id_conductor,
                                   @Field("ID_Garage") int id_garage);
    @POST("/reservacion")
    Call<Reservacion> insertsReserva(@Body Reservacion reservacion);
/*
*   3. Resenas
*/
    @POST("/resena")
    Call<Resena> insertResena(@Body Resena resena);
/*
*   Update
*
*   1. Garage
*/
    @Headers("Content-type: multipart/form-data")
    @PUT("Garage.php")
    Call<Garage> updateDisponibilidad( @Query("Disponibilidad") String disponibilidad, @Query("ID") int id);
}
