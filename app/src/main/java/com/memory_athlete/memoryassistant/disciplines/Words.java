package com.memory_athlete.memoryassistant.disciplines;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;


public class Words extends DisciplineFragment {
    private ArrayList<String> mDictionary = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        new DictionaryAsyncTask().execute();
        Timber.v("Activity Created");
        return rootView;
    }

    @Override
    protected String background() {
        Timber.v("doInBackground() entered");

        StringBuilder stringBuilder = new StringBuilder();
        //String textString = "";
        Random rand = new Random();
        short n;

        for (int i = 0; i < a.get(1);) {
            n = (short) rand.nextInt(mDictionary.size());
            stringBuilder.append(mDictionary.get(n)).append("\n");
            if ((++i) % 20 == 0) stringBuilder.append("\n");
            if (a.get(2) == 0) break;
        }
        return stringBuilder.toString();
    }

    private void createDictionary() {
        BufferedReader dict = null;

        try {
            dict = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.words)));
            String word;
            while ((word = dict.readLine()) != null) {
                mDictionary.add(word);
           //     Log.v(LOG_TAG, word);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dict.close(); //had if (dict!=null)
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //(rootView.findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            //setContentView(R.layout.activity_disciplines);
            levelSpinner();

            setButtons();
            ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.words_small));
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

}