package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.inAppBilling.DonateActivity;

import java.util.Arrays;
import java.util.List;

import timber.log.Timber;


public class Contribute extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Helper.theme(this, Contribute.this);
        setContentView(R.layout.activity_get_pro);
        setTitle(R.string.get_pro);
        setAdapter();
    }

    public void setAdapter() {
        final List<Item> list = setList();

        MainAdapter adapter = new MainAdapter(this, list);
        ListView listView = findViewById(R.id.contribute_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = list.get(position);
                switch (item.type) {
                    case PLAY_STORE:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "market://details?id=" + getPackageName())));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                                    "https://play.google.com/store/apps/details?id=" + getPackageName())));
                        }
                        break;
                    case ACTIVITY:
                        startActivity(new Intent(getApplicationContext(), DonateActivity.class));
                        break;
                    case GITHUB:
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
            }
        });
        Timber.v("Adapter set!");
    }

    private List<Item> setList() {
        String[] headers = getResources().getStringArray(R.array.contribute_headers);
        String[] bodies = getResources().getStringArray(R.array.contribute_bodies);
        return Arrays.asList(
                new Item(headers[0], bodies[0], PART_TYPE.PLAY_STORE),
                new Item(headers[1], bodies[1], PART_TYPE.EMAIL),
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
}
