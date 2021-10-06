package com.example.myapplication.ui.dialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    EditText etFecha, etFechaMedia, etFechaEstadia;

    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        etFecha = getActivity().findViewById(R.id.etFecha);
        etFechaMedia = getActivity().findViewById(R.id.etFechaMedia);
        etFechaEstadia = getActivity().findViewById(R.id.etFechaEstadia);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis() - 1000);
        return datePickerDialog;
    }

    @SuppressLint("SetTextI18n")
    public void onDateSet(DatePicker view, int year, int month, int day) {
        FragmentManager fragmanager = getActivity().getSupportFragmentManager();

        if(fragmanager.findFragmentByTag("dpFecha") != null){
            etFecha.setText(day + "/" + (month+1) + "/" + year);
        }

        if(fragmanager.findFragmentByTag("dpFechaMedia") != null) {
            //Asumiendo que tu textview para mostrar la fecha inicial es fromDate
            etFechaMedia.setText(day + "/" + (month+1) + "/" + year);

        }
        if(fragmanager.findFragmentByTag("dpFechaEstadia") != null){
            etFechaEstadia.setText(day + "/" + (month+1) + "/" + year);
        }

        Toast.makeText(getActivity(), "Fecha elegida " + day + "/" + (month+1) + "/" + year, Toast.LENGTH_SHORT).show();

    }

}
