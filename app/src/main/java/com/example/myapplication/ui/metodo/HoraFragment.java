package com.example.myapplication.ui.metodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.api.APIService;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.dialogFragment.DatePickerFragment;
import com.example.myapplication.ui.dialogFragment.TimePickerFragment;
import com.example.myapplication.ui.models.Estadia;
import com.example.myapplication.ui.models.Reservacion;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HoraFragment extends Fragment {

    private MainActivity activity;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switch1;
    private TextView tvHoraFTitulo,tvFechaTitulo;
    private EditText etFecha, etHora, etHoraF;
    private Calendar calFechaI, calFechaF, calInicio, calFinal;
    private DateFormat df;
    private Date date;
    private int idConductor, idGarage;
    private final APIService mAPIService = ApiUtils.getAPIService();
    private final List<Estadia> listEstadia = new ArrayList<>();
    private Estadia estadia;
    private String fecha, horaF, horaI, vehiculo;
    private Button btnReservaHora;

    private int cantidad, precio;
    private int dias = 0;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setDrawer_locker();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reserva_hora, container, false);
        switch1 = root.findViewById(R.id.switch1);
        etFecha = root.findViewById(R.id.etFecha);
        etHora = root.findViewById(R.id.etHora);
        etHoraF = root.findViewById(R.id.etHoraF);
        tvHoraFTitulo = root.findViewById(R.id.tvHoraFTitulo);
        tvFechaTitulo = root.findViewById(R.id.tvFechaTitulo);
        btnReservaHora = root.findViewById(R.id.btnReservaHora);

        return root;
    }

    @SuppressLint("SimpleDateFormat")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        calInicio = new GregorianCalendar();
        calFinal = new GregorianCalendar();
        etFecha.setInputType(InputType.TYPE_NULL);
        etHora.setInputType(InputType.TYPE_NULL);
        etHoraF.setInputType(InputType.TYPE_NULL);

        etHora.setOnClickListener(this::showHoras);

        etHoraF.setOnClickListener(this::showHoraF);

        etFecha.setOnClickListener(this::showFecha);

        SharedPreferences prefs = activity.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        idConductor = prefs.getInt("idConductor", 0);
        idGarage = prefs.getInt("idGarage", 0);
        vehiculo = prefs.getString("Vehiculo", null);

        establecerEstadia();

        if(!listEstadia.isEmpty()){
            Toast.makeText(activity, "Hay Estadia", Toast.LENGTH_SHORT).show();
        }

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
            for (Estadia e : listEstadia){
                estadia = e;
            }
            fecha = etFecha.getText().toString();
            horaI = etHora.getText().toString();
            horaF = etHoraF.getText().toString();
            if(estadia == null){
                Toast.makeText(activity, "Error Estadia = " + idConductor + " - " + idGarage, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, "Paso piola", Toast.LENGTH_SHORT).show();
            }
            if(!horaI.isEmpty()){
                cantidad = calcularCantidadHora(horaI);
                if(switch1.isChecked()){
                    if(!fecha.isEmpty()){
                        dias = calcularCantidadDias(fecha);
                        Toast.makeText(activity, dias + " Dias Total", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(activity, "Seleccione una fecha", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(activity, "Estas seguro de hacer la reserva?", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                if(precio!=0){
                    Toast.makeText(activity, "Precio: "+ precio + " ,Estadia: Hora, Cantidad: " +cantidad, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, "Fecha_Inicio: " + df.format(calFechaI.getTime()) + ", Fecha_Final: " + df.format(calFechaF.getTime()), Toast.LENGTH_SHORT).show();
                    Reservacion reservacion = new Reservacion(precio,"Hora",cantidad,df.format(calFechaI.getTime()),df.format(calFechaF.getTime()),"Esperando",idConductor,idGarage);
                    Call<Reservacion> callReserva = mAPIService.insertsReserva(reservacion);
                    //Call<Reservacion> callReserva = mAPIService.insertReserva(precio,"Hora",cantidad,df.format(calFechaI.getTime()),df.format(calFechaF.getTime()),"Esperando",idConductor,idGarage);
                    callReserva.enqueue(new Callback<Reservacion>() {
                        @Override
                        public void onResponse(@NotNull Call<Reservacion> call,@NotNull Response<Reservacion> response) {
                            if(response.isSuccessful() && response.body() != null){
                                Toast.makeText(activity,"Registro Exitoso", Toast.LENGTH_SHORT).show();
                                Reservacion reservacion = response.body();
                                SharedPreferences.Editor pref = activity.getSharedPreferences("Tiempo", Context.MODE_PRIVATE).edit();
                                pref.putBoolean("timerRunning",true);
                                pref.putInt("idReservacion",reservacion.getId());
                                pref.apply();

                                Navigation.findNavController(v).navigate(R.id.nav_home);
                            }else{
                                Toast.makeText(activity,"Registro Fallido", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(@NotNull Call<Reservacion> call,@NotNull Throwable t) {
                            Toast.makeText(activity,"Error consulta...", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });


                }else{
                    Toast.makeText(activity,"No hay precio", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(activity, "Seleccione una hora", Toast.LENGTH_SHORT).show();
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

    @SuppressLint("SimpleDateFormat")
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
        assert date != null;
        calFinal.setTime(date);
        int startTime = calInicio.get(Calendar.DAY_OF_YEAR);
        int endTime = calFinal.get(Calendar.DAY_OF_YEAR);
        /*
        long startTime = calInicio.getTimeInMillis();
        long endTime = calFinal.getTimeInMillis();
        long diffTime = endTime - startTime;
        long diffDays = diffTime / (1000 * 60 * 60 * 24);
*/
        return endTime - startTime;
    }

    private Map<String, String> getFields(){
        Map<String,String> fields = new HashMap<>();
        fields.put("Precio",String.valueOf(precio));
        fields.put("Estadia","Hora");
        fields.put("Cantidad",String.valueOf(cantidad));
        fields.put("Fecha_inicio",df.format(calFechaI.getTime()));
        fields.put("Fecha_final",df.format(calFechaF.getTime()));
        fields.put("Estado","Esperando");
        fields.put("ID_Conductor",String.valueOf(idConductor));
        fields.put("ID_Garage",String.valueOf(idGarage));
        return fields;
    }

    @SuppressLint("SimpleDateFormat")
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

        assert date != null;
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

    private void establecerEstadia(){
        Call<Estadia> estadiaCall = mAPIService.verificarEstadia(idGarage,vehiculo,"Hora");

        estadiaCall.enqueue(new Callback<Estadia>() {
            @Override
            public void onResponse(Call<Estadia> call, @NotNull Response<Estadia> response) {
                if(!response.isSuccessful() && response.body() == null){
                    Toast.makeText(activity,"Paso 1 " + response.code(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity,"Paso 2 " + response.code(), Toast.LENGTH_SHORT).show();
                }
                Estadia estadia = response.body();
                if (estadia != null) {
                    Toast.makeText(activity, "Estado: " + estadia.getPrecio(), Toast.LENGTH_SHORT).show();
                }
                listEstadia.add(estadia);
                if(!listEstadia.isEmpty()){
                    Toast.makeText(activity, "Hay Estadia", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Estadia> call, Throwable t) {
                Toast.makeText(activity, String.valueOf(t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showHoras(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(activity.getSupportFragmentManager(), "tpHora");

    }

    public void showHoraF(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(activity.getSupportFragmentManager(), "tpHoraF");

    }

    public void showFecha(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(activity.getSupportFragmentManager(), "dpFecha");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.setDrawer_unlocker();
    }

}
