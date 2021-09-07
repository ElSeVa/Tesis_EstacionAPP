package com.example.myapplication.ui.slideshow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.ui.metodo.EstadiaActivity;
import com.example.myapplication.ui.metodo.HoraActivity;
import com.example.myapplication.ui.metodo.MapMuestra;
import com.example.myapplication.ui.metodo.MediaActivity;

public class SlideshowFragment extends AppCompatActivity implements View.OnClickListener {


    private Button btnHora, btnMedia, btnEstadia;
    private Intent horaAct, mediaAct, estadiaAct;
    final Context context = SlideshowFragment.this;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_slideshow);
        btnHora = findViewById(R.id.btnHora);
        btnMedia = findViewById(R.id.btnMedia);
        btnEstadia = findViewById(R.id.btnEstadia);

        btnHora.setOnClickListener(this);
        btnMedia.setOnClickListener(this);
        btnEstadia.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v==btnHora){
            horaAct = new Intent(context, HoraActivity.class);
            startActivity(horaAct);
        }

        if(v==btnMedia){
            mediaAct = new Intent(context, MediaActivity.class);
            startActivity(mediaAct);
        }

        if(v==btnEstadia){
            estadiaAct = new Intent(context, EstadiaActivity.class);
            startActivity(estadiaAct);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}