package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.util.Random;

import timber.log.Timber;

public class Letters extends DisciplineFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        Timber.v("Activity Created");
        return rootView;
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