package com.memory_athlete.memoryassistant.main;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;

import java.util.ArrayList;

import timber.log.Timber;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        ArrayList<Item> items = setList();
        CreditAdapter adapter = new CreditAdapter(getApplicationContext(), items);
        ListView listView = findViewById(R.id.credits_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = (Item) parent.getItemAtPosition(position);
            String url = item.mUrl;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });
    }

    // getting the names to be displayed
    ArrayList<Item> setList(){
        ArrayList<Item> contributors = new ArrayList<>();
        String[] names = getResources().getStringArray(R.array.contributor_names);
        String[] urls = getResources().getStringArray(R.array.contributor_urls);
        for (int i=0; i<names.length; i++) contributors.add(new Item(names[i], urls[i]));
        return contributors;
    }

    private class Item {
        String mUrl, mName;

        Item(String name, String url) {
            mName = name;
            mUrl = url;
        }
    }

    // defining each item of adapter
    private class CreditAdapter extends ArrayAdapter<Item> {

        CreditAdapter(Context context, ArrayList<Item> list) {
            super(context, 0, list);
            Timber.v("CreditAdapter() entered");
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(final int position, View listItemView, @NonNull final ViewGroup parent) {
            TextView textView = (TextView) listItemView;
            if (textView == null) textView = (TextView) LayoutInflater.from(getContext())
                    .inflate(R.layout.text_view_credit, null, true);
            Item item = getItem(position);
            assert item != null;
            textView.setText(item.mName);
            return textView;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
