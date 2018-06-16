package com.memory_athlete.memoryassistant.disciplines;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.recall.RecallComplex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import timber.log.Timber;

public class Dates extends WordDisciplineFragment {
    private ArrayList<String> events = new ArrayList<>();
    private int startYear, endYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.events));
        CheckBox dateCheckBox = rootView.findViewById(R.id.negative_or_date);
        dateCheckBox.setText(R.string.show_only_the_year);
        dateCheckBox.setChecked(true);
        dateCheckBox.setVisibility(View.VISIBLE);
        setDateSpinners();
        mRecallClass = RecallComplex.class;

        return rootView;
    }

    void setDateSpinners() {
        ArrayList<String> arrayList = new ArrayList<>(), arrayList1 = new ArrayList<>();
        Spinner dateSpinner = rootView.findViewById(R.id.start_date);
        dateSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (NullPointerException e) {
                    Timber.e("Couldn't hide keypad ", e);
                }
                return false;
            }
        });

        arrayList.add(getString(R.string.start_year));
        for (int i = 0; i < 2000; i += 500) arrayList.add(String.format("%04d", i));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, arrayList);
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
        dateSpinner1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (NullPointerException e) {
                    Timber.e("Couldn't hide keypad ", e);
                }
                return false;
            }
        });

        arrayList1.clear();
        arrayList1.add(getString(R.string.end_year));
        arrayList1.add("2099");
        for (int i = 2499; i <= 9999; i += 500) arrayList1.add(String.format("%04d", i));
        dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
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

        String[] month = new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};

        for (int i = 0; i < a.get(NO_OF_VALUES); ) {
            year = rand.nextInt(endYear - startYear) + startYear;
            stringBuilder.append(String.format("%04d", year));
            n = (short) rand.nextInt(indexList.size());
            if (!((CheckBox) rootView.findViewById(R.id.negative_or_date)).isChecked()) {
                n = rand.nextInt(12);
                stringBuilder.append(" ").append(month[n]).append(" ");
                switch (n) {
                    case 0:
                    case 2:
                    case 4:
                    case 6:
                    case 7:
                    case 9:
                    case 11:                    //month of 31 days
                        stringBuilder.append(rand.nextInt(32) + 1);
                        break;
                    case 3:
                    case 5:
                    case 8:
                    case 10:                    //month of 30 days
                        stringBuilder.append(rand.nextInt(31) + 1);
                        break;
                    case 1:                     //February
                        int max = 29;
                        if (year % 4 == 0) max++;
                        stringBuilder.append(rand.nextInt(max) + 1);
                }
            }
            stringBuilder.append(" - ");
            stringBuilder.append(events.get(indexList.get(n))).append("\n\n");
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

        int[] files = {R.raw.twentieth_century, R.raw.twenty_first_century};

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

    @Override
    protected RandomAdapter startRandomAdapter(ArrayList list) {
        return new RandomAdapter(getActivity(), list, 18);
    }
}
