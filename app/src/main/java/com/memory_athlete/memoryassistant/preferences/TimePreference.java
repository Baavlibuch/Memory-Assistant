package com.memory_athlete.memoryassistant.preferences;

/**
 * Created by Manik on 26/08/17.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;


public class TimePreference extends DialogPreference {
    int lastHour = 0;
    int lastMinute = 0;

    public static int getHour(String time) {
        String[] pieces = time.split(":");

        int hr = 0;
        try{
            hr = Integer.parseInt(pieces[0]);
        } catch(NumberFormatException ex){ // handle your exception

        }

        return (hr);
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        int min = 0;
        try{
            min = Integer.parseInt(pieces[0]);
        } catch(NumberFormatException ex){ // handle your exception

        }

        return (min);

    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
    }


    public void persistStringValue(String value)
    {
        persistString(value);
    }

}