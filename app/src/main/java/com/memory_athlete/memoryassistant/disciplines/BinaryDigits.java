package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.util.Random;

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

        for (int i = 0; i < a.get(1) / a.get(0); i++) {
            for (int j = 0; j < a.get(0); j++) {
                n = rand.nextInt(2);
                stringBuilder.append(n);
                //textString += n;
                //stringBuilder.append(" ");
            }
            stringBuilder.append(getString(R.string.tab)).append("   ");
            if (a.get(2) == 0) break;
        }
        return stringBuilder.toString();
    }

}
