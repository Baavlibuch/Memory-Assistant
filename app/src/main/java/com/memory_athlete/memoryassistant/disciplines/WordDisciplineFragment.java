package com.memory_athlete.memoryassistant.disciplines;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.memory_athlete.memoryassistant.R;

public abstract class WordDisciplineFragment extends DisciplineFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        (new DictionaryAsyncTask()).execute();
        hasGroup = false;
        return rootView;
    }

    //Read files and make a list of names
    abstract protected void createDictionary();

    @Override
    protected void numbersVisibility(int visibility) {
        rootView.findViewById(R.id.practice_list_view).setVisibility(visibility);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void generateRandom() {
        new GenerateRandomArrayListAsyncTask().execute(a);
    }

    // fixing this leak would require removal of inheritance
    @SuppressLint("StaticFieldLeak")
    private class DictionaryAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... a) {
            createDictionary();
            return "";
        }
    }
}
