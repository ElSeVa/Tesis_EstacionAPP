package com.example.myapplication.ui.promotions;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.adapters.AdapterBasePromo;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Item_Promocion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GivePromotionFragment extends Fragment {

    private MainActivity activity;
    private final APIService mAPIService = ApiUtils.getAPIService();
    private ExpandableListView listView;
    private Call<Garage> garageCall;

    private HashMap<Item_Promocion, List<Item_Promocion>> item_promocionHashMap;
    private final Preferencias loginPref = new Preferencias("Login");
    private Button btnListGive;
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
                    completarItem(g.getId());
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
        //SharedPreferences preferences = activity.getSharedPreferences("Login", Context.MODE_PRIVATE);
        //if(preferences != null){
        btnListGive = root.findViewById(R.id.btn_list_give);
        int idConductor = loginPref.getPrefInteger(activity,"idConductor",0);//preferences.getInt("idConductor", 0);
            //conductorCall = mAPIService.findConductor(idConductor);
        garageCall = mAPIService.findIDGarage(idConductor);
        //}

        listView = root.findViewById(R.id.listPromo);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void completarItem(int idGarage) {
        Call<List<Item_Promocion>> promocionCall = mAPIService.obtenerFrecuencia(idGarage);
        promocionCall.enqueue(new Callback<List<Item_Promocion>>() {
            @Override
            public void onResponse(Call<List<Item_Promocion>> call, Response<List<Item_Promocion>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    item_promocionHashMap = new HashMap<>();
                    List<Item_Promocion> promocionsList = new ArrayList<>(response.body());
                    for (Item_Promocion listItem1 : promocionsList){
                        for(Item_Promocion listItem2 : promocionsList){
                            if(listItem1.getId().equals(listItem2.getId())){
                                List<Item_Promocion> listChild = new ArrayList<>();
                                listChild.add(listItem2);
                                item_promocionHashMap.put(listItem1,listChild);
                            }
                        }
                    }
                    AdapterBasePromo adapterBase = new AdapterBasePromo(activity, promocionsList,item_promocionHashMap);
                    listView.setAdapter(adapterBase);
                    adapterBase.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Item_Promocion>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
