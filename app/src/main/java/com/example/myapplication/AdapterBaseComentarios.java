package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.ui.models.Resena;

import java.util.ArrayList;

public class AdapterBaseComentarios extends BaseAdapter {

    private Context context;
    private ArrayList<Resena> comentarios;

    public AdapterBaseComentarios(Context context, ArrayList<Resena> comentarios){
        this.comentarios = comentarios;
        this.context = context;
    }

    @Override
    public int getCount() {
        return comentarios.size();
    }

    @Override
    public Object getItem(int position) {
        return comentarios.get(position);
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
            convertView = inflater.inflate(R.layout.resena_list_adapter, parent, false);
        }

        Resena currentName = (Resena) getItem(position);

        agregarTextView(convertView,currentName,position);

        return convertView;
    }

    private void agregarTextView(View view, Resena comentarios, int position){
        TextView tvNumero = view.findViewById(R.id.tvNumeroComentario);
        TextView tvUsuarioNombre = view.findViewById(R.id.tvResenaNombre);
        TextView tvComentario = view.findViewById(R.id.tvComentario);
        RatingBar rbValoracion = view.findViewById(R.id.rbResena);

        tvNumero.setText(String.valueOf(position+1));
        tvUsuarioNombre.setText(comentarios.getUsuario());
        tvComentario.setText(comentarios.getTexto());
        rbValoracion.setRating(comentarios.getValoracion());
    }
}
