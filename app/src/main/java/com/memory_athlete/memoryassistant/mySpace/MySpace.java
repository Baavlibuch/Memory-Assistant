package com.memory_athlete.memoryassistant.mySpace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.MakeList;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

public class MySpace extends AppCompatActivity {
    int listViewId = 0;
    File dir = null;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MakeList.theme(this, MySpace.this);
        setContentView(R.layout.activity_my_space);
        setTitle(title + getString(R.string.my_space));
        Timber.v("Title Set");
        listViewId++;
        //findViewById(R.id.)
        //setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.v("listViewId = " + listViewId);
        if (findViewById(R.id.my_space_relative_layout).findViewById(listViewId) != null)
            ((RelativeLayout) findViewById(R.id.my_space_relative_layout)).removeViewAt(listViewId);
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        if (listViewId != 0) {
            Timber.v("listViewId = " + listViewId);
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.my_space_relative_layout);
            if (relativeLayout.findViewById(listViewId) != null)
                relativeLayout.removeViewAt(listViewId);
            if (relativeLayout.findViewById(--listViewId) != null) {
                relativeLayout.findViewById(listViewId).setVisibility(View.VISIBLE);
                if (listViewId == 0)
                    findViewById(R.id.add).setVisibility(View.GONE);
                setTitle(title + getString(R.string.my_space));
                return;
            }
        }
        super.onBackPressed();
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
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (listViewId == 1) {
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16 * scale + 0.5f);
            layoutParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
        listView.setLayoutParams(layoutParams);
        //if (listViewId==1) listView.MarginLayoutParams
        listView.setId(listViewId);
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.my_space_relative_layout);
        layout.addView(listView);
        listView.setAdapter(adapter);
        final ArrayList<Item> finalArrayList = arrayList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = finalArrayList.get(position);
                Timber.v("item.mPath = " + item.mItem);
                if (listViewId == 1) {
                    dir = new File(getFilesDir().getAbsolutePath() + File.separator + item.mItem);
                    layout.findViewById(listViewId).setVisibility(View.GONE);
                    listViewId++;
                    setTitle(title + item.mName);
                    findViewById(R.id.add).setVisibility(View.VISIBLE);
                    setAdapter();
                    Timber.v("going to id 1, listViewId = " + listViewId);
                } else {
                    Timber.v("listViewId = " + listViewId);
                    String fileName = getFilesDir().getAbsolutePath() + File.separator + getTitle();
                    Intent intent = new Intent(getApplicationContext(), WriteFile.class);
                    intent.putExtra("mHeader", item.mName);
                    intent.putExtra("fileString", item.mItem);
                    intent.putExtra("fileName", fileName);
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
        intent.putExtra("fileName", getFilesDir().getAbsolutePath() + File.separator + getTitle());
        startActivity(intent);
    }

    ArrayList<Item> setList() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item(getString(R.string.majors), R.drawable.major_system, WriteFile.class));
        list.add(new Item(getString(R.string.ben), R.drawable.ben_system, WriteFile.class));
        list.add(new Item(getString(R.string.wardrobes), R.drawable.wardrobe_method, WriteFile.class));
        list.add(new Item(getString(R.string.lists), R.drawable.lists, WriteFile.class));
        list.add(new Item(getString(R.string.words), R.drawable.words1, WriteFile.class));
        //TODO:
        //list.add(new Item(getString(R.string.equations), WriteEquations.class));
        //list.add(new Item(getString(R.string.algos), WriteAlgo.class));
        //list.add(new Item(getString(R.string.derivations), WriteEquations.class));
        return list;
    }

    private class Item {
        String mItem, mName;
        Class mClass;
        int mImageId;

        Item(String itemName, int im, Class class1) {
            mItem = itemName;
            mName = itemName.endsWith(".txt") ? itemName.substring(0, itemName.length() - 4) : itemName;
            mClass = class1;
            mImageId = im;
            Timber.v("Item set!");
        }

        Item(String itemName, Class class1) {
            mItem = itemName;
            mName = itemName.endsWith(".txt") ? itemName.substring(0, itemName.length() - 4) : itemName;
            mClass = class1;
            Timber.v("Item set!");
        }
    }

    private class MySpaceAdapter extends ArrayAdapter<Item> {

        MySpaceAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (listViewId == 1) {
                if (listItemView == null) {
                    listItemView = LayoutInflater.from(getContext()).inflate(R.layout.category, null, true);
                }

                TextView textView = listItemView.findViewById(R.id.text);
                textView.setText(getItem(position).mName);
                ImageView img = listItemView.findViewById(R.id.image);
                Picasso
                        .with(getApplicationContext())
                        .load(getItem(position).mImageId)
                        .placeholder(R.mipmap.launcher_ic)
                        .fit()
                        .centerCrop()
                        .into(img);

                return listItemView;
            }
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