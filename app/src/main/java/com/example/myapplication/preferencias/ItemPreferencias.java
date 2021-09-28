package com.example.myapplication.preferencias;

import android.content.Context;

import java.util.Map;

public interface ItemPreferencias {
    void setPrefTiempos(Context context, Map<String, String> value);
    void setPrefLogin(Context context, Map<String, String> value);
    void setPrefCuenta(Context context, Map<String, String> value);
    void setPrefMantener(Context context, Map<String, String> value);
    void setPrefFiltros(Context context, Map<String, String> value);
}
