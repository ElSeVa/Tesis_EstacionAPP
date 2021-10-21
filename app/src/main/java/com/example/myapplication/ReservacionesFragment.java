package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.myapplication.adapters.AdapterBaseReservacion;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Item_Reservacion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservacionesFragment extends Fragment implements Callback<List<Item_Reservacion>> {

    private MainActivity activity;

    private ListView listReservaciones;

    private APIService mAPIService = ApiUtils.getAPIService();

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Preferencias loginPrefs = new Preferencias("Login");
        int idConductor = loginPrefs.getPrefInteger(activity,"idConductor",0);

        Call<List<Item_Reservacion>> listCall = mAPIService.obtenerReservasConductor(idConductor);
        listCall.enqueue(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservaciones, container, false);
        listReservaciones = (ListView) view.findViewById(R.id.listReservaciones);
        activity.setDrawer_unlocker();
        return view;
    }

    @Override
    public void onResponse(Call<List<Item_Reservacion>> call, Response<List<Item_Reservacion>> response) {
        if(response.isSuccessful() && response.body() != null){
            ArrayList<Item_Reservacion> reservacionList = new ArrayList<>(response.body());
            AdapterBaseReservacion adapterBase = new AdapterBaseReservacion(activity, reservacionList);
            listReservaciones.setAdapter(adapterBase);
        }
    }

    @Override
    public void onFailure(Call<List<Item_Reservacion>> call, Throwable t) {
        t.printStackTrace();
    }
}