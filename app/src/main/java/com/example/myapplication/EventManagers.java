package com.example.myapplication;

public interface EventManagers {
    void agregar(EventListeners observer);// Agregar observador

    void eliminar(EventListeners observer);// Eliminar observador

    void notificar(boolean event);// Notificar al observador
}
