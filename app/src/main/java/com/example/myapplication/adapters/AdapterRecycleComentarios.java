package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ui.models.Resena;

import java.util.ArrayList;

public class AdapterRecycleComentarios extends RecyclerView.Adapter<AdapterRecycleComentarios.ViewHolder> {
    private final ArrayList<Resena> localDataSet;

    public AdapterRecycleComentarios(ArrayList<Resena> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resena_list_adapter, null, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Resena resena = localDataSet.get(position);
        viewHolder.asignarDatos(resena,position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNumero,tvUsuarioNombre,tvComentario;
        private final RatingBar rbValoracion;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            tvNumero = view.findViewById(R.id.tvNumeroComentario);
            tvUsuarioNombre = view.findViewById(R.id.tvResenaNombre);
            tvComentario = view.findViewById(R.id.tvComentario);
            rbValoracion = view.findViewById(R.id.rbResena);
        }

        public void asignarDatos(Resena resena,int position){
            tvNumero.setText(String.valueOf(position+1));
            tvUsuarioNombre.setText(resena.getUsuario());
            tvComentario.setText(resena.getTexto());
            rbValoracion.setRating(resena.getValoracion());
        }

        public TextView getNumeroView() {
            return tvNumero;
        }

        public TextView getUsuarioView() {
            return tvUsuarioNombre;
        }

        public TextView getComentarioView() {
            return tvComentario;
        }

        public RatingBar getValoracionView() {
            return rbValoracion;
        }
    }

}
