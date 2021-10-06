package com.example.myapplication.ui;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.myapplication.EventListeners;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Reservacion;
import com.google.android.gms.common.data.DataBufferObserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
@SuppressLint("LogNotTimber")
public class Temporizador implements EventListeners, LifecycleObserver {

    public CountDownTimer temporizador;
    public boolean isExecuteTime = false;
    public long tiempo;
    private long intervalos;
    public long tiempoFinal;
    public TextView textView;
    public final long tiempoTotal = 1800000;
    private final Context context;
    private final Preferencias tiempoPref;

    public void registerLifecycle(LifecycleOwner lifecycleOwner){
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    public Temporizador(Context context, long intervalos, int idConductor) {
        this.context = context;
        this.intervalos = intervalos;
        tiempoPref = new Preferencias("Tiempo"+idConductor);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void create() {
        Log.d("lifeCyCle","1 ON_CREATE Creando...");
        temporizador = null;
        tiempo = tiempoPref.getPrefLong(context,"tiempoRestante",tiempoTotal);
        tiempoFinal = tiempoPref.getPrefLong(context,"tiempoFinal",0L);
        isExecuteTime = tiempoPref.getPrefBoolean(context,"seEstaEjecutando",false);
        Log.d("lifeCyCle","1 ejecutando: " + isExecuteTime);
        Log.d("lifeCyCle","1 tiempo final: " + tiempoFinal);
        if(isExecuteTime){
            if(tiempo == tiempoTotal){
                creandoTemporizador(tiempoTotal);
                comenzarTemporizador();
                Log.d("lifeCyCle","1 ON_CREATE Creando Temporizador y comenzando...");
            }
        }else {
            creandoTemporizador(tiempoTotal);
            Log.d("lifeCyCle","1 ON_CREATE Creando Temporizador...");
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void started() {
        Log.d("lifeCyCle","2 ON_START Empezando...");
        tiempo = tiempoPref.getPrefLong(context,"tiempoRestante",tiempoTotal);
        isExecuteTime = tiempoPref.getPrefBoolean(context,"seEstaEjecutando",false);
        tiempoFinal = tiempoPref.getPrefLong(context,"tiempoFinal",0L);
        if(isExecuteTime){
            if(tiempo != tiempoTotal){
                if(tiempoFinal == 0){
                    creandoTemporizador(tiempo);
                    comenzarTemporizador();
                    Log.d("lifeCyCle","2 ON_START Creando Temporizador y comenzando...");
                }
            }
        }else {
            creandoTemporizador(tiempo);
            cancelarTemporizador();
            Log.d("lifeCyCle","2 ON_START Creando Temporizador y cancelando...");
        }


    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resume() {
        Log.d("lifeCyCle","3 ON_RESUME Resumiendo...");
        tiempoFinal = tiempoPref.getPrefLong(context,"tiempoFinal",0L);
        tiempo = tiempoFinal - System.currentTimeMillis();
        if(isExecuteTime){
            if(tiempoFinal != 0){
                creandoTemporizador(tiempo);
                comenzarTemporizador();
                Log.d("lifeCyCle","3 ON_RESUME Creando Temporizador y comenzando...");
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void pause() {
        Log.d("lifeCyCle","4 ON_PAUSE Pausando...");
        cancelarTemporizador();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void stopping() {
        Log.d("lifeCyCle","5 ON_STOP Parando...");
        if(isExecuteTime){
            tiempoFinal = System.currentTimeMillis() + tiempo;
            if(tiempoPref.setPrefLong(context,"tiempoFinal",tiempoFinal)){
                Log.d("lifeCyCle","5 ON_STOP Aplicando tiempo final...");
            }
            if(tiempoPref.setPrefLong(context,"tiempoRestante",tiempo)){
                Log.d("lifeCyCle","5 ON_STOP Aplicando tiempo restante...");
            }
        }else{
            if(tiempoPref.setPrefLong(context,"tiempoFinal",0L)){
                Log.d("lifeCyCle","5 ON_STOP Aplicando tiempo final 0...");
            }
        }

        //tiempoPref.setPrefTiempos(context,crearClaveValor(tiempo,isExecuteTime,tiempoFinal));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroy() {
        Log.d("lifeCyCle","6 ON_DESTROY Destruyendo...");
        if(isExecuteTime){
            if(tiempoPref.setPrefLong(context,"tiempoFinal",tiempoFinal)){
                Log.d("lifeCyCle","6 ON_DESTROY Aplicando tiempo final...");
            }
            if(tiempoPref.setPrefLong(context,"tiempoRestante",tiempo)){
                Log.d("lifeCyCle","6 ON_DESTROY Aplicando tiempo restante...");
            }
        }else{
            if(tiempoPref.setPrefLong(context,"tiempoFinal",0L)){
                Log.d("lifeCyCle","6 ON_DESTROY Aplicando tiempo final 0...");
            }
        }
        temporizador = null;
        //tiempoPref.setPrefTiempos(context,crearClaveValor(tiempo,isExecuteTime,tiempoFinal));
    }

    public void updateCountDownText() {
        int minutes = (int) (tiempo / 1000) / 60;
        int seconds = (int) (tiempo / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText(timeLeftFormatted);
    }

    private void creandoTemporizador(long tiempos){
        temporizador = new CountDownTimer(tiempos,intervalos) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempo = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isExecuteTime = false;
            }
        };
    }

    public void comenzarTemporizador(){
        temporizador.start();
    }

    public void cancelarTemporizador(){
        temporizador.cancel();
    }

    public void resetTimer(){
        tiempo = tiempoTotal;
        tiempoFinal = 0;
        isExecuteTime = false;
        if(tiempoPref.clearPref(context)){
            updateCountDownText();
        }
    }

    @Override
    public void update(boolean event) {
        if(!event){
            resetTimer();
        }
        setExecuteTime(event);
    }

    private void startTimer() {
        if(temporizador!=null){
            tiempoFinal = System.currentTimeMillis() + tiempo;
            //temporizador = new Temporizador(tiempo, 1000);
            //temporizador.setTiempoFinal(tiempoFinal);
            //temporizador.setExecuteTime(true);
            //temporizador.start();

            //temporizador.setTextView(tvTemporizador);
            //temporizador.setTiempo(tiempo);
            //temporizador.setExecuteTime(isExecute);
            //temporizador.setTiempoFinal(tiempoFinal);

        }
    }

    private void stopTimer() {
        if (temporizador != null) {
            Preferencias tiempoPref = new Preferencias("Tiempo");
            //temporizador.cancel();
            //temporizador.setExecuteTime(false);
            //mapTiempo.putAll(crearClaveValor());
            //tiempoPref.removePref(activity,"idReservacion");
            //tiempoPref.setPrefTiempos(activity,mapTiempo);
            //call.notificar(false);
        }
    }

    /*
    Esto se hace cuando se vuelve al home del mapa con el temporizador
    para obtener el tiempo final es:
        Hora Actual(System.currentTimeMillis()) + Tiempo Restante (temporizador.getTiempo())
    Esto se hace cuando se va a otro fragment o cierra la app
    y para el tiempo seria al revez:
        Tiempo Final(temporizador.getTiempoFinal()) - Hora Actual(System.currentTimeMillis())
     */


    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public long getTiempoFinal() {
        return tiempoFinal;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    public void setTiempoFinal(long tiempoFinal){
        this.tiempoFinal = tiempoFinal;
    }

    public boolean getExecuteTime(){
        return this.isExecuteTime;
    }

    public void setExecuteTime(boolean isExecuteTime){
        this.isExecuteTime = isExecuteTime;
    }

    public long getIntervalos() {
        return intervalos;
    }

    public void setIntervalos(long intervalos) {
        this.intervalos = intervalos;
    }
}
