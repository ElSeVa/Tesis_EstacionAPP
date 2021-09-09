package com.example.myapplication.ui;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.example.myapplication.EventListeners;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Reservacion;
import com.google.android.gms.common.data.DataBufferObserver;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;

public class Temporizador extends CountDownTimer implements EventListeners {

    public boolean isExecuteTime = false;
    public long tiempo;
    public long tiempoFinal;
    public static long tiempoTotal = 1800000;
    public TextView textView;


    public Temporizador(long tiempo, long intervalos) {
        super(tiempo, intervalos);
    }

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

    @Override
    public void onTick(long millisUntilFinished) {
        tiempo = millisUntilFinished;
        updateCountDownText();
    }

    @Override
    public void onFinish() {
        isExecuteTime = false;
    }

    public void updateCountDownText() {
        int minutes = (int) (tiempo / 1000) / 60;
        int seconds = (int) (tiempo / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textView.setText(timeLeftFormatted);
    }

    public boolean getExecuteTime(){
        return this.isExecuteTime;
    }

    public void setExecuteTime(boolean isExecuteTime){
        this.isExecuteTime = isExecuteTime;
    }

    public void resetTimer(){
        tiempo = tiempoTotal;
        tiempoFinal = 0;
        isExecuteTime = false;
        updateCountDownText();
    }

    @Override
    public void update(boolean event) {
        if(!event){
            resetTimer();
        }
        setExecuteTime(event);
    }
}
