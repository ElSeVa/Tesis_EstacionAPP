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
import android.widget.Toast;

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
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Item_Reservacion;
import com.example.myapplication.ui.models.Reservacion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasFragment extends Fragment implements Callback<List<Reservacion>> {
    MainActivity activity;
    Button btnAceptar, btnCancelar;
    TextView tvNoHayLista;
    RecyclerView listReservas;
    Call<Garage> garageCall;
    Garage garage;
    private AdapterRecycleReservas adapterRecycleReservas;
    Timer timer = new Timer();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferencias loginPrefs = new Preferencias("Login");
        int id = loginPrefs.getPrefInteger(activity,"idConductor",0);
        garageCall = ApiUtils.getAPIService().findIDGarage(id);
        garageCall.enqueue(new Callback<Garage>() {
            @Override
            public void onResponse(Call<Garage> call, Response<Garage> response) {
                if(response.body() != null && response.isSuccessful()){
                    garage = response.body();
                    Toast.makeText(activity,"id: " + id , Toast.LENGTH_SHORT).show();
                    Call<List<Reservacion>> listCall = ApiUtils.getAPIService().obtenerReservasEstados(garage.getId());
                    listCall.enqueue(ReservasFragment.this);
                }
            }

            @Override
            public void onFailure(Call<Garage> call, Throwable t) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reservas, container, false);
        tvNoHayLista = root.findViewById(R.id.tvNoHayReservas);
        btnAceptar = root.findViewById(R.id.btnAceptar);
        btnCancelar = root.findViewById(R.id.btnCancelar);
        listReservas = root.findViewById(R.id.listReservas);
        listReservas.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(garage != null){
            TimerTask timerTask = new TimerTask() {
                int tic = 0;
                @Override
                public void run() {
                    if(tic%2==0){
                        //Preferencias loginPrefs = new Preferencias("Login");
                        Call<List<Reservacion>> listCall = ApiUtils.getAPIService().obtenerReservasEstados(garage.getId());
                        listCall.enqueue(ReservasFragment.this);
                    }
                    tic++;
                }
            };
            timer.schedule(timerTask,2,2000);
        }
    }

    @Override
    public void onResponse(Call<List<Reservacion>> call, Response<List<Reservacion>> response) {
        if(response.isSuccessful() && response.body() != null){
            ArrayList<Reservacion> reservacionList = new ArrayList<>(response.body());
            if(reservacionList.size() != 0){
                tvNoHayLista.setVisibility(View.GONE);
                adapterRecycleReservas = new AdapterRecycleReservas(reservacionList);
                //AdapterBaseReservas adapterBase = new AdapterBaseReservas(activity, reservacionList);
                listReservas.setAdapter(adapterRecycleReservas);
                adapterRecycleReservas.notifyDataSetChanged();
                //adapterBase.notifyDataSetChanged();
            }else{
                listReservas.setVisibility(View.GONE);
                tvNoHayLista.setText("No hay ninguna reservacion");
            }
        }
    }

    @Override
    public void onFailure(Call<List<Reservacion>> call, Throwable t) {
        t.printStackTrace();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
