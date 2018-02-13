package com.memory_athlete.memoryassistant.disciplines;

import android.os.AsyncTask;
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

public class Places extends DisciplineFragment {

    private ArrayList<String> mPlace = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //setTitle(getString(R.string.g));

        //makeSpinner();
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + getString(R.string.places_small));

        (new DictionaryAsyncTask()).execute();
        Timber.v("Activity Created");
        return rootView;
    }

    @Override
    public boolean reset() {
        rootView.findViewById(R.id.group).setVisibility(View.GONE);
        return super.reset();
    }

    @Override
    protected String background() {
        Timber.v("doInBackground() entered");

        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(NO_OF_VALUES); i++) {
            n = rand.nextInt(mPlace.size());
            stringBuilder.append(mPlace.get(n)).append(" \n");
            if((i+1)%20 == 0) stringBuilder.append("\n");
            if (a.get(RUNNING) == FALSE) break;
        }
        return stringBuilder.toString();
    }

    //Read files and make a list of places
    private void createDictionary() {
        BufferedReader dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.cities)));
            String city;
            while ((city = dict.readLine()) != null) {
                mPlace.add(city);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }

        dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.countries)));
            String country;
            while ((country = dict.readLine()) != null) {
                mPlace.add(country);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }

        dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.waterfalls)));
            String falls;
            while ((falls = dict.readLine()) != null) {
                mPlace.add(falls);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }

        dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.mountains)));
            String mountain;
            while ((mountain = dict.readLine()) != null) {
                mPlace.add(mountain);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }

        dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.lakes)));
            String lake;
            while ((lake = dict.readLine()) != null) {
                mPlace.add(lake);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }

        dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.islands)));
            String island;
            while ((island = dict.readLine()) != null) {
                mPlace.add(island);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }

        dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.heritage)));
            String heritage;
            while ((heritage = dict.readLine()) != null) {
                mPlace.add(heritage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }

        dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.rivers)));
            String rivers;
            while ((rivers = dict.readLine()) != null) {
                mPlace.add(rivers);
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

    private class DictionaryAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setContentView(R.layout.loading);
//            (rootView.findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... a) {
            createDictionary();
            return "";
        }
    }
}