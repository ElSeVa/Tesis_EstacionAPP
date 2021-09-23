package com.example.myapplication.ui.metodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Imagenes;
import com.example.myapplication.ui.models.Resena;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapMuestraFragment extends Fragment implements Callback<List<Imagenes>> {
    private MainActivity activity;

    private TextView tvNombreGarage;
    private TextView tvDireccionGarage;
    private TextView tvDisponibilidadGarage;
    private TextView tvVehiculosEstadia;
    private Button btnReserva;

    private RatingBar ratingBar;

    private ImageView ivFotoSecund;
    private CircleImageView ivFotoPrinc;

    private Call<Conductor> conductorCall;
    private Call<List<Garage>> garage;
    private Call<List<Resena>> resena;

    private Integer idGarage;

    private final APIService mApiService = ApiUtils.getAPIService();


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setDrawer_locker();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.map_muestra_activity, container, false);
        ratingBar = root.findViewById(R.id.rbMuestras);

        ivFotoPrinc = root.findViewById(R.id.ivFotoPrinc);
        ivFotoSecund = root.findViewById(R.id.ivFotoSecund);

        tvNombreGarage = root.findViewById(R.id.tvNombreGarage);
        tvDireccionGarage = root.findViewById(R.id.tvDireccionGarage);
        tvDisponibilidadGarage = root.findViewById(R.id.tvDisponibilidadGarage);
        tvVehiculosEstadia = root.findViewById(R.id.tvVehiculosEstadia);
        TextView tvError = root.findViewById(R.id.tvError);


        btnReserva = root.findViewById(R.id.btnReservacion);
        Button btnComentarios = root.findViewById(R.id.btnComentarios);

        SharedPreferences prefs = activity.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        idGarage = prefs.getInt("idGarage", 0);
        int idConductor = prefs.getInt("idConductor", 0);
        resena = mApiService.obtenerPorIdGarage(idGarage);
        garage = mApiService.findAllGarage();
        Call<List<Estadia>> estadia = mApiService.groupByPorIdGarage(idGarage, "Si");
        Call<List<Imagenes>> imagenes = mApiService.obtenerImagenesPorIdGarage(idGarage);

        conductorCall = mApiService.findConductor(idConductor);



        estadia.enqueue(new Callback<List<Estadia>>() {
            @Override
            public void onResponse(@NotNull Call<List<Estadia>> call,@NotNull Response<List<Estadia>> response) {
                buscarYCompletar(garage,response);
                estaLaEstadia(conductorCall,response);
            }

            @Override
            public void onFailure(@NotNull Call<List<Estadia>> call,@NotNull Throwable t) {

            }
        });

        imagenes.enqueue(this);

        asignarRating();

        btnReserva.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.menuReservaFragment));

        btnComentarios.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.comentariosFragment));

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }

    private void estaLaEstadia(Call<Conductor> callConductor, Response<List<Estadia>> responseEstadia){
        callConductor.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful()){
                    Conductor conductor = response.body();
                    assert responseEstadia.body() != null;
                    assert conductor != null;
                    for (Estadia estadia : responseEstadia.body()){
                        if(estadia.getVehiculoPermitido().equalsIgnoreCase(conductor.getTipoVehiculo())){
                            btnReserva.setEnabled(true);
                            return;
                        }
                    }
                    btnReserva.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {

            }
        });
    }

    private void buscarYCompletar(Call<List<Garage>> callGarage, Response<List<Estadia>> responseEstadia){
        callGarage.enqueue(new Callback<List<Garage>>() {
            @Override
            public void onResponse(@NotNull Call<List<Garage>> call, @NotNull Response<List<Garage>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (Garage garage : response.body()){
                        if(garage.getId().equals(idGarage) && responseEstadia.body() != null){
                            tvNombreGarage.setText(garage.getNombre());
                            tvDireccionGarage.setText(garage.getDireccion());
                            tvDisponibilidadGarage.setText(garage.getDisponibilidad());
                            for (Estadia estadia : responseEstadia.body()){
                                if(garage.getId().equals(estadia.getIdGarage())){
                                    tvVehiculosEstadia.append(estadia.getVehiculoPermitido() + ", ");
                                }
                            }
                            String text = tvVehiculosEstadia.getText().toString();
                            String substring = text.substring(0, text.length() - 2);
                            tvVehiculosEstadia.setText(substring);
                            if(garage.getDisponibilidad().equalsIgnoreCase("Abierto") || garage.getDisponibilidad().equalsIgnoreCase("Promocion")){
                                btnReserva.setEnabled(true);
                                return;
                            }
                            btnReserva.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Garage>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void asignarRating(){
        resena.enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if(response.body() != null && response.isSuccessful()){
                    int total_resenas = response.body().size();
                    int suma_valoracion = 0;
                    for (Resena resena : response.body()){
                        suma_valoracion += resena.getValoracion();
                    }
                    if(suma_valoracion != 0){
                        ratingBar.setRating((float) suma_valoracion/total_resenas);
                    }else{
                        ratingBar.setRating(0);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                t.printStackTrace();
            }
        });
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

                        ivFotoPrinc.setImageBitmap(fotoPrincipal);
                    }
                    if(img.getTipo().equals("Secundario")){
                        String base64Image = img.getImagen();

                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap fotoSecundario = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        bitmapList.add(fotoSecundario);
                        int nAleatorio = rand.nextInt(bitmapList.size());
                        ivFotoSecund.setImageBitmap(bitmapList.get(nAleatorio));
                    }
                }
            }
        }else {
            mostrarMensaje("error busqueda");
        }
    }

    @Override
    public void onFailure(Call<List<Imagenes>> call, Throwable t) {
        mostrarMensaje("error consulta");

    }

    private void mostrarMensaje(String text){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.setDrawer_unlocker();
    }
}
