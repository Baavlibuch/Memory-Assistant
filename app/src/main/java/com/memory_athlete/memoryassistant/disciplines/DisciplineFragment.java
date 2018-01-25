package com.memory_athlete.memoryassistant.disciplines;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;
import com.memory_athlete.memoryassistant.main.Recall;

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

public class DisciplineFragment extends Fragment {
    View rootView;                                  //This view contains the fragment
    protected CountDownTimer cdt;
    protected long mTime = 0;
    protected boolean isTimerRunning = false, hasStandard = true;
    public ArrayList<Integer> a = new ArrayList<>();             //Instructs the background thread
    public final int GROUP_SIZE = 0, NO_OF_VALUES = 1, RUNNING = 2, TRUE = 1, FALSE = 0, NORMAL = 0;
    //protected boolean hasAsync;

    public DisciplineFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_disciplines, container, false);

        Bundle bundle = getArguments();
        Timber.i("0 means error in getting title resource string ID through bundle");
        try {
            String s = getString(bundle.getInt("nameID", 0));
            if (s.equals(getString(R.string.cards))) hasStandard = false;
            else if (s.equals(getString(R.string.digits))) s = getString(R.string.numbers);
            getActivity().setTitle(s);
        } catch (Exception e) {
            getActivity().setTitle(bundle.getString("name"));
            if (bundle.getString("name").equals(getString(R.string.cards)))
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
        return rootView;
    }

    //Build the levelSpinner
    protected void levelSpinner() {
        Timber.v("Entered levelSpinner()");
        //Get the current level
        int level = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("level", 1);
        ArrayList<String> levelList = new ArrayList<>();
        levelList.add(getString(R.string.chose_level));
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
            //categories.add(getString(R.string.clump));
            //categories.add("Don't group");
            categories.add(0, getString(R.string.clump));
            categories.add(1, "Don't group");
        } else {                                                //Discipline is numbers
            categories.add(0, getString(R.string.sz));
            categories.add(1, "1");
            //categories.add(getString(R.string.sz));
            //categories.add("1");
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

    //Everything common in different start methods
    protected void startCommon() {
        a.set(RUNNING, TRUE);
        (new GenerateRandomAsyncTask()).execute(a);
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
        String string = ((TextView) rootView.findViewById(R.id.random_values)).getText().toString();
        if (string.equals("")) return false;

        //Directory of practice
        String path = getActivity().getFilesDir().getAbsolutePath() + File.separator
                + getString(R.string.practice);
        if (Helper.makeDirectory(path)) {
            //Directory of the discipline
            path = path + File.separator + getActivity().getTitle().toString();
            if (Helper.makeDirectory(path)) {
                path += File.separator + ((new SimpleDateFormat("yy-MM-dd_HH:mm")).format(new Date()))
                        + ".txt";

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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    protected void reset() {
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
        (rootView.findViewById(R.id.group)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.time)).setVisibility(View.VISIBLE);
        if (hasStandard) {
            rootView.findViewById(R.id.standard_custom_radio_group).setVisibility(View.VISIBLE);
            if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked())
                rootView.findViewById(R.id.level).setVisibility(View.VISIBLE);
        }
        ((RadioGroup) rootView.findViewById(R.id.time)).clearCheck();
        ((TextView) rootView.findViewById(R.id.random_values)).setText("");
        isTimerRunning = false;
    }

    protected void setButtons() {
        Timber.v("setButtons entered");

        rootView.findViewById(R.id.standard_radio).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rootView.findViewById(R.id.custom_layout).setVisibility(View.GONE);
                rootView.findViewById(R.id.level).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.custom_radio).setSelected(false);
            }
        });
        Timber.v("standard_radio onClickListener set");

        rootView.findViewById(R.id.custom_radio).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rootView.findViewById(R.id.custom_layout).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.level).setVisibility(View.GONE);
                rootView.findViewById(R.id.standard_radio).setSelected(false);
            }
        });
        Timber.v("standard_radio onClickListener set");

        rootView.findViewById(R.id.sw).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rootView.findViewById(R.id.clock_edit).setVisibility(View.GONE);
            }
        });
        Timber.v("Stopwatch onClickListener set");

        rootView.findViewById(R.id.timer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rootView.findViewById(R.id.clock_edit).setVisibility(View.VISIBLE);
                ((EditText) rootView.findViewById(R.id.min)).setText("");
                ((EditText) rootView.findViewById(R.id.sec)).setText("");
                rootView.findViewById(R.id.min).requestFocus();
            }
        });
        Timber.v("timer onClickListener set");

        rootView.findViewById(R.id.none).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rootView.findViewById(R.id.clock_edit).setVisibility(View.GONE);
            }
        });
        Timber.v("none onClickListener set");

        (rootView.findViewById(R.id.start)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Start();
            }
        });
        Timber.v("start onClickListener set");


        (rootView.findViewById(R.id.reset)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });
        Timber.v("reset onClickListener set");

        (rootView.findViewById(R.id.stop)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
            }
        });
        Timber.v("stop onClickListener set");

        rootView.findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
            }
        });
        Timber.v("resume onClickListener set");

        (rootView.findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });
        Timber.v("save onClickListener set");

        rootView.findViewById(R.id.recall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recall();
            }
        });

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
        Intent intent = new Intent(getActivity().getApplicationContext(), Recall.class);
        intent.putExtra("file exists", save());
        intent.putExtra("discipline", "" + getActivity().getTitle());
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
            noOfValues = (s.equals(getString(R.string.chose_level))) ? 8 : (int) pow(2, Integer.parseInt(s) + 2);
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
    protected String background() {
        return "";
    }

    //Runs when the random generating thread is complete
    protected void postExecute(String s) {
        (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(RUNNING) == FALSE) {
            reset();
            return;
        }
        ((TextView) rootView.findViewById(R.id.random_values)).setText(s);
        numbersVisibility(View.VISIBLE);
    }

    //Thread to generate the random list
    private class GenerateRandomAsyncTask extends AsyncTask<ArrayList<Integer>, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            preExecute();
        }

        @SafeVarargs
        @Override
        protected final String doInBackground(ArrayList<Integer>... a) {
            return background();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            postExecute(s);
        }
    }
}
