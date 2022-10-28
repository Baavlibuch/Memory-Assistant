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
    //    private TimePicker picker=null;
    //    private String time;

    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }


//    @Override
//    protected View onCreateDialogView() {
//        picker = new TimePicker(getContext());
//
//        return (picker);
//    }
//
//    @Override
//    protected void onBindDialogView(View v) {
//        super.onBindDialogView(v);
//
//        picker.setCurrentHour(lastHour);
//        picker.setCurrentMinute(lastMinute);
//    }
//
//    @Override
//    protected void onDialogClosed(boolean positiveResult) {
//        super.onDialogClosed(positiveResult);
//
//        if (positiveResult) {
//            lastHour = picker.getCurrentHour();
//            lastMinute = picker.getCurrentMinute();
//
//            String time = String.valueOf(lastHour) + ":" + String.valueOf(lastMinute);
//
//            if (callChangeListener(time)) {
//                persistString(time);
//            }
//        }
//    }

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








