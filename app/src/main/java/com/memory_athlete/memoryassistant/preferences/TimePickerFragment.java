package com.memory_athlete.memoryassistant.preferences;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

/**
 * Created by Manik on 25/08/17.
 */


public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        SharedPreferences prefer = requireActivity().getApplicationContext().getSharedPreferences("MyPREFERENCES", 0);
        SharedPreferences.Editor preferencesEditor = prefer.edit();
        preferencesEditor.putInt("Hour", hourOfDay);
        preferencesEditor.putInt("Min", minute);


        if (!preferencesEditor.commit())
            Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();

    }

}
