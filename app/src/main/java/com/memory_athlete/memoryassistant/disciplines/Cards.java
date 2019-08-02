package com.memory_athlete.memoryassistant.disciplines;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.recall.RecallCards;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

import timber.log.Timber;

import static android.view.View.GONE;
import static java.util.Objects.requireNonNull;

public class Cards extends DisciplineFragment {
    int mPosition = 0;
    int[] cards = Helper.makeCards();
    ArrayList<Integer> randomList = new ArrayList<>();
    private boolean mSingleCard = false;
    GridView gridView;

    @Override
    public void onClick(View v) {
        Timber.v("clicked viewId " + v.getId());
        Timber.v("R.id.cards_and_speech = " + R.id.cards_and_speech);
        Timber.v("R.id.activity_cards = " + R.id.activity_cards);
        switch (v.getId()) {
            case R.id.cards_and_speech:
                next();
                break;
            case R.id.prev:
                previous();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        gridView = rootView.findViewById(R.id.cards_practice_grid);
        cardAndSpeechImageView = rootView.findViewById(R.id.cards_and_speech);
        ((EditText) rootView.findViewById(R.id.no_of_values)).setHint(
                getString(R.string.enter) + " " + getString(R.string.decks));

        String theme = PreferenceManager.getDefaultSharedPreferences(activity)
                .getString(getString(R.string.theme), getString(R.string.light));
        switch (requireNonNull(theme)) {
            case "Dark":
                cardAndSpeechImageView.setAlpha(0.8f);
                gridView.setAlpha(0.8f);
                break;
            case "Night":
                cardAndSpeechImageView.setAlpha(0.7f);
                gridView.setAlpha(0.8f);
        }

        mSingleCard = sharedPreferences.getBoolean(getString(R.string.single_card), false);
        gridView.setNumColumns(Integer.parseInt(requireNonNull(sharedPreferences.getString(
                getString(R.string.practice_grid_columns), "4"))));

        hasGroup = false;
        hasSpeech = false;
        mRecallClass = RecallCards.class;
        rootView.findViewById(R.id.speech_check_box).setVisibility(View.GONE);
        cardAndSpeechImageView.setOnClickListener(this);
        rootView.findViewById(R.id.prev).setOnClickListener(this);
        return rootView;
    }

    @Override
    protected void numbersVisibility(int visibility) {
        if (mSingleCard) {
            cardAndSpeechImageView.setVisibility(visibility);
            (rootView.findViewById(R.id.prev)).setVisibility(visibility);
        } else gridView.setVisibility(visibility);
    }

    void setCard() {
        Timber.v("setting card");
        cardAndSpeechImageView.setImageResource(cards[randomList.get(mPosition)]);
    }

    //Button to show previous
    public void previous() {
        if (mPosition > 0) {
            mPosition--;
            setCard();
        } else Toast.makeText(activity, "This is the first card!", Toast.LENGTH_LONG).show();
    }

    //Show the next card
    public void next() {
        if (mPosition < a.get(NO_OF_VALUES) * 52 - 1) {
            Timber.v("next card");
            mPosition++;
            setCard();
        } else Toast.makeText(activity, "This is the last card!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String backgroundString() {
        try {
            ArrayList<Integer> cards = new ArrayList<>();
            int n;
            //Random rand = new Random();

            ArrayList<Integer> indexList = new ArrayList<>();
            boolean shuffleDecks = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getBoolean(getString(R.string.shuffle_decks), false);
            if (shuffleDecks) for (int i = 0; i < a.get(NO_OF_VALUES); i++)
                for (int j = 0; j < 52; j++)
                    indexList.add(j);

            try {
                for (int i = 0; i < (a.get(NO_OF_VALUES)) * 52; i++) {
                    if (!shuffleDecks && indexList.size() == 0) for (int j = 0; j < 52; j++)
                        indexList.add(j);

                    n = (new Random()).nextInt(indexList.size());
                    cards.add(indexList.get(n));
                    indexList.remove(n);
                    if (a.get(RUNNING) == FALSE) break;
                }
            } catch (IllegalStateException e) {
                throw new RuntimeException("IllegalStateException from ViewPager.populate() "
                        + "caused in Cards.backgroundString while generating random", e);
            }

            StringBuilder stringBuilder = new StringBuilder();

            for (Integer i : cards)
                stringBuilder.append(Integer.toString(i)).append(getString(R.string.tab));
            return stringBuilder.toString();

        } catch (IllegalStateException e) {
            throw new RuntimeException("IllegalStateException from ViewPager.populate() "
                    + "caused in Cards.backgroundString", e);
        }
    }

    @Override
    protected void postExecuteString(String s) {
        if (a.get(RUNNING) == FALSE) {
            reset();
            return;
        }
        (new RandomArrayScanner()).execute(s);
    }

    @Override
    protected boolean save() {
        if (randomList.isEmpty()) return false;
        StringBuilder stringBuilder = new StringBuilder();

        //Practice Directory
        String path = Helper.APP_FOLDER + File.separator
                + getString(R.string.practice);

        if (Helper.makeDirectory(path)) {
            //Discipline Directory
            path += File.separator + activity.getTitle();
            if (Helper.makeDirectory(path)) {
                //File Path
                path += File.separator + ((new SimpleDateFormat(
                        "yy-MM-dd_HH:mm", Locale.getDefault())).format(new Date())) + ".txt";
                try {
                    FileOutputStream outputStream = new FileOutputStream(new File(path));

                    for (Integer i : randomList)// 0; i < randomList.size(); i++)
                        stringBuilder.append(Integer.toString(i)).append("\n");
                    //\n is also a delimiter used in recall

                    outputStream.write(stringBuilder.toString().getBytes());
                    outputStream.close();
                    Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return false;
    }

    @Override
    public boolean reset() {
        mPosition = 0;
        gridView.setAdapter(new CardAdapter(activity, new ArrayList<Integer>()));
        randomList.clear();
        numbersVisibility(GONE);
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.VISIBLE);
        cardAndSpeechImageView.setImageDrawable(null);
        return super.reset();
        //findViewById(R.id.cards).setVisibility(View.GONE);
        //findViewById(R.id.progress_bar_discipline).setVisibility(View.GONE);
    }

    @Override
    protected void startCommon() {
        super.startCommon();
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(GONE);
    }

    private class CardAdapter extends ArrayAdapter<Integer> {

        CardAdapter(Activity context, ArrayList<Integer> cards) {
            super(context, 0, cards);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (convertView == null) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setVisibility(View.VISIBLE);
                imageView.setAdjustViewBounds(true);
                //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //imageView.setPadding(8, 8, 8, 8);
            }

            Picasso
                    //.setLoggingEnabled(true)
                    .get()
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
            try {
                Scanner scanner = new Scanner(strings[0]).useDelimiter(getString(R.string.tab));
                ArrayList<Integer> list = new ArrayList<>();

                while (scanner.hasNext()) {
                    list.add(Integer.parseInt(scanner.next()));
                    if (a.get(RUNNING) == FALSE) return list;
                }
                return list;
            } catch (IllegalStateException e) {
                throw new RuntimeException("IllegalStateException from ViewPager.populate() " +
                        "caused in Cards.RandomArrayScanner.doInBackground()");
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Integer> list) {
            try {
                super.onPostExecute(list);
                (rootView.findViewById(R.id.save)).setVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.progress_bar_discipline)).setVisibility(GONE);

                if (a.get(RUNNING) == FALSE) {
                    reset();
                    return;
                }

                randomList = list;

                if (mSingleCard) setCard();
                else {
                    numbersVisibility(View.VISIBLE);
                    Timber.v("Setting the card adapter");
                    CardAdapter adapter = new CardAdapter(activity, randomList);
                    gridView.setAdapter(adapter);
                    Timber.v("card adapter set");
                }
                //((TextView) findViewById(R.id.numbers)).setText(s);
                numbersVisibility(View.VISIBLE);
                (rootView.findViewById(R.id.no_of_values)).setVisibility(GONE);
                if (mSingleCard) Toast.makeText(activity, "Tap the card for the next card",
                        Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
                throw new RuntimeException("IllegalStateException from ViewPager.populate() " +
                        "caused in Cards.RandomArrayScanner.onPostExecute()");
            }
        }
    }

}
//TODO: uses custom theme(), don't remove this comment