package com.example.myapplication.notificaciones;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.preferencias.Preferencias;

@SuppressLint("LogNotTimber")
public class NotificacionesServices extends Service {
    NotificacionesHelper notificacionesHelper;
    Thread t;

    public NotificacionesServices() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificacionesHelper = new NotificacionesHelper(getBaseContext());
        /*
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i<= 60; i++){
                    notificacionesHelper.verificarEstadosGarage();
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                }
            }
        });
        */
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Services-Notificacion","Iniciando Servicio");
        notificacionesHelper.createNotificationChannel();
        notificacionesHelper.activarLuegoDeXtiempo();
        //t.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Services-Notificacion","Destruyendo Servicio");
        notificacionesHelper.pararTimer();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
