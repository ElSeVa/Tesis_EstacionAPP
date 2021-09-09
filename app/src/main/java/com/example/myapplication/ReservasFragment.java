package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Item_Reservacion;
import com.example.myapplication.ui.models.Reservacion;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ReservasFragment extends Fragment implements Callback<List<Item_Reservacion>> {

    private MainActivity activity;

    private ListView listView;

    private APIService mAPIService = ApiUtils.getAPIService();

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getActivity().getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        int idConductor = prefs.getInt("idConductor", 0);

        Call<List<Item_Reservacion>> listCall = mAPIService.findReservacionConductor(idConductor);
        listCall.enqueue(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);
        listView = (ListView) view.findViewById(R.id.listReserva);
        activity.setDrawer_unlocker();
        return view;
    }

    @Override
    public void onResponse(Call<List<Item_Reservacion>> call, Response<List<Item_Reservacion>> response) {
        if(response.isSuccessful() && response.body() != null){
            ArrayList<Item_Reservacion> reservacionList = new ArrayList<>(response.body());
            AdapterBaseReservas adapterBase = new AdapterBaseReservas(activity, reservacionList);
            listView.setAdapter(adapterBase);
        }
    }

    @Override
    public void onFailure(Call<List<Item_Reservacion>> call, Throwable t) {
        t.printStackTrace();
    }
}