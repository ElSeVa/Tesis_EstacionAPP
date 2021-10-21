package com.example.myapplication.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Resena;
import com.example.myapplication.ui.models.Reservacion;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterBaseReservas extends BaseAdapter {

    private final Context context;
    private final ArrayList<Reservacion> reservas;

    public AdapterBaseReservas(Context context, ArrayList<Reservacion> reservas){
        this.reservas = reservas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return reservas.size();
    }

    @Override
    public Object getItem(int position) {
        return reservas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Inflamos la vista con nuestro propio layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_reservas_adapter, parent, false);
        }

        Reservacion currentName = (Reservacion) getItem(position);

        agregarTextView(convertView,currentName);

        return convertView;
    }

    private void agregarTextView(View view, Reservacion reservas){
        TextView tvConductor = view.findViewById(R.id.tvConductor);
        TextView tvVehiculo = view.findViewById(R.id.tvVehiculo);
        TextView tvPrecio = view.findViewById(R.id.tvPrecio);
        TextView tvFInicial = view.findViewById(R.id.tvFInicial);
        TextView tvFFinal = view.findViewById(R.id.tvFFinal);
        Button btnAceptar = view.findViewById(R.id.btnAceptar);
        Button btnCancelar = view.findViewById(R.id.btnCancelar);

        Call<Conductor> conductorCall = ApiUtils.getAPIService().findConductor(reservas.getIdConductor());
        conductorCall.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    tvConductor.setText(response.body().getNombre());
                    tvVehiculo.setText(response.body().getTipoVehiculo());
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {

            }
        });

        tvPrecio.setText(String.valueOf(reservas.getPrecio()));
        tvFInicial.setText(reservas.getFechaInicio());
        tvFFinal.setText(reservas.getFechaFinal());

        btnAceptar.setOnClickListener(v -> {
            reservas.setEstado("Aceptado");
            Call<Reservacion> reservacionCall = ApiUtils.getAPIService().uptadeReserva(reservas);
            reservacionCall.enqueue(new Callback<Reservacion>() {
                @Override
                public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                    Log.d("Botones","Aceptado la reserva");
                }

                @Override
                public void onFailure(Call<Reservacion> call, Throwable t) {
                    Log.d("Botones","Error boton");
                }
            });
        });

        btnCancelar.setOnClickListener(v -> {
            reservas.setEstado("Cancelado");
            Call<Reservacion> reservacionCall = ApiUtils.getAPIService().uptadeReserva(reservas);
            reservacionCall.enqueue(new Callback<Reservacion>() {
                @Override
                public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                    Log.d("Botones","Cancelado la reserva");
                }

                @Override
                public void onFailure(Call<Reservacion> call, Throwable t) {
                    Log.d("Botones","Error boton");
                }
            });
        });
    }
}
