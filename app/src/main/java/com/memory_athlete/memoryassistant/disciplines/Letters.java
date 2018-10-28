package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.util.Objects;
import java.util.Random;

import timber.log.Timber;

public class Letters extends DisciplineFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values))
                .setHint(getString(R.string.enter) + " " + getString(R.string.st));
        Timber.v("Activity Created");
        return rootView;
    }

    @Override
    protected String backgroundString() {
        StringBuilder stringBuilder = new StringBuilder("");
        Random rand = new Random(), letterCaseRand = new Random();
        int letterCase = Integer.parseInt(Objects.requireNonNull(
                sharedPreferences.getString(getString(R.string.letter_case), "0")));
        int letterA = letterCase == 0 ? 97 : 65;
        int mixed = letterCase == 2 ? 1 : 0;

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < a.get(NO_OF_VALUES)/a.get(GROUP_SIZE); i++) {
            for (int j = 0; j < a.get(GROUP_SIZE); j++) {
                char c = (char) (rand.nextInt(26) + letterA +
                        (letterCaseRand.nextInt(2) * 32 * mixed));
                Timber.v("value of c = " + c);
                if (c != 'm' && c != 'w' && c != 'M' && c!= 'W') s.append(" ");
                if (c == 'i' || c == 'j' || c == 'l' || c == 't' || c=='f' || c=='I') s.append(" ");
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