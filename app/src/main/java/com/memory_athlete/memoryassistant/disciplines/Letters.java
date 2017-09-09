package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.util.Log;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.util.Random;

public class Letters extends Disciplines {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Activity Created");
    }

    @Override
    protected String background() {
        StringBuilder stringBuilder = new StringBuilder("");
        Random rand = new Random();

        String s = "";
        for (int i = 0; i < a.get(1); i++) {
            for (int j = 0; j < a.get(0); j++) {
                char c = (char) (rand.nextInt(26) + 97);
                Log.v(LOG_TAG, "value of c = " + c);
                if (c != 'm' && c != 'w') s += " ";
                if (c == 'i' || c == 'j' || c == 'l' || c == 't' || c=='f') s += " ";
                stringBuilder.append(String.valueOf(c));
                if (a.get(2) == 0) break;
            }
            stringBuilder.append(s).append(getString(R.string.tab)).append("   ");
            s="";
        }
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, stringBuilder.toString());
        return stringBuilder.toString();
    }
}