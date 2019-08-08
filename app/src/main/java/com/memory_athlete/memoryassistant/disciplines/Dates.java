package com.memory_athlete.memoryassistant.disciplines;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.recall.RecallComplex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Dates extends WordDisciplineFragment {
    private ArrayList<String> events = new ArrayList<>();
    private int startYear, endYear;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values))
                .setHint(getString(R.string.enter) + " " + getString(R.string.events));
        negativeOrDateCheckBox.setText(R.string.show_only_the_year);
        negativeOrDateCheckBox.setChecked(true);
        negativeOrDateCheckBox.setVisibility(View.VISIBLE);
        setDateSpinners();
        mRecallClass = RecallComplex.class;
        hasSpeech = false;
        rootView.findViewById(R.id.speech_check_box).setVisibility(View.GONE);
        Toast.makeText(getContext(), R.string.fake_dates, Toast.LENGTH_LONG).show();
        return rootView;
    }

    private void setDateSpinners() {
        ArrayList<String> arrayList = new ArrayList<>(), arrayList1 = new ArrayList<>();
        Spinner dateSpinner = rootView.findViewById(R.id.start_date);
        dateSpinner.setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            InputMethodManager im = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) Objects.requireNonNull(im).hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
            return false;
        });

        arrayList.add(getString(R.string.start_year));
        for (int i = 0; i < 2000; i += 500)
            arrayList.add(
                    String.format(Locale.getDefault(), "%04d", i));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                activity, android.R.layout.simple_spinner_item, arrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Can't recall why this is here but it is important
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        dateSpinner.setAdapter(dataAdapter);
        dateSpinner.setVisibility(View.VISIBLE);

        Spinner dateSpinner1 = rootView.findViewById(R.id.end_date);
        dateSpinner1.setOnTouchListener((view, motionEvent) -> {
            view.performClick();
            InputMethodManager im = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) Objects.requireNonNull(im).hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
            return false;
        });

        arrayList1.clear();
        arrayList1.add(getString(R.string.end_year));
        arrayList1.add("2099");
        for (int i = 2499; i <= 9999; i += 500)
            arrayList1.add(String.format(Locale.getDefault(), "%04d", i));
        dataAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item,
                arrayList1);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Can't recall why this is here but it is important
        dateSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        dateSpinner1.setAdapter(dataAdapter);
        dateSpinner1.setVisibility(View.VISIBLE);
    }

    @Override
    protected void preExecute() {
        super.preExecute();
        String string = ((Spinner) rootView.findViewById(R.id.start_date)).getSelectedItem()
                .toString();
        boolean standardRadio = ((RadioButton) rootView.findViewById(R.id.standard_radio))
                .isChecked();
        if (standardRadio || string.equals(getString(R.string.start_year))) startYear = 1000;
        else startYear = Integer.parseInt(string);

        string = ((Spinner) rootView.findViewById(R.id.end_date)).getSelectedItem().toString();
        if (standardRadio || string.equals(getString(R.string.end_year))) endYear = 2099;
        else endYear = Integer.parseInt(string);
    }

    @Override
    protected ArrayList backgroundArray() {
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        ArrayList<String> arrayList = new ArrayList<>();
        int n, year;

        ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < events.size(); i++) indexList.add(i);

        for (int i = 0; i < a.get(NO_OF_VALUES); ) {
            year = rand.nextInt(endYear - startYear) + startYear;
            stringBuilder.append(String.format(Locale.getDefault(), "%04d", year));
            n = (short) rand.nextInt(indexList.size());
            // Date
            if (!negativeOrDateCheckBox.isChecked()) {
                n = rand.nextInt(12);
                stringBuilder.append("/");
                if (n < 10) stringBuilder.append(0);
                stringBuilder.append(n).append("/");

                // generate date of month
                switch (n) {
                    case 0:
                    case 2:
                    case 4:
                    case 6:
                    case 7:
                    case 9:
                    case 11:
                        //month of 31 days
                        stringBuilder.append(rand.nextInt(32) + 1);
                        break;
                    case 3:
                    case 5:
                    case 8:
                    case 10:
                        //month of 30 days
                        stringBuilder.append(rand.nextInt(31) + 1);
                        break;
                    case 1:
                        //February
                        int max = 29;
                        if (year % 4 == 0) max++;
                        stringBuilder.append(rand.nextInt(max) + 1);
                }
            }
            // Delimiter
            stringBuilder.append(" - ");
            // Event
            stringBuilder.append(events.get(indexList.get(n))).append("\n\n");
            // split the entire list into blocks of 20 for efficiency
            if ((++i) % 20 == 0) {
                arrayList.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
            indexList.remove(n);
            if (a.get(RUNNING) == FALSE || indexList.size() == 0) break;
        }
        arrayList.add(stringBuilder.toString());
        return arrayList;
    }

    @Override
    protected void createDictionary() {
        int[] files = {R.raw.century_twentieth,
                R.raw.century_twenty_first,
                R.raw.century_eighteenth,
                R.raw.century_seventeenth};

        for (int fileID : files) {
            BufferedReader dict = null;
            try {
                dict = new BufferedReader(new InputStreamReader(
                        getResources().openRawResource(fileID)));
                String line;
                while ((line = dict.readLine()) != null) {
                    Scanner scanner = new Scanner(line).useDelimiter(",\"'|'\"|', '");
                    scanner.next();

                    while (scanner.hasNext()) events.add(scanner.next());
                }
                dict.close();

            } catch (Exception e) {
                // exception handling
                try {
                    if (dict != null) dict.close();
                } catch (IOException e1) {
                    throw new RuntimeException("Failed to close the buffer", e1);
                }
                if (e instanceof IOException) return;
                if (e instanceof IllegalStateException) {
                    Activity activity = getActivity();
                    if (activity == null || activity.isFinishing()) return;
                    throw new RuntimeException("Activity is not null!", e);
                }
            }
        }
    }

    @Override
    protected RandomAdapter startRandomAdapter(ArrayList list) {
        return new RandomAdapter(activity, list, 18);
    }
}
