package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.ui.models.Item_Reservacion;

import java.util.ArrayList;

public class AdapterBaseReservas extends BaseAdapter {
    private final Context context;
    private final ArrayList<Item_Reservacion> reservas;

    public AdapterBaseReservas(Context context, ArrayList<Item_Reservacion> reservas){
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        //Inflamos la vista con nuestro propio layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_list_adapter, viewGroup, false);
        }

        Item_Reservacion currentName  = (Item_Reservacion) getItem(position);

        // Referenciamos el elemento a modificar y lo rellenamos
        agregarTextView(convertView, currentName, position);

        //Devolvemos la vista inflada
        return convertView;
    }

    private void agregarTextView(View view, Item_Reservacion reservacion, int position){
        TextView tvNumero = view.findViewById(R.id.tvNumero);
        TextView tvGarageNombre = view.findViewById(R.id.tvGarageNombre);
        TextView tvFechaInicial = view.findViewById(R.id.tvFechaInicial);
        TextView tvFechaFinal = view.findViewById(R.id.tvFechaFinal);
        TextView tvEstado = view.findViewById(R.id.tvEstado);

        tvNumero.setText(String.valueOf(position+1));
        tvGarageNombre.setText(reservacion.getNombre());
        tvFechaInicial.setText(reservacion.getFechaInicio());
        tvFechaFinal.setText(reservacion.getFechaFinal());
        tvEstado.setText(reservacion.getEstado());
    }
}

