package com.maniksejwal.memoryathletes.disciplines;

import android.os.Bundle;
import android.widget.EditText;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.main.Disciplines;

import java.util.Random;

public class BinaryDigits extends Disciplines {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + getString(R.string.st));
    }

    @Override
    protected String background(){
        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n;

        for (int i = 0; i < a.get(1) / a.get(0); i++) {
            for (int j = 0; j < a.get(0); j++) {
                n = rand.nextInt(2);
                stringBuilder.append(n);
                //textString += n;
                if (j==a.get(0)) stringBuilder.append(" ").append(getString(R.string.tab));
                //textString += " " + getString(R.string.tab);
                stringBuilder.append(" ").append(getString(R.string.tab));
            }

            if (a.get(2) == 0) break;
        }
        return stringBuilder.toString();
    }

}
/*    private static String TAG = "Position::--__";
    private CountDownTimer cdt;
    long mTime = 0;
    private boolean isTimerRunning = false;
    private ArrayList<Integer> a = new ArrayList<>();
    myAsyncTask task = new myAsyncTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binary_digits);

        setTitle("Binary Digits");

        makeSpinner();
        setButtons();
        a.add(0);
        a.add(0);
        a.add(0);
        a.add(0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.i(TAG, "Activity Created");
    }

    void Start() {
        Log.i(TAG, "Start entered");
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't hide keypad ", e);
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
                Toast.makeText(BinaryDigits.this, "Please enter the duration",
                        Toast.LENGTH_SHORT).show();
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

        (findViewById(R.id.numbers)).setVisibility(View.VISIBLE);
        startCommon();
        Log.i(TAG, "Start complete");
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
                    getText(R.string.clump)) || (((Spinner) findViewById(R.id.spinner)).
                    getSelectedItem().toString() == "Don't group")) {
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
        protected String doInBackground(ArrayList<Integer>... a) {
            String textString = "";
            Random rand = new Random();
            int n;

            for (int i = 0; i < a[0].get(1) / a[0].get(0); i++) {
                for (int j = 0; j < a[0].get(0); j++) {
                    n = rand.nextInt(2);
                    textString += n;
                    if (j==a[0].get(0)) textString+="\t ";
                    textString += " \t";
                }

                if (a[0].get(2) == 0) break;
            }
            return textString;
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