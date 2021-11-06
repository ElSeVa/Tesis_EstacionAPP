package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.preferencias.Preferencias;
import com.example.myapplication.ui.ObserverReservaciones;
import com.example.myapplication.ui.Temporizador;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Reservacion;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelarReservaActivity extends AppCompatActivity {

    private final ObserverReservaciones observerReservaciones = new ObserverReservaciones();
    Button btnCancelarReserva;
    TextView tvFechaHoraReserva, tvFechaHoraRetiro, tvTipoVehiculo, tvDuracion, tvEstados, tvGarage;
    Reservacion reservacion;
    Call<Reservacion> callReservacion;
    Integer idReservacion;
    private APIService mAPIService = ApiUtils.getAPIService();
    private CountDownTimer contador;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar);
        tvGarage = (TextView) findViewById(R.id.tvGarage);
        tvEstados = (TextView) findViewById(R.id.tvEstados);
        tvDuracion = (TextView) findViewById(R.id.tvDuracion);
        tvTipoVehiculo = (TextView) findViewById(R.id.tvTipoVehiculo);
        tvFechaHoraRetiro = (TextView) findViewById(R.id.tvFechaHoraRetiro);
        tvFechaHoraReserva = (TextView) findViewById(R.id.tvFechaHoraReserva);
        btnCancelarReserva = (Button) findViewById(R.id.btnCancelarReserva);
        Preferencias loginPref = new Preferencias("Login");
        int idConductor = loginPref.getPrefInteger(this,"idConductor",0);
        Preferencias tiempoPref = new Preferencias("Tiempo"+idConductor);
        idReservacion = tiempoPref.getPrefInteger(this,"idReservacion",0);
        Temporizador temporizador = new Temporizador(this, 1000, idConductor);
        temporizador.setTextView(tvDuracion);
        temporizador.registerLifecycle(this);

        observerReservaciones.agregar(temporizador);
        if(idReservacion != 0){
            Log.d("lifeCyCle","hay reserva");
            callReservacion = mAPIService.obtenerReservacionPorId(idReservacion);
            contador = new CountDownTimer(1800000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    callReservacion = mAPIService.obtenerReservacionPorId(idReservacion);
                    if(!callReservacion.isExecuted()){
                        callReservacion.enqueue(new Callback<Reservacion>() {
                            @Override
                            public void onResponse(@NotNull Call<Reservacion> call, @NotNull Response<Reservacion> response) {
                                if(response.isSuccessful()){
                                    Reservacion r = response.body();
                                    if (r != null) {
                                        if(r.getEstado().equalsIgnoreCase("Esperando")){
                                            observerReservaciones.notificar(true);

                                        }else{
                                            contador.onFinish();
                                            new Handler().postDelayed(() -> {

                                            },2000);
                                        }
                                    }

                                }
                            }

                            @SuppressLint("LogNotTimber")
                            @Override
                            public void onFailure(@NotNull Call<Reservacion> call, @NotNull Throwable t) {
                                Log.d("ERROR","Tira error todo el rato " + t.getMessage());
                            }
                        });
                    }
                    callReservacion.timeout();
                }

                @Override
                public void onFinish() {
                    observerReservaciones.notificar(false);
                    contador.cancel();
                    temporizador.cancelarTemporizador();
                }
            }.start();
        }
        Bundle parametros = this.getIntent().getExtras();
        if(parametros != null){
            int id = parametros.getInt("id");
            String nombre = parametros.getString("nombre");
            if(id != 0){
                tvGarage.setText(nombre);
                obtenerReservacion(id);
                obtenerConductor();
            }
        }else{
            Toast.makeText(this, "Error no hay savedInstanceState", Toast.LENGTH_SHORT).show();
        }

        btnCancelarReserva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reservacion != null){
                    reservacion.setEstado("Cancelado");
                    Call<Reservacion> reservacionCall = ApiUtils.getAPIService().uptadeReserva(reservacion);
                    reservacionCall.enqueue(new Callback<Reservacion>() {
                        @Override
                        public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(CancelarReservaActivity.this, "La reservacion fue cancelada", Toast.LENGTH_SHORT).show();
                                btnCancelarReserva.setEnabled(false);
                            }
                        }

                        @Override
                        public void onFailure(Call<Reservacion> call, Throwable t) {

                        }
                    });
                }
            }
        });

    }

    private void obtenerReservacion(int id){
        Call<Reservacion> call = ApiUtils.getAPIService().obtenerReservacionPorId(id);
        call.enqueue(new Callback<Reservacion>() {
            @Override
            public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                if(response.isSuccessful() && response.body() != null){
                    reservacion = response.body();
                    tvFechaHoraReserva.setText(reservacion.getFechaInicio());
                    tvFechaHoraRetiro.setText(reservacion.getFechaFinal());
                    tvEstados.setText(reservacion.getEstado());
                    if(reservacion.getEstado().equalsIgnoreCase("Aceptado") ||
                        reservacion.getEstado().equalsIgnoreCase("Cancelado") ){
                        btnCancelarReserva.setEnabled(false);
                        tvDuracion.setText("00:00");
                    }
                }
            }

            @Override
            public void onFailure(Call<Reservacion> call, Throwable t) {

            }
        });
    }

    private void obtenerConductor(){
        Preferencias loginPref = new Preferencias("Login");
        int idConductor = loginPref.getPrefInteger(this, "idConductor",0);
        Call<Conductor> call = ApiUtils.getAPIService().findConductor(idConductor);
        call.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful() && response.body() != null){
                    Conductor conductor = response.body();
                    tvTipoVehiculo.setText(conductor.getTipoVehiculo());
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {

            }
        });
    }
}
