package com.example.myapplication.ui.metodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.myapplication.ui.models.Conductor;
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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstadiaFragment extends Fragment {
    private MainActivity activity;

    private EditText etHoraEstadia, etFechaEstadia;
    private Button btnReservaEstadia;
    private Calendar calFechaI, calFechaF, calInicio, calFinal;
    private Integer idConductor, idGarage;
    private final APIService mAPIService = ApiUtils.getAPIService();
    private final List<Estadia> listEstadia = new ArrayList<>();
    private Estadia estadia;
    private DateFormat df;
    private String vehiculo;
    private int cantidad,dias,precio;

    @Override
    public void onStart() {
        super.onStart();
        activity.setDrawer_locker();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reserva_estadia, container, false);

        etHoraEstadia = root.findViewById(R.id.etHoraEstadia);
        etFechaEstadia = root.findViewById(R.id.etFechaEstadia);

        btnReservaEstadia = root.findViewById(R.id.btnReservaEstadia);

        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        calInicio = new GregorianCalendar();
        calFinal = new GregorianCalendar();

        etHoraEstadia.setOnClickListener(this::showHoraEstadia);

        etFechaEstadia.setOnClickListener(this::showFechaEstadia);

        SharedPreferences prefs = activity.getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        idConductor = prefs.getInt("idConductor", 0);
        idGarage = prefs.getInt("idGarage", 0);
        vehiculo = prefs.getString("Vehiculo", null);
        establecerEstadia();

        if(!listEstadia.isEmpty()){
            Toast.makeText(activity, "Hay Estadia", Toast.LENGTH_SHORT).show();
        }

        btnReservaEstadia.setOnClickListener(v -> {
            for (Estadia e : listEstadia){
                estadia = e;
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
                Toast.makeText(activity, "Precio: "+ precio + " ,Estadia: Estadia, Cantidad: " +cantidad, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "Fecha_Inicio: " + df.format(calFechaI.getTime()) + ", Fecha_Final: " + df.format(calFechaF.getTime()), Toast.LENGTH_SHORT).show();
                if(precio != 0){
                    Call<Reservacion> call = mAPIService.insertReserva(precio,"Estadia",cantidad,df.format(calFechaI.getTime()),df.format(calFechaF.getTime()),"Esperando",idConductor,idGarage);
                    call.enqueue(new Callback<Reservacion>() {
                        @Override
                        public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                            if(response.isSuccessful()){
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
                        public void onFailure(Call<Reservacion> call, Throwable t) {
                            Toast.makeText(activity,"Error consulta...", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });


                }else{
                    Toast.makeText(activity,"No hay precio", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(activity, "Seleccione una hora o Fecha", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void establecerEstadia(){
        Call<Estadia> estadiaCall = mAPIService.verificarEstadia(idGarage,vehiculo,"Estadia");

        estadiaCall.enqueue(new Callback<Estadia>() {
            @Override
            public void onResponse(Call<Estadia> call, Response<Estadia> response) {
                if(!response.isSuccessful() && response.body() == null){
                    Toast.makeText(activity, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                }else{
                    Toast.makeText(activity, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                }
                Estadia estadia = response.body();
                Toast.makeText(activity, "Estado: " + estadia.getPrecio(), Toast.LENGTH_SHORT).show();
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
        Date date = null;
        try {
            date = df.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            calFinal.setTime(date);
        }
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

    public void showFechaEstadia(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(activity.getSupportFragmentManager(), "dpFechaEstadia");
    }

    public void showHoraEstadia(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(activity.getSupportFragmentManager(), "tpHoraEstadia");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.setDrawer_unlocker();
    }
}
