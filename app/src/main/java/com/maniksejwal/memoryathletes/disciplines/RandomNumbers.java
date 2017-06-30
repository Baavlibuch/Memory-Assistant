package com.maniksejwal.memoryathletes.disciplines;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.main.Disciplines;

import java.math.BigDecimal;
import java.util.Random;


public class RandomNumbers extends Disciplines {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.st));
        findViewById(R.id.negative).setVisibility(View.VISIBLE);
        findViewById(R.id.decimal).setVisibility(View.VISIBLE);

        Log.i(TAG, "Activity Created");
    }

    @Override
    protected String background() {
        //String textString = "";
        StringBuilder stringBuilder = new StringBuilder();
        Random rand = new Random();
        int n1=1, n2=0;

        if(((CheckBox) findViewById(R.id.decimal)).isChecked()){
            double n;
            if(((CheckBox) findViewById(R.id.negative)).isChecked()){
                n1 = 2;
                n2 = 1;
            }
            for (int i = 0; i < a.get(1); i++) {
                n = round((rand.nextDouble()*n1*(Math.pow(10, a.get(0))) - n2*Math.pow(10, a.get(0)))
                        , a.get(0));
                stringBuilder.append(n).append(" ").append(getString(R.string.tab));
                if (a.get(2)==0) break;
            }
        }

        for (int i = 0; i < a.get(1); i++) {
            int n;
            if(((CheckBox) findViewById(R.id.negative)).isChecked()){
                n1 = 2;
                n2 = 1;
            }
            n = rand.nextInt(n1*((int) Math.pow(10, a.get(0)))) - n2*((int) Math.pow(10, a.get(0)));
            stringBuilder.append(n).append(" ").append(getString(R.string.tab));
            if (a.get(2)==0) break;
        }
        return stringBuilder.toString();
    }

    public static double round(double d, int decimalPlace){
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
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
    private static String TAG = "Position::--";
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
                (findViewById(spinner)).setVisibility(View.VISIBLE);
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
                save();
            }
        });

        (findViewById(R.id.save)).setVisibility(View.GONE);
        (findViewById(R.id.reset)).setVisibility(View.GONE);
        (findViewById(R.id.resume)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.GONE);
        (findViewById(R.id.chronometer)).setVisibility(View.GONE);
        (findViewById(R.id.clock_text)).setVisibility(View.GONE);
        Log.i(TAG, "setButtons complete");
    }

    void save(){
        String string = ((TextView) findViewById(R.id.numbers)).getText().toString();

        String fname = getFilesDir().getAbsolutePath() + File.separator + getString(b) + File.separator +
                ((new SimpleDateFormat("yy-MM-dd_HH:mm")).format(new Date())) + ".txt";

        String dirPath = getFilesDir().getAbsolutePath() + File.separator + getString(b);
        File pDir = new File(dirPath);
        boolean isDirectoryCreated=pDir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = pDir.mkdir();
        }
        if(isDirectoryCreated) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(fname));
                outputStream.write(string.getBytes());

                outputStream.close();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't save the file", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(getApplicationContext(), "Couldn't save the file", Toast.LENGTH_SHORT).show();
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
            if ((((Spinner) findViewById(spinner)).getSelectedItem().toString() ==
                    getText(R.string.sz)) || (((Spinner) findViewById(spinner)).getSelectedItem()
                    .toString() == "1")) {
                size = 1;
            } else {
                size = Integer.parseInt(((Spinner) findViewById(spinner)).getSelectedItem().toString());
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

            for (int i = 0; i < a[0].get(1); i++) {
                n = rand.nextInt((int) Math.pow(10, a[0].get(0)));
                textString += (n + " \t");
               // b.add(n);
                    if (a[0].get(2)==0) break;
            }
            return textString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            (findViewById(R.id.save)).setVisibility(View.VISIBLE);
            if(a.get(2)==0){
                return;
            }
            ((TextView) findViewById(R.id.numbers)).setText(s);
            (findViewById(R.id.numbers)).setVisibility(View.VISIBLE);
            (findViewById(R.id.progress_bar)).setVisibility(View.GONE);
        }
    }
}

*/