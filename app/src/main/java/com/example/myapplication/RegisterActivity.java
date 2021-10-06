package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText etNombre, etEmail, etPassword, etPasswordAgain, etVehiculo;

    Button btnRegister;

    final Context context = RegisterActivity.this;

    private APIService mAPIService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        etNombre = findViewById(R.id.etNombreR);
        etEmail = findViewById(R.id.etEmailR);
        etPassword = findViewById(R.id.etPasswordR);
        etPasswordAgain = findViewById(R.id.etPasswordAgainR);
        etVehiculo = findViewById(R.id.etVehiculoR);
        mAPIService = ApiUtils.getAPIService();
        btnRegister = findViewById(R.id.btnRegister);

        Bundle b = this.getIntent().getExtras();

        if(b!=null){
            etEmail.setText(b.getString("email"));
            etNombre.setText(b.getString("nombre"));
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Conductor conductor = new Conductor();
                conductor.setNombre(etNombre.getText().toString());
                conductor.setEmail(etEmail.getText().toString());
                conductor.setContrasena(etPassword.getText().toString());
                conductor.setTipoVehiculo(etVehiculo.getText().toString());
                conductor.setPropietario(0);
                String passwordAgain = etPasswordAgain.getText().toString();

                if(verificarDatos(conductor) && !passwordAgain.isEmpty()){
                    if(conductor.getContrasena().equals(passwordAgain)){
                        sendPost(conductor);
                    }else {
                        mostrarMensaje("Las contrasenas no coinciden");
                    }
                }else {
                    mostrarMensaje("Complete todos los campos");
                }
            }
        });

    }

    public void sendPost(Conductor conductor) {
        Call<Conductor> callConductor = mAPIService.insertConductor(conductor);
        callConductor.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful()){
                    mostrarMensaje("registro exitoso");
                    cambiarIntent();
                }else {
                    mostrarMensaje("registro error");
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {
                mostrarMensaje("error consulta");
                mostrarMensaje(t.getMessage());
                call.cancel();
            }
        });
    }

    private boolean verificarDatos(Conductor conductor){
        return !conductor.getNombre().isEmpty()
                && !conductor.getEmail().isEmpty()
                && !conductor.getContrasena().isEmpty()
                && !conductor.getTipoVehiculo().isEmpty();
    }

    public void loginIntent(View v){
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
    }

    private void cambiarIntent(){
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
