package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.util.Random;

import timber.log.Timber;

public class Letters extends Disciplines {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        Timber.v("Activity Created");
    }

    @Override
    protected String background() {
        StringBuilder stringBuilder = new StringBuilder("");
        Random rand = new Random();

        String s = "";
        for (int i = 0; i < a.get(1); i++) {
            for (int j = 0; j < a.get(0); j++) {
                char c = (char) (rand.nextInt(26) + 97);
                Timber.v("value of c = " + c);
                if (c != 'm' && c != 'w') s += " ";
                if (c == 'i' || c == 'j' || c == 'l' || c == 't' || c=='f') s += " ";
                stringBuilder.append(String.valueOf(c));
                if (a.get(2) == 0) break;
            }
            stringBuilder.append(s).append(getString(R.string.tab)).append("   ");
            s="";
        }
        Timber.v(stringBuilder.toString());
        return stringBuilder.toString();
    }
}