package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.MakeList;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Cards extends Disciplines {

    int mPosition = 0;
    int[] cards = MakeList.makeCards(); //new int[52];
    ArrayList<Integer> randomList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EditText) findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.decks));
    }

    @Override
    protected void numbersVisibility(int v) {
        (findViewById(R.id.cards)).setVisibility(v);
    }

    void setCard() {
        ((ImageView) findViewById(R.id.cards)).setImageResource(cards[randomList.get(mPosition)]);
    }

    public void previous(View view) {
        if (mPosition > 0) {
            mPosition--;
            setCard();
        } else {
            Toast.makeText(this, "This is the first card!", Toast.LENGTH_LONG).show();
        }
    }

    public void next(View view) {
        if (mPosition < a.get(1) * 52) {
            mPosition++;
            setCard();
        } else {
            Toast.makeText(this, "This is the last card!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected String background() {
        if (BuildConfig.DEBUG) {
            Log.v(LOG_TAG, "do in background entered to create string");
        }
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

        for (Integer i : cards)// 0; i < cards.size(); i++)
            stringBuilder.append(Integer.toString(i)).append(getString(R.string.tab));
        return stringBuilder.toString();
    }

    @Override
    protected void postExecute(String s) {
        (findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(2) == 0) {
            return;
        }
        String string="";

        Scanner scanner = new Scanner(s).useDelimiter(getString(R.string.tab));

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
    protected boolean save() {
        if(randomList.isEmpty()) return false;
        StringBuilder stringBuilder = new StringBuilder("");

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

                for (Integer i : randomList)// 0; i < randomList.size(); i++)
                    stringBuilder.append(Integer.toString(i)).append("\n");

                outputStream.write(stringBuilder.toString().getBytes());

                outputStream.close();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getApplicationContext(), "Couldn't save the card list", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void reset() {
        super.reset();
        mPosition = 0;
        randomList.clear();
        findViewById(R.id.spinner).setVisibility(View.GONE);
        findViewById(R.id.prev).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.cards)).setImageDrawable(null);
        //findViewById(R.id.cards).setVisibility(View.GONE);
        //findViewById(R.id.progress_bar_discipline).setVisibility(View.GONE);
    }
}

/*

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
       if (BuildConfig.DEBUG) Log.v(LOG_TAG, "startCommon entered");
        a.set(2, 1);
        (new myAsyncTask()).execute(a);
        (findViewById(R.id.time)).setVisibility(View.GONE);
        (findViewById(R.id.stop)).setVisibility(View.VISIBLE);
        (findViewById(R.id.start)).setVisibility(View.GONE);
        ((RadioGroup) findViewById(R.id.time)).clearCheck();
       if (BuildConfig.DEBUG) Log.v(LOG_TAG, "startCommon ended");
    }
}

*/