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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.preferencias.Preferencias;
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

public class MediaFragment extends Fragment {
    private MainActivity activity;
    private EditText etHoraMedia, etFechaMedia;
    private Button btnReservaMedia;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swAm;
    private Calendar calFechaI;
    private Calendar calFechaF;
    private Integer idConductor, idGarage;
    private final APIService mAPIService = ApiUtils.getAPIService();
    private final List<Estadia> listEstadia = new ArrayList<>();
    private Estadia estadia;
    private DateFormat df;
    private int cantidad,dias,precio;
    private String vehiculo;
    private final Preferencias loginPref = new Preferencias("Login");
    private final Preferencias tiempoPref = new Preferencias("Tiempo");
    private final Map<String, String> mapTiempo= new HashMap<>();

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
        View root = inflater.inflate(R.layout.fragment_reserva_media, container, false);
        btnReservaMedia = root.findViewById(R.id.btnReservaMedia);
        etHoraMedia = root.findViewById(R.id.etHoraMedia);
        etFechaMedia = root.findViewById(R.id.etFechaMedia);
        swAm = root.findViewById(R.id.swAm);
        return root;
    }

    @SuppressLint("SimpleDateFormat")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //SharedPreferences prefs = activity.getSharedPreferences("Login", Context.MODE_PRIVATE);
        idConductor = loginPref.getPrefInteger(activity,"idConductor",0);// prefs.getInt("idConductor", 0);
        idGarage = loginPref.getPrefInteger(activity,"idGarage",0);// prefs.getInt("idGarage", 0);
        vehiculo = loginPref.getPrefString(activity,"Vehiculo",null);// prefs.getString("Vehiculo", null);

        etHoraMedia.setInputType(InputType.TYPE_NULL);
        etFechaMedia.setInputType(InputType.TYPE_NULL);
        etHoraMedia.setOnClickListener(this::showHoraMedia);
        etFechaMedia.setOnClickListener(this::showFechaMedia);
        establecerEstadia();
        btnReservaMedia.setOnClickListener(v -> {
            String fecha = etFechaMedia.getText().toString();
            String hora = etHoraMedia.getText().toString();

            if(!hora.isEmpty() && !fecha.isEmpty()) {
                for (Estadia e : listEstadia){
                    estadia = e;
                }
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
                    if(multiplo(cantidad)){
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
                Toast.makeText(activity, "Precio: "+ precio + " ,Estadia: Hora, Cantidad: " +cantidad, Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "Fecha_Inicio: " + df.format(calFechaI.getTime()) + ", Fecha_Final: " + df.format(calFechaF.getTime()), Toast.LENGTH_SHORT).show();
                if(precio != 0){
                    Reservacion reservacion = new Reservacion(precio,"Media Estadia",cantidad,df.format(calFechaI.getTime()),df.format(calFechaF.getTime()),"Esperando",idConductor,idGarage);
                    Call<Reservacion> call = mAPIService.insertsReserva(reservacion);
                    call.enqueue(new Callback<Reservacion>() {
                        @Override
                        public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                            if(response.isSuccessful()){
                                Toast.makeText(activity,"Registro Exitoso", Toast.LENGTH_SHORT).show();
                                Reservacion reservacion = response.body();
                                assert reservacion != null;
                                mapTiempo.put("seEstaEjecutando",String.valueOf(true));
                                mapTiempo.put("idReservacion",String.valueOf(reservacion.getId()));
                                tiempoPref.setPrefTiempos(activity,mapTiempo);
                                /*
                                SharedPreferences.Editor pref = activity.getSharedPreferences("Tiempo", Context.MODE_PRIVATE).edit();
                                pref.putBoolean("seEstaEjecutando",true);
                                pref.putInt("idReservacion",reservacion.getId());
                                pref.apply();
                                */
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

            }else {
                Toast.makeText(activity, "Tiene que llenar todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void establecerEstadia(){
        Call<Estadia> estadiaCall = mAPIService.verificarEstadia(idGarage,vehiculo,"Media Estadia");

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

    private boolean multiplo(int valor1){
        return (valor1 % 2 ==0);
    }

    private void separarTexto(String texto, Calendar cal){
        String[] parts = texto.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        cal.set(Calendar.HOUR_OF_DAY,hour);
        cal.set(Calendar.MINUTE,minute);
    }

    @SuppressLint("SimpleDateFormat")
    private int calcularCantidadDias(String fecha){
        Calendar calInicio = new GregorianCalendar();
        Calendar calFinal = new GregorianCalendar();

        df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = df.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
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

    public void showFechaMedia(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(activity.getSupportFragmentManager(), "dpFechaMedia");
    }

    public void showHoraMedia(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(activity.getSupportFragmentManager(), "tpHoraMedia");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.setDrawer_unlocker();
    }
}
