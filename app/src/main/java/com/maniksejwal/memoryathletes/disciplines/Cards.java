package com.maniksejwal.memoryathletes.disciplines;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.main.Disciplines;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Cards extends Disciplines {

    int mPosition = 0;
    int[] cards = new int[52];
    ArrayList<Integer> randomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeCards();
        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.decks));
    }

    @Override
    protected void numbersVisibility(int v){(findViewById(R.id.cards)).setVisibility(v);}

    void makeCards(){
        cards[0]=R.drawable.s2;
        cards[1]=R.drawable.s3;
        cards[2]=R.drawable.s4;
        cards[3]=R.drawable.s5;
        cards[4]=R.drawable.s6;
        cards[5]=R.drawable.s7;
        cards[6]=R.drawable.s8;
        cards[7]=R.drawable.s9;
        cards[8]=R.drawable.s10;
        cards[9]=R.drawable.sj;
        cards[10]=R.drawable.sq;
        cards[11]=R.drawable.sk;
        cards[12]=R.drawable.sa;
        cards[15]=R.drawable.h2;
        cards[13]=R.drawable.h3;
        cards[14]=R.drawable.h4;
        cards[16]=R.drawable.h5;
        cards[17]=R.drawable.h6;
        cards[18]=R.drawable.h7;
        cards[19]=R.drawable.h8;
        cards[20]=R.drawable.h9;
        cards[21]=R.drawable.h10;
        cards[22]=R.drawable.hj;
        cards[23]=R.drawable.hq;
        cards[24]=R.drawable.hk;
        cards[25]=R.drawable.ha;
        cards[26]=R.drawable.d2;
        cards[27]=R.drawable.d3;
        cards[28]=R.drawable.d4;
        cards[29]=R.drawable.d5;
        cards[30]=R.drawable.d6;
        cards[31]=R.drawable.d7;
        cards[32]=R.drawable.d8;
        cards[33]=R.drawable.d9;
        cards[34]=R.drawable.d10;
        cards[35]=R.drawable.dj;
        cards[36]=R.drawable.dq;
        cards[37]=R.drawable.dk;
        cards[38]=R.drawable.da;
        cards[39]=R.drawable.c2;
        cards[40]=R.drawable.c3;
        cards[41]=R.drawable.c4;
        cards[42]=R.drawable.c5;
        cards[43]=R.drawable.c6;
        cards[44]=R.drawable.c7;
        cards[45]=R.drawable.c8;
        cards[46]=R.drawable.c9;
        cards[47]=R.drawable.c10;
        cards[48]=R.drawable.cj;
        cards[49]=R.drawable.cq;
        cards[50]=R.drawable.ck;
        cards[51]=R.drawable.ca;
    }

    void setCard(){
        ((ImageView) findViewById(R.id.cards)).setImageResource(cards[randomList.get(mPosition)]);
    }

    public void previous(View view){
        if(mPosition>0){
            mPosition--;
            setCard();
        } else {
            Toast.makeText(this, "This is the first card!", Toast.LENGTH_LONG).show();
        }
    }

    public void next(View view){
        if(mPosition<a.get(1)*52){
            mPosition++;
            setCard();
        } else {
            Toast.makeText(this, "This is the last card!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected String background() {
        ArrayList<Integer> cards = new ArrayList<>();
        //Random rand = new Random();
        int n;

        for (int i = 0; i < (a.get(1)) * 52; i++) {
            n = (new Random()).nextInt(52);
            cards.add(n);
            if (a.get(2) == 0) break;
        }

        //String string="";
        StringBuilder stringBuilder = new StringBuilder();

        for(int i=0; i<cards.size(); i++)
            stringBuilder.append(Integer.toString(cards.get(i))).append(" ").append(getString(R.string.tab));
        return stringBuilder.toString();
    }

    @Override
    protected void postExecute(String s){
        (findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(2) == 0) {
            return;
        }
        String string;

        Scanner scanner = new Scanner(s).useDelimiter(" " + getString(R.string.tab));

        while (scanner.hasNext()) {
            string = scanner.next();
            randomList.add(Integer.parseInt(string));
        }

        setCard();
        //((TextView) findViewById(R.id.numbers)).setText(s);
        (findViewById(R.id.save)).setVisibility(View.VISIBLE);
        numbersVisibility(View.VISIBLE);
        (findViewById(R.id.prev)).setVisibility(View.VISIBLE);
        (findViewById(R.id.no_of_values)).setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "Tap the image for the next card", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void save() {
        String string="";

        String fname = getFilesDir().getAbsolutePath() + File.separator + getTitle() + File.separator +
                ((new SimpleDateFormat("yy-MM-dd_HH:mm")).format(new Date())) + ".txt";
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + getTitle();
        File pDir = new File(dirPath);
        boolean isDirectoryCreated=pDir.exists();

        if (!isDirectoryCreated) {
            isDirectoryCreated = pDir.mkdir();
        }

        if(isDirectoryCreated) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(fname));

                for(int i = 0; i< randomList.size(); i++)
                    string += Integer.toString(randomList.get(i)) + " " + getString(R.string.tab);

                outputStream.write(string.getBytes());

                outputStream.close();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't save the file", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(getApplicationContext(), "Couldn't save the file", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void reset() {
        super.reset();
        mPosition=0;
        randomList.clear();
        findViewById(R.id.spinner).setVisibility(View.GONE);
        findViewById(R.id.prev).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.cards)).setImageDrawable(null);
        //findViewById(R.id.cards).setVisibility(View.GONE);
        //findViewById(R.id.progress_bar_discipline).setVisibility(View.GONE);
    }
}

