package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("LogNotTimber")
public class CambiarContrasena extends AppCompatActivity {
    final Context context = CambiarContrasena.this;
    EditText etNuevaContrasena, etConfirmarContrasena;
    Button btnCambiarContrasena;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasena);
        etNuevaContrasena = findViewById(R.id.etNuevaContrasena);
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        Preferencias recuperarPref = new Preferencias("recuperar");
        //Si no esta el email en la Clase Preferencias lo tira para la clase LoginActivity
        if(recuperarPref.getPrefString(context,"email",null) != null){
            // ATTENTION: This was auto-generated to handle app links.
            handleIntent();

            btnCambiarContrasena.setOnClickListener(v -> {
                if(etNuevaContrasena.getText().toString().equals(etConfirmarContrasena.getText().toString())){
                    if(validarPwd(etNuevaContrasena.getText().toString()) && validarPwd(etConfirmarContrasena.getText().toString())){
                        Call<Conductor> conductorCall = ApiUtils.getAPIService().findConductorEmail(recuperarPref.getPrefString(context,"email",null));
                        conductorCall.enqueue(new Callback<Conductor>() {
                            @Override
                            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                                if(response.isSuccessful()&&response.body()!=null){
                                    Toast.makeText(context, "Su contraseña fue cambiada con exito", Toast.LENGTH_LONG).show();
                                    recuperarPref.removePref(context,"email");
                                    hacerElCambio(response);
                                }else{
                                    Toast.makeText(context, "Coincide y no exite email", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Conductor> call, Throwable t) {
                                Toast.makeText(context, "Error" + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(context, "La contraseña tiene que tener 1 Mayuscula, 1 Caracter Especial, Numeros, Minusculas y minimo 8 caracteres", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if(appLinkData != null){
            String passwordId = appLinkData.getLastPathSegment();

        }
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

    //Se encarga de verificar las contraseñas con los parametros de minima seguridad
    public boolean validarPwd(String pwd){
        boolean rtn = true;
        int seguidos = 0;
        char ultimo = 0xFF;

        int minuscula = 0;
        int mayuscula = 0;
        int numero = 0;
        int especial = 0;
        boolean espacio = false;
        if(pwd.length() < 8 || pwd.length() > 16) return false; // tamaño
        for(int i=0;i<pwd.length(); i++){
            char c = pwd.charAt(i);
            if(c <= ' ' || c > '~' ){
                rtn = false; //Espacio o fuera de rango
                break;
            }
            if( (c > ' ' && c < '0') || (c >= ':' && c < 'A') || (c >= '[' && c < 'a') || (c >= '{' && c < 127) ){
                especial++;
            }
            if(c >= '0' && c < ':') numero++;
            if(c >= 'A' && c < '[') mayuscula++;
            if(c >= 'a' && c < '{') minuscula++;

            seguidos = (c==ultimo) ? seguidos + 1 : 0;
            if(seguidos >= 2){
                rtn = false; // 3 seguidos
                break;
            }
            ultimo = c;
        }
        rtn = rtn && especial > 0 && numero > 0 && minuscula > 0 && mayuscula > 0;
        return rtn;
    }

    private void hacerElCambio(Response<Conductor> conductorResponse){
        Conductor conductor = conductorResponse.body();
        if(conductor != null){
            conductor.setContrasena(etNuevaContrasena.getText().toString());
            Call<Conductor> call = ApiUtils.getAPIService().insertConductor(conductor);
            call.enqueue(new Callback<Conductor>() {
                @Override
                public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                    if(response.isSuccessful() && response.body() != null){
                        Toast.makeText(context, "Cambio de contraseña exitoso", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Conductor> call, Throwable t) {
                    Log.d("Sugerencias",t.getMessage());
                }
            });
        }

    }
}
