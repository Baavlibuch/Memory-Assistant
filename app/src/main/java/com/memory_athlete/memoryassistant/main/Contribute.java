package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.inAppBilling.DonateActivity;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class Contribute extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.theme(this, Contribute.this);
        setContentView(R.layout.activity_contribute);
        setTitle(R.string.get_pro);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        setAdapter();
    }


    // setting the adapter for displaying the list support, fund,..
    public void setAdapter() {
        final List<Item> list = setList();

        MainAdapter adapter = new MainAdapter(this, list);
        ListView listView = findViewById(R.id.contribute_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = list.get(position);
            switch (item.type) {
                case PLAY_STORE:                                                    // review
                    mFirebaseAnalytics.logEvent("wants_to_review", null);
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "market://details?id=" + getPackageName())));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                    break;
                case ACTIVITY:                                                      // donate
                    mFirebaseAnalytics.logEvent("wants_to_donate", null);
                    startActivity(new Intent(getApplicationContext(), DonateActivity.class));
                    break;
                case GITHUB:                                                        // code
                    mFirebaseAnalytics.logEvent("wants_to_code", null);
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/maniksejwal/Memory-Assistant")));
                    break;
                case EMAIL:
                    Toast.makeText(getApplicationContext(),
                            "Your eMail address will be added to the list of alpha testers manually",
                            Toast.LENGTH_LONG).show();
                    String mailto = "mailto:memoryassistantapp@gmail.com" +
                            "?cc=" + "" +
                            "&subject=" + Uri.encode("Alpha tester") +
                            "&body=" + Uri.encode("I'd like to join the alpha testers");
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse(mailto));

                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(),
                                "No eMail application found", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CREDITS:
                    startActivity(new Intent(getApplicationContext(), CreditsActivity.class));
                    break;
            }
        });
        Timber.v("Adapter set!");
    }

    // defining the list items
    private List<Item> setList() {
        String[] headers = getResources().getStringArray(R.array.contribute_headers);
        String[] bodies = getResources().getStringArray(R.array.contribute_bodies);
        return Arrays.asList(
                new Item(headers[0], bodies[0], PART_TYPE.PLAY_STORE),
                //new Item(headers[1], bodies[1], PART_TYPE.EMAIL),             alpha testers no longer needed
                new Item(headers[2], bodies[2], PART_TYPE.ACTIVITY),
                new Item(headers[3], bodies[3], PART_TYPE.GITHUB),
                new Item(headers[4], bodies[4], PART_TYPE.CREDITS));
    }

    private class Item {
        String head, body;
        PART_TYPE type;

        Item(String title, String body, PART_TYPE type) {
            this.head = title;
            this.body = body;
            this.type = type;
        }
    }

    // defining each item of adapter
    private class MainAdapter extends ArrayAdapter<Item> {

        MainAdapter(Activity context, List<Item> words) {
            super(context, 0, words);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_contribute, parent, false);

            Item item = getItem(position);
            assert item != null;
            ((TextView) convertView.findViewById(R.id.contribute_head)).setText(item.head);
            ((TextView) convertView.findViewById(R.id.contribute_body)).setText(item.body);

            return convertView;
        }
    }

    enum PART_TYPE {PLAY_STORE, ACTIVITY, GITHUB, EMAIL, CREDITS}

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
