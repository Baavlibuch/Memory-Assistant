package com.memory_athlete.memoryassistant.disciplines;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class Places extends Disciplines {

    private ArrayList<String> mPlace = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTitle(getString(R.string.g));

        //makeSpinner();
        (new DictionaryAsyncTask()).execute();
        if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Activity Created");
    }

    @Override
    protected void reset() {
        super.reset();
        findViewById(R.id.group).setVisibility(View.GONE);
    }

    @Override
    protected String background() {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "doInBackground() entered");

        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(1); i++) {
            n = rand.nextInt(mPlace.size());
            stringBuilder.append(mPlace.get(n)).append(" \n");
            if((i+1)%20 == 0) stringBuilder.append("\n");
            if (a.get(2) == 0) break;
        }
        return stringBuilder.toString();
    }

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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
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
            if (BuildConfig.DEBUG) Log.e(LOG_TAG, "File not closed");
        }
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
            ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + getString(R.string.places));
            levelSpinner();

            setButtons();
            a.add(0);
            a.add(0);
            a.add(0);
            a.add(0);
            Places.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }
}
/*
    void Start() {
        Log.i(LOG_TAG, "Start entered");
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                    (getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Couldn't hide keypad ", e);
        }
        ((TextView) findViewById(clock_text)).setText("");

        if (((RadioButton) findViewById(R.id.timer)).isChecked()) {
            if (((EditText) (findViewById(R.id.clock_edit)).findViewById(R.id.min))
                    .getText().toString().length() > 0 &&
                    ((EditText) (findViewById(R.id.clock_edit))
                            .findViewById(R.id.sec)).getText().toString().length() > 0) {
                startCommon();
                timer();
                isTimerRunning = true;
                (findViewById(R.id.clock_edit)).setVisibility(View.GONE);
                (findViewById(clock_text)).setVisibility(View.VISIBLE);
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Please enter the duration",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (((RadioButton) findViewById(R.id.sw)).isChecked()) {
            startCommon();
            (findViewById(R.id.chronometer)).setVisibility(View.VISIBLE);
            ((Chronometer) findViewById(R.id.chronometer)).setBase(SystemClock.
                    elapsedRealtime());
            ((Chronometer) findViewById(R.id.chronometer)).start();
            if (!(((RadioButton) findViewById(R.id.sw)).isChecked() &&
                    ((RadioButton) findViewById(R.id.timer)).isChecked())) {
                findViewById(R.id.stop).setVisibility(View.GONE);
                findViewById(R.id.reset).setVisibility(View.VISIBLE);
            }
            return;
        }

        startCommon();
        (findViewById(R.id.numbers)).setVisibility(View.VISIBLE);
        Log.i(LOG_TAG, "Start complete");
    }
}

*/