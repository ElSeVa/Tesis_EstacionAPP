package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.home.HomeFragment;
import com.example.myapplication.ui.mapabox.MapaBox;
import com.example.myapplication.ui.models.Estadia;
import com.mapbox.android.core.location.LocationEngineProvider;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment{


    private Spinner spVehiculos, spHorario, spPrecio;
    private MainActivity activity;
    private final APIService mAPIService = ApiUtils.getAPIService();
    private final List<String> items = new ArrayList<>();
    private final String[] itemsHorario = new String[]{"Hora","Media Estadia","Estadia"};
    private final String[] itemsPrecio = new String[]{"Alto","Bajo"};

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crearItems();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        spVehiculos =(Spinner) root.findViewById(R.id.spVehiculos);
        spHorario =(Spinner) root.findViewById(R.id.spHorario);
        spPrecio =(Spinner) root.findViewById(R.id.spPrecio);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, itemsHorario);
        spHorario.setAdapter(adapter);
        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line,itemsPrecio);
        spPrecio.setAdapter(adapter);

        Button btnActualizar = root.findViewById(R.id.btnActualizar);
        Button btnLimpiar = root.findViewById(R.id.btnLimpiar);

        SharedPreferences preferences = activity.getSharedPreferences("Filtros", Context.MODE_PRIVATE);
        if(preferences != null){
            String bo = preferences.getString("filtro",null);
            if(bo.equals("Si")){
                String vehiculo = preferences.getString("vehiculo",null);
                String horario = preferences.getString("horario",null);
                String precio = preferences.getString("precio",null);
                seleccionSpinnerHorario(horario);
                seleccionSpinnerPrecio(precio);
                seleccionSpinnerVehiculo(items,vehiculo);
            }
        }

        btnActualizar.setOnClickListener(v -> {
            String vehiculo = spVehiculos.getSelectedItem().toString();
            String horario = spHorario.getSelectedItem().toString();
            String precio = spPrecio.getSelectedItem().toString();
            enviarFiltros(vehiculo,horario,precio,"Si");
            Navigation.findNavController(v).navigate(R.id.nav_home);
        });

        btnLimpiar.setOnClickListener(v -> {
            enviarFiltros("","","","No");
            Navigation.findNavController(v).navigate(R.id.nav_home);
        });
        activity.setDrawer_unlocker();
        return root;
    }

    private void seleccionSpinnerHorario(String valor){
        switch (valor){
            case "Hora":
                spHorario.setSelection(0);
                break;
            case "Media Estadia":
                spHorario.setSelection(1);
                break;
            case "Estadia":
                spHorario.setSelection(2);
                break;
            default:
                break;
        }
    }

    private void seleccionSpinnerPrecio(String valor){
        if ("Bajo".equals(valor)) {
            spPrecio.setSelection(1);
        } else {
            spPrecio.setSelection(0);
        }
    }

    private void seleccionSpinnerVehiculo(List<String> list,String valor){
        if(list != null && list.size() != 0){
            int total_vehiculos = list.size();

            for(int i = 0; i <= total_vehiculos; i++){
                String vehiculo = list.get(i);
                if (vehiculo.equals(valor)) {
                    spVehiculos.setSelection(i);
                }
            }
        }
    }

    private void enviarFiltros(String vehiculo, String horario, String precio, String filtro){
        SharedPreferences.Editor editor = activity.getSharedPreferences("Filtros", Context.MODE_PRIVATE).edit();
        editor.putString("vehiculo",vehiculo);
        editor.putString("horario",horario);
        editor.putString("precio",precio);
        editor.putString("filtro",filtro);
        editor.apply();

        //Toast.makeText(activity, "Tipo Vehiculo: " + vehiculo + ", Horario: "+ horario+", Precio: "+precio, Toast.LENGTH_SHORT).show();
/*
        Fragment nuevoFragmento = new HomeFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        //FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, nuevoFragmento);
        transaction.addToBackStack(null);
        // Commit a la transacción

        transaction.commit();
 */
    }

    private void crearItems(){
        Call<List<Estadia>> estadia = mAPIService.groupBy("Si");
        estadia.enqueue(new Callback<List<Estadia>>() {
            @Override
            public void onResponse(Call<List<Estadia>> call, Response<List<Estadia>> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    for (Estadia estadia : response.body()){
                        items.add(estadia.getVehiculoPermitido());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line,items);

                    spVehiculos.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Estadia>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

}