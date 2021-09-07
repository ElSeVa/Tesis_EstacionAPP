package com.example.myapplication.ui.promotions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.AdapterBasePromo;
import com.example.myapplication.AdapterBaseReservas;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Item_Promocion;
import com.example.myapplication.ui.models.Item_Reservacion;
import com.example.myapplication.ui.models.Reservacion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GivePromotionFragment extends Fragment {

    private MainActivity activity;
    private APIService mAPIService = ApiUtils.getAPIService();
    private ListView listView;
    private SharedPreferences preferences;
    private Call<Garage> garageCall;
    private Call<List<Item_Promocion>> promocionCall;
    private Call<Conductor> conductorCall;
    private int idConductor;


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        garageCall.enqueue(new Callback<Garage>() {
            @Override
            public void onResponse(Call<Garage> call, Response<Garage> response) {
                if(response.isSuccessful()){
                    Garage g = response.body();
                    assert g!=null;
                    completarItem(g.getID());
                }
            }

            @Override
            public void onFailure(Call<Garage> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_give_promotion, container, false);
        preferences = activity.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        if(preferences != null){
            idConductor = preferences.getInt("idConductor",0);
            //conductorCall = mAPIService.findConductor(idConductor);
            garageCall = mAPIService.findIDGarage(idConductor);
        }

        listView = root.findViewById(R.id.listPromo);
        return root;
    }

    private void completarItem(int idGarage) {
        promocionCall = mAPIService.findReservacionGarage(idGarage);
        promocionCall.enqueue(new Callback<List<Item_Promocion>>() {
            @Override
            public void onResponse(Call<List<Item_Promocion>> call, Response<List<Item_Promocion>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    ArrayList<Item_Promocion> promocionsList = new ArrayList<>(response.body());
                    AdapterBasePromo adapterBase = new AdapterBasePromo(activity, promocionsList);
                    listView.setAdapter(adapterBase);
                }
            }

            @Override
            public void onFailure(Call<List<Item_Promocion>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
