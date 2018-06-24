package com.memory_athlete.memoryassistant.disciplines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;
import com.memory_athlete.memoryassistant.main.RecallSelector;
import com.memory_athlete.memoryassistant.recall.RecallSimple;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

import static java.lang.Math.pow;

/**
 * Created by manik on 5/11/17.
 */

public abstract class DisciplineFragment extends Fragment implements View.OnClickListener {
    View rootView;                                               //This view contains the fragment
    protected CountDownTimer cdt;
    protected long mTime = 0;
    protected boolean isTimerRunning = false, hasStandard = true, hasGroup = true;
    public ArrayList<Integer> a = new ArrayList<>();             //Instructs the backgroundString thread
    public final int GROUP_SIZE = 0, NO_OF_VALUES = 1, RUNNING = 2, TRUE = 1, FALSE = 0, NORMAL = 0;
    protected Class mRecallClass;
    //protected boolean hasAsync;

    public DisciplineFragment() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.standard_radio:
                rootView.findViewById(R.id.custom_layout).setVisibility(View.GONE);
                rootView.findViewById(R.id.level).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.custom_radio).setSelected(false);
                break;
            case R.id.custom_radio:
                rootView.findViewById(R.id.custom_layout).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.level).setVisibility(View.GONE);
                rootView.findViewById(R.id.standard_radio).setSelected(false);
                break;
            case R.id.sw:
                rootView.findViewById(R.id.clock_edit).setVisibility(View.GONE);
                break;
            case R.id.timer:
                rootView.findViewById(R.id.clock_edit).setVisibility(View.VISIBLE);
                ((EditText) rootView.findViewById(R.id.min)).setText("");
                ((EditText) rootView.findViewById(R.id.sec)).setText("");
                rootView.findViewById(R.id.min).requestFocus();
                break;
            case R.id.none:
                rootView.findViewById(R.id.clock_edit).setVisibility(View.GONE);
                break;
            case R.id.stop:
                a.set(RUNNING, FALSE);
                if (isTimerRunning) {
                    cdt.cancel();
                } else {
                    mTime = ((Chronometer) rootView.findViewById(R.id.chronometer)).getBase();
                    ((Chronometer) rootView.findViewById(R.id.chronometer)).stop();
                }
                (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.resume)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.reset)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.stop)).setVisibility(View.GONE);
                (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
                break;
            case R.id.resume:
                if (isTimerRunning) {
                    timer();
                } else {
                    ((Chronometer) rootView.findViewById(R.id.chronometer)).setBase(mTime);
                    ((Chronometer) rootView.findViewById(R.id.chronometer)).start();
                }
                (rootView.findViewById(R.id.resume)).setVisibility(View.GONE);
                (rootView.findViewById(R.id.stop)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.reset)).setVisibility(View.GONE);
                //a.set(RUNNING, FALSE);
                break;
            case R.id.start:
                Start();
                break;
            case R.id.reset:
                reset();
                break;
            case R.id.save:
                save();
                break;
            case R.id.recall:
                recall();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_disciplines, container, false);

        Bundle bundle = getArguments();
        Timber.i("0 means error in getting title resource string ID through bundle");
        try {
            String s = getString(bundle.getInt("nameID", 0));
            if (s.equals(getString(R.string.cards))) hasStandard = false;
            else if (s.equals(getString(R.string.digits))) s = getString(R.string.numbers);
            getActivity().setTitle(s);
        } catch (Exception e) {
            String s= bundle.getString("name");
            String[] strings = s.split("/");
            s=strings[strings.length - 1];
            getActivity().setTitle(s);
            if (s.equals(getString(R.string.cards)))
                hasStandard = false;
        }

        Timber.i("dictionary loads before the contentView is set");
        setButtons();
        if (bundle.getBoolean("hasSpinner", false)) {
            makeSpinner(bundle.getInt("spinnerContent", 0));
        }
        levelSpinner();
        if (!hasStandard) {
            rootView.findViewById(R.id.standard_custom_radio_group).setVisibility(View.GONE);
            rootView.findViewById(R.id.custom_layout).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.level).setVisibility(View.GONE);
        }
        //initialise a
        a.add(0);
        a.add(0);
        a.add(0);
        a.add(0);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Timber.v("Activity Created");
        rootView.setOnClickListener(this);
         mRecallClass = RecallSimple.class;
        return rootView;
    }

    //Build the levelSpinner
    protected void levelSpinner() {
        Timber.v("Entered levelSpinner()");
        //Get the current level
        int level = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("level", 1);
        ArrayList<String> levelList = new ArrayList<>();
        levelList.add(getString(R.string.choose_level));
        for (; level > 0; level--) levelList.add(String.valueOf(level));
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, levelList);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = rootView.findViewById(R.id.level);
        spinner.setAdapter(levelAdapter);
        Timber.v("levelSpinner() complete");
    }

    //Builds the spinner to select the size of groupings
    protected void makeSpinner(int spinnerContent) {
        Timber.v("makeSpinner() entered");
        Spinner spinner = rootView.findViewById(R.id.group);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    Timber.d("Check if this one works properly");
                    // TODO Check if this one works properly
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (NullPointerException e) {
                    Timber.e("Couldn't hide keypad ", e);
                }
                return false;
            }
        });
        ArrayList<String> categories = new ArrayList<>(11);
        if (spinnerContent == NORMAL) {                         //All the disciplines
            categories.add(0, getString(R.string.clump));
            categories.add(1, "Don't group");
        } else {                                                //Discipline is numbers
            categories.add(0, getString(R.string.sz));
            categories.add(1, "1");
        }
        for (int i = 2; i < 11; i++) categories.add(i, Integer.toString(i));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, categories);
        //Can't recall why this is here but it is important
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        Timber.i("makeSpinner() complete");
    }

    protected void generateRandom() {
        (new GenerateRandomStringAsyncTask()).execute(a);
    }

    //Everything common in different start methods
    protected void startCommon() {
        a.set(RUNNING, TRUE);
        generateRandom();
        rootView.findViewById(R.id.standard_custom_radio_group).setVisibility(View.GONE);
        rootView.findViewById(R.id.level).setVisibility(View.GONE);
        rootView.findViewById(R.id.time).setVisibility(View.GONE);
        rootView.findViewById(R.id.start).setVisibility(View.GONE);
        rootView.findViewById(R.id.no_of_values).setVisibility(View.GONE);
        rootView.findViewById(R.id.group).setVisibility(View.GONE);
        rootView.findViewById(R.id.recall).setVisibility(View.VISIBLE);
        numbersVisibility(View.VISIBLE);

        if (((RadioButton) rootView.findViewById(R.id.sw)).isChecked() || ((RadioButton) rootView.findViewById(R.id.timer)).isChecked()) {
            (rootView.findViewById(R.id.stop)).setVisibility(View.VISIBLE);
        } else {
            (rootView.findViewById(R.id.reset)).setVisibility(View.VISIBLE);
        }
    }

    //Make the list of randoms visible, just to be lazy
    protected void numbersVisibility(int v) {
        (rootView.findViewById(R.id.random_values)).setVisibility(v);
    }

    protected void Start() {
        Timber.v("Start entered");
        try {
            //Hide the keypad
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Timber.e("Couldn't hide keypad ", e);
        }
        ((TextView) rootView.findViewById(R.id.clock_text)).setText("");

        //Start with levels
        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked() && hasStandard) {
            startCommon();
            return;
        }

        if (((RadioButton) rootView.findViewById(R.id.timer)).isChecked()) {    //Start with timer
            //Check if all the required entries are filled
            if (((EditText) rootView.findViewById(R.id.min)).getText().toString().length() > 0) {
                startCommon();
                timer();
                isTimerRunning = true;
                (rootView.findViewById(R.id.clock_edit)).setVisibility(View.GONE);
                (rootView.findViewById(R.id.clock_text)).setVisibility(View.VISIBLE);
                return;
            } else {
                ((EditText) rootView.findViewById(R.id.sec)).setError("Please enter the duration");
                //Toast.makeText(getActivity().getApplicationContext(), "Please enter the duration",
                //      Toast.LENGTH_SHORT).show();
                rootView.findViewById(R.id.min).requestFocus();
                return;
            }
        }

        //Start with stopwatch
        if (((RadioButton) rootView.findViewById(R.id.sw)).isChecked()) {
            (rootView.findViewById(R.id.chronometer)).setVisibility(View.VISIBLE);
            ((Chronometer) rootView.findViewById(R.id.chronometer)).setBase(SystemClock.
                    elapsedRealtime());
            ((Chronometer) rootView.findViewById(R.id.chronometer)).start();
            startCommon();
            if (!(((RadioButton) rootView.findViewById(R.id.sw)).isChecked() &&
                    ((RadioButton) rootView.findViewById(R.id.timer)).isChecked())) {
                rootView.findViewById(R.id.stop).setVisibility(View.GONE);
                rootView.findViewById(R.id.reset).setVisibility(View.VISIBLE);
            }
            return;
        }

        //Just start
        startCommon();
        Timber.v("Start complete");
    }

    //Saves the list
    protected boolean save() {
        String string;
        //practice_list_view is visible if arrays are used, gone if a single string is used
        if (rootView.findViewById(R.id.practice_list_view).getVisibility() == View.VISIBLE) {
            ListView l = rootView.findViewById(R.id.practice_list_view);
            StringBuilder s = new StringBuilder();
            int count = l.getAdapter().getCount();
            Timber.v("view count = " + count);
            for (int i = 0; i < count; i++) s.append(l.getAdapter().getItem(i));
            string = s.toString();
        } else string = ((TextView) rootView.findViewById(R.id.random_values)).getText().toString();

        if (string.equals("")) return false;

        //Directory of practice
        String path = getActivity().getFilesDir().getAbsolutePath() + File.separator
                + getString(R.string.practice);
        if (Helper.makeDirectory(path)) {
            //Directory of the discipline
            path = path + File.separator + getActivity().getTitle().toString();
            if (Helper.makeDirectory(path)) {
                path += File.separator + ((new SimpleDateFormat("yy-MM-dd_HH:mm"))
                        .format(new Date())) + ".txt";

                //Write the file
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(path));
                    outputStream.write(string.getBytes());

                    outputStream.close();
                    Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    public boolean reset() {
        if (rootView.findViewById(R.id.reset).getVisibility() == View.GONE) return true;
        if (((RadioButton) rootView.findViewById(R.id.timer)).isChecked()) {
            cdt.cancel();
            (rootView.findViewById(R.id.clock_text)).setVisibility(View.GONE);
        } else {
            ((Chronometer) rootView.findViewById(R.id.chronometer)).stop();
            (rootView.findViewById(R.id.chronometer)).setVisibility(View.GONE);
        }
        a.set(RUNNING, FALSE);
        (rootView.findViewById(R.id.reset)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.stop)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.resume)).setVisibility(View.GONE);
        (rootView.findViewById(R.id.save)).setVisibility(View.GONE);
        numbersVisibility(View.GONE);

        (rootView.findViewById(R.id.start)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.no_of_values)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.time)).setVisibility(View.VISIBLE);
        if (hasGroup) (rootView.findViewById(R.id.group)).setVisibility(View.VISIBLE);
        if (hasStandard) {
            rootView.findViewById(R.id.standard_custom_radio_group).setVisibility(View.VISIBLE);
            if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked())
                rootView.findViewById(R.id.level).setVisibility(View.VISIBLE);
        }
        ((RadioGroup) rootView.findViewById(R.id.time)).clearCheck();
        ((TextView) rootView.findViewById(R.id.random_values)).setText("");
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.VISIBLE);
        isTimerRunning = false;
        return false;
    }

    protected void setButtons() {
        rootView.findViewById(R.id.standard_radio).setOnClickListener(this);
        rootView.findViewById(R.id.custom_radio).setOnClickListener(this);
        rootView.findViewById(R.id.sw).setOnClickListener(this);
        rootView.findViewById(R.id.timer).setOnClickListener(this);
        rootView.findViewById(R.id.none).setOnClickListener(this);
        rootView.findViewById(R.id.stop).setOnClickListener(this);
        rootView.findViewById(R.id.resume).setOnClickListener(this);
        rootView.findViewById(R.id.start).setOnClickListener(this);
        rootView.findViewById(R.id.reset).setOnClickListener(this);
        rootView.findViewById(R.id.save).setOnClickListener(this);
        rootView.findViewById(R.id.recall).setOnClickListener(this);

        rootView.findViewById(R.id.save).setVisibility(View.GONE);
        rootView.findViewById(R.id.reset).setVisibility(View.GONE);
        rootView.findViewById(R.id.resume).setVisibility(View.GONE);
        rootView.findViewById(R.id.stop).setVisibility(View.GONE);
        rootView.findViewById(R.id.chronometer).setVisibility(View.GONE);
        rootView.findViewById(R.id.clock_text).setVisibility(View.GONE);
        rootView.findViewById(R.id.prev).setVisibility(View.GONE);
        Timber.v("setButtons complete");
    }

    //When recall button is pressed
    protected void recall() {
        boolean fileExists = save();
        Timber.v("fileExists = " + fileExists);
        Intent intent;
        if (fileExists) intent = new Intent(getActivity().getApplicationContext(), mRecallClass);
        else intent = new Intent(getActivity().getApplicationContext(), RecallSelector.class);
        intent.putExtra("file exists", fileExists);
        intent.putExtra(getString(R.string.discipline), "" + getActivity().getTitle());
        Timber.v("recalling" + getActivity().getTitle());
        startActivity(intent);
    }

    //To set the timer.
    protected void timer() {
        Timber.v("timer() entered");
        ((TextView) rootView.findViewById(R.id.clock_text)).setText("");
        if (!isTimerRunning) {
            String s = ((EditText) rootView.findViewById(R.id.sec)).getText().toString();
            if (s.length() == 0) s = "0";
            cdt = new CountDownTimer(((Long.parseLong(((EditText) rootView.findViewById(R.id.min)).getText()
                    .toString()) * 60000 + Integer.parseInt(s) * 1000)), 1000) {

                boolean isRunning = true;

                public void onTick(long millisUntilFinished) {
                    ((TextView) rootView.findViewById(R.id.clock_text)).setText("" + millisUntilFinished / 60000 +
                            " min  " + (millisUntilFinished / 1000) % 60 + " sec");
                    mTime = millisUntilFinished;
                }

                public void onFinish() {
                    ((TextView) rootView.findViewById(R.id.clock_text)).setText(R.string.time_up);
                    numbersVisibility(View.GONE);
                    rootView.findViewById(R.id.stop).setVisibility(View.GONE);
                    rootView.findViewById(R.id.reset).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.VISIBLE);
                    isRunning = false;
                }
            }.start();
        } else {
            cdt = new CountDownTimer(mTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    ((TextView) rootView.findViewById(R.id.clock_text)).setText("" + millisUntilFinished / 60000 +
                            " min  " + (millisUntilFinished
                            / 1000) % 60 + " sec");
                    mTime = millisUntilFinished;
                }

                public void onFinish() {
                    ((TextView) rootView.findViewById(R.id.clock_text)).setText(R.string.time_up);
                    numbersVisibility(View.GONE);
                }
            }.start();
        }
        Timber.v("timer() complete");
    }

    //Runs before the list thread
    protected void preExecute() {
        (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.VISIBLE);//Loading icon
        int noOfValues, size;
        try {
            if ((((Spinner) rootView.findViewById(R.id.group)).getSelectedItemPosition() < 2)) {
                size = 1;   //default
            } else {
                size = Integer.parseInt(((Spinner) rootView.findViewById(R.id.group)).getSelectedItem().toString());
            }
            a.set(GROUP_SIZE, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set NO_OF_Values based on level, level 1 is 8, every level doubles the values
        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked() && hasStandard) {
            String s = ((Spinner) rootView.findViewById(R.id.level)).getSelectedItem().toString();
            noOfValues = (s.equals(getString(R.string.choose_level))) ? 8 : (int) pow(2, Integer.parseInt(s) + 2);
            a.set(NO_OF_VALUES, noOfValues);
            return;
        }
        //set NO_OF_Values based on input
        if (((EditText) rootView.findViewById(R.id.no_of_values)).getText().toString().length() > 0)
            noOfValues = Integer.parseInt((((EditText) rootView.findViewById(R.id.no_of_values)).getText().toString()));
        else if (!hasStandard) noOfValues = 1;
        else noOfValues = 100;
        a.set(NO_OF_VALUES, noOfValues);
    }

    //Function to generate the random list. It is inherited by other fragments
    protected String backgroundString() {
        return "";
    }

    protected ArrayList backgroundArray() {
        return null;
    }

    //Runs when the random generating thread is complete
    protected void postExecuteString(String s) {
        (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(RUNNING) == FALSE) {
            reset();
            return;
        }
        Timber.v("Setting text");
        ((TextView) rootView.findViewById(R.id.random_values)).setText(s);
        numbersVisibility(View.VISIBLE);
    }

    protected void postExecuteArrayList(ArrayList list) {
        (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(RUNNING) == FALSE) {
            reset();
            return;
        }
        numbersVisibility(View.VISIBLE);
        Timber.v("Setting text");
        ((ListView) rootView.findViewById(R.id.practice_list_view))
                .setAdapter(startRandomAdapter(list));
        //((TextView) rootView.findViewById(R.id.random_values)).setText(s);
        //numbersVisibility(View.VISIBLE);
    }

    protected RandomAdapter startRandomAdapter(ArrayList list){
        return new RandomAdapter(getActivity(), list);
    }

    //Thread to generate the random list as string
    protected class GenerateRandomStringAsyncTask extends AsyncTask<ArrayList<Integer>, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            preExecute();
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(ArrayList<Integer>... a) {
            Timber.v("doInBackground entered");
            return backgroundString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            postExecuteString(s);
            Timber.v("onPostExecute() complete");
        }
    }

    //Thread to generate the random list as arrayList
    protected class GenerateRandomArrayListAsyncTask extends AsyncTask<ArrayList<Integer>, Void, ArrayList> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            preExecute();
            rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.GONE);
        }

        @Override
        protected ArrayList doInBackground(ArrayList<Integer>[] a) {
            return backgroundArray();
        }

        @Override
        protected void onPostExecute(ArrayList list) {
            super.onPostExecute(list);
            postExecuteArrayList(list);
        }
    }


    protected class RandomAdapter extends ArrayAdapter {
        private int textSize = 24;

        RandomAdapter(Activity context, ArrayList cards) {
            super(context, 0, cards);
        }

        RandomAdapter(Activity context, ArrayList cards, int textSize) {
            super(context, 0, cards);
            this.textSize = textSize;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) convertView;
            if (convertView == null) {
                textView = new TextView(getContext());
                textView.setLayoutParams(new ListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextSize(textSize);
                textView.setVisibility(View.VISIBLE);
            }

            Timber.v((String) getItem(position));
            textView.setText((String) getItem(position));

            Timber.v("getView() complete");

            return textView;
        }
    }
}

//TODO: set text spacing based on the level