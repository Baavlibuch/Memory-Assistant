package com.maniksejwal.memoryathletes.disciplines;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.main.Disciplines;

import java.util.Random;

public class RandomLetters extends Disciplines {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + getString(R.string.st));
        Log.i(TAG, "Activity Created");
    }

    @Override
    protected String background() {
        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(1); i++) {
            for (int j = 0; j < a.get(0); j++) {
                n = rand.nextInt(26) + 97;
                stringBuilder.append((char) n);
                if (a.get(2) == 0) break;
            }
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
/*
    private static String TAG = "Position::--__";
    private CountDownTimer cdt;
    long mTime = 0;
    private boolean isTimerRunning = false;
    private ArrayList<Integer> a = new ArrayList<>();
    myAsyncTask task = new myAsyncTask();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_letters);
        setTitle("Random Letters");

        makeSpinner();
        setButtons();
        a.add(0);
        a.add(0);
        a.add(0);
        a.add(0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.i(TAG, "Activity Created");
    }

    void makeSpinner() {
        Log.i(TAG, "makeSpinner() entered");
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't hide keypad ", e);
                }
                return false;
            }
        });
        ArrayList<String> categories = new ArrayList<>();
        categories.add(getString(R.string.clump));
        categories.add("Don't group");
        categories.add("2");
        categories.add("3");
        categories.add("4");
        categories.add("5");
        categories.add("6");
        categories.add("7");
        categories.add("8");
        categories.add("9");
        categories.add("10");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, categories);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        Log.i(TAG, "makeSpinner() complete");
    }

    void startCommon() {
        (findViewById(R.id.time)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.VISIBLE);
        (findViewById(R.id.start)).setVisibility(View.GONE);
        (findViewById(R.id.no_of_values)).setVisibility(View.GONE);
        (findViewById(R.id.spinner)).setVisibility(View.GONE);
        ((RadioGroup) findViewById(R.id.time)).clearCheck();
    }

    void Start() {
        Log.i(TAG, "Start entered");
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                    (getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't hide keypad ", e);
        }
        ((TextView) findViewById(clock_text)).setText("");

        if (((RadioButton) findViewById(R.id.timer)).isChecked()) {
            if (((EditText) (findViewById(R.id.clock_edit)).findViewById(R.id.min))
                    .getText().toString().length() > 0 &&
                    ((EditText) (findViewById(R.id.clock_edit))
                            .findViewById(R.id.sec)).getText().toString().length() > 0) {
                a.set(2, 1);
                (new myAsyncTask()).execute(a);
                timer();
                isTimerRunning = true;
                (findViewById(R.id.clock_edit)).setVisibility(View.GONE);
                (findViewById(clock_text)).setVisibility(View.VISIBLE);
                startCommon();
                return;
            } else {
                Toast.makeText(RandomLetters.this, "Please enter the duration",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (((RadioButton) findViewById(R.id.sw)).isChecked()) {
            a.set(2, 1);
            (new myAsyncTask()).execute(a);
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

        a.set(2, 1);
        (new myAsyncTask()).execute(a);
        (findViewById(R.id.numbers)).setVisibility(View.VISIBLE);
        startCommon();
        Log.i(TAG, "Start complete");
    }

    void setButtons() {
        Log.i(TAG, "setButtons entered");

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
                    (findViewById(clock_text)).setVisibility(View.GONE);
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
                (findViewById(R.id.spinner)).setVisibility(View.VISIBLE);
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
                    Toast.makeText(RandomLetters.this, "Saved", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RandomLetters.this, "Couldn't save the file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        (findViewById(R.id.save)).setVisibility(View.GONE);
        (findViewById(R.id.reset)).setVisibility(View.GONE);
        (findViewById(R.id.resume)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.GONE);
        (findViewById(R.id.chronometer)).setVisibility(View.GONE);
        (findViewById(clock_text)).setVisibility(View.GONE);
        Log.i(TAG, "setButtons complete");
    }

    void timer() {
        Log.i(TAG, "timer() entered");
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
        Log.i(TAG, "timer() complete");
    }

    private class myAsyncTask extends AsyncTask<ArrayList<Integer>, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
            int noOfValues, size;
            if ((((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString() ==
                    getText(R.string.clump)) || (((Spinner) findViewById(R.id.spinner)).getSelectedItem()
                    .toString() == "Don't group")) {
                size = 1;
            } else {
                size = Integer.parseInt(((Spinner) findViewById(R.id.spinner)).getSelectedItem().toString());
            }

            if ((((EditText) findViewById(R.id.no_of_values)).getText().toString().length() > 0)) {
                noOfValues = Integer.parseInt((((EditText) findViewById(R.id.no_of_values)).getText().toString()));
            } else {
                noOfValues = 100;
            }

            a.set(0, size);
            a.set(1, noOfValues);
        }

        @Override
        protected String doInBackground(ArrayList<Integer>... a) {//TODO:read about safe varargs; short int;

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