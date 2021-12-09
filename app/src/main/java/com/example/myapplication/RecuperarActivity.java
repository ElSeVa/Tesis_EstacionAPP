package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Sugerencias;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarActivity extends AppCompatActivity {

    Button btnRecuperar;
    EditText etEmailRecuperar;
    final Context context = RecuperarActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);
        etEmailRecuperar = findViewById(R.id.etEmailRecuperar);
        btnRecuperar = findViewById(R.id.btnRecuperar);
        btnRecuperar.setOnClickListener(v -> {
            if(etEmailRecuperar.length() != 0){
                String body = "Ingresa a este link para recuperar su contraseña\nhttps://naniof.000webhostapp.com/cambiarContrasena";
                Preferencias recuperarPref = new Preferencias("recuperar");
                Call<String> call = ApiUtils.getAPIService().enviarRecuperar(new Sugerencias(etEmailRecuperar.getText().toString(),"Recuperar contraseña",body));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful() && response.body()!=null){
                            recuperarPref.setPrefString(context,"email",etEmailRecuperar.getText().toString());
                            Toast.makeText(RecuperarActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(RecuperarActivity.this, "Revise su casilla de email", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(RecuperarActivity.this, "Revise su casilla de email onFailure", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return false;
    }
}
