package com.example.myapplication.ui.menuReservas;

import android.content.Context;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

public class MenuReservaFragment extends Fragment implements View.OnClickListener {
    private MainActivity activity;
    private Button btnHora, btnMedia, btnEstadia;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        btnHora = root.findViewById(R.id.btnHora);
        btnMedia = root.findViewById(R.id.btnMedia);
        btnEstadia = root.findViewById(R.id.btnEstadia);
        activity.setDrawer_locker();
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        btnHora.setOnClickListener(this);
        btnMedia.setOnClickListener(this);
        btnEstadia.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==btnHora){
            Navigation.findNavController(v).navigate(R.id.action_menuReservaFragment_to_horaFragment);
        }

        if(v==btnMedia){
            Navigation.findNavController(v).navigate(R.id.action_menuReservaFragment_to_mediaFragment);
        }

        if(v==btnEstadia){
            Navigation.findNavController(v).navigate(R.id.action_menuReservaFragment_to_estadiaFragment);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity.setDrawer_unlocker();
    }
}
