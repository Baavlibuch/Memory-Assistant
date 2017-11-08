package com.memory_athlete.memoryassistant.disciplines;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    View rootView;
    protected CountDownTimer cdt;
    protected long mTime = 0;
    protected boolean isTimerRunning = false, hasStandard = true;
    protected ArrayList<Integer> a = new ArrayList<>();
    //protected boolean hasAsync;

    public DisciplineFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_disciplines, container, false);

        Bundle bundle = getArguments();
        Timber.i("0 means error in getting title resource string ID through bundle");
        //theme();
        try {
            String s = getString(bundle.getInt("nameID", 0));
            if (s.equals(getString(R.string.cards))) hasStandard = false;
            else if (s.equals(getString(R.string.digits))) s = getString(R.string.numbers);
            getActivity().setTitle(s);
        } catch (Exception e) {
            getActivity().setTitle(bundle.getString("name"));
            if (bundle.getString("name") == getString(R.string.cards))
                hasStandard = false;
        }

        Timber.i("dictionary loads before the contentView is set");
        //if (!bundle.getBoolean("hasAsyncTask", false)) {
            //setContentView(R.layout.activity_disciplines);            TODO
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
        //}
        //setContentView(R.layout.activity_binary_digits); TODO: fix it!
        //setTitle("Binary Digits"); TODO: fix it!

        //makeSpinner();
        a.add(0);
        a.add(0);
        a.add(0);
        a.add(0);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Timber.v("Activity Created");
        return rootView;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_space, menu);
        return true;
    }                                                 //TODO

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.visit_my_space:
                startActivity(new Intent(this, MySpace.class));
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    protected void theme() {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme");
        switch (theme) {
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
        }
    }*/

    protected void levelSpinner() {
        Timber.v("Entered levelSpinner()");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int level = preferences.getInt("level", 1);
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
        ArrayList<String> categories = new ArrayList<>();
        if (spinnerContent == 0) {
            categories.add(getString(R.string.clump));
            categories.add("Don't group");
        } else {
            categories.add(getString(R.string.sz));
            categories.add("1");
        }
        categories.add("2");
        categories.add("3");
        categories.add("4");
        categories.add("5");
        categories.add("6");
        categories.add("7");
        categories.add("8");
        categories.add("9");
        categories.add("10");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, categories);
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

    protected void startCommon() {
        a.set(2, 1);
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

    protected void numbersVisibility(int v) {
        (rootView.findViewById(R.id.random_values)).setVisibility(v);
    }

    protected void Start() {
        Timber.v("Start entered");
        try {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Timber.e("Couldn't hide keypad ", e);
        }
        ((TextView) rootView.findViewById(R.id.clock_text)).setText("");

        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked() && hasStandard) {
            startCommon();
            return;
        }

        if (((RadioButton) rootView.findViewById(R.id.timer)).isChecked()) {
            if (((EditText) rootView.findViewById(R.id.min)).getText().toString().length() > 0) {
                startCommon();
                timer();
                isTimerRunning = true;
                (rootView.findViewById(R.id.clock_edit)).setVisibility(View.GONE);
                (rootView.findViewById(R.id.clock_text)).setVisibility(View.VISIBLE);
                return;
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Please enter the duration",
                        Toast.LENGTH_SHORT).show();
                rootView.findViewById(R.id.min).requestFocus();
                return;
            }
        }


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

        startCommon();
        Timber.v("Start complete");
    }

    protected boolean save() {
        String string = ((TextView) rootView.findViewById(R.id.random_values)).getText().toString();
        if (string.equals("")) return false;

        String fname = getActivity().getFilesDir().getAbsolutePath() + File.separator
                + getString(R.string.practice) + File.separator + getActivity().getTitle()
                + File.separator + ((new SimpleDateFormat("yy-MM-dd_HH:mm")).format(new Date())) + ".txt";
        String dirPath = getActivity().getFilesDir().getAbsolutePath() + File.separator + getActivity().getTitle();
        File pDir = new File(dirPath);
        boolean isDirectoryCreated = pDir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = pDir.mkdir();
        }
        if (isDirectoryCreated) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(fname));
                outputStream.write(string.getBytes());

                outputStream.close();
                Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getActivity().getApplicationContext(), "Couldn't save the list", Toast.LENGTH_SHORT).show();
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
        a.set(2, 0);
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
                a.set(2, 0);
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
                a.set(2, 0);
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

    protected void recall() {
        Intent intent = new Intent(getActivity().getApplicationContext(), Recall.class);
        intent.putExtra("file exists", save());
        intent.putExtra("discipline", "" + getActivity().getTitle());
        Timber.v("recalling" + getActivity().getTitle());
        startActivity(intent);
    }

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

    protected void preExecute() {
        (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.VISIBLE);
        int noOfValues, size;
        try {
            if ((((Spinner) rootView.findViewById(R.id.group)).getSelectedItemPosition() < 2)) {
                size = 1;
            } else {
                size = Integer.parseInt(((Spinner) rootView.findViewById(R.id.group)).getSelectedItem().toString());
            }
            a.set(0, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (((RadioButton) rootView.findViewById(R.id.standard_radio)).isChecked() && hasStandard) {
            String s = ((Spinner) rootView.findViewById(R.id.level)).getSelectedItem().toString();
            noOfValues = (s == getString(R.string.chose_level)) ? 8 : (int) pow(2, Integer.parseInt(s) + 2);
            a.set(1, noOfValues);
            return;
        }
        if (((EditText) rootView.findViewById(R.id.no_of_values)).getText().toString().length() > 0)
            noOfValues = Integer.parseInt((((EditText) rootView.findViewById(R.id.no_of_values)).getText().toString()));
        else if (!hasStandard) noOfValues = 1;
        else noOfValues = 100;
        a.set(1, noOfValues);
    }

    protected String background() {
        return "";
    }

    protected void postExecute(String s) {
        (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(2) == 0) {
            return;
        }
        ((TextView) rootView.findViewById(R.id.random_values)).setText(s);
        numbersVisibility(View.VISIBLE);
    }

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
