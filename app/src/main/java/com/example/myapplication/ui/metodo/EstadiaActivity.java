package com.example.myapplication.ui.metodo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.dialogFragment.DatePickerFragment;
import com.example.myapplication.ui.dialogFragment.TimePickerFragment;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Reservacion;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadiaActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText etHoraEstadia, etFechaEstadia;
    Button btnReservaEstadia;
    Calendar calFechaI, calFechaF, calInicio, calFinal;
    SharedPreferences prefs;
    Integer idConductor, idGarage;
    APIService mAPIService = ApiUtils.getAPIService();
    Conductor conductor;
    Estadia estadia;
    Call<Conductor> conductorCall;
    Call<List<Estadia>> estadiaCall;
    DateFormat df;
    Date date;
    int cantidad,dias,precio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reserva_estadia);
        //toolbar = findViewById(R.id.tp_estadia);
        setSupportActionBar(toolbar);
        //ab.setDisplayHomeAsUpEnabled(true);
        //(TimePickerFragmentgetActivity()).changeStatus("timePicker");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etHoraEstadia = findViewById(R.id.etHoraEstadia);
        etFechaEstadia = findViewById(R.id.etFechaEstadia);

        etHoraEstadia.setInputType(InputType.TYPE_NULL);
        etFechaEstadia.setInputType(InputType.TYPE_NULL);

        btnReservaEstadia = findViewById(R.id.btnReservaEstadia);

        prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        idConductor = prefs.getInt("idConductor", 0);
        idGarage = prefs.getInt("idGarage", 0);

        establecerConductor(idConductor);

        btnReservaEstadia.setOnClickListener(v -> {
            if(conductor != null){
            }else{
                Toast.makeText(this, "Error Conductor = " + idConductor + " - " + idGarage, Toast.LENGTH_SHORT).show();
            }
            String fecha = etFechaEstadia.getText().toString();
            String hora = etHoraEstadia.getText().toString();

            if(!hora.isEmpty() && !fecha.isEmpty()){
                dias = calcularCantidadDias(fecha);
                calFechaI = new GregorianCalendar();
                calFechaF = new GregorianCalendar();
                separarTexto(hora,calFechaF);
                calFechaI.add(Calendar.MINUTE,30);
                calFechaF.add(Calendar.DAY_OF_YEAR, dias);
                cantidad = dias;
                try {
                    precio = estadia.getPrecio() * cantidad;
                } catch (Exception ex){
                    ex.printStackTrace();
                }
                df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Toast.makeText(this, "Precio: "+ precio + " ,Estadia: Estadia, Cantidad: " +cantidad, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Fecha_Inicio: " + df.format(calFechaI.getTime()) + ", Fecha_Final: " + df.format(calFechaF.getTime()), Toast.LENGTH_SHORT).show();
                if(precio != 0){
                    Call<Reservacion> call = mAPIService.insertReserva(precio,"Estadia",cantidad,df.format(calFechaI.getTime()),df.format(calFechaF.getTime()),"Esperando",idConductor,idGarage);
                    call.enqueue(new Callback<Reservacion>() {
                        @Override
                        public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(EstadiaActivity.this,"Registro Exitoso", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(EstadiaActivity.this,"Registro Fallido", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Reservacion> call, Throwable t) {
                            Toast.makeText(EstadiaActivity.this,"Error consulta...", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });

                    finish();
                }
            }
        });

    }

    private void separarTexto(String texto, Calendar cal){
        String[] parts = texto.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,minute);
    }

    private int calcularCantidadDias(String fecha){
        calInicio = new GregorianCalendar();
        calFinal = new GregorianCalendar();

        df = new SimpleDateFormat("dd/MM/yyyy");
        date = null;
        try {
            date = df.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calFinal.setTime(date);
        int startTime = calInicio.get(Calendar.DAY_OF_YEAR);
        int endTime = calFinal.get(Calendar.DAY_OF_YEAR);
        int diffTime = endTime - startTime;
/*
        long startTime = calInicio.getTimeInMillis();
        long endTime = calFinal.getTimeInMillis();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);
*/
        return (int) diffTime;
    }

    private void establecerConductor(Integer idConductor){
        Call<Conductor> conductorCall = mAPIService.findConductor(idConductor);
        conductorCall.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(@NotNull Call<Conductor> call, @NotNull Response<Conductor> response) {
                if (response.body() != null) {
                    if(response.isSuccessful()){
                        conductor = response.body();
                    }
                } else if(response.code() != 0) {
                    Log.d("Error", String.valueOf(response.code()) + response.message());
                }
            }

            @Override
            public void onFailure(@NotNull Call<Conductor> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public void showFechaEstadia(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "dpFechaEstadia");
    }

    public void showHoraEstadia(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "tpHoraEstadia");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
