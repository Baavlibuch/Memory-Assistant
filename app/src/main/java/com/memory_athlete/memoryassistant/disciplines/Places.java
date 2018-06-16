package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

public class Places extends WordDisciplineFragment {
    private ArrayList<String> mPlace = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + getString(R.string.places_small));
        return rootView;
    }

    @Override
    public boolean reset() {
        rootView.findViewById(R.id.group).setVisibility(View.GONE);
        return super.reset();
    }

    @Override
    protected ArrayList backgroundArray() {
        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        ArrayList<String> arrayList = new ArrayList<>();
        int n;

        for (int i = 0; i < a.get(NO_OF_VALUES); ) {
            n = rand.nextInt(mPlace.size());
            stringBuilder.append(mPlace.get(n)).append(" \n");
            if ((++i) % 20 == 0) {
                arrayList.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
            if (a.get(RUNNING) == FALSE) break;
        }
        arrayList.add(stringBuilder.toString());
        return arrayList;
    }

    //Read files and make a list of places
    @Override
    protected void createDictionary() {
        int[] files = {R.raw.cities, R.raw.countries, R.raw.waterfalls, R.raw.mountains,
                R.raw.lakes, R.raw.islands, R.raw.heritage, R.raw.rivers};
        for (int fileID : files) {
            BufferedReader dict = null;
            try {
                dict = new BufferedReader(new InputStreamReader(
                        getResources().openRawResource(fileID)));
                String place;
                while ((place = dict.readLine()) != null) {
                    mPlace.add(place);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                dict.close();
            } catch (IOException e) {
                Timber.e("File not closed");
            }
        }
    }
}