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
    Call<List<Estadia>> obtenerEstadiaPorIdGarage(@Path(value="id_garage", encoded=true) int idGarage);

    @GET("/estadia/verificar/{idGarage}/{vehiculo}/{horario}")
    Call<Estadia> verificarEstadia(@Path("idGarage") Integer idGarage, @Path("vehiculo") String tipoVehiculo, @Path("horario") String estadia);

    @GET("/estadia")
    Call<List<Estadia>> obtenerEstadias();

    @GET("/estadia/ordenar")
    Call<List<Estadia>> ordenarPrecios(@Query(value="precio", encoded = true) String precio);

    @GET("/estadia/filter/{id_garage}")
    Call<List<Estadia>> groupByPorIdGarage(@Path(value="id_garage", encoded = true) Integer idGarage, @Query(value="filter", encoded = true) String filter);

    @GET("/estadia/filter")
    Call<List<Estadia>> groupBy(@Query(value="groupBy", encoded = true) String filter);

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
    Call<List<Imagenes>> obtenerImagenesPorIdGarage(@Path(value = "idGarage") int idGarage);

    @GET("/imagenes")
    Call<List<Imagenes>> findAllImagenes();

/*
*   6. Reseña
*/
    @GET("/resena/{id}")
    Call<Resena> obtenerPorId(@Path(value = "id") int id);

    @GET("/resena/garage/{idGarage}")
    Call<List<Resena>> obtenerPorIdGarage(@Path(value = "idGarage") int idGarage);

    @GET("/resena")
    Call<List<Resena>> obtenerResenas();

/*
*   7. Reservaciones
*/

    @GET("/reservacion/{id}")
    Call<Reservacion> obtenerReservacionPorId(@Path(value = "id", encoded = true) int id);

    @GET("/reservacion/promo")
    Call<List<Item_Promocion>> obtenerFrecuencia(@Query(value = "id_garage", encoded = true) int idGarage);

    @GET("/reservacion/query")
    Call<List<Item_Reservacion>> obtenerReservasConductor(@Query(value = "id_conductor", encoded = true) int idConductor);

    @GET("/reservacion/estado/{idGarage}")
    Call<List<Reservacion>> obtenerReservasEstados(@Path(value = "idGarage") int idGarage);

    @GET("/reservacion/estados/{idConductor}")
    Call<List<Reservacion>> obtenerReservasEstadosConductor(@Path(value = "idConductor") int idConductor);

    @GET("/reservacion")
    Call<List<Reservacion>> obtenerReservaciones();

/*
*   Post
*
*   1. Conductor
*/
    @POST("/conductor")
    Call<Conductor> insertConductor(@Body Conductor conductor);

    @FormUrlEncoded
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
    @POST("/garage/{id}")
    Call<Garage> updateDisponibilidad( @Path("id") int id, @Body Garage garage);
}
