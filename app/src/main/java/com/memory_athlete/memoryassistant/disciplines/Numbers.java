package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.memory_athlete.memoryassistant.R;

import java.math.BigDecimal;
import java.util.Random;


public class Numbers extends Disciplines {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        findViewById(R.id.negative).setVisibility(View.VISIBLE);
        findViewById(R.id.decimal).setVisibility(View.VISIBLE);

        if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Activity Created");
    }

    @Override
    protected String background() {
        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n1 = 1, n2 = 0;
        if (((CheckBox) findViewById(R.id.negative)).isChecked()) {
            n1 = 2;
            n2 = 1;
        }
        //                                                          /t doesn't work with setText()
        if (((CheckBox) findViewById(R.id.decimal)).isChecked()) {
            double n;
            for (int i = 0; i < a.get(1); i++) {
                n = round((rand.nextDouble() * n1 * (Math.pow(10, a.get(0)))
                        - n2 * Math.pow(10, a.get(0))), a.get(0));
                stringBuilder.append(n).append(getString(R.string.tab));
                for (int j = 0; j <= a.get(0); j++) {
                    stringBuilder.append(" ");
                }
                int j;
                for (j = 0; j / 2 <= 2 * a.get(0) - Double.toString(n).length() + 1; j++) {
                    stringBuilder.append(" ");
                }
                if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Entered " + j);
                if (n < 0) stringBuilder.append(" ");
                if (a.get(2) == 0) break;
            }
        } else {

            for (int i = 0; i < a.get(1); i++) {
                int n;
                n = n1 * rand.nextInt((int) Math.pow(10, a.get(0)))
                        - n2 * ((int) Math.pow(10, a.get(0)) - 1);
                stringBuilder.append(n).append(getString(R.string.tab));
                for (int j = 0; j <= a.get(0) / 2; j++) {
                    stringBuilder.append(" ");
                }
                for (int j = 0; j / 2 <= a.get(0) - Integer.toString(n).length(); j++) {
                    stringBuilder.append(" ");
                }
                if (n < 0) stringBuilder.append(" ");
                if (a.get(2) == 0) break;
            }
        }
        return stringBuilder.toString();
    }

    public static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_DOWN);
        return bd.doubleValue();
    }


    @Override
    protected void startCommon() {
        super.startCommon();
        findViewById(R.id.negative).setVisibility(View.GONE);
        findViewById(R.id.decimal).setVisibility(View.GONE);
    }

    @Override
    protected void reset() {
        super.reset();
        findViewById(R.id.decimal).setVisibility(View.VISIBLE);
        findViewById(R.id.negative).setVisibility(View.VISIBLE);
    }
}

/*
    private static String LOG_TAG = "Position::--";
    private CountDownTimer cdt;
    long mTime = 0;
    private boolean isTimerRunning = false;
    private ArrayList<Integer> a = new ArrayList<>();
   // private ArrayList<Integer> b = new ArrayList<>();
    myAsyncTask task = new myAsyncTask();



    void makeSpinner() {

    }

    void startCommon(){
        a.set(2, 1);
        (new myAsyncTask()).execute(a);
        (findViewById(R.id.time)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.VISIBLE);
        (findViewById(R.id.start)).setVisibility(View.GONE);
        (findViewById(R.id.no_of_values)).setVisibility(View.GONE);
        (findViewById(spinner)).setVisibility(View.GONE);
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
            if(!(((RadioButton) findViewById(R.id.sw)).isChecked() &&
                    ((RadioButton) findViewById(R.id.timer)).isChecked())){
                findViewById(R.id.stop).setVisibility(View.GONE);
                findViewById(R.id.reset).setVisibility(View.VISIBLE);
            }
            return;
        }

        startCommon();
        (findViewById(R.id.numbers)).setVisibility(View.VISIBLE);
        Log.i(LOG_TAG, "Start complete");
    }
}

*/