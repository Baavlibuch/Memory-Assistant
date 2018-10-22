package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.recall.RecallComplex;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Faces extends ComplexDisciplineFragment {
    String[] faces;
    ArrayList<Integer> randomList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values))
                .setHint(getString(R.string.enter) + " " + getString(R.string.images));

        mRecallClass = RecallComplex.class;
        hasSpeech = false;
        rootView.findViewById(R.id.speech_check_box).setVisibility(View.GONE);
        return rootView;
    }

    @Override
    protected void createDictionary() {
        File obbDir = activity.getObbDir();
        File f = new File(obbDir.getPath() + File.separator + "faces");
        faces = f.list();
    }

    @Override
    protected ArrayList backgroundArray() {
        ArrayList<Integer> indexList = new ArrayList<>(a.get(NO_OF_VALUES));
        ArrayList<Integer> arrayList = new ArrayList<>(a.get(NO_OF_VALUES));
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(NO_OF_VALUES); i++) indexList.add(i);

        for (int i = 0; i < (a.get(NO_OF_VALUES)); i++) {
            n = rand.nextInt(indexList.size());
            arrayList.add(indexList.get(n));
            indexList.remove(n);
            if (a.get(RUNNING) == FALSE) break;
        }

        return arrayList;
    }
}
