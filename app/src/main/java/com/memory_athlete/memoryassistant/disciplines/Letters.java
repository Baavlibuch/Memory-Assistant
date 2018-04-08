package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.preference.PreferenceManager;
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
    protected String backgroundString() {
        StringBuilder stringBuilder = new StringBuilder("");
        Random rand = new Random();
        int letterA = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(getString(R.string.double_back_to_exit), false)
                ? 65 : 97;

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < a.get(NO_OF_VALUES)/a.get(GROUP_SIZE); i++) {
            for (int j = 0; j < a.get(GROUP_SIZE); j++) {
                char c = (char) (rand.nextInt(26) + letterA);
                Timber.v("value of c = " + c);
                if (c != 'm' && c != 'w') s.append(" ");
                if (c == 'i' || c == 'j' || c == 'l' || c == 't' || c=='f') s.append(" ");
                stringBuilder.append(String.valueOf(c));
                if (a.get(RUNNING) == FALSE) break;
            }
            stringBuilder.append(s).append(getString(R.string.tab)).append("   ");
            s = new StringBuilder();
        }
        Timber.v(stringBuilder.toString());
        return stringBuilder.toString();
    }
}