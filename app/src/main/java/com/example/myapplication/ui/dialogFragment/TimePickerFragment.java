package com.example.myapplication.ui.dialogFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    EditText etHora,etHoraF, etHoraMedia, etHoraEstadia;
    @Override
    public @NotNull Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        etHoraF = getActivity().findViewById(R.id.etHoraF);
        etHora = getActivity().findViewById(R.id.etHora);
        etHoraMedia = getActivity().findViewById(R.id.etHoraMedia);
        etHoraEstadia = getActivity().findViewById(R.id.etHoraEstadia);

        // Create a new instance of TimePickerDialog and return it

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        FragmentManager fragmanager = getActivity().getSupportFragmentManager();
        if(fragmanager.findFragmentByTag("tpHora") != null) {
            acomodandoText(hourOfDay, minute, etHora);
        }
        if(fragmanager.findFragmentByTag("tpHoraF") != null) {
            acomodandoText(hourOfDay, minute, etHoraF);
        }
        if(fragmanager.findFragmentByTag("tpHoraMedia") != null) {
            acomodandoText(hourOfDay, minute, etHoraMedia);
        }
        if(fragmanager.findFragmentByTag("tpHoraEstadia") != null) {
            acomodandoText(hourOfDay, minute, etHoraEstadia);
        }

        Toast.makeText(getActivity(),"Fecha elegida " + hourOfDay + ":" + minute  , Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void acomodandoText(int hourOfDay, int minute, EditText editText){
        if(hourOfDay<=9){
            if(minute <= 9){
                editText.setText("0"+hourOfDay + ":" +"0"+minute);
            }else{
                editText.setText("0"+hourOfDay + ":"+minute);
            }
        }else{
            if(minute <= 9){
                editText.setText(hourOfDay + ":" +"0"+minute);
            }else{
                editText.setText(hourOfDay + ":"+minute);
            }
        }
    }

}
