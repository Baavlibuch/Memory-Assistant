package com.memory_athlete.memoryassistant.mySpace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.compat.BuildConfig;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

public class MySpace extends AppCompatActivity {
    private static final String LOG_TAG = "\tMySpace :";
    int listViewId = 0;
    File dir = null;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        theme();
        listViewId++;
        //setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.v("listViewId = " + listViewId);
        if (listViewId > 1) {
            ((RelativeLayout) findViewById(R.id.my_space_relative_layout)).removeViewAt(listViewId);
        }
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        if (listViewId==1) super.onBackPressed();
        else {
            Timber.v("listViewId = " + listViewId);
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.my_space_relative_layout);
            relativeLayout.removeViewAt(listViewId);
            relativeLayout.findViewById(--listViewId).setVisibility(View.VISIBLE);
            setTitle(Html.fromHtml(title + getString(R.string.my_space)));
        }
    }

    protected void theme() {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme");
        switch (theme) {
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
                title = "<font color=#FFFFFF>";
        }
        setContentView(R.layout.activity_my_space);
        setTitle(Html.fromHtml(title + getString(R.string.my_space)));
        //path = intent.getStringExtra(getString(R.string.apply));
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "Title Set");
    }

    public void setAdapter() {
        Timber.v("setAdapter started");
        ArrayList<Item> arrayList = new ArrayList<>();
        if (listViewId == 1) arrayList = setList();
        else {
            File[] files = dir.listFiles();
            if (files == null) {
                return;
            }
            if (files.length == 0) {
                return;
            } else {
                for (File file : files) {
                    Timber.d("FileName: " + file.getName());
                    arrayList.add(new Item(file.getName(), WriteFile.class));
                }
            }
        }
        Timber.v("list set");
        MySpaceAdapter adapter = new MySpaceAdapter(this, arrayList);
        final ListView listView = new ListView(this);
        listView.setLayoutParams(new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        listView.setId(listViewId);
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.my_space_relative_layout);
        layout.addView(listView);
        listView.setAdapter(adapter);
        final ArrayList<Item> finalArrayList = arrayList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = finalArrayList.get(position);
                Timber.v("item.mItem = " + item.mItem);
                if (listViewId == 1) {
                    dir = new File(getFilesDir().getAbsolutePath() + File.separator + item.mItem);
                    layout.findViewById(listViewId).setVisibility(View.GONE);
                    listViewId++;
                    setTitle(Html.fromHtml(title + item.mName));
                    findViewById(R.id.floatingActionButton).setVisibility(View.VISIBLE);
                    setAdapter();
                    Timber.v("going to id 1, listViewId = " + listViewId);
                } else {
                    Timber.v("listViewId = " + listViewId);
                    String fileName = getFilesDir().getAbsolutePath() + File.separator + getTitle();
                    Intent intent = new Intent(getApplicationContext(), WriteFile.class);
                    intent.putExtra("mHeader", item.mName);
                    intent.putExtra("fileString", item.mItem);
                    intent.putExtra("path", fileName);
                    File file = new File(fileName);
                    boolean isDirectoryCreated = file.exists();
                    if (!isDirectoryCreated) {
                        isDirectoryCreated = file.mkdir();
                    }
                    if (isDirectoryCreated) {
                        startActivity(intent);
                    } else
                        Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void add(View view) {
        Intent intent = new Intent(getApplicationContext(), WriteFile.class);
        intent.putExtra("mHeader", getTitle());
        intent.putExtra("name", false);
        intent.putExtra("path", getFilesDir().getAbsolutePath() + File.separator + getTitle());
        startActivity(intent);
    }

    ArrayList<Item> setList() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item(getString(R.string.majors), WriteFile.class));
        list.add(new Item(getString(R.string.ben), WriteFile.class));
        list.add(new Item(getString(R.string.wardrobes), WriteFile.class));
        list.add(new Item(getString(R.string.lists), WriteFile.class));
        list.add(new Item(getString(R.string.words), WriteFile.class));
        //TODO:
        //list.add(new Item(getString(R.string.equations), WriteEquations.class));
        //list.add(new Item(getString(R.string.algos), WriteAlgo.class));
        //list.add(new Item(getString(R.string.derivations), WriteEquations.class));
        return list;
    }

    private class Item {
        String mItem, mName;
        Class mClass;

        Item(String itemName, Class class1) {
            mItem = itemName;
            mName = itemName.endsWith(".txt") ? itemName.substring(0, itemName.length() - 4) : itemName;
            mClass = class1;
            if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Item set!");
        }
    }

    private class MySpaceAdapter extends ArrayAdapter<Item> {

        MySpaceAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, null, true);
            }

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(getItem(position).mName);

            return listItemView;
        }
    }
}

//major, ben, equations, derivations, lists, algos, words, wardrobes