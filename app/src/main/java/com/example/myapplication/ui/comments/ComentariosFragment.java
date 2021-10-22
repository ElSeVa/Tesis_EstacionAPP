package com.example.myapplication.ui.comments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapters.AdapterRecycleComentarios;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
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

public class ComentariosFragment extends Fragment implements Callback<List<Imagenes>>{

    private MainActivity activity;

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
    private final Preferencias loginPref = new Preferencias("Login");

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
        View root = inflater.inflate(R.layout.fragment_comments, container, false);

        rbValoracion = root.findViewById(R.id.rbValoracion);

        etTextoComentario = root.findViewById(R.id.etTextoComentario);

        Button btnEnviarComentario = root.findViewById(R.id.btnEnviarComentario);

        listComentarios = root.findViewById(R.id.listComentarios);
        listComentarios.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));

        tvNombreGarageComentario = root.findViewById(R.id.tvNombreGarageComentario);

        ivFotoPrincComentario = root.findViewById(R.id.ivFotoPrincComentario);
        //ivFotoSecundComentario = root.findViewById(R.id.ivFotoSecundComentario);

        mAPIService = ApiUtils.getAPIService();


        SharedPreferences sharedPreferences = activity.getSharedPreferences("Login", Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            idGarage = loginPref.getPrefInteger(activity,"idGarage",0);//sharedPreferences.getInt("idGarage",0);
            int idConductor = loginPref.getPrefInteger(activity,"idConductor",0);//sharedPreferences.getInt("idConductor",0);
            callConductor = mAPIService.findConductor(idConductor);
            callResena = mAPIService.obtenerPorIdGarage(idGarage);
            callImagenes = mAPIService.obtenerImagenesPorIdGarage(idGarage);
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
            Resena resena = new Resena();
            resena.setUsuario(nombre);
            resena.setTexto(texto);
            resena.setValoracion(valoracion);
            resena.setIdGarage(idGarage);
            postResena = mAPIService.insertResena(resena);
            postResena.enqueue(new Callback<Resena>() {
                @Override
                public void onResponse(@NotNull Call<Resena> call, @NotNull Response<Resena> response) {
                    if(response.isSuccessful() && response.body() != null){
                        etTextoComentario.setText("");
                        rbValoracion.setRating(0);
                        mostrarMensaje("Exito");
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

        return root;
    }

    private void mostrarMensaje(String text){
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
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
                    /*
                    if(img.getTipo().equals("Secundario")){
                        String base64Image = img.getImagen();

                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap fotoSecundario = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        bitmapList.add(fotoSecundario);
                        int nAleatorio = rand.nextInt(bitmapList.size());
                        ivFotoSecundComentario.setImageBitmap(bitmapList.get(nAleatorio));
                    }
                    */
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
