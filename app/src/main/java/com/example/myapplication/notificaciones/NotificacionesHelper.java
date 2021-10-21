package com.example.myapplication.notificaciones;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Reservacion;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
@SuppressLint("LogNotTimber")
public class NotificacionesHelper {

    private Integer ID_NOTIFIC = 4421;
    private final String ID_CHANNEL = "Canal 1";
    private final Timer timer;

    private final Integer idConductor;
    private Integer idGarage = 0;
    private Integer idReserva = 0;
    private Notification notificacion;
    private Integer totalNotificaciones = 0;
    private Integer anteriorTotalNotificacion = 0;
    private Integer totalNotificacionesConductor = 0;
    private Integer anteriorTotalNotificacionConductor = 0;
    private boolean nuevaNotificacion = false;
    private Reservacion notificacionReserva;
    private final Preferencias tiempoPrefs;
    private List<Reservacion> reservacions;

    private final NotificationManagerCompat notificationManager;

    private final Context context;
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
        notificacion = null;
        this.context = context;
        Preferencias loginPref = new Preferencias("Login");
        idConductor = loginPref.getPrefInteger(context,"idConductor",0);
        tiempoPrefs = new Preferencias("Tiempo"+idConductor);
        obtenerIdGarage();
        notificationManager = NotificationManagerCompat.from(context);
        timer = new Timer();
    }


    public void activarLuegoDeXtiempo() {
        TimerTask timerTask = new TimerTask() {
            int tic = 0;
            @Override
            public void run() {
                if(tic%2==0){
                    if(idGarage != 0){
                        totalDeNotificacionesGarage();
                    }
                    totalDeNotificacionesConductor();
                }else {
                    if(idGarage != 0){
                        enviarNotificacionGarage();
                    }
                    enviarNotificacionConductor();
                }
                tic++;
            }
        };
        timer.schedule(timerTask,2,2000);
    }

    public void pararTimer(){
        timer.cancel();
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
            Log.d("Services-Notificacion","Crea Notificacion Channel");
        }
    }

    private void crearNotificacionGarage(int conteo){
        notificacion = null;
        if(conteo<=1){
            notificacion = new NotificationCompat.Builder(context, ID_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Nueva Reserva")
                    .setContentText("Tienes "+conteo+" nueva reservacion para confirmar")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        }else {
            notificacion = new NotificationCompat.Builder(context, ID_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Nueva Reserva")
                    .setContentText("Tienes "+conteo+" nuevas reservaciones para confirmar")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
        }
        Log.d("Services-Notificacion","Se crea la notificacion del garage");
    }

    private void totalDeNotificacionesGarage(){
        Call<List<Reservacion>> reservacionCall = mAPIService.obtenerReservasEstados(idGarage);
        reservacionCall.enqueue(new Callback<List<Reservacion>>() {
            @Override
            public void onResponse(Call<List<Reservacion>> call, Response<List<Reservacion>> response) {
                if(response.isSuccessful() && response.body() != null){
                    List<Reservacion> reservacionList = response.body();
                    reservacions = response.body();
                    totalNotificaciones = reservacionList.size();
                    Log.d("Services-Notificacion","Se obtiene el total de las notificaciones");
                }
            }

            @Override
            public void onFailure(Call<List<Reservacion>> call, Throwable t) {
                Log.d("Services-Notificacion","Tira error la consulta g");
            }
        });
    }

    public void enviarNotificacionGarage(){
        if(idGarage != 0){
            //boolean nuevaNotificacion=false;
            Log.d("Services-Notificacion","Existe un id_garage");
            if(totalNotificaciones != 0){
                if(totalNotificaciones.equals(anteriorTotalNotificacion)){
                    nuevaNotificacion = false;
                }else{
                    nuevaNotificacion = true;
                    crearNotificacionGarage(totalNotificaciones);
                    anteriorTotalNotificacion = totalNotificaciones;
                }
                if(nuevaNotificacion){
                    if(notificacion != null){
                        notificationManager.notify(ID_NOTIFIC++,notificacion);
                        Alerta();
                        Log.d("Services-Notificacion","Se envia notificacion al garage");
                    }
                }
            }
        }

    }

    public void obtenerIdGarage(){
        Call<Garage> garageCall = mAPIService.findIDGarage(idConductor);
        if(idConductor!=0){
            garageCall.enqueue(new Callback<Garage>() {
                @Override
                public void onResponse(Call<Garage> call, Response<Garage> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Garage garage = response.body();
                        idGarage = garage.getId();
                        Log.d("Services-Notificacion","Se obtiene la id_garage");
                    }
                }

                @Override
                public void onFailure(Call<Garage> call, Throwable t) {
                    Log.d("Services-Notificacion","Tira error la consulta ig");
                }
            });
        }
    }

    private void crearNotificacionConductor(String estado){
        if(nuevaNotificacion){
            Log.d("Services-Notificacion","Notificacion Reserva no es Nulo");
            switch (estado){
                case "Aceptado":
                    Log.d("Services-Notificacion","Se crea la notificacion 'Aceptado' del conductor");
                    notificacion = new NotificationCompat.Builder(context, ID_CHANNEL)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Aceptaron su reservacion")
                            .setContentText("El garage Acepto su reservacion")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
                    break;
                case "Cancelado":
                    Log.d("Services-Notificacion","Se crea la notificacion 'Cancelado' del conductor");
                    notificacion = new NotificationCompat.Builder(context, ID_CHANNEL)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Cancelaron su reservacion")
                            .setContentText("El garage Cancelo su reservacion")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
                    break;
                default:
                    notificacion = null;
                    Log.d("Services-Notificacion","No hay reservas de notificacion");
                    break;
            }
        }else{
            Log.d("Services-Notificacion","Notificacion Reserva es Nulo");
        }
    }

    private void totalDeNotificacionesConductor(){
        if(new Preferencias("notificacion").getPrefInteger(context,"idReservas",0) != 0){
            idReserva = new Preferencias("notificacion").getPrefInteger(context,"idReservas",0);
        }
        if(idReserva != 0){
            Call<Reservacion> reservacionCall = mAPIService.obtenerReservacionPorId(idReserva);
            reservacionCall.enqueue(new Callback<Reservacion>() {
                @Override
                public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                    if(response.isSuccessful() && response.body() != null){
                        notificacionReserva = response.body();
                        switch (notificacionReserva.getEstado()){
                            case "Aceptado":
                                Log.d("Services-Notificacion","Hay reserva Aceptado "+idReserva);
                                idReserva = tiempoPrefs.getPrefInteger(context,"idReservacion",0);
                                nuevaNotificacion = true;
                                totalNotificacionesConductor = 1;
                                anteriorTotalNotificacionConductor = 0;
                                if(new Preferencias("notificacion").clearPref(context)){
                                    Log.d("Services-Notificacion","idReservas:" + new Preferencias("notificacion").getPrefInteger(context,"idReservas",0));
                                }
                                break;
                            case "Cancelado":
                                Log.d("Services-Notificacion","Hay reserva Cancelado "+idReserva);
                                idReserva = tiempoPrefs.getPrefInteger(context,"idReservacion",0);
                                nuevaNotificacion = true;
                                totalNotificacionesConductor = 1;
                                anteriorTotalNotificacionConductor = 0;
                                if(new Preferencias("notificacion").clearPref(context)){
                                    Log.d("Services-Notificacion","idReservas:" + new Preferencias("notificacion").getPrefInteger(context,"idReservas",0));
                                }
                                break;
                            default:
                                //Log.d("Services-Notificacion","Hay reserva en modo Esperando "+notificacionReserva.getId());
                                nuevaNotificacion = false;
                                totalNotificacionesConductor = 0;
                                break;
                        }

                    }
                }

                @Override
                public void onFailure(Call<Reservacion> call, Throwable t) {
                    Log.d("Services-Notificacion","Tira error la consulta c" + t.getMessage());
                }
            });
        }
    }

    public void enviarNotificacionConductor(){
        if(totalNotificacionesConductor != 0){
            if(totalNotificacionesConductor.equals(anteriorTotalNotificacionConductor)){
                nuevaNotificacion = false;
            }else {
                nuevaNotificacion = true;
                crearNotificacionConductor(notificacionReserva.getEstado());
                anteriorTotalNotificacionConductor = totalNotificacionesConductor;
            }
            if(nuevaNotificacion) {
                if (notificacion != null) {
                    notificationManager.notify(ID_NOTIFIC++, notificacion);
                    Alerta();
                    Log.d("Services-Notificacion", "Se envia la notificacion al conductor");
                }
            }
        }
    }

    private void Alerta(){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(800);
    }

}
