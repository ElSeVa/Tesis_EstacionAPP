package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.myapplication.ui.models.Item_Promocion;
import com.example.myapplication.ui.models.Item_Reservacion;
import com.example.myapplication.ui.models.Resena;

import java.util.ArrayList;

public class AdapterBasePromo extends BaseAdapter {

    private Context context;
    private ArrayList<Item_Promocion> promocions;

    public AdapterBasePromo(Context context, ArrayList<Item_Promocion> promocions){
        this.promocions = promocions;
        this.context = context;
    }

    @Override
    public int getCount() {
        return promocions.size();
    }

    @Override
    public Object getItem(int position) {
        return promocions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // ¿Existe el view actual?
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_list_promotion, parent, false);
        }

        Item_Promocion currentName  = (Item_Promocion) getItem(position);

        // Referenciamos el elemento a modificar y lo rellenamos
        agregarTextView(convertView, currentName, position);

        //Devolvemos la vista inflada
        return convertView;
    }

    @SuppressLint("SetTextI18n")
    private void agregarTextView(View view, Item_Promocion promocions, int position){
        TextView tvNumero = view.findViewById(R.id.tvP_Numero);
        TextView tvP_Nombre = view.findViewById(R.id.tvP_Nombre);
        TextView tvP_Vehiculo = view.findViewById(R.id.tvP_Vehiculo);
        TextView tvP_Frecuencia = view.findViewById(R.id.tvP_Frecuencia);

        tvNumero.setText(String.valueOf(position+1));
        tvP_Nombre.setText(promocions.getNombre());
        tvP_Vehiculo.setText(promocions.getTipoVehiculo());
        tvP_Frecuencia.setText(promocions.getFrecuencia().toString());
    }
}
