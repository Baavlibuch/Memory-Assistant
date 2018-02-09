package com.memory_athlete.memoryassistant.disciplines;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import timber.log.Timber;

public class Cards extends DisciplineFragment {
    int mPosition = 0;
    int[] cards = Helper.makeCards();
    ArrayList<Integer> randomList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.decks));
        rootView.findViewById(R.id.cards).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        rootView.findViewById(R.id.prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
        String theme = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.theme), getString(R.string.light));
        switch (theme) {
            case "Dark":
                rootView.findViewById(R.id.cards).setAlpha((float) 0.8);
                break;
            case "Night":
                rootView.findViewById(R.id.cards).setAlpha((float) 0.7);
        }

        return rootView;
    }

    @Override
    protected void numbersVisibility(int v) {
        (rootView.findViewById(R.id.cards)).setVisibility(v);
    }


    void setCard() {
        ((ImageView) rootView.findViewById(R.id.cards)).setImageResource(cards[randomList.get(mPosition)]);
    }

    //Button to show previous
    public void previous() {
        if (mPosition > 0) {
            mPosition--;
            setCard();
        } else {
            Toast.makeText(getActivity(), "This is the first card!", Toast.LENGTH_LONG).show();
        }
    }

    //Show the next card
    public void next() {
        if (mPosition < a.get(NO_OF_VALUES) * 52 - 1) {
            mPosition++;
            setCard();
        } else {
            Toast.makeText(getActivity(), "This is the last card!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected String background() {
        Timber.v("do in background entered to create string");
        ArrayList<Integer> cards = new ArrayList<>();
        //Random rand = new Random();
        int n;
        int[] occurenceCount = new int[52];
        for (int i = 0; i < (a.get(NO_OF_VALUES)) * 52; i++) {
            n = (new Random()).nextInt(52);
            if(occurenceCount[i]>NO_OF_VALUES){
                i--;
                continue;
            }
            cards.add(n);
            if (a.get(RUNNING) == FALSE) break;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (Integer i : cards)
            stringBuilder.append(Integer.toString(i)).append(getString(R.string.tab));
        return stringBuilder.toString();
    }

    @Override
    protected void postExecute(String s) {
        (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);
        if (a.get(RUNNING) == FALSE) {
            return;
        }
        String string;

        Scanner scanner = new Scanner(s).useDelimiter(getString(R.string.tab));

        while (scanner.hasNext()) {
            string = scanner.next();
            randomList.add(Integer.parseInt(string));
        }

        setCard();
        //((TextView) findViewById(R.id.numbers)).setText(s);
        (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
        numbersVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.prev)).setVisibility(View.VISIBLE);
        (rootView.findViewById(R.id.no_of_values)).setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Tap the image for the next card", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected boolean save() {
        if (randomList.isEmpty()) return false;
        StringBuilder stringBuilder = new StringBuilder("");

        //Practice Directory
        String path = getActivity().getFilesDir().getAbsolutePath() + File.separator
                + getString(R.string.practice);


        if (Helper.makeDirectory(path)) {
            //Discipline Directory
            path += File.separator + getActivity().getTitle();
            if (Helper.makeDirectory(path)) {
                //File Path
                path += File.separator
                        + ((new SimpleDateFormat("yy-MM-dd_HH:mm")).format(new Date()))
                        + ".txt";
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(path));

                    for (Integer i : randomList)// 0; i < randomList.size(); i++)
                        stringBuilder.append(Integer.toString(i)).append("\n");
                    //\n is also a delimiter used in recall

                    outputStream.write(stringBuilder.toString().getBytes());

                    outputStream.close();
                    Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }

    @Override
    protected void reset() {
        super.reset();
        mPosition = 0;
        randomList.clear();
        rootView.findViewById(R.id.group).setVisibility(View.GONE);
        rootView.findViewById(R.id.prev).setVisibility(View.GONE);
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.VISIBLE);
        ((ImageView) rootView.findViewById(R.id.cards)).setImageDrawable(null);
        //findViewById(R.id.cards).setVisibility(View.GONE);
        //findViewById(R.id.progress_bar_discipline).setVisibility(View.GONE);
    }

    @Override
    protected void startCommon() {
        super.startCommon();
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.GONE);
    }
}