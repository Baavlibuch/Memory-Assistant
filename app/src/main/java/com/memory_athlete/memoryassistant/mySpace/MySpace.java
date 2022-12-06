package com.memory_athlete.memoryassistant.mySpace;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import timber.log.Timber;

public class MySpace extends AppCompatActivity {
    int listViewId, MIN_DYNAMIC_VIEW_ID = 1;
    File dir = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.theme(this, MySpace.this);
        try {
            setContentView(R.layout.activity_my_space);
        } catch (Resources.NotFoundException e) {
            // Timber.e(e);
            Toast.makeText(this, R.string.dl_from_play, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setTitle(getString(R.string.my_space));
        Timber.v("Title Set");
        listViewId = MIN_DYNAMIC_VIEW_ID;

        if (!Helper.mayAccessStorage(this)) {
            Snackbar.make(findViewById(R.id.my_space_relative_layout),
                    "Storage permissions are required", Snackbar.LENGTH_SHORT)
                    .setAction("Grant", view -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    })
                    .show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.v("listViewId = %s", listViewId);
        if (findViewById(R.id.my_space_relative_layout).findViewById(listViewId) != null)
            ((RelativeLayout) findViewById(R.id.my_space_relative_layout)).removeViewAt(listViewId);
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        if (listViewId != 0) {
            Timber.v("listViewId = %s", listViewId);
            RelativeLayout relativeLayout = findViewById(R.id.my_space_relative_layout);
            if (relativeLayout.findViewById(listViewId) != null)
                relativeLayout.removeViewAt(listViewId);
            if (relativeLayout.findViewById(--listViewId) != null) {
                relativeLayout.findViewById(listViewId).setVisibility(View.VISIBLE);
                if (listViewId == MIN_DYNAMIC_VIEW_ID)
                    findViewById(R.id.add).setVisibility(View.GONE);
                setTitle(getString(R.string.my_space));
                return;
            }
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void setAdapter() {
        Timber.v("setAdapter started");
        ArrayList<Item> arrayList = new ArrayList<>();
        if (listViewId == MIN_DYNAMIC_VIEW_ID) arrayList = setList();

        else {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return;
            for (File file : files) {
                Timber.d("FileName: %s", file.getName());
                arrayList.add(new Item(file.getName(), WriteFile.class));
            }
        }
        Timber.v("list set");

        MySpaceAdapter adapter = new MySpaceAdapter(this, arrayList);
        final ListView listView = new ListView(this);

        listView.setDivider(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        listView.setDividerHeight(0);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        float scale = getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (8 * scale + 0.5f);
        layoutParams.setMargins(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
        listView.setLayoutParams(layoutParams);

        //if (listViewId==MIN_DYNAMIC_VIEW_ID) listView.MarginLayoutParams
        listView.setId(listViewId);

        final RelativeLayout layout = findViewById(R.id.my_space_relative_layout);
        layout.addView(listView);
        listView.setAdapter(adapter);

        final ArrayList<Item> finalArrayList = arrayList;
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = finalArrayList.get(position);
            Timber.v("item.mPath = %s", item.mItem);
            if (listViewId == MIN_DYNAMIC_VIEW_ID) {

                //Directory of practice - external storage
                int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CODE);
                File folder = getFilesDir();
                dir = new File(folder + File.separator +getString(R.string.my_space) + File.separator + item.mItem);

                //dir = new File(Helper.APP_FOLDER + getString(R.string.my_space) + File.separator + item.mItem);
                layout.findViewById(listViewId).setVisibility(View.GONE);
                listViewId++;
                setTitle(item.mName);
                findViewById(R.id.add).setVisibility(View.VISIBLE);
                setAdapter();
                Timber.v("going to id 1, listViewId = %s", listViewId);
                return;
            }

            Timber.v("listViewId = %s", listViewId);

            //Directory of practice - external storage
            int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_CODE);
            File folder = getFilesDir();
            String fileName = folder + File.separator + getString(R.string.my_space) + File.separator + getTitle().toString();

            //String fileName = Helper.APP_FOLDER + getString(R.string.my_space) + File.separator + getTitle();

            Intent intent = new Intent(getApplicationContext(), WriteFile.class);
            intent.putExtra("mHeader", item.mName);
            intent.putExtra("fileString", item.mItem);
            intent.putExtra("fileName", fileName);

            File file = new File(fileName);
            boolean isDirectoryCreated = file.exists();
            if (!isDirectoryCreated) isDirectoryCreated = file.mkdirs();
            if (isDirectoryCreated) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
        });
    }

    public void add(View view) {
        Intent intent = new Intent(getApplicationContext(), WriteFile.class);
        intent.putExtra("mHeader", getTitle());
        intent.putExtra("name", false);

        //Directory of practice - external storage
        int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_PERMISSION_CODE);
        File folder = getFilesDir();
        intent.putExtra("fileName", folder + File.separator + getString(R.string.my_space) + File.separator + getTitle());

        //intent.putExtra("fileName", Helper.APP_FOLDER + getString(R.string.my_space) + File.separator + getTitle());
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private ArrayList<Item> setList() {
        return new ArrayList<>(Arrays.asList(
                new Item(getString(R.string.majors), R.drawable.major_system, WriteFile.class),
                new Item(getString(R.string.wardrobes), R.drawable.wardrobe_method, WriteFile.class),
                new Item(getString(R.string.lists), R.drawable.lists, WriteFile.class),
                new Item(getString(R.string.words), R.drawable.vocabulary, WriteFile.class)));
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

    // define each list item of mySpace
    private class MySpaceAdapter extends ArrayAdapter<Item> {

        MySpaceAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
            Item item = Objects.requireNonNull(getItem(position));
            if (listViewId == MIN_DYNAMIC_VIEW_ID) {
                if (listItemView == null) listItemView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_category, null, true);

                TextView textView = listItemView.findViewById(R.id.text);
                textView.setText(item.mName);
                ImageView img = listItemView.findViewById(R.id.image);
                Picasso
                        .get()
                        .load(item.mImageId)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(img);

                return listItemView;
            }
            if (listItemView == null) listItemView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_file, null, true);

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(item.mName);

            return listItemView;
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}

//major, ben, equations, derivations, lists, algos, words, wardrobes