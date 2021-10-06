package com.example.myapplication.notificaciones;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

@SuppressLint("LogNotTimber")
public class NotificacionesServices extends Service {
    NotificacionesHelper notificacionesHelper;

    public NotificacionesServices() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificacionesHelper = new NotificacionesHelper(getBaseContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Services-Notificacion","Iniciando Servicio");
        notificacionesHelper.createNotificationChannel();
        notificacionesHelper.crearNotificacion();
        new Handler().postDelayed(() -> {
            notificacionesHelper.verificarEstadosGarage();
        },2000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Services-Notificacion","Destruyendo Servicio");
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
