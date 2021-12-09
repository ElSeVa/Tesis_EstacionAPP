package com.example.myapplication.ui.sugerencias;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Sugerencias;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("LogNotTimber")
public class SugerenciasFragment extends Fragment {
    Context activity;
    Button btnSugerenciaEnviar;
    EditText etSugerenciaComentario;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sugerencias,container,false);
        etSugerenciaComentario = view.findViewById(R.id.etSugerenciaComentario);
        btnSugerenciaEnviar = view.findViewById(R.id.btnSugerenciaEnviar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSugerenciaEnviar.setOnClickListener(v -> {
            if(etSugerenciaComentario.getText() != null) {
                APIService mApiService = ApiUtils.getAPIService();
                Call<String> call = mApiService.enviarEmail(new Sugerencias("potoxsdes@gmail.com","Sugerencias", etSugerenciaComentario.getText().toString()));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                        if(response.isSuccessful()){
                            String s = response.body();
                            assert s != null;
                            etSugerenciaComentario.setText("");
                            Toast.makeText(activity,"Gracias por enviar una sugerencia.",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                        Log.d("Sugerencias",t.getMessage());
                    }
                });
            }
        });
    }
}
