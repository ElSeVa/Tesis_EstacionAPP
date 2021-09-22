package com.example.myapplication.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Imagenes;
import com.example.myapplication.ui.models.Item_Reservacion;

import org.jetbrains.annotations.NotNull;

import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class GalleryFragment extends Fragment implements Callback<List<Imagenes>> {

    private final APIService mAPIService = ApiUtils.getAPIService();
    private Button btnPerfilCambios;
    private Garage propietario;
    private Call<Garage> callGarage;
    private MainActivity activity;
    private ImageView ivFotoSecundGarage;
    private CircleImageView ivFotoPrincGarage;
    private TextView tvPerfilNombreGarage, tvPerfilDireccionGarage, tvPerfilVehiculosEstadia;
    private Spinner spPerfilDisponibilidad;
    private final String[] itemsHorario = new String[]{"Abierto","Cerrado","Completo"};
    private int idGarage;


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        tvPerfilNombreGarage = root.findViewById(R.id.tvPerfilNombreGarage);
        tvPerfilDireccionGarage = root.findViewById(R.id.tvPerfilDireccionGarage);
        tvPerfilVehiculosEstadia = root.findViewById(R.id.tvPerfilVehiculosEstadia);

        spPerfilDisponibilidad = root.findViewById(R.id.spPerfilDisponibilidad);

        ivFotoPrincGarage = root.findViewById(R.id.ivFotoPrincGarage);
        ivFotoSecundGarage = root.findViewById(R.id.ivFotoSecundGarage);

        btnPerfilCambios = root.findViewById(R.id.btnPerfilCambios);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, itemsHorario);
        spPerfilDisponibilidad.setAdapter(adapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = activity.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);

        if(preferences != null){
            int idConductor = preferences.getInt("idConductor", 0);
            callGarage = mAPIService.findIDGarage(idConductor);
        }

        callGarage.enqueue(new Callback<Garage>() {
            @Override
            public void onResponse(Call<Garage> call, Response<Garage> response) {
                if(response.isSuccessful()){
                    propietario = response.body();
                    if(propietario != null){
                        idGarage = propietario.getId();
                        tvPerfilNombreGarage.setText(propietario.getNombre());
                        tvPerfilDireccionGarage.setText(propietario.getDireccion());
                        seleccionSpinner(propietario.getDisponibilidad());

                        llenarImagenes(idGarage);

                        llenarVehiculos(idGarage);
                    }
                }
            }

            @Override
            public void onFailure(Call<Garage> call, Throwable t) {
                t.printStackTrace();
            }

        });

        btnPerfilCambios.setOnClickListener(v -> {
            String disponibilidad = spPerfilDisponibilidad.getSelectedItem().toString();
            Call<Garage> putGarage = mAPIService.updateDisponibilidad(disponibilidad, idGarage);
            putGarage.enqueue(new Callback<Garage>() {
                @Override
                public void onResponse(Call<Garage> call, Response<Garage> response) {
                    //como el response esta vacio por defecto pasara por onFailure
                }

                @Override
                public void onFailure(Call<Garage> call, Throwable t) {
                    mostrarMensaje("Se aplicaron los cambios");
                }
            });
        });

    }

    private void seleccionSpinner(String disponibilidad){
        switch (disponibilidad){
            case "Abierto":
                spPerfilDisponibilidad.setSelection(0);
                break;
            case "Cerrado":
                spPerfilDisponibilidad.setSelection(1);
                break;
            case "Completo":
                spPerfilDisponibilidad.setSelection(2);
                break;
            default:
                break;
        }
    }

    private void llenarVehiculos(int idGarage) {
        Call<List<Estadia>> callEstadia = mAPIService.groupByPorIdGarage(idGarage, "Si");
        callEstadia.enqueue(new Callback<List<Estadia>>() {
            @Override
            public void onResponse(Call<List<Estadia>> call, Response<List<Estadia>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (Estadia estadia : response.body()){
                        tvPerfilVehiculosEstadia.append(estadia.getVehiculoPermitido() + ", ");
                    }
                    String text = tvPerfilVehiculosEstadia.getText().toString();
                    String substring = text.substring(0, text.length() - 2);
                    tvPerfilVehiculosEstadia.setText(substring);
                }
            }

            @Override
            public void onFailure(Call<List<Estadia>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void llenarImagenes(int idGarage){
        Call<List<Imagenes>> callImagenes = mAPIService.findImagenes(idGarage);
        callImagenes.enqueue(this);
    }

    private void mostrarMensaje(String text){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(Call<List<Imagenes>> call, Response<List<Imagenes>> response) {
        if(response.isSuccessful()){
            List<Bitmap> bitmapList = new ArrayList<>();
            Random rand = new Random();
            if(response.body() != null){
                for (Imagenes img : response.body()){
                    if(img.getTipo().equals("Principal")){
                        String base64Image = img.getImagen();

                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap fotoPrincipal = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        ivFotoPrincGarage.setImageBitmap(fotoPrincipal);
                    }
                    if(img.getTipo().equals("Secundario")){
                        String base64Image = img.getImagen();

                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap fotoSecundario = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        bitmapList.add(fotoSecundario);
                        int nAleatorio = rand.nextInt(bitmapList.size());
                        ivFotoSecundGarage.setImageBitmap(bitmapList.get(nAleatorio));
                    }
                }
            }
        }else {
            mostrarMensaje("error busqueda");
        }
    }

    @Override
    public void onFailure(Call<List<Imagenes>> call, Throwable t) {
        t.printStackTrace();
    }
}