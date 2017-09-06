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


public class Words extends Disciplines {

    private ArrayList<String> mDictionary = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new DictionaryAsyncTask().execute();
        if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Activity Created");
    }

    @Override
    protected String background() {
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "doInBackground() entered");

        StringBuilder stringBuilder = new StringBuilder();
        //String textString = "";
        Random rand = new Random();
        short n;

        for (int i = 0; i < a.get(1); i++) {
            n = (short) rand.nextInt(mDictionary.size());
            stringBuilder.append(mDictionary.get(n)).append(" ").append(getString(R.string.tab));
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

            setButtons();
            ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.words_small));
            Words.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

}
/*
    private static String LOG_TAG = "Position::--";
    private CountDownTimer cdt;
    long mTime = 0;
    private boolean isTimerRunning = false;
    private ArrayList<Integer> a = new ArrayList<>();
    myAsyncTask task = new myAsyncTask();




    void startCommon() {
        a.set(2, 1);
        (new myAsyncTask()).execute(a);
        (findViewById(R.id.time)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.VISIBLE);
        (findViewById(R.id.start)).setVisibility(View.GONE);
        (findViewById(R.id.no_of_values)).setVisibility(View.GONE);
        ((RadioGroup) findViewById(R.id.time)).clearCheck();
    }

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

    void setButtons() {
        Log.i(LOG_TAG, "setButtons entered");

        findViewById(R.id.sw).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.clock_edit).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.timer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.clock_edit).setVisibility(View.VISIBLE);
                ((EditText) findViewById(R.id.min)).setText("");
                ((EditText) findViewById(R.id.sec)).setText("");
            }
        });

        findViewById(R.id.none).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                findViewById(R.id.clock_edit).setVisibility(View.GONE);
            }
        });

        (findViewById(R.id.start)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Start();
            }
        });

        (findViewById(R.id.reset)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((RadioButton) findViewById(R.id.timer)).isChecked()) {
                    cdt.cancel();
                    (findViewById(R.id.clock_text)).setVisibility(View.GONE);
                } else {
                    ((Chronometer) findViewById(R.id.chronometer)).stop();
                    (findViewById(R.id.chronometer)).setVisibility(View.GONE);
                }
                task.cancel(true);
                (findViewById(R.id.start)).setVisibility(View.VISIBLE);
                (findViewById(R.id.reset)).setVisibility(View.GONE);
                (findViewById(R.id.stop)).setVisibility(View.GONE);
                (findViewById(R.id.resume)).setVisibility(View.GONE);
                (findViewById(R.id.save)).setVisibility(View.GONE);

                (findViewById(R.id.no_of_values)).setVisibility(View.VISIBLE);
                (findViewById(R.id.time)).setVisibility(View.VISIBLE);
                (findViewById(R.id.numbers)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.numbers)).setText("");
                isTimerRunning = false;
            }
        });

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
                (findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            }
        });

        (findViewById(R.id.resume)).setOnClickListener(new View.OnClickListener() {
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

        (findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String string = findViewById(R.id.numbers).toString();

                DateFormat df = new SimpleDateFormat("yyMMdd_HH:mm");
                Date date = new Date();
                String fname = (df.format(date));

                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(fname, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());

                    outputStream.close();
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Couldn't save the file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        (findViewById(R.id.save)).setVisibility(View.GONE);
        (findViewById(R.id.reset)).setVisibility(View.GONE);
        (findViewById(R.id.resume)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.GONE);
        (findViewById(R.id.chronometer)).setVisibility(View.GONE);
        (findViewById(R.id.clock_text)).setVisibility(View.GONE);
        Log.i(LOG_TAG, "setButtons complete");
    }

    void timer() {
        Log.i(LOG_TAG, "timer() entered");
        ((TextView) findViewById(clock_text)).setText("");
        if (!isTimerRunning) {
            cdt = new CountDownTimer(((Long.parseLong(((EditText)
                    ((LinearLayout) findViewById(R.id.clock_edit)).findViewById(R.id.min)).getText().
                    toString()) * 60000 + Integer.parseInt(((EditText) ((LinearLayout) findViewById
                    (R.id.clock_edit)).findViewById(R.id.sec)).getText().toString()) * 1000)), 1000) {

                boolean isRunning = true;

                public void onTick(long millisUntilFinished) {
                    ((TextView) findViewById(clock_text)).setText("" + millisUntilFinished / 60000 +
                            " min  " + (millisUntilFinished / 1000) % 60 + " sec");
                    mTime = millisUntilFinished;
                }

                public void onFinish() {
                    ((TextView) findViewById(clock_text)).setText(R.string.time_up);
                    (findViewById(R.id.numbers)).setVisibility(View.GONE);
                    (findViewById(R.id.time)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.start)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.stop)).setVisibility(View.INVISIBLE);
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
                    (findViewById(R.id.numbers)).setVisibility(View.GONE);
                }
            }.start();
        }
        Log.i(LOG_TAG, "timer() complete");
    }

    private class myAsyncTask extends AsyncTask<ArrayList<Integer>, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
            int noOfValues;

            if ((((EditText) findViewById(R.id.no_of_values)).getText().toString().length() > 0)) {
                noOfValues = Integer.parseInt((((EditText) findViewById(R.id.no_of_values)).getText().toString()));
            } else {
                noOfValues = 100;
            }

            a.set(1, noOfValues);
        }

        @Override
        protected String doInBackground(ArrayList<Integer>... a) {

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            (findViewById(R.id.save)).setVisibility(View.VISIBLE);
            if (a.get(2) == 0) {
                return;
            }
            ((TextView) findViewById(R.id.numbers)).setText(s);
            (findViewById(R.id.numbers)).setVisibility(View.VISIBLE);
            (findViewById(R.id.progress_bar)).setVisibility(View.GONE);
        }
    }
}

*/