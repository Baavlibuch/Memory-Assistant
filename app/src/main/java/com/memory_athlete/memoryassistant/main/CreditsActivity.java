package com.memory_athlete.memoryassistant.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) parent.getItemAtPosition(position);
                String url = item.mUrl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

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

    private class CreditAdapter extends ArrayAdapter<Item> {

        CreditAdapter(Context context, ArrayList<Item> list) {
            super(context, 0, list);
            Timber.v("CreditAdapter() entered");
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(final int position, View listItemView, @NonNull final ViewGroup parent) {
            Item item = getItem(position);
            assert item != null;

            TextView textView = new TextView(getApplicationContext());
            textView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
                    ListView.LayoutParams.WRAP_CONTENT));
            textView.setText(item.mName);
            parent.addView(textView);

            return listItemView;
        }
    }
}
