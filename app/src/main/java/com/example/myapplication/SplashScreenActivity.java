package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private final APIService mAPIService = ApiUtils.getAPIService();
    private final Preferencias mantenerPref = new Preferencias("MantenerUsuario");
    final Context context = SplashScreenActivity.this;

    private GoogleApiClient googleApiClient;

    private void handlerSignInResult(GoogleSignInResult googleSignInResult) {
        if(googleSignInResult.isSuccess()){
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();
            assert account != null;
            Call<Conductor> conductorCall = mAPIService.findConductorEmail(account.getEmail());
            conductorCall.enqueue(new Callback<Conductor>() {
                @Override
                public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                    if(response.isSuccessful()){
                        cambiarIntent(MainActivity.class);
                    }
                }

                @Override
                public void onFailure(Call<Conductor> call, Throwable t) {
                    cambiarIntent(LoginActivity.class);
                }
            });
        }else{
            cambiarIntent(LoginActivity.class);
        }
    }

    private void goLogInScreen() {
        Intent intent = new Intent(context,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                OptionalPendingResult<GoogleSignInResult> optionalPendingResult =  Auth.GoogleSignInApi.silentSignIn(googleApiClient);
                if(optionalPendingResult.isDone()){
                    GoogleSignInResult googleSignInResult = optionalPendingResult.get();
                    handlerSignInResult(googleSignInResult);
                }else {
                    optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                        @Override
                        public void onResult(@NonNull @NotNull GoogleSignInResult googleSignInResult) {
                            handlerSignInResult(googleSignInResult);
                        }
                    });

                    //SharedPreferences mantenerUsuario = getSharedPreferences("MantenerUsuario", MODE_PRIVATE);
                    //if(mantenerUsuario != null){

                        String usuario = mantenerPref.getPrefString(context,"Usuario",null); //mantenerUsuario.getString("Usuario",null);
                        String password = mantenerPref.getPrefString(context,"Password",null); //mantenerUsuario.getString("Password",null);
                        boolean check = mantenerPref.getPrefBoolean(context,"Check",false); //mantenerUsuario.getBoolean("Check", false);
                        if(check){
                            sendPost(usuario,password);
                        }
                    //}
                    cambiarIntent(LoginActivity.class);
                }

            }
        },3000);
    }

    public void sendPost(String email, String contrasena) {
        Call<Conductor> callConductor = mAPIService.findConductorLogin(email,contrasena);
        callConductor.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful()){
                    mostrarMensaje("login exitoso");
                    cambiarIntent(MainActivity.class);
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

    private void mostrarMensaje(String mensaje){
        Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
    }

    private void cambiarIntent(Class activity){
        Intent intent = new Intent(context, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }
}
