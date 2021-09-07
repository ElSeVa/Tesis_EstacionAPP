package com.example.myapplication.ui.metodo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class HoraActivity extends AppCompatActivity {

    private Switch switch1;
    private TextView tvHoraFTitulo,tvFechaTitulo;
    private EditText etFecha, etHora, etHoraF;
    private Calendar calFechaI, calFechaF, calInicio, calFinal;
    private DateFormat df;
    private Date date;
    private int idConductor, idGarage;
    private APIService mAPIService = ApiUtils.getAPIService();
    private Estadia estadia = null;
    private String fecha;
    private String horaI;
    private String horaF;

    private int cantidad, precio;
    private int dias = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reserva_hora);

        //Toolbar toolbar = findViewById(R.id.tp_hora);
        //setSupportActionBar(toolbar);
        calInicio = new GregorianCalendar();
        calFinal = new GregorianCalendar();

        switch1 = findViewById(R.id.switch1);
        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etHoraF = findViewById(R.id.etHoraF);

        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        idConductor = prefs.getInt("idConductor", 0);
        idGarage = prefs.getInt("idGarage", 0);
        String vehiculo = prefs.getString("Vehiculo", null);


        tvHoraFTitulo = findViewById(R.id.tvHoraFTitulo);
        tvFechaTitulo = findViewById(R.id.tvFechaTitulo);
        etFecha.setInputType(InputType.TYPE_NULL);
        etHora.setInputType(InputType.TYPE_NULL);
        etHoraF.setInputType(InputType.TYPE_NULL);


        new Handler().postDelayed(() -> {
            if(estadia != null){
                Toast.makeText(this, "Estadia cargado", Toast.LENGTH_SHORT).show();
            }
        },3000);


        Button btnReservaHora = findViewById(R.id.btnReservaHora);
        //ab.setDisplayHomeAsUpEnabled(true);
        //(TimePickerFragmentgetActivity()).changeStatus("timePicker");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        switch1.setOnClickListener(v -> {
            if(switch1.isChecked()){
                tvHoraFTitulo.setVisibility(View.VISIBLE);
                etHoraF.setVisibility(View.VISIBLE);
                etFecha.setVisibility(View.VISIBLE);
                tvFechaTitulo.setVisibility(View.VISIBLE);
            }else {
                tvHoraFTitulo.setVisibility(View.GONE);
                etHoraF.setVisibility(View.GONE);
                etFecha.setVisibility(View.GONE);
                tvFechaTitulo.setVisibility(View.GONE);
                etHoraF.setText("");
                etFecha.setText("");
            }
        });

        btnReservaHora.setOnClickListener(v -> {
            fecha = etFecha.getText().toString();
            horaI = etHora.getText().toString();
            horaF = etHoraF.getText().toString();
            if(estadia == null){
                Toast.makeText(HoraActivity.this, "Error Estadia = " + idConductor + " - " + idGarage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(HoraActivity.this, "Paso piola", Toast.LENGTH_SHORT).show();
            }
            if(!horaI.isEmpty()){
                cantidad = calcularCantidadHora(horaI);
                if(switch1.isChecked()){
                    if(!fecha.isEmpty()){
                        dias = calcularCantidadDias(fecha);
                        Toast.makeText(this, dias + " Dias Total", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
                    }
                }
                calFechaI = new GregorianCalendar();
                calFechaF = new GregorianCalendar();

                if(dias != 0){
                    calFechaI.add(Calendar.DAY_OF_MONTH,dias);
                    calFechaF.add(Calendar.DAY_OF_MONTH,dias);
                }

                if(switch1.isChecked()){
                    separarTexto(horaI,calFechaI);
                    separarTexto(horaF,calFechaF);
                    cantidad = calcularCantidadHoraConvesional(calFechaI,calFechaF);
                }else{
                    calFechaI.add(Calendar.MINUTE,30);
                    separarTexto(horaI,calFechaF);
                }

                if(calFechaI.get(Calendar.HOUR_OF_DAY) > calFechaF.get(Calendar.HOUR_OF_DAY)){
                    calFechaF.add(Calendar.DAY_OF_MONTH,1);
                    cantidad = calcularCantidadHoraConvesional(calFechaI,calFechaF);
                }

                df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                try {

                    if(estadia != null){
                        precio = estadia.getPrecio();
                        precio = precio * cantidad;
                    }else{
                        Toast.makeText(HoraActivity.this, "Estas seguro de hacer la reserva?", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                if(precio!=0){
                    Toast.makeText(this, "Precio: "+ precio + " ,Estadia: Hora, Cantidad: " +cantidad, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Fecha_Inicio: " + df.format(calFechaI.getTime()) + ", Fecha_Final: " + df.format(calFechaF.getTime()), Toast.LENGTH_SHORT).show();
                    Call<Reservacion> call = mAPIService.insertReserva(precio,"Hora",cantidad,df.format(calFechaI.getTime()),df.format(calFechaF.getTime()),"Esperando",idConductor,idGarage);
                    call.enqueue(new Callback<Reservacion>() {
                        @Override
                        public void onResponse(@NotNull Call<Reservacion> call,@NotNull Response<Reservacion> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(HoraActivity.this,"Registro Exitoso", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(HoraActivity.this,"Registro Fallido", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(@NotNull Call<Reservacion> call,@NotNull Throwable t) {
                            Toast.makeText(HoraActivity.this,"Error consulta...", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });

                    finish();
                }else{
                    Toast.makeText(HoraActivity.this,"No hay precio", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Seleccione una hora", Toast.LENGTH_SHORT).show();
            }

        });
    }



    private void separarTexto(String texto, Calendar cal){
        if(!texto.isEmpty()){
            String[] parts = texto.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            cal.set(Calendar.HOUR_OF_DAY,hour);
            cal.set(Calendar.MINUTE,minute);
        }
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

    private int calcularCantidadHora(String hora){
        calInicio = new GregorianCalendar();
        calFinal = new GregorianCalendar();

        df = new SimpleDateFormat("HH:mm");
        date = null;
        try {
            date = df.parse(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calFinal.setTime(date);
        int horaInicio = calInicio.get(Calendar.HOUR_OF_DAY);
        int horaFinal = calFinal.get(Calendar.HOUR_OF_DAY);
        return Math.abs(horaFinal - horaInicio);
    }

    private int calcularCantidadHoraConvesional(Calendar horaI, Calendar horaF){
        long diferencia = horaI.getTimeInMillis() - horaF.getTimeInMillis();
        long diffHours = diferencia / (60 * 60 * 1000);
        return (int) Math.abs(diffHours);
    }

    public void showHora(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "tpHora");

    }

    public void showHoraF(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "tpHoraF");

    }

    public void showFecha(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "dpFecha");
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }


}
