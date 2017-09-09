package com.memory_athlete.memoryassistant.disciplines;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import static com.memory_athlete.memoryassistant.R.raw.first;

public class Names extends Disciplines {

    private ArrayList<String> mFirstName = new ArrayList<>();
    private ArrayList<String> mLastName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        (new DictionaryAsyncTask()).execute();
        Log.i(LOG_TAG, "Activity Created");
    }

    private void createDictionary() {
        BufferedReader dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(first)));
            String first;
            while ((first = dict.readLine()) != null) {
                mFirstName.add(first.substring(0, 1) + first.substring(1).toLowerCase());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (dict != null) dict.close();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (dict != null) dict.close();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
        }
    }

    @Override
    protected String background() {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "doInBackground() entered");

        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(1); i++) {
            n = rand.nextInt(mFirstName.size());
            stringBuilder.append(mFirstName.get(n)).append(" ");
            n = rand.nextInt(mLastName.size());
            stringBuilder.append(mLastName.get(n)).append("\n");
            if((1+i)%20 == 0) stringBuilder.append("\n");
            if (a.get(2) == 0) break;
        }
        return stringBuilder.toString();
    }

    private class DictionaryAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setContentView(R.layout.loading);
//            (findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... a) {
            createDictionary();
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //(findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            setContentView(R.layout.activity_disciplines);
            ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.nm));
            levelSpinner();
            setButtons();
            a.add(0);
            a.add(0);
            a.add(0);
            a.add(0);
            Names.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

}