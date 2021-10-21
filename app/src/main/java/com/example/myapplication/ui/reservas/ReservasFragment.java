package com.example.myapplication.ui.reservas;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.AdapterBaseReservacion;
import com.example.myapplication.adapters.AdapterBaseReservas;
import com.example.myapplication.adapters.AdapterRecycleComentarios;
import com.example.myapplication.adapters.AdapterRecycleReservas;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Item_Reservacion;
import com.example.myapplication.ui.models.Reservacion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasFragment extends Fragment implements Callback<List<Reservacion>> {
    MainActivity activity;
    Button btnAceptar, btnCancelar;
    RecyclerView listReservas;
    private AdapterRecycleReservas adapterRecycleReservas;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;

    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferencias loginPrefs = new Preferencias("Login");
        Call<List<Reservacion>> listCall = ApiUtils.getAPIService().obtenerReservasEstados(loginPrefs.getPrefInteger(activity,"idGarage",0));
        listCall.enqueue(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reservas, container, false);
        btnAceptar = root.findViewById(R.id.btnAceptar);
        btnCancelar = root.findViewById(R.id.btnCancelar);
        listReservas = root.findViewById(R.id.listReservas);
        listReservas.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        return root;
    }

    @Override
    public void onResponse(Call<List<Reservacion>> call, Response<List<Reservacion>> response) {
        if(response.isSuccessful() && response.body() != null){
            ArrayList<Reservacion> reservacionList = new ArrayList<>(response.body());
            adapterRecycleReservas = new AdapterRecycleReservas(reservacionList);
            //AdapterBaseReservas adapterBase = new AdapterBaseReservas(activity, reservacionList);
            listReservas.setAdapter(adapterRecycleReservas);
            //adapterBase.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Call<List<Reservacion>> call, Throwable t) {
        t.printStackTrace();
    }
}
