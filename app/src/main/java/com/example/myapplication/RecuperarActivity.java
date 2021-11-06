package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.api.ApiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarActivity extends AppCompatActivity {

    Button btnRecuperar;
    EditText etEmailRecuperar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);
        etEmailRecuperar = findViewById(R.id.etEmailRecuperar);
        btnRecuperar = findViewById(R.id.btnRecuperar);
        btnRecuperar.setOnClickListener(v -> {
            if(etEmailRecuperar.length() != 0){
                String body = "Ingresa a este link para recuperar su contraseña\nhttp://localhost/tesis/index.php?seccion=recuperar&codigo=";
                Call<Boolean> call = ApiUtils.getAPIService().enviarRecuperar(etEmailRecuperar.getText().toString(),"Recuperar contraseña",body);
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            Toast.makeText(RecuperarActivity.this, "Revise su casilla de email", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                if(call.isExecuted()){
                    startActivity(new Intent(this,LoginActivity.class));
                }
            }
        });
    }
}
