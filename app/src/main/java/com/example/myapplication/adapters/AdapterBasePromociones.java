package com.example.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.ui.api.ApiUtils;
import com.example.myapplication.ui.models.Item_Reservacion;
import com.example.myapplication.ui.models.Promo;
import com.example.myapplication.ui.models.Promociones;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterBasePromociones extends BaseAdapter {

    private final Context context;
    ArrayList<Promociones> arrayPromociones;

    public AdapterBasePromociones(Context context, ArrayList<Promociones> arrayPromociones) {
        this.context = context;
        this.arrayPromociones = arrayPromociones;
    }

    @Override
    public int getCount() {
        return arrayPromociones.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayPromociones.get(position);
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
            convertView = inflater.inflate(R.layout.item_list_promotion, viewGroup, false);
        }

        Promociones currentName  = (Promociones) getItem(position);

        Call<Promo> callPromo = ApiUtils.getAPIService().obtenerPromoId(currentName.getIdPromo());

        // Referenciamos el elemento a modificar y lo rellenamos
        agregarTextView(convertView, callPromo, position);

        return convertView;
    }

    private void agregarTextView(View convertView, Call<Promo> call, int position){
        TextView tvNumero = convertView.findViewById(R.id.tvP_Numero);
        TextView tvTipoPromo = convertView.findViewById(R.id.tvTipoPromo);
        TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
        tvNumero.setText(String.valueOf(position+1));
        call.enqueue(new Callback<Promo>() {
            @Override
            public void onResponse(Call<Promo> call, Response<Promo> response) {
                if(response.isSuccessful() && response.body() != null){
                    tvTipoPromo.setText(response.body().getTipoPromo());
                    tvDescripcion.setText(response.body().getDescripcion());
                }
            }

            @Override
            public void onFailure(Call<Promo> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
