package com.memory_athlete.memoryassistant.recall;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;
import com.memory_athlete.memoryassistant.main.RecallSimple;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Scanner;

import timber.log.Timber;

import static com.memory_athlete.memoryassistant.data.Helper.makeCardString;
import static java.lang.Integer.parseInt;

public class RecallCards extends RecallSimple {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme();
    }

    private void theme() {
        String theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.theme), "AppTheme");
        switch (theme) {
            case "Dark":
                mSuitBackground = R.color.color_suit_background_dark;
                gridView = findViewById(R.id.cards_responses);
                gridView.setAlpha((float) 0.8);
                break;
            case "Night":
                mSuitBackground = R.color.color_suit_background_night;
                gridView = findViewById(R.id.cards_responses);
                gridView.setAlpha((float) 0.7);
                break;
            default:
                mSuitBackground = R.color.color_suit_background_light;
                gridView = findViewById(R.id.cards_responses);
        }
    }

    void updateGridView() {
        CardAdapter adapter = new CardAdapter(this, responses);
        gridView.setAdapter(adapter);
    }

    void cardSelected(int card) {
        card = (card == 0 ? 12 : card - 1);
        responses.add(String.valueOf(card + selectedSuit));
        updateGridView();
        //mAdapter.notifyDataSetChanged();
        Timber.v("cardSelected complete");
    }

    @Override
    protected void setResponseLayout() {
        gridView.setNumColumns(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
                this).getString(getString(R.string.recall_grid_columns), "6")));
        cardImageIds = Helper.makeCards();
        Timber.v("cardResponseLayout() started");
        gridView.setVisibility(View.VISIBLE);
        compareFormat = 2;
        responseFormat = 2;

        LinearLayout suitLayout = findViewById(R.id.card_suit);
        if (suitLayout.getChildCount() != 0) return;
        for (int i = 0; i < 4; i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setImageResource(Helper.makeSuits()[i]);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (8 * scale + 0.5f);
            imageView.setPadding(2 * dpAsPixels, dpAsPixels, 2 * dpAsPixels, dpAsPixels);
            imageView.setId(i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int j = 0; j < 4; j++) {
                        findViewById(R.id.card_suit).findViewById(j).setBackgroundColor(0);
                    }
                    view.setBackgroundColor(getResources().getColor(mSuitBackground));
                    selectedSuit = (int) view.getId() * 13;
                    Timber.v("selectedSuit = " + selectedSuit);
                }
            });
            suitLayout.addView(imageView);
        }
        suitLayout.findViewById(0).setBackgroundColor(getResources().getColor(mSuitBackground));

        LinearLayout numberLayout = findViewById(R.id.card_numbers);
        for (int i = 0; i <= 13; i++) {
            TextView textView = new TextView(this);
            textView.setId(i);
            if (i == 0) textView.setText("A");
            else if (i < 10) textView.setText(String.valueOf(i + 1));
            else if (i == 10) textView.setText("J");
            else if (i == 11) textView.setText("Q");
            else if (i == 12) textView.setText("K");
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (8 * scale + 0.5f);
            textView.setPadding(dpAsPixels, 0, dpAsPixels, 0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardSelected(view.getId());
                    Timber.v("Selected Card = " + view.getId());
                }
            });
            numberLayout.addView(textView);
        }

        findViewById(R.id.result).setVisibility(View.GONE);
        findViewById(R.id.response_input).setVisibility(View.GONE);
        findViewById(R.id.card_suit).setVisibility(View.VISIBLE);
        findViewById(R.id.card_numbers).setVisibility(View.VISIBLE);
        gridView.setVisibility(View.VISIBLE);

        findViewById(R.id.recall_layout).setVisibility(View.GONE);
        findViewById(R.id.progress_bar_recall).setVisibility(View.GONE);
        findViewById(R.id.response_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.button_bar).setVisibility(View.VISIBLE);
        findViewById(R.id.reset).setVisibility(View.GONE);

        Timber.v("Card layout set");
    }

    @Override
    protected String formatAnswers(Scanner scanner, StringBuilder sb) {
        String[] cards = makeCardString();

        while (scanner.hasNext())
            sb.append(cards[Integer.parseInt(scanner.next())]).append(whitespace);

        Timber.v("giveUp() complete, returns " + sb.toString());
        return sb.toString();
    }

    @Override
    protected void compare(boolean words) {
        String[] cardStrings = makeCardString();
        for (int i = 0; i < responses.size(); i++) {
            responses.set(i, cardStrings[Integer.parseInt(responses.get(i))]);
        }
        for (int i = 0; i < answers.size(); i++) {
            answers.set(i, cardStrings[Integer.parseInt(answers.get(i))]);
        }
        super.compare(false);
        Timber.v("compareCards() complete");
    }

    @Override
    protected void reset() {
        super.reset();
        updateGridView();
        findViewById(R.id.reset).setVisibility(View.VISIBLE);
    }

    private class CardAdapter extends ArrayAdapter<String> {

        CardAdapter(Activity context, ArrayList<String> cards) {
            super(context, 0, cards);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (convertView == null) {
                imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setVisibility(View.VISIBLE);
                //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setAdjustViewBounds(true);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        responses.remove(position);
                        updateGridView();
                    }
                });
                //imageView.setPadding(8, 8, 8, 8);
            }

            Picasso.with(getApplicationContext())
                    .load(cardImageIds[parseInt(responses.get(position))])
                    .placeholder(R.drawable.sa)
                    .fit()
                    //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                    .into(imageView);

            Timber.v("getView() complete");

            //((ImageView) listItemView.findViewById(R.id.card_image)).setImageResource(
            //      cardImageIds[parseInt(responses.get(position))]);
            return imageView;//listItemView;
        }
    }
}


// TODO: uses custom theme(), don't remove this comment