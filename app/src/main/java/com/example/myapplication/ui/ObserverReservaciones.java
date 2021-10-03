package com.example.myapplication.ui;

import android.os.CountDownTimer;

import com.example.myapplication.EventListeners;
import com.example.myapplication.EventManagers;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Reservacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObserverReservaciones implements EventManagers {
    private final List<EventListeners> personList = new ArrayList<>();

    @Override
    public void agregar(EventListeners observer) {
        personList.add(observer);
    }

    @Override
    public void eliminar(EventListeners observer) {
        personList.remove(observer);
    }

    @Override
    public void notificar(boolean event) {
        for (EventListeners listeners : personList){
            listeners.update(event);
        }
    }
}
