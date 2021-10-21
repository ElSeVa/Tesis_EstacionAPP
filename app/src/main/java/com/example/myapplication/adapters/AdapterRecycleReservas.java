package com.example.myapplication.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Conductor;
import com.example.myapplication.ui.models.Resena;
import com.example.myapplication.ui.models.Reservacion;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRecycleReservas extends RecyclerView.Adapter<AdapterRecycleReservas.ViewHolder>{
    private final ArrayList<Reservacion> localDataSet;

    public AdapterRecycleReservas(ArrayList<Reservacion> dataSet) {
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservas_adapter, null, false);
        return new AdapterRecycleReservas.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Reservacion reservas = localDataSet.get(position);
        Call<Conductor> conductorCall = ApiUtils.getAPIService().findConductor(reservas.getIdConductor());
        conductorCall.enqueue(new Callback<Conductor>() {
            @Override
            public void onResponse(Call<Conductor> call, Response<Conductor> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    viewHolder.getTvConductor().setText(response.body().getNombre());
                    viewHolder.getTvVehiculo().setText(response.body().getTipoVehiculo());
                }
            }

            @Override
            public void onFailure(Call<Conductor> call, Throwable t) {

            }
        });

        viewHolder.getTvPrecio().setText(String.valueOf(reservas.getPrecio()));
        viewHolder.getTvFInicial().setText(reservas.getFechaInicio());
        viewHolder.getTvFFinal().setText(reservas.getFechaFinal());

        viewHolder.getBtnAceptar().setOnClickListener(v -> {
            reservas.setEstado("Aceptado");
            Call<Reservacion> reservacionCall = ApiUtils.getAPIService().uptadeReserva(reservas);
            reservacionCall.enqueue(new Callback<Reservacion>() {
                @Override
                public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                    Log.d("Botones","Aceptado la reserva");
                    localDataSet.remove(position);
                    notifyItemRemoved(position);
                }

                @Override
                public void onFailure(Call<Reservacion> call, Throwable t) {
                    Log.d("Botones","Error boton");
                }
            });
        });

        viewHolder.getBtnCancelar().setOnClickListener(v -> {
            reservas.setEstado("Cancelado");
            Call<Reservacion> reservacionCall = ApiUtils.getAPIService().uptadeReserva(reservas);
            reservacionCall.enqueue(new Callback<Reservacion>() {
                @Override
                public void onResponse(Call<Reservacion> call, Response<Reservacion> response) {
                    Log.d("Botones","Cancelado la reserva");
                    localDataSet.remove(position);
                    notifyItemRemoved(position);
                }

                @Override
                public void onFailure(Call<Reservacion> call, Throwable t) {
                    Log.d("Botones","Error boton");
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvConductor, tvVehiculo,tvPrecio, tvFInicial,tvFFinal;
        private final Button btnAceptar, btnCancelar;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvConductor = view.findViewById(R.id.tvConductor);
            tvVehiculo = view.findViewById(R.id.tvVehiculo);
            tvPrecio = view.findViewById(R.id.tvPrecio);
            tvFInicial = view.findViewById(R.id.tvFInicial);
            tvFFinal = view.findViewById(R.id.tvFFinal);
            btnAceptar = view.findViewById(R.id.btnAceptar);
            btnCancelar = view.findViewById(R.id.btnCancelar);

        }

        public TextView getTvConductor() {
            return tvConductor;
        }

        public TextView getTvVehiculo() {
            return tvVehiculo;
        }

        public TextView getTvPrecio() {
            return tvPrecio;
        }

        public TextView getTvFInicial() {
            return tvFInicial;
        }

        public TextView getTvFFinal() {
            return tvFFinal;
        }

        public Button getBtnAceptar() {
            return btnAceptar;
        }

        public Button getBtnCancelar() {
            return btnCancelar;
        }
    }

}
