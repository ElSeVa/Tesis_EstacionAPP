package com.example.myapplication.notificaciones;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Reservacion;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@SuppressLint("LogNotTimber")
public class NotificacionesHelper {

    private final Integer ID_NOTIFIC = 4421;
    private final String ID_CHANNEL = "Canal 1";

    private final Integer idConductor;
    private Integer idGarage = 0;
    private Notification notificacion;
    private final NotificationManagerCompat notificationManager;

    private final Context context;
    private Call<Garage> garageCall;
    private final APIService mAPIService = ApiUtils.getAPIService();

    public Integer getIdConductor() {
        return idConductor;
    }

    public Integer getIdGarage() {
        return idGarage;
    }

    public void setIdGarage(Integer idGarage) {
        this.idGarage = idGarage;
    }

    public NotificacionesHelper(Context context){
        this.context = context;
        Preferencias loginPref = new Preferencias("Login");
        idConductor = loginPref.getPrefInteger(context,"idConductor",0);
        garageCall = mAPIService.findIDGarage(idConductor);
        obtenerIdGarage(garageCall);
        notificationManager = NotificationManagerCompat.from(context);
        Log.d("Services-Notificacion","10 "+idConductor+" del NotificacionesHelper");
        Log.d("Services-Notificacion","11 "+idGarage+" del NotificacionesHelper");
    }

    public void crearNotificacion(){
        notificacion = new NotificationCompat.Builder(context, ID_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Nueva Reserva")
                .setContentText("Tienes una nueva reservacion para confirmar")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        Log.d("Services-Notificacion","2 Crea Notificacion del NotificacionesHelper");
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(ID_CHANNEL, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d("Services-Notificacion","1 Crea Notificacion Channel del NotificacionesHelper");
        }
    }

    public void verificarEstadosGarage(){
        if(idGarage != 0){
            Log.d("Services-Notificacion","3 Existe idGarage verificarEstadosGarage() del NotificacionesHelper");
            Call<List<Reservacion>> reservacionCall = mAPIService.obtenerReservasEstados(idGarage);
            reservacionCall.enqueue(new Callback<List<Reservacion>>() {
                @Override
                public void onResponse(Call<List<Reservacion>> call, Response<List<Reservacion>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        List<Reservacion> reservacionList = response.body();
                        if(reservacionList.size() != 0){
                            Log.d("Services-Notificacion","4 Envio notificacion verificarEstadosGarage() del NotificacionesHelper");
                            notificationManager.notify(ID_NOTIFIC,notificacion);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Reservacion>> call, Throwable t) {
                    Log.d("Services-Notificacion","5 Tira error la consulta verificarEstadosGarage() del NotificacionesHelper");
                }
            });
        }else{
            Log.d("Services-Notificacion","6 No existe idGarage verificarEstadosGarage() del NotificacionesHelper");
        }
    }

    public void obtenerIdGarage(Call<Garage> garageCall){
        if(idConductor!=0){
            garageCall.enqueue(new Callback<Garage>() {
                @Override
                public void onResponse(Call<Garage> call, Response<Garage> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Garage garage = response.body();
                        idGarage = garage.getId();
                        Log.d("Services-Notificacion","7 Obtiene la consulta obtenerIdGarage() del NotificacionesHelper");
                    }
                }

                @Override
                public void onFailure(Call<Garage> call, Throwable t) {
                    Log.d("Services-Notificacion","8 Tira error la consulta obtenerIdGarage() del NotificacionesHelper");
                }
            });
        }
    }
}
