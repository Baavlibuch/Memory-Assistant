package com.memory_athlete.memoryassistant.disciplines;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;
import com.squareup.picasso.Picasso;

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
    private boolean mSingleCard = false;
    GridView gridView;
    ImageView cardImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        gridView = rootView.findViewById(R.id.cards_practice_grid);
        cardImageView = rootView.findViewById(R.id.cards);
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(getString(R.string.enter) + " " + getString(R.string.decks));
        cardImageView.setOnClickListener(new View.OnClickListener() {
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
                cardImageView.setAlpha((float) 0.8);
                gridView.setAlpha((float) 0.8);
                break;
            case "Night":
                cardImageView.setAlpha((float) 0.7);
                gridView.setAlpha((float) 0.8);
        }

        mSingleCard = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(
                getString(R.string.single_card), false);

        gridView.setNumColumns(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
                getContext()).getString(getString(R.string.practice_grid_columns), "4")));

        return rootView;
    }

    @Override
    protected void numbersVisibility(int visibility) {
        if (mSingleCard) {
            cardImageView.setVisibility(visibility);
            (rootView.findViewById(R.id.prev)).setVisibility(visibility);
        } else gridView.setVisibility(visibility);
    }


    void setCard() {
        cardImageView.setImageResource(cards[randomList.get(mPosition)]);
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
        int[] occurrenceCount = new int[52];
        for (int i = 0; i < (a.get(NO_OF_VALUES)) * 52; i++) {
            n = (new Random()).nextInt(52);
            if (occurrenceCount[n] >= NO_OF_VALUES) {
                i--;
                occurrenceCount[n]++;
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
        if (a.get(RUNNING) == FALSE) {
            reset();
            return;
        }
        (new RandomArrayScanner()).execute(s);
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
    public boolean reset() {
        mPosition = 0;
        gridView.setAdapter(new CardAdapter(getActivity(), new ArrayList<Integer>()));
        randomList.clear();
        numbersVisibility(View.GONE);
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.VISIBLE);
        cardImageView.setImageDrawable(null);
        boolean b = super.reset();
        rootView.findViewById(R.id.group).setVisibility(View.GONE);
        return b;
        //findViewById(R.id.cards).setVisibility(View.GONE);
        //findViewById(R.id.progress_bar_discipline).setVisibility(View.GONE);
    }

    @Override
    protected void startCommon() {
        super.startCommon();
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.GONE);
    }

    private class CardAdapter extends ArrayAdapter<Integer> {

        CardAdapter(Activity context, ArrayList<Integer> cards) {
            super(context, 0, cards);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (convertView == null) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setVisibility(View.VISIBLE);
                //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setAdjustViewBounds(true);

                //imageView.setPadding(8, 8, 8, 8);
            }

            Picasso
                    //.setLoggingEnabled(true)
                    .with(getContext())
                    .load(cards[randomList.get(position)])
                    .placeholder(R.drawable.sa)
                    .fit()
                    //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                    .into(imageView);
            //imageView.setImageResource(cardImageIds[parseInt(responses.get(position))]);

            Timber.v("getView() complete");

            //((ImageView) listItemView.findViewById(R.id.card_image)).setImageResource(
            //      cardImageIds[parseInt(responses.get(position))]);
            return imageView;//listItemView;
        }
    }

    private class RandomArrayScanner extends AsyncTask<String, Void, ArrayList<Integer>> {

        @Override
        protected ArrayList<Integer> doInBackground(String... strings) {
            Scanner scanner = new Scanner(strings[0]).useDelimiter(getString(R.string.tab));
            ArrayList<Integer> list = new ArrayList<>();

            while (scanner.hasNext()) {
                list.add(Integer.parseInt(scanner.next()));
                if (a.get(RUNNING) == FALSE) return list;
            }

            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> list) {
            super.onPostExecute(list);
            (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
            (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(View.GONE);

            if (a.get(RUNNING) == FALSE){
                reset();
                return;
            }

            randomList = list;

            if (mSingleCard) setCard();
            else {
                numbersVisibility(View.VISIBLE);
                Timber.v("Setting the card adapter");
                CardAdapter adapter = new CardAdapter(getActivity(), randomList);
                gridView.setAdapter(adapter);
                Timber.v("card adapter set");
            }
            //((TextView) findViewById(R.id.numbers)).setText(s);
            numbersVisibility(View.VISIBLE);
            (rootView.findViewById(R.id.no_of_values)).setVisibility(View.GONE);
            if(mSingleCard) Toast.makeText(getActivity(), "Tap the card for the next card",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
//TODO: uses custom theme(), don't remove this comment