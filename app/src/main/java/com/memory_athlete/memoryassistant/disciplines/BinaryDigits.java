package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.util.Random;

import static com.memory_athlete.memoryassistant.R.string.tab;

public class BinaryDigits extends DisciplineFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        return rootView;
    }

    @Override
    protected String background() {
        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(NO_OF_VALUES) / a.get(GROUP_SIZE); i++) {
            for (int j = 0; j < a.get(GROUP_SIZE); j++) {
                n = rand.nextInt(2);
                stringBuilder.append(n);
                //textString += n;
                //stringBuilder.append(" ");
            }
            stringBuilder.append(getString(tab)).append("   ");          //tab is the delimiter used in recall
            if (a.get(RUNNING) == FALSE) break;
        }
        return stringBuilder.toString();
    }
}
