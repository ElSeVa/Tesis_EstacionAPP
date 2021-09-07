package com.example.myapplication.ui.metodo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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

import java.sql.Time;
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

public class MediaActivity extends AppCompatActivity {
    Toolbar toolbar;
    EditText etHoraMedia, etFechaMedia;
    Button btnReservaMedia;
    Switch swAm;
    Calendar calFechaI, calFechaF, calInicio, calFinal;
    SharedPreferences prefs;
    Integer idConductor, idGarage;
    APIService mAPIService = ApiUtils.getAPIService();
    Conductor conductor;
    Estadia estadia;
    DateFormat df;
    Date date;
    int cantidad,dias,precio;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reserva_media);
        //toolbar = findViewById(R.id.tp_media);
        setSupportActionBar(toolbar);
        //ab.setDisplayHomeAsUpEnabled(true);
        //(TimePickerFragmentgetActivity()).changeStatus("timePicker");
        btnReservaMedia = findViewById(R.id.btnReservaMedia);
        etHoraMedia = findViewById(R.id.etHoraMedia);
        etFechaMedia = findViewById(R.id.etFechaMedia);
        etHoraMedia.setInputType(InputType.TYPE_NULL);
        etFechaMedia.setInputType(InputType.TYPE_NULL);
        swAm = findViewById(R.id.swAm);

        prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        idConductor = prefs.getInt("idConductor", 0);
        idGarage = prefs.getInt("idGarage", 0);

        establecerConductor(idConductor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnReservaMedia.setOnClickListener(v -> {
            if(conductor != null){
            }else{
                Toast.makeText(this, "Error Conductor = " + idConductor + " - " + idGarage, Toast.LENGTH_SHORT).show();
            }
            String fecha = etFechaMedia.getText().toString();
            String hora = etHoraMedia.getText().toString();

            if(!hora.isEmpty() && !fecha.isEmpty()) {
                //cantidad = calcularCantidadHora(hora);
                dias = calcularCantidadDias(fecha);
                calFechaI = new GregorianCalendar();
                calFechaF = new GregorianCalendar();

                df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                //Tiene que elegir una hora y fecha de reserva EJ: 6:30pm 26/5/2021
                //entonces se reserva desde las 6:30pm hasta las 6:30AM del dia siguiente
                //6:30pm 26/5/2021 - 6:30am 27/5/2021
                //en caso de que eliga el mismo dia
                //esto seria en cantidad 1
                //para otras dias se tomara como 24 horas
                //6:30pm 26/5/2021 - 6:30pm 27/5/2021
                //esto seria en cantidad 2
                //6:30pm 26/5/2021 - 6:30am 28/5/2021
                //esto seria en cantidad 3
                //6:30pm 26/5/2021 - 6:30pm 28/5/2021
                //esto seria en cantidad 4
                separarTexto(hora,calFechaI);
                separarTexto(hora,calFechaF);
                if(dias == 0){
                    if(calFechaI.get(Calendar.AM_PM)==Calendar.PM){
                        calFechaF.set(Calendar.AM_PM, Calendar.AM);
                        calFechaF.add(Calendar.DAY_OF_MONTH,1);
                        calFechaF.add(Calendar.HOUR_OF_DAY,1);
                        cantidad = 1;
                    }else{
                        calFechaF.set(Calendar.AM_PM,Calendar.PM);
                        calFechaF.add(Calendar.HOUR_OF_DAY,1);
                    }
                }else{
                    calFechaF.add(Calendar.DAY_OF_MONTH,dias);
                    cantidad = 2 * dias;
                    if(multiplo(cantidad,2)){
                        if(swAm.isChecked()){
                            calFechaF.set(Calendar.AM_PM,Calendar.AM);
                            cantidad = cantidad - 1;
                        }
                    }
                }
                try {
                    precio = estadia.getPrecio() * cantidad;
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                Toast.makeText(this, "Precio: "+ precio + " ,Estadia: Hora, Cantidad: " +cantidad, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "Fecha_Inicio: " + df.format(calFechaI.getTime()) + ", Fecha_Final: " + df.format(calFechaF.getTime()), Toast.LENGTH_SHORT).show();
                if(precio != 0){
                    Call<Reservacion> call = mAPIService.insertReserva(precio,"Media Estadia",cantidad,df.format(calFechaI.getTime()),df.format(calFechaF.getTime()),"Esperando",idConductor,idGarage);
                    call.enqueue(new Callback<Reservacion>() {
                        @Override
                        public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(MediaActivity.this,"Registro Exitoso", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MediaActivity.this,"Registro Fallido", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Reservacion> call, Throwable t) {
                            Toast.makeText(MediaActivity.this,"Error consulta...", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });

                    finish();
                }

            }else {
                Toast.makeText(this, "Tiene que llenar todos los campos.", Toast.LENGTH_SHORT).show();
            }
            });

    }
    private boolean multiplo(int x1, int x2){
        return (x1%x2==0);
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




    public void showFechaMedia(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "dpFechaMedia");
    }

    public void showHoraMedia(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "tpHoraMedia");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}
