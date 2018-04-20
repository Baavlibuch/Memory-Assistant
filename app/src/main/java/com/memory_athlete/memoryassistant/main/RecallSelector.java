package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.memory_athlete.memoryassistant.data.Helper;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

public class RecallSelector extends AppCompatActivity {
    int listViewId = 0, MIN_DYNAMIC_VIEW_ID = 0;
    File dir = null;
    String mDiscipline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_space);
        Helper.theme(this, this);
        setTitle(R.string.chose_discipline);

        if (!Helper.mayAccessStorage(this)) {
            Snackbar.make(findViewById(R.id.my_space_relative_layout),
                    "Storage permissions are required", Snackbar.LENGTH_SHORT)
                    .setAction("Grant", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
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
            RelativeLayout relativeLayout = findViewById(R.id.my_space_relative_layout);

            if (relativeLayout.findViewById(listViewId) != null)
                relativeLayout.removeViewAt(listViewId);

            if (relativeLayout.findViewById(--listViewId) != null) {
                relativeLayout.findViewById(listViewId).setVisibility(View.VISIBLE);
                if (listViewId == MIN_DYNAMIC_VIEW_ID)
                    findViewById(R.id.add).setVisibility(View.GONE);

                setTitle(getString(R.string.chose_discipline));
                return;
            }
        }
        super.onBackPressed();
    }

    public void setAdapter() {
        Timber.v("setAdapter started");
        ArrayList<Item> arrayList = new ArrayList<>();
        if (listViewId == MIN_DYNAMIC_VIEW_ID) arrayList = setList();
        else {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return;
            else {
                for (File file : files) {
                    Timber.d("FileName: " + file.getName());
                    arrayList.add(new Item(file.getName()));
                }
            }
        }
        Timber.v("list set");
        MySpaceAdapter adapter = new MySpaceAdapter(this, arrayList);
        final ListView listView = new ListView(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (listViewId == MIN_DYNAMIC_VIEW_ID) {
            float scale = getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (16 * scale + 0.5f);
            layoutParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        }
        listView.setLayoutParams(layoutParams);
        //if (listViewId==MIN_DYNAMIC_VIEW_ID) listView.MarginLayoutParams
        listView.setId(listViewId);
        final RelativeLayout layout = findViewById(R.id.my_space_relative_layout);
        layout.addView(listView);
        listView.setAdapter(adapter);
        final ArrayList<Item> finalArrayList = arrayList;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = finalArrayList.get(position);
                Timber.v("item.mPath = " + item.mFileName);
                if (listViewId == MIN_DYNAMIC_VIEW_ID) {
                    mDiscipline = item.mFileName;
                    dir = new File(Helper.APP_FOLDER + File.separator
                            + getString(R.string.practice) + File.separator + mDiscipline);

                    File[] files = dir.listFiles();
                    if (files == null) throw new RuntimeException("the discipline contains null " +
                            "instead of the practice folder");
                    if (files.length == 0) {
                        practice(Helper.APP_FOLDER + File.separator
                                + getString(R.string.practice) + File.separator + mDiscipline);
                        return;
                    }

                    layout.findViewById(listViewId).setVisibility(View.GONE);
                    listViewId++;
                    setTitle(item.mName);
                    findViewById(R.id.add).setVisibility(View.VISIBLE);
                    setTitle(R.string.chose_file);
                    setAdapter();
                    Timber.v("going to id 1, listViewId = " + listViewId);
                } else {
                    Timber.v("listViewId = " + listViewId);
                    String filePath = Helper.APP_FOLDER + File.separator
                            + getString(R.string.practice) + File.separator + getTitle();
                    Intent intent = new Intent(getApplicationContext(), Recall.class);
                    intent.putExtra("name", item.mName);
                    intent.putExtra("file", item.mFileName);
                    intent.putExtra("filePath", filePath);
                    intent.putExtra("discipline", mDiscipline);

                    File file = new File(filePath);
                    boolean isDirectoryCreated = file.exists();
                    if (!isDirectoryCreated) isDirectoryCreated = file.mkdir();
                    if (isDirectoryCreated) startActivity(intent);
                    else Toast.makeText(getApplicationContext(), "Try again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void practice(final String discipline) {
        Snackbar.make(findViewById(0), "Nothing saved, try practicing", Snackbar.LENGTH_SHORT)
                .setAction(R.string.practice, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String s = discipline;
                        if (s.equals(getString(R.string.digits)))
                            s = getString(R.string.numbers);
                        s = s.replaceAll("\\s", "");
                        Timber.v("s= " + s);

                        int classId;
                        if (discipline.equals(getString(R.string.numbers))) {
                            classId = 1;
                        } else if (discipline.equals(getString(R.string.digits))) {
                            classId = 1;
                        } else if (discipline.equals(getString(R.string.words))) {
                            classId = 2;
                        } else if (discipline.equals(getString(R.string.names))) {
                            classId = 3;
                        } else if (discipline.equals(getString(R.string.places_capital))) {
                            classId = 4;
                        } else if (discipline.equals(getString(R.string.cards))) {
                            classId = 5;
                        } else if (discipline.equals(getString(R.string.binary))) {
                            classId = 6;
                        } else if (discipline.equals(getString(R.string.letters))) {
                            classId = 7;
                        } else //if(discipline.equals(getString(R.string.dates)))
                        {
                            classId = 8;
                        }

                        Timber.v("classId = " + classId);
                        try {
                            //Timber.d("com.memory_athlete.memoryassistant.disciplines." + s);
                            Intent i = new Intent(getApplicationContext(), DisciplineActivity.class);
                            i.putExtra("class", classId);
                            i.putExtra("name", s);
                            startActivity(i);
                        }/* catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(Recall.this, R.string.report_to_dev, Toast.LENGTH_SHORT).show();
                        }*/ catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }//TODO : trow the exception
                    }
                }).show();
    }

    private ArrayList<Item> setList() {
        String[] list = {
                getString(R.string.digits),
                getString(R.string.binary),
                getString(R.string.cards),
                getString(R.string.letters),
                getString(R.string.names),
                getString(R.string.numbers),
                getString(R.string.places_capital),
                getString(R.string.words),
                getString(R.string.dates)
        };
        ArrayList<Item> list1 = new ArrayList<>();
        for (String item : list) list1.add(new Item(item));
        return list1;
    }

    private class MySpaceAdapter extends ArrayAdapter<Item> {

        MySpaceAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (listViewId == MIN_DYNAMIC_VIEW_ID) {
                if (listItemView == null)
                    listItemView = LayoutInflater.from(getContext()).inflate(R.layout.category,
                            null, true);
                ((TextView) listItemView.findViewById(R.id.text)).setText(getItem(position).mName);
                return listItemView;
            }

            if (listItemView == null) listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_main, null, true);

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(getItem(position).mName);
            return listItemView;
        }
    }

    private class Item {
        String mFileName, mName;

        Item(String fileName) {
            mFileName = fileName;
            mName = fileName.endsWith(".txt") ? fileName.substring(0, fileName.length() - 4) : fileName;
            Timber.v("Item set!");
        }
    }
}

//TODO: open the recall activity and test everything