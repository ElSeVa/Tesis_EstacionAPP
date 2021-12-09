package com.example.myapplication.ui.promotions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapters.AdapterBasePromo;
import com.example.myapplication.adapters.AdapterBasePromociones;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Promo;
import com.example.myapplication.ui.models.Promociones;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TakePromotionFragment extends Fragment {

    private MainActivity activity;
    private ListView listView;
    private Call<List<Promociones>> callPromociones;
    private TextView tvNoHayPromociones;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        activity.setDrawer_locker();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_take_promotion, container, false);
        tvNoHayPromociones = root.findViewById(R.id.tvNoHayPromociones);
        listView = root.findViewById(R.id.listPromociones);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callPromociones = ApiUtils.getAPIService().obtenerPromocionesIdConductor(new Preferencias("Login").getPrefInteger(activity,"idConductor",0));
        callPromociones.enqueue(new Callback<List<Promociones>>() {
            @Override
            public void onResponse(Call<List<Promociones>> call, Response<List<Promociones>> response) {
                if(response.isSuccessful() && response.body() != null){
                    if(response.body().size() != 0){
                        tvNoHayPromociones.setVisibility(View.GONE);
                        AdapterBasePromociones adapterBase = new AdapterBasePromociones(activity, new ArrayList<>(response.body()));
                        listView.setAdapter(adapterBase);
                    }else{
                        listView.setVisibility(View.GONE);
                        tvNoHayPromociones.setText("No tienes ninguna promocion");
                    }

                }
            }

            @Override
            public void onFailure(Call<List<Promociones>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
