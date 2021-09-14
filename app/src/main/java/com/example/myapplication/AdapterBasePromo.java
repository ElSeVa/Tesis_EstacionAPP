package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.ui.models.Item_Promocion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdapterBasePromo extends BaseExpandableListAdapter {

    private final Context context;
    private final List<Item_Promocion> listGroupPromotion;
    private final HashMap<Item_Promocion, List<Item_Promocion>> listItemPromotion;

    public AdapterBasePromo(Context context, List<Item_Promocion> listGroupPromotion,HashMap<Item_Promocion, List<Item_Promocion>> listItemPromotion){
        this.listGroupPromotion = listGroupPromotion;
        this.listItemPromotion = listItemPromotion;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return listGroupPromotion.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listItemPromotion.get(this.listGroupPromotion.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listGroupPromotion.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listItemPromotion.get(this.listGroupPromotion.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Item_Promocion group = (Item_Promocion) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_promotion,null);
        }
        TextView tvList_Parent = convertView.findViewById(R.id.list_parent);
        TextView tvList_Vehiculo = convertView.findViewById(R.id.list_vehiculo);
        tvList_Parent.setText(group.getNombre());
        tvList_Vehiculo.setText(group.getTipoVehiculo());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Item_Promocion child = (Item_Promocion) getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_promotion,null);
        }

        TextView tvFrecuencia = convertView.findViewById(R.id.list_frecuencia);
        tvFrecuencia.setText(child.getFrecuencia().toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
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