/*

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        setTitle("Random Cards");

        //makeSpinner();
        setButtons();
        a.add(0);
        a.add(0);
        a.add(0);
        a.add(0);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Log.i(TAG, "Activity Created");
    }



    void setCard(){
        ((ImageView) findViewById(R.id.numbers)).setImageResource(cards[randomList.get(mPosition)]);
    }

    public void previous(View view){
        if(mPosition>0){
            mPosition--;
            setCard();
        } else {
            Toast.makeText(this, "This is the first card!", Toast.LENGTH_LONG).show();
        }
    }

    public void next(View view){
        if(mPosition<a.get(1)*52){
            mPosition++;
            setCard();
        } else {
            Toast.makeText(this, "This is the last card!", Toast.LENGTH_LONG).show();
        }
    }

    void startCommon() {
        Log.v(TAG, "startCommon entered");
        a.set(2, 1);
        (new myAsyncTask()).execute(a);
        (findViewById(R.id.time)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.VISIBLE);
        (findViewById(R.id.start)).setVisibility(View.GONE);
        ((RadioGroup) findViewById(R.id.time)).clearCheck();
        Log.v(TAG, "startCommon ended");
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
                timer();
                isTimerRunning = true;
                (findViewById(R.id.clock_edit)).setVisibility(View.GONE);
                (findViewById(clock_text)).setVisibility(View.VISIBLE);
                startCommon();
                return;
            } else {
                Toast.makeText(Cards.this, "Please enter the duration",
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
                (findViewById(R.id.prev)).setVisibility(View.GONE);

                (findViewById(R.id.no_of_values)).setVisibility(View.VISIBLE);
                (findViewById(R.id.time)).setVisibility(View.VISIBLE);
                (findViewById(R.id.numbers)).setVisibility(View.GONE);
                mPosition=0;
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
                (findViewById(R.id.prev)).setVisibility(View.GONE);
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
                //String string = findViewById(R.id.numbers).toString();


            }
        });

        (findViewById(R.id.prev)).setVisibility(View.GONE);
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

    private class myAsyncTask extends AsyncTask<ArrayList<Integer>, Void, ArrayList<Integer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            (findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
            int noOfValues;


            if ((((EditText) findViewById(R.id.no_of_values)).getText().toString().length() > 0)) {
                noOfValues = Integer.parseInt((((EditText) findViewById(R.id.no_of_values)).getText().toString()));
            } else {
                noOfValues = 1;
            }

            makeCards();

            //  a.set(0, size);
            a.set(1, noOfValues);
        }


        protected ArrayList<Integer> doInBackground(ArrayList<Integer>... a) {
            ArrayList<Integer> cards = new ArrayList<>();
            //Random rand = new Random();
            int n;

            for (int i = 0; i < (a[0].get(1)) * 52; i++) {
                n = (new Random()).nextInt(52);
                cards.add(n);
                if (a[0].get(2) == 0) break;
            }
            return cards;
        }


        protected void onPostExecute(ArrayList<Integer> s) {
            super.onPostExecute(s);
            if (a.get(2) == 0) {
                return;
            }
            randomList = s;
            setCard();
            //((TextView) findViewById(R.id.numbers)).setText(s);
            (findViewById(R.id.save)).setVisibility(View.VISIBLE);
            (findViewById(R.id.numbers)).setVisibility(View.VISIBLE);
            (findViewById(R.id.prev)).setVisibility(View.VISIBLE);
            (findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            (findViewById(R.id.no_of_values)).setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Tap the image for the next card", Toast.LENGTH_SHORT).show();
        }
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
        categories.add(getString(R.string.sz));
        categories.add("1");
        categories.add("2");
        categories.add("3");
        categories.add("4");
        categories.add("5");
        categories.add("6");
        categories.add("7");
        categories.add("8");
        categories.add("9");

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
}

*/