package com.memory_athlete.memoryassistant.disciplines;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

import com.memory_athlete.memoryassistant.BuildConfig;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpace;
import com.memory_athlete.memoryassistant.recall.Recall;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

import static com.memory_athlete.memoryassistant.R.id.clock_text;
import static java.lang.Math.pow;

/**
 * Created by Manik on 25/04/17.
 */

public class Disciplines extends AppCompatActivity {

    protected static String LOG_TAG = "\tDiscipline: ";
    protected CountDownTimer cdt;
    protected long mTime = 0;
    protected boolean isTimerRunning = false, hasStandard = true;
    protected ArrayList<Integer> a = new ArrayList<>();
    //protected boolean hasAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Timber.i("0 means error in getting title resource string ID through intent");
        theme();
        setTitle(getString(intent.getIntExtra("nameID", 0)));

        if (getString(intent.getIntExtra("nameID", 0)).equals(getString(R.string.cards)))
            hasStandard = false;

        Timber.i("dictionary loads before the contentView is set");
        if (!intent.getBooleanExtra("hasAsyncTask", false)) {
            setContentView(R.layout.activity_disciplines);
            setButtons();
            if (intent.getBooleanExtra("hasSpinner", false)) {
                makeSpinner(intent.getIntExtra("spinnerContent", 0));
            }
            levelSpinner();
            if (!hasStandard) {
                findViewById(R.id.standard_custom_radio_group).setVisibility(View.GONE);
                findViewById(R.id.custom_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.level).setVisibility(View.GONE);
            }
        }
        //setContentView(R.layout.activity_binary_digits); TODO: fix it!
        //setTitle("Binary Digits"); TODO: fix it!

