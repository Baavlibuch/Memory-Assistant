package com.memory_athlete.memoryassistant.disciplines;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.memory_athlete.memoryassistant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

import static com.memory_athlete.memoryassistant.R.raw.first;

public class Names extends DisciplineFragment {

    private ArrayList<String> mFirstName = new ArrayList<>();
    private ArrayList<String> mLastName = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        (new DictionaryAsyncTask()).execute();
        Timber.v("Activity Created");
        return rootView;
    }

    //Read files and make a list of names
    private void createDictionary() {
        BufferedReader dict = null;                 //Reads a line from the file

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(first)));
            String first;
            while ((first = dict.readLine()) != null) {
                mFirstName.add(first.substring(0, 1) + first.substring(1).toLowerCase());//All were in caps
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
        String last;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.last)));
            while ((last = dict.readLine()) != null) {
                // if(last.length()>1)
                mLastName.add(last.substring(0, 1) + last.substring(1).toLowerCase());
            }

        } catch (IOException e) {
            e.printStackTrace();
            // } catch (StringIndexOutOfBoundsException e){
            //   Log.e(LOG_TAG, "error" + mLastName.size());
        }

        try {
            dict.close();
        } catch (IOException e) {
            Timber.e("File not closed");
        }
    }

    @Override
    protected String background() {
        Timber.v("doInBackground() entered");

        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(NO_OF_VALUES);) {
            n = rand.nextInt(mFirstName.size());
            stringBuilder.append(mFirstName.get(n)).append(" ");
            n = rand.nextInt(mLastName.size());
            stringBuilder.append(mLastName.get(n)).append("\n");
            if ((++i) % 20 == 0) stringBuilder.append("\n");        //empty line between 20 names
            if (a.get(RUNNING) == FALSE) break;
        }
        return stringBuilder.toString();
    }

    private class DictionaryAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //setContentView(R.layout.loading);
//            (findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... a) {
            createDictionary();
            return "";
        }
    }
}