package com.example.myapplication.ui.metodo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.myapplication.enums.Disponibilidad;
import com.example.myapplication.enums.Horario;
import com.example.myapplication.preferencias.Preferencias;
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

public class DetallesGarageFragment extends Fragment implements Callback<List<Imagenes>> {
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
    private Call<Garage> garage;
    private Call<List<Resena>> resena;

    private Integer idGarage;

    private final Preferencias loginPref = new Preferencias("Login");

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
        View root = inflater.inflate(R.layout.fragment_detalles_garage, container, false);
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

        //SharedPreferences prefs = activity.getSharedPreferences("Login", Context.MODE_PRIVATE);
        idGarage = loginPref.getPrefInteger(activity,"idGarage",0);// prefs.getInt("idGarage", 0);
        int idConductor = loginPref.getPrefInteger(activity,"idConductor",0);// prefs.getInt("idConductor", 0);
        resena = mApiService.obtenerPorIdGarage(idGarage);
        garage = mApiService.findGarage(idGarage);
        Call<List<Estadia>> estadia = mApiService.groupByPorIdGarage(idGarage, "Si");
        Call<List<Imagenes>> imagenes = mApiService.obtenerImagenesPorIdGarage(idGarage);

        conductorCall = mApiService.findConductor(idConductor);

        estadia.enqueue(new Callback<List<Estadia>>() {
            @Override
            public void onResponse(@NotNull Call<List<Estadia>> call,@NotNull Response<List<Estadia>> response) {
                new Handler().postDelayed(() -> {
                    buscarYCompletar(garage,response);
                },10);
                estaLaEstadia(conductorCall,response);
            }

            @Override
            public void onFailure(@NotNull Call<List<Estadia>> call,@NotNull Throwable t) {

            }
        });

        imagenes.enqueue(this);

        asignarRating();

        btnReserva.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapMuestraFragment_to_menuReservaFragment));

        btnComentarios.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_mapMuestraFragment_to_comentariosFragment));

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }

    private void buscarYCompletar(Call<Garage> callGarage, Response<List<Estadia>> responseEstadia){
        callGarage.enqueue(new Callback<Garage>() {
            @Override
            public void onResponse(@NotNull Call<Garage> call, @NotNull Response<Garage> response) {
                if(response.isSuccessful() && response.body() != null){
                    Garage garage = response.body();
                    //Toast.makeText(activity, garage.getId()+" Disponibilidad "+garage.getDisponibilidad(), Toast.LENGTH_SHORT).show();
                    if(responseEstadia.body() != null){
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
                        //Toast.makeText(activity, "Disponibilidad: Abierto", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(activity, "Disponibilidad: Cerrado o Completado", Toast.LENGTH_SHORT).show();
                        switch (garage.getDisponibilidad()){
                            case "Cerrado":
                            case "Completo":
                                btnReserva.setEnabled(false);
                                break;
                            default:
                                btnReserva.setEnabled(true);
                                break;
                        }

                    }

                }
            }

            @Override
            public void onFailure(Call<Garage> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void estaLaEstadia(Call<Conductor> callConductor, Response<List<Estadia>> responseEstadia){
        int siHayReserva = new Preferencias("notificacion").getPrefInteger(activity,"idReservas",0);
        //Toast.makeText(activity, "notificacion: " + siHayReserva, Toast.LENGTH_SHORT).show();
        callConductor.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful()){
                    Conductor conductor = response.body();

                    assert responseEstadia.body() != null;
                    assert conductor != null;
                    for (Estadia estadia : responseEstadia.body()){
                        if(estadia.getVehiculoPermitido().equalsIgnoreCase(conductor.getTipoVehiculo()) && siHayReserva == 0){
                            btnReserva.setEnabled(true);
                            //Toast.makeText(activity, "Tiene el mismo vehiculo y no hay reserva", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if(siHayReserva != 0){
                        btnReserva.setEnabled(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {

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
