package com.memory_athlete.memoryassistant.preferences;

import android.content.Context;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

public class TimeDialog extends PreferenceDialogFragmentCompat implements DialogPreference.TargetFragment
{
     TimePicker picker = null;

    @Override
    protected View onCreateDialogView(Context context)
    {
        picker = new TimePicker(context);
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v)
    {
        super.onBindDialogView(v);
        TimePreference pref = (TimePreference) getPreference();
        picker.setCurrentHour(pref.lastHour);
        picker.setCurrentMinute(pref.lastMinute);
    }

    @Override
    public void onDialogClosed(boolean positiveResult)
    {
        if (positiveResult)
        {
            TimePreference pref = (TimePreference) getPreference();
            pref.lastHour = picker.getCurrentHour();
            pref.lastMinute = picker.getCurrentMinute();

            String time = String.valueOf(pref.lastHour) + ":" + String.valueOf(pref.lastMinute);;
            if (pref.callChangeListener(time)) pref.persistStringValue(time);
        }


    }

    @Nullable
    @Override
    public <T extends Preference> T findPreference(@NonNull CharSequence key) {
        return null;
    }

//    @Override
//    public Preference findPreference(@NonNull CharSequence charSequence)
//    {
//        return getPreference();
//    }

}













