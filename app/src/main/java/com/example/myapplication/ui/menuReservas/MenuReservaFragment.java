package com.example.myapplication.ui.menuReservas;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.enums.Estados;
import com.example.myapplication.enums.Horario;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Estadia;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuReservaFragment extends Fragment implements View.OnClickListener {
    private MainActivity activity;
    private Button btnHora, btnMedia, btnEstadia;
    private Preferencias loginPref;
    private List<Estadia> listEstadia = new ArrayList<>();

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu_reservas, container, false);
        btnHora = root.findViewById(R.id.btnHora);
        btnMedia = root.findViewById(R.id.btnMedia);
        btnEstadia = root.findViewById(R.id.btnEstadia);
        loginPref = new Preferencias("Login");
        int idGarage = loginPref.getPrefInteger(activity,"idGarage",0);
        if(idGarage != 0){
            Call<List<Estadia>> estadiaCall = ApiUtils.getAPIService().obtenerEstadiaPorIdGarage(idGarage);
            estadiaCall.enqueue(new Callback<List<Estadia>>() {
                @Override
                public void onResponse(Call<List<Estadia>> call, Response<List<Estadia>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        List<Estadia> list = response.body();
                        listEstadia.addAll(list);
                    }
                }

                @Override
                public void onFailure(Call<List<Estadia>> call, Throwable t) {
                    Toast.makeText(activity, "Error estadia", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(activity, "Error idGarage", Toast.LENGTH_SHORT).show();
        }
        /*
        if(!listEstadia.isEmpty()){
            Toast.makeText(activity, "La lista no esta vacia", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(activity, "La lista esta vacia", Toast.LENGTH_SHORT).show();
        }
        */
        activity.setDrawer_locker();
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnHora.setOnClickListener(this);
        btnMedia.setOnClickListener(this);
        btnEstadia.setOnClickListener(this);

        new Handler().postDelayed(() -> {
            /*
            if(listEstadia.isEmpty()){
                Toast.makeText(activity, "La lista esta vacia 1", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "La lista no esta vacia 1", Toast.LENGTH_SHORT).show();
            }
            */

            for (Estadia estadia : listEstadia){
                switch (estadia.getHorario()){
                    case "Hora":
                        btnHora.setEnabled(true);
                        break;
                    case "Media Estadia":
                        btnMedia.setEnabled(true);
                        break;
                    case "Estadia":
                        btnEstadia.setEnabled(true);
                        break;
                    default:
                        break;
                }
            }

        },200);


    }

    @Override
    public void onClick(View v) {
        if(v==btnHora){
            Navigation.findNavController(v).navigate(R.id.action_menuReservaFragment_to_horaFragment);
        }

        if(v==btnMedia){
            Navigation.findNavController(v).navigate(R.id.action_menuReservaFragment_to_mediaFragment);
        }

        if(v==btnEstadia){
            Navigation.findNavController(v).navigate(R.id.action_menuReservaFragment_to_estadiaFragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.setDrawer_unlocker();
    }

}