        //makeSpinner();
        a.add(0);
        a.add(0);
        a.add(0);
        a.add(0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Timber.v("Activity Created");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_space, menu);
        return true;
    }

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
    }

    protected void levelSpinner() {
        Timber.v("Entered levelSpinner()");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int level = preferences.getInt("level", 1);
        ArrayList<String> levelList = new ArrayList<>();
        levelList.add(getString(R.string.chose_level));
        for (; level > 0; level--) levelList.add(String.valueOf(level));
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, levelList);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.level);
        spinner.setAdapter(levelAdapter);
        Timber.v("levelSpinner() complete");
    }

    protected void makeSpinner(int spinnerContent) {
        Timber.v("makeSpinner() entered");
        Spinner spinner = (Spinner) findViewById(R.id.group);
        spinner.setVisibility(View.VISIBLE);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
                this, android.R.layout.simple_spinner_item, categories);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        if (BuildConfig.DEBUG) {
            Log.i(LOG_TAG, "makeSpinner() complete");
        }
    }

    protected void startCommon() {
        a.set(2, 1);
        (new GenerateRandomAsyncTask()).execute(a);
        findViewById(R.id.standard_custom_radio_group).setVisibility(View.GONE);
        findViewById(R.id.level).setVisibility(View.GONE);
        findViewById(R.id.time).setVisibility(View.GONE);
        findViewById(R.id.start).setVisibility(View.GONE);
        findViewById(R.id.no_of_values).setVisibility(View.GONE);
        findViewById(R.id.group).setVisibility(View.GONE);
        findViewById(R.id.recall).setVisibility(View.VISIBLE);
        numbersVisibility(View.VISIBLE);

        if (((RadioButton) findViewById(R.id.sw)).isChecked() || ((RadioButton) findViewById(R.id.timer)).isChecked()) {
            (findViewById(R.id.stop)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.reset)).setVisibility(View.VISIBLE);
        }
    }

    protected void numbersVisibility(int v) {
        (findViewById(R.id.random_values)).setVisibility(v);
    }

    protected void Start() {
        Timber.v("Start entered");
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Timber.e("Couldn't hide keypad ", e);
        }
        ((TextView) findViewById(clock_text)).setText("");

        if (((RadioButton) findViewById(R.id.standard_radio)).isChecked() && hasStandard) {
            startCommon();
            return;
        }

        if (((RadioButton) findViewById(R.id.timer)).isChecked()) {
            if (((EditText) findViewById(R.id.min)).getText().toString().length() > 0) {
                startCommon();
                timer();
                isTimerRunning = true;
                (findViewById(R.id.clock_edit)).setVisibility(View.GONE);
                (findViewById(clock_text)).setVisibility(View.VISIBLE);
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Please enter the duration",
                        Toast.LENGTH_SHORT).show();
                findViewById(R.id.min).requestFocus();
                return;
            }
        }


        if (((RadioButton) findViewById(R.id.sw)).isChecked()) {
            (findViewById(R.id.chronometer)).setVisibility(View.VISIBLE);
            ((Chronometer) findViewById(R.id.chronometer)).setBase(SystemClock.
                    elapsedRealtime());
            ((Chronometer) findViewById(R.id.chronometer)).start();
            startCommon();
            if (!(((RadioButton) findViewById(R.id.sw)).isChecked() &&
                    ((RadioButton) findViewById(R.id.timer)).isChecked())) {
                findViewById(R.id.stop).setVisibility(View.GONE);
                findViewById(R.id.reset).setVisibility(View.VISIBLE);
            }
            return;
        }

        startCommon();
        Timber.v("Start complete");
    }

    protected boolean save() {
        String string = ((TextView) findViewById(R.id.random_values)).getText().toString();
        if (string.equals("")) return false;

        String fname = getFilesDir().getAbsolutePath() + File.separator + getTitle() + File.separator +
                ((new SimpleDateFormat("yy-MM-dd_HH:mm")).format(new Date())) + ".txt";
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + getTitle();
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
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getApplicationContext(), "Couldn't save the list", Toast.LENGTH_SHORT).show();
        return false;
    }

    protected void reset() {
        if (((RadioButton) findViewById(R.id.timer)).isChecked()) {
            cdt.cancel();
            (findViewById(clock_text)).setVisibility(View.GONE);
        } else {
            ((Chronometer) findViewById(R.id.chronometer)).stop();
            (findViewById(R.id.chronometer)).setVisibility(View.GONE);
        }
        a.set(2, 0);
        (findViewById(R.id.reset)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.GONE);
        (findViewById(R.id.resume)).setVisibility(View.GONE);
        (findViewById(R.id.save)).setVisibility(View.GONE);
        numbersVisibility(View.GONE);

        (findViewById(R.id.start)).setVisibility(View.VISIBLE);
        (findViewById(R.id.no_of_values)).setVisibility(View.VISIBLE);
        (findViewById(R.id.group)).setVisibility(View.VISIBLE);
        (findViewById(R.id.time)).setVisibility(View.VISIBLE);
        if (hasStandard) {
            findViewById(R.id.standard_custom_radio_group).setVisibility(View.VISIBLE);
            if (((RadioButton) findViewById(R.id.standard_radio)).isChecked())
                findViewById(R.id.level).setVisibility(View.VISIBLE);
        }
        ((RadioGroup) findViewById(R.id.time)).clearCheck();
        ((TextView) findViewById(R.id.random_values)).setText("");
        isTimerRunning = false;
    }

    protected void setButtons() {
        if (BuildConfig.DEBUG) Log.i(LOG_TAG, "setButtons entered");

        findViewById(R.id.standard_radio).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.custom_layout).setVisibility(View.GONE);
                findViewById(R.id.level).setVisibility(View.VISIBLE);
                findViewById(R.id.custom_radio).setSelected(false);
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "standard_radio onClickListener set");

        findViewById(R.id.custom_radio).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.custom_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.level).setVisibility(View.GONE);
                findViewById(R.id.standard_radio).setSelected(false);
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "standard_radio onClickListener set");

        findViewById(R.id.sw).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.clock_edit).setVisibility(View.GONE);
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "Stopwatch onClickListener set");

        findViewById(R.id.timer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.clock_edit).setVisibility(View.VISIBLE);
                ((EditText) findViewById(R.id.min)).setText("");
                ((EditText) findViewById(R.id.sec)).setText("");
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "timer onClickListener set");

        findViewById(R.id.none).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.clock_edit).setVisibility(View.GONE);
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "none onClickListener set");

        (findViewById(R.id.start)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Start();
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "start onClickListener set");


        (findViewById(R.id.reset)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "reset onClickListener set");

        (findViewById(R.id.stop)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                a.set(2, 0);
                if (isTimerRunning) {
                    cdt.cancel();
                } else {
                    mTime = ((Chronometer) findViewById(R.id.chronometer)).getBase();
                    ((Chronometer) findViewById(R.id.chronometer)).stop();
                }
                (findViewById(R.id.save)).setVisibility(View.VISIBLE);
                (findViewById(R.id.resume)).setVisibility(View.VISIBLE);
                (findViewById(R.id.reset)).setVisibility(View.VISIBLE);
                (findViewById(R.id.stop)).setVisibility(View.GONE);
                (findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "stop onClickListener set");

        findViewById(R.id.resume).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isTimerRunning) {
                    timer();
                } else {
                    ((Chronometer) findViewById(R.id.chronometer)).setBase(mTime);
                    ((Chronometer) findViewById(R.id.chronometer)).start();
                }
                (findViewById(R.id.resume)).setVisibility(View.GONE);
                (findViewById(R.id.stop)).setVisibility(View.VISIBLE);
                (findViewById(R.id.reset)).setVisibility(View.GONE);
                a.set(2, 0);
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "resume onClickListener set");

        (findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "save onClickListener set");

        findViewById(R.id.recall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recall();
            }
        });

        findViewById(R.id.save).setVisibility(View.GONE);
        findViewById(R.id.reset).setVisibility(View.GONE);
        findViewById(R.id.resume).setVisibility(View.GONE);
        findViewById(R.id.stop).setVisibility(View.GONE);
        findViewById(R.id.chronometer).setVisibility(View.GONE);
        findViewById(clock_text).setVisibility(View.GONE);
        findViewById(R.id.prev).setVisibility(View.GONE);
        Timber.v("setButtons complete");
    }

    protected void recall() {
        Intent intent = new Intent(getApplicationContext(), Recall.class);
        intent.putExtra("file exists", save());
        intent.putExtra("discipline", "" + getTitle());
        Timber.v("recalling" + getTitle());
        startActivity(intent);
    }

    protected void timer() {
        Timber.v("timer() entered");
        ((TextView) findViewById(clock_text)).setText("");
        if (!isTimerRunning) {
            String s = ((EditText) findViewById(R.id.sec)).getText().toString();
            if (s.length()==0) s="0";
            cdt = new CountDownTimer(((Long.parseLong(((EditText) findViewById(R.id.min)).getText()
                    .toString()) * 60000 + Integer.parseInt(s) * 1000)), 1000) {

                boolean isRunning = true;

                public void onTick(long millisUntilFinished) {
                    ((TextView) findViewById(clock_text)).setText("" + millisUntilFinished / 60000 +
                            " min  " + (millisUntilFinished / 1000) % 60 + " sec");
                    mTime = millisUntilFinished;
                }

                public void onFinish() {
                    ((TextView) findViewById(clock_text)).setText(R.string.time_up);
                    numbersVisibility(View.GONE);
                    findViewById(R.id.stop).setVisibility(View.GONE);
                    findViewById(R.id.reset).setVisibility(View.VISIBLE);
                    findViewById(R.id.nested_scroll_view).setVisibility(View.VISIBLE);
                    isRunning = false;
                }
            }.start();
        } else {
            cdt = new CountDownTimer(mTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    ((TextView) findViewById(clock_text)).setText("" + millisUntilFinished / 60000 +
                            " min  " + (millisUntilFinished
                            / 1000) % 60 + " sec");
                    mTime = millisUntilFinished;
                }

                public void onFinish() {
                    ((TextView) findViewById(clock_text)).setText(R.string.time_up);
                    numbersVisibility(View.GONE);
                }
            }.start();
        }
        Timber.v("timer() complete");
    }

    protected void preExecute() {
        (findViewById(R.id.progress_bar_discipline)).setVisibility(View.VISIBLE);
        int noOfValues, size;
        try {
            if ((((Spinner) findViewById(R.id.group)).getSelectedItemPosition() < 2)) {
                size = 1;
            } else {
                size = Integer.parseInt(((Spinner) findViewById(R.id.group)).getSelectedItem().toString());
            }
            a.set(0, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (((RadioButton) findViewById(R.id.standard_radio)).isChecked() && hasStandard) {
            String s = ((Spinner) findViewById(R.id.level)).getSelectedItem().toString();
            noOfValues = (s == getString(R.string.chose_level)) ? 8 : (int) pow(2, Integer.parseInt(s) + 2);
            a.set(1, noOfValues);
            return;
        }
        if (((EditText) findViewById(R.id.no_of_values)).getText().toString().length() > 0)
            noOfValues = Integer.parseInt((((EditText) findViewById(R.id.no_of_values)).getText().toString()));
        else if (!hasStandard) noOfValues = 1;
        else noOfValues = 100;
        a.set(1, noOfValues);
    }

    protected String background() {
        return "";
    }

    protected void postExecute(String s) {
        (findViewById(R.id.save)).setVisibility(View.VISIBLE);
        (findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(2) == 0) {
            return;
        }
        ((TextView) findViewById(R.id.random_values)).setText(s);
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
