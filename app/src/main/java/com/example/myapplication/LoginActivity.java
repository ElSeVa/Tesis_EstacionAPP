package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ui.api.APIService;

import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    EditText etEmail, etPasswod;

    Button btnLogin;
    CheckBox cbRecordarLogin;

    final Context context = LoginActivity.this;

    Conductor conductor;
    private APIService mAPIService;
    private SharedPreferences.Editor editor;

    private static final int SIGN_IN_CODE = 8777;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        etEmail = findViewById(R.id.etEmail);
        etPasswod = findViewById(R.id.etPassword);
        mAPIService = ApiUtils.getAPIService();
        btnLogin = findViewById(R.id.btnLogin);
        cbRecordarLogin = findViewById(R.id.cbRecordarLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.signInButton);

        signInButton.setOnClickListener(v -> {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent,SIGN_IN_CODE);
        });

        btnLogin.setOnClickListener(v -> {
            if(etEmail != null && etPasswod != null){
                sendPost(etEmail.getText().toString(),etPasswod.getText().toString());
            }else{
                mostrarMensaje("Ingrese su email y contrasena");
            }
        });

    }

    public void sendPost(String email, String contrasena) {
        Call<Conductor> callConductor = mAPIService.findConductorLogin(email,contrasena);
        callConductor.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    conductor = response.body();

                    mostrarMensaje("login exitoso");
                    if(cbRecordarLogin.isChecked()){
                        SharedPreferences.Editor preferences = getSharedPreferences("MantenerUsuario", MODE_PRIVATE).edit();
                        preferences.putBoolean("Check", cbRecordarLogin.isChecked());
                        preferences.putString("Usuario",email);
                        preferences.putString("Password",contrasena);
                        preferences.apply();
                    }
                    editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                    editor.putInt("idConductor", conductor.getId());
                    editor.putString("Vehiculo",conductor.getTipoVehiculo());
                    editor.apply();
                    cambiarIntent();
                }else {
                    mostrarMensaje("login error");
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {
                mostrarMensaje("error consulta");
                mostrarMensaje(t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            assert result != null;
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            Call<Conductor> conductorCall = mAPIService.findConductorEmail(account.getEmail());
            conductorCall.enqueue(new Callback<Conductor>() {
                @Override
                public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                    if(response.isSuccessful()){
                        conductor = response.body();
                        editor = getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit();
                        editor.putInt("idConductor", conductor.getId());
                        editor.putString("Vehiculo",conductor.getTipoVehiculo());
                        editor.apply();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Conductor> call, Throwable t) {
                    Intent intent = new Intent(context, RegisterActivity.class);
                    intent.putExtra("email",account.getEmail());
                    intent.putExtra("nombre",account.getDisplayName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });

        }else{
            mostrarMensaje("No se pudo iniciar sesion");
        }
    }

    public void registerIntent(View v){
        Intent intent = new Intent(context, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void mostrarMensaje(String mensaje){
        Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
    }
    private void cambiarIntent(){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "Fallo conexion", Toast.LENGTH_SHORT).show();
    }
}
