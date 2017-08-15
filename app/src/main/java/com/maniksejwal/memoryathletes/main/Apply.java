package com.maniksejwal.memoryathletes.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.lessons.Lessons;

import java.io.IOException;
import java.util.ArrayList;

public class Apply extends AppCompatActivity {
    String path;
    private static final String TAG = "Log : ";
    int listViewId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        setTitle(getString(R.string.apply));
        Intent intent = getIntent();
        path = intent.getStringExtra(getString(R.string.apply));
        Log.v(TAG, "Title Set");

        setAdapter();
    }

    public void setAdapter() {
        try {
            String[] list = listAssetFiles(path);
            Log.v(TAG, "list set");
            if (list == null) {
                Toast.makeText(this, "Empty List!", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.v(TAG, "list.size() = " + list.length);
            final ArrayList<Item> arrayList = new ArrayList<>();
            for (String i : list) arrayList.add(new Item(i, true));
            Log.v(TAG, "arrayList set");
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
                    String string = item.mItem;

                    if (string.endsWith(".txt")) {
                        Intent intent = new Intent(getApplicationContext(), Lessons.class);
                        intent.putExtra("headerString", item.mItem);
                        intent.putExtra("webView", true);
                        intent.putExtra("fileString", path + "/" + string);
                        startActivity(intent);
                    } else {
                        path += "/" + string;
                        linearLayout.findViewById(listViewId).setVisibility(View.GONE);
                        listViewId++;
                        setAdapter();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error in setAdapter", Toast.LENGTH_SHORT).show();
        }
    }


    private String[] listAssetFiles(String path) {

        String[] list = new String[0];
        try {
            list = getAssets().list(path);
            Log.v(TAG, "got assets");
        } catch (IOException e) {
            Toast.makeText(this, "empty directory", Toast.LENGTH_SHORT).show();
            Log.v(TAG, "couldn't get assets");
        }
        return list;
    }

    private class Item {
        String mItem;
        boolean webView = false;

        Item(String item, boolean wV) {
            mItem = item;
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
