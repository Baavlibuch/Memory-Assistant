package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;
import com.memory_athlete.memoryassistant.lessons.ImplementLesson;

import java.io.IOException;
import java.util.ArrayList;

import timber.log.Timber;

public class Implement extends AppCompatActivity {
    ArrayList<String> pathList = new ArrayList<>();
    int listViewId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Helper.theme(this, Implement.this);
        setContentView(R.layout.activity_implement_list);
        setTitle(getString(R.string.apply));
        Timber.v("Title Set");
        pathList.add(intent.getStringExtra(getString(R.string.apply)));

        setAdapter();
    }

    @Override
    public void onBackPressed() {
        if (listViewId == 0) {
            super.onBackPressed();
            return;
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.apply_layout);
        linearLayout.removeViewAt(listViewId--);
        linearLayout.findViewById(listViewId).setVisibility(View.VISIBLE);
        pathList.remove(pathList.size() - 1);
    }

    public void setAdapter() {
        try {
            StringBuilder path = new StringBuilder("");
            for (String i : pathList) path.append(i);
            Timber.v("path = " + path);
            String[] list = listAssetFiles(path.toString());
            Timber.v("list set");
            if (list == null) {
                Toast.makeText(this, "Nothing here", Toast.LENGTH_SHORT).show();
                return;
            }
            Timber.v("list.size() = " + list.length);
            final ArrayList<Item> arrayList = new ArrayList<>();
            for (String i : list) arrayList.add(new Item(i, true));
            Timber.v("arrayList set");
            ApplyAdapter adapter = new ApplyAdapter(this, arrayList);
            ListView listView = new ListView(this);
            listView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
            listView.setId(listViewId);
            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.apply_layout);
            linearLayout.addView(listView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    Item item = arrayList.get(position);
                    boolean webView, hasList;
                    if (item.mFileName.equals("Vocabulary.txt")) {
                        webView = false;
                        hasList = true;
                    } else {
                        webView = true;
                        hasList = false;
                    }

                    if (item.mFileName.endsWith(".txt")) {
                        Intent intent = new Intent(getApplicationContext(), ImplementLesson.class);
                        intent.putExtra("headerString", item.mItem);
                        intent.putExtra("webView", webView);
                        intent.putExtra("list", hasList);
                        intent.putExtra("resource", true);
                        StringBuilder path = new StringBuilder("");
                        for (String i : pathList) path.append(i);
                        intent.putExtra("fileString", path + "/" + item.mFileName);
                        startActivity(intent);
                    } else {
                        pathList.add("/" + item.mFileName);
                        linearLayout.findViewById(listViewId).setVisibility(View.GONE);
                        listViewId++;
                        setAdapter();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private String[] listAssetFiles(String path) {

        String[] list = new String[0];
        try {
            list = getAssets().list(path);
            Timber.v("got assets");
        } catch (IOException e) {
            Toast.makeText(this, "Nothing here", Toast.LENGTH_SHORT).show();
            Timber.v("couldn't get assets");
        }
        return list;
    }

    private class Item {
        String mItem, mFileName;
        boolean webView = false;

        Item(String item, boolean wV) {
            mFileName = item;
            mItem = item.endsWith(".txt") ? item.substring(0, item.length() - 4) : item;
            webView = wV;
        }
    }

    private class ApplyAdapter extends ArrayAdapter<Item> {

        ApplyAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, null, true);
            }

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(getItem(position).mItem);

            return listItemView;
        }
    }
}
