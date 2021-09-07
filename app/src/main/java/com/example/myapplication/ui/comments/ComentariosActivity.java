package com.example.myapplication.ui.comments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AdapterBaseComentarios;
import com.example.myapplication.AdapterRecycleComentarios;
import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.metodo.MapMuestra;
import com.example.myapplication.ui.models.Conductor;
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

public class ComentariosActivity extends AppCompatActivity implements Callback<List<Imagenes>> {

    private APIService mAPIService;

    private RecyclerView listComentarios;

    private RatingBar rbValoracion;

    private EditText etTextoComentario;

    private Conductor conductor;

    private Call<Garage> callGarage;
    private Call<List<Imagenes>> callImagenes;
    private Call<List<Resena>> callResena;
    private Call<Conductor> callConductor;
    private Call<Resena> postResena;

    private ImageView ivFotoSecundComentario;
    private CircleImageView ivFotoPrincComentario;

    private TextView tvNombreGarageComentario;

    private ArrayList<Resena> resenaArrayList;

    private int idGarage;

    private AdapterRecycleComentarios adapterRecycleComentarios;

    final Context context = ComentariosActivity.this;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        rbValoracion = findViewById(R.id.rbValoracion);

        etTextoComentario = findViewById(R.id.etTextoComentario);

        Button btnEnviarComentario = findViewById(R.id.btnEnviarComentario);

        listComentarios = findViewById(R.id.listComentarios);
        listComentarios.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        tvNombreGarageComentario = findViewById(R.id.tvNombreGarageComentario);

        ivFotoPrincComentario = findViewById(R.id.ivFotoPrincComentario);
        ivFotoSecundComentario = findViewById(R.id.ivFotoSecundComentario);

        mAPIService = ApiUtils.getAPIService();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            idGarage = sharedPreferences.getInt("idGarage",0);
            int idConductor = sharedPreferences.getInt("idConductor",0);
            callConductor = mAPIService.findConductor(idConductor);
            callResena = mAPIService.findResenaID_Garage(idGarage);
            callImagenes = mAPIService.findImagenes(idGarage);
            callGarage = mAPIService.findGarage(idGarage);
            establecerConductor();
        }

        callResena.enqueue(new Callback<List<Resena>>() {
            @Override
            public void onResponse(Call<List<Resena>> call, Response<List<Resena>> response) {
                if(response.body() != null && response.isSuccessful()){
                    resenaArrayList = new ArrayList<>(response.body());
                    adapterRecycleComentarios = new AdapterRecycleComentarios(resenaArrayList);
                    //AdapterBaseComentarios adapter = new AdapterBaseComentarios(context,resenaArrayList);
                    listComentarios.setAdapter(adapterRecycleComentarios);
                }
            }

            @Override
            public void onFailure(Call<List<Resena>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        callGarage.enqueue(new Callback<Garage>() {
            @Override
            public void onResponse(Call<Garage> call, Response<Garage> response) {
                if(response.body() != null && response.isSuccessful()){
                    Garage garage = response.body();
                    tvNombreGarageComentario.setText(garage.getNombre());
                }
            }

            @Override
            public void onFailure(Call<Garage> call, Throwable t) {
                t.printStackTrace();
            }
        });

        callImagenes.enqueue(this);

        btnEnviarComentario.setOnClickListener(v -> {
            String nombre = conductor.getNombre();
            String texto = etTextoComentario.getText().toString();
            int valoracion = (int) Math.abs(rbValoracion.getRating());
            postResena = mAPIService.insertResena(nombre,texto,valoracion,idGarage);
            postResena.enqueue(new Callback<Resena>() {
                @Override
                public void onResponse(@NotNull Call<Resena> call, @NotNull Response<Resena> response) {
                    if(response.isSuccessful() && response.body() != null){
                        etTextoComentario.setText("");
                        rbValoracion.setRating(0);
                        mostrarMensaje("Exito");
                        Resena resena = response.body();
                        resenaArrayList.add(resena);
                        adapterRecycleComentarios.notifyItemInserted(resenaArrayList.size());
                    }else{
                        mostrarMensaje("Error");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Resena> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
        });

    }

    private void mostrarMensaje(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    private void establecerConductor(){
        callConductor.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful() && response.body() != null){
                    conductor = response.body();
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {
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

                        ivFotoPrincComentario.setImageBitmap(fotoPrincipal);
                    }
                    if(img.getTipo().equals("Secundario")){
                        String base64Image = img.getImagen();

                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap fotoSecundario = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        bitmapList.add(fotoSecundario);
                        int nAleatorio = rand.nextInt(bitmapList.size());
                        ivFotoSecundComentario.setImageBitmap(bitmapList.get(nAleatorio));
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
