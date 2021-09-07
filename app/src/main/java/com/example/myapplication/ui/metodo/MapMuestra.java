package com.example.myapplication.ui.metodo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.comments.ComentariosActivity;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Garage;
import com.example.myapplication.ui.models.Imagenes;
import com.example.myapplication.ui.models.Resena;
import com.example.myapplication.ui.slideshow.SlideshowFragment;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapMuestra extends AppCompatActivity implements Callback<List<Imagenes>> {

    Integer idGarage, idConductor;

    private Call<List<Imagenes>> imagenes;
    private Call<List<Garage>> garage;
    private Call<List<Estadia>> estadia;
    private Call<List<Resena>> resena;

    private ImageView ivFotoSecund;
    private CircleImageView ivFotoPrinc;

    private RatingBar ratingBar;

    private TextView tvNombreGarage, tvDireccionGarage, tvDisponibilidadGarage, tvVehiculosEstadia, tvError;

    private APIService mApiService;

    private Button btnReserva, btnComentarios;

    private SharedPreferences prefs;

    final Context context = MapMuestra.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_muestra_activity);

        ratingBar = findViewById(R.id.rbMuestras);

        ivFotoPrinc = findViewById(R.id.ivFotoPrinc);
        ivFotoSecund = findViewById(R.id.ivFotoSecund);

        tvNombreGarage = findViewById(R.id.tvNombreGarage);
        tvDireccionGarage = findViewById(R.id.tvDireccionGarage);
        tvDisponibilidadGarage = findViewById(R.id.tvDisponibilidadGarage);
        tvVehiculosEstadia = findViewById(R.id.tvVehiculosEstadia);
        tvError = findViewById(R.id.tvError);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnReserva = findViewById(R.id.btnReservacion);
        btnComentarios = findViewById(R.id.btnComentarios);
        prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        idConductor = prefs.getInt("idConductor", 0);
        idGarage = prefs.getInt("idGarage", 0);
        mApiService = ApiUtils.getAPIService();

        resena = mApiService.findResenaID_Garage(idGarage);
        garage = mApiService.findAllGarage();
        estadia = mApiService.findAllFilterEstadiaID(idGarage,"Si");
        imagenes = mApiService.findImagenes(idGarage);

        mostrarMensaje(idGarage.toString());

        estadia.enqueue(new Callback<List<Estadia>>() {
            @Override
            public void onResponse(@NotNull Call<List<Estadia>> call,@NotNull Response<List<Estadia>> response) {
                buscarYCompletar(garage,response);
            }

            @Override
            public void onFailure(@NotNull Call<List<Estadia>> call,@NotNull Throwable t) {

            }
        });

        imagenes.enqueue(this);

        asignarRating();

        btnReserva.setOnClickListener(v -> {
            Intent intent = new Intent(context, SlideshowFragment.class);
            startActivity(intent);
        });

        btnComentarios.setOnClickListener(v -> {
            Intent intent = new Intent(context, ComentariosActivity.class);
            startActivity(intent);
        });

    }

    private void mostrarMensaje(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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

    private void buscarYCompletar(Call<List<Garage>> callGarage, Response<List<Estadia>> responseEstadia){
        callGarage.enqueue(new Callback<List<Garage>>() {
            @Override
            public void onResponse(@NotNull Call<List<Garage>> call, @NotNull Response<List<Garage>> response) {
                if(response.isSuccessful() && response.body() != null){
                    for (Garage garage : response.body()){
                        if(garage.getID().equals(idGarage) && responseEstadia.body() != null){
                            tvNombreGarage.setText(garage.getNombre());
                            tvDireccionGarage.setText(garage.getDireccion());
                            tvDisponibilidadGarage.setText(garage.getDisponibilidad());
                            for (Estadia estadia : responseEstadia.body()){
                                if(garage.getID().equals(estadia.getID_Garage())){
                                    tvVehiculosEstadia.append(estadia.getVehiculoPermitido() + ", ");
                                }
                            }
                            String text = tvVehiculosEstadia.getText().toString();
                            String substring = text.substring(0, text.length() - 2);
                            tvVehiculosEstadia.setText(substring);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
