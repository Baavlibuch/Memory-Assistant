package com.memory_athlete.memoryassistant.reminders;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Manik on 25/08/17.
 */


public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

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

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        SharedPreferences prefer = getActivity().getApplicationContext().getSharedPreferences("MyPREFERENCES", 0);
        SharedPreferences.Editor preferencesEditor = prefer.edit();
        preferencesEditor.putInt("Hour", hourOfDay);
        preferencesEditor.putInt("Min", minute);
        if (!preferencesEditor.commit())
            Toast.makeText(getActivity(), "Try again", Toast.LENGTH_LONG).show();

    }

}
