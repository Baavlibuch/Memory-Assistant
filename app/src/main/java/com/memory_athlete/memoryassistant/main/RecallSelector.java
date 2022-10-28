package com.memory_athlete.memoryassistant.main;

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
import com.memory_athlete.memoryassistant.recall.RecallCards;
import com.memory_athlete.memoryassistant.recall.RecallComplex;
import com.memory_athlete.memoryassistant.recall.RecallSimple;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import timber.log.Timber;

public class RecallSelector extends AppCompatActivity {
    int listViewId, MIN_DYNAMIC_VIEW_ID = 1;
    File dir = null;
    String mDiscipline;
    Class targetClass;
    String intentDisciple;
    boolean intentFileExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Helper.theme(this, this);
            setContentView(R.layout.activity_my_space);
        } catch (Resources.NotFoundException e) {
            // Timber.e(e);
            Toast.makeText(this, R.string.dl_from_play, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setTitle(R.string.recall);

        listViewId = MIN_DYNAMIC_VIEW_ID;
        findViewById(R.id.add).setVisibility(View.GONE);

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
                    }).show();
        }

        Intent intent = getIntent();
        intentDisciple = intent.getStringExtra(getString(R.string.discipline));
        intentFileExists = intent.getBooleanExtra("file exists", false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.v("listViewId = %s", listViewId);
        if (findViewById(R.id.my_space_relative_layout).findViewById(listViewId) != null)
            ((RelativeLayout) findViewById(R.id.my_space_relative_layout)).removeViewAt(listViewId);
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        Timber.v("listViewId = %s", listViewId);
        if (listViewId == MIN_DYNAMIC_VIEW_ID) {
            super.onBackPressed();
            return;
        }
        RelativeLayout relativeLayout = findViewById(R.id.my_space_relative_layout);

        if (relativeLayout.findViewById(listViewId) != null)
            relativeLayout.removeViewAt(listViewId);

        if (relativeLayout.findViewById(--listViewId) == null) return;

        relativeLayout.findViewById(listViewId).setVisibility(View.VISIBLE);
        setTitle(getString(R.string.choose_discipline));
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
                arrayList.add(new Item(file.getName()));
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
        listView.setOnItemClickListener((parent, view, position, id) -> onMyItemClick(finalArrayList, position, layout));

        if (listViewId == MIN_DYNAMIC_VIEW_ID && intentDisciple != null && !intentDisciple.equals("")) {
            ArrayList<String> list = new ArrayList<>();
            for (Item item : setList()) list.add(item.getName());
            int index = list.indexOf(intentDisciple);
            listView.performItemClick(
                    listView.getAdapter().getView(index, null, null),
                    index, listView.getAdapter().getItemId(index));
        } else if (intentFileExists) {
            listView.performItemClick(
                    listView.getAdapter().getView(0, null, null),
                    0, listView.getAdapter().getItemId(0));
            intentFileExists = false;
        }
    }

    private void onMyItemClick(ArrayList<Item> finalArrayList, int position, RelativeLayout layout) {
        Item item = finalArrayList.get(position);
        Timber.v("item.mPath = %s", item.mFileName);
        if (listViewId == MIN_DYNAMIC_VIEW_ID) {
            // selected discipline
            targetClass = item.mClass;
            mDiscipline = item.mFileName;

            //Directory of practice - external storage
            int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_CODE);

            File folder = getFilesDir();
            dir = new File(folder + File.separator
                    + getString(R.string.practice) + File.separator + mDiscipline);

//            dir = new File(Helper.APP_FOLDER + File.separator
//                    + getString(R.string.practice) + File.separator + mDiscipline);
            Timber.v("directory path = %s", dir.getAbsolutePath());

            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                String s = (mDiscipline.equals(getString(R.string.digits)))
                        ? getString(R.string.numbers) : mDiscipline;

                //Directory of practice - external storage
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_PERMISSION_CODE);

                File folder1 = getFilesDir();
                practice(folder1 + File.separator
                        + getString(R.string.practice) + File.separator + s);

//                practice(Helper.APP_FOLDER + File.separator
//                        + getString(R.string.practice) + File.separator + s);
                return;
            }

            layout.findViewById(listViewId).setVisibility(View.GONE);
            listViewId++;
            setTitle(item.mName);
            setTitle(R.string.choose_file);
            setAdapter();
            Timber.v("going to id 1, listViewId = %s", listViewId);
        } else {
            // selected stored file within the discipline
            Timber.v("listViewId = %s", listViewId);

            //Directory of practice - external storage
            int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_CODE);

            File folder = getFilesDir();
            String filePath = folder + File.separator
                    + getString(R.string.practice) + File.separator + mDiscipline + File.separator + item.mFileName;


//            String filePath = Helper.APP_FOLDER + File.separator
//                    + getString(R.string.practice) + File.separator + mDiscipline + File.separator + item.mFileName;
            Intent intent = new Intent(getApplicationContext(), targetClass);        // file's readable name (without extension)
            intent.putExtra("name", item.mName);                               // filepath
            intent.putExtra("file", filePath);                                 // discipline
            intent.putExtra("discipline", mDiscipline);

            Timber.i("RecallSelector/name = " + item.mName);             // file's readable name (without extension)
            Timber.i("RecallSelector/file = " + filePath);               // filepath
            Timber.i("RecallSelector/discipline = " + mDiscipline);      // discipline

            File file = new File(filePath);
            boolean isDirectoryCreated = file.exists();
            if (!isDirectoryCreated) isDirectoryCreated = file.mkdirs();
            if (isDirectoryCreated) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else Toast.makeText(getApplicationContext(), "Try again",
                    Toast.LENGTH_SHORT).show();
        }
    }

    void practice(String disciplinePath) {
        String[] strings = disciplinePath.split("/");
        final String discipline = strings[strings.length - 1];
        Snackbar.make(findViewById(listViewId), "Nothing saved, try practicing", Snackbar.LENGTH_SHORT)
                .setAction(R.string.practice, view -> {
                    String s = discipline;
                    if (s.equals(getString(R.string.digits))) s = getString(R.string.numbers);
                    //s = s.replaceAll("\\s", "");                          why?    remove this!
                    Timber.v("s= %s", s);

                    int classId;
                    if (discipline.equals(getString(R.string.numbers))) classId = 1;
                    else if (discipline.equals(getString(R.string.digits))) classId = 1;
                    else if (discipline.equals(getString(R.string.words))) classId = 2;
                    else if (discipline.equals(getString(R.string.names))) classId = 3;
                    else if (discipline.equals(getString(R.string.places_capital))) classId = 4;
                    else if (discipline.equals(getString(R.string.cards))) classId = 5;
                    else if (discipline.equals(getString(R.string.binary))) classId = 6;
                    else if (discipline.equals(getString(R.string.letters))) classId = 7;
                    else if (discipline.equals(getString(R.string.dates))) classId = 8;
                    else {
                        Helper.fixBug(getApplicationContext());
                        throw new RuntimeException("Practice from recall received unexpected case" +
                                "\tDiscipline = " + discipline);
                    }

                    Timber.v("classId = %s", classId);
                    //Timber.d("com.memory_athlete.memoryassistant.disciplines." + s);
                    Intent i = new Intent(getApplicationContext(), DisciplineActivity.class);
                    i.putExtra("class", classId);
                    i.putExtra("name", s);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }).show();
    }

    private ArrayList<Item> setList() {
        return new ArrayList<>(Arrays.asList(
                new Item(getString(R.string.digits), RecallSimple.class, R.drawable.numbers),
                new Item(getString(R.string.binary), RecallSimple.class, R.drawable.binary),
                new Item(getString(R.string.cards), RecallCards.class, R.drawable.cards),
                new Item(getString(R.string.letters), RecallSimple.class, R.drawable.letters),
                new Item(getString(R.string.names), RecallSimple.class, R.drawable.names),
                new Item(getString(R.string.numbers), RecallSimple.class, R.drawable.numbers),
                new Item(getString(R.string.places_capital), RecallSimple.class, R.drawable.places),
                new Item(getString(R.string.words), RecallSimple.class, R.drawable.vocabulary),
                new Item(getString(R.string.dates), RecallComplex.class, R.drawable.dates)));
    }

    private class MySpaceAdapter extends ArrayAdapter<Item> {

        MySpaceAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }


        @SuppressLint("InflateParams")  // passing null as rootView
        @NonNull
        @Override
        public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
            //Chose discipline
            if (listViewId == MIN_DYNAMIC_VIEW_ID) {
                if (listItemView == null)
                    listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_category,
                            null, true);

                ((TextView) listItemView.findViewById(R.id.text)).setText(Objects.requireNonNull(
                        getItem(position)).mName);

                ImageView img = listItemView.findViewById(R.id.image);
                Picasso.get()
                        .load(Objects.requireNonNull(getItem(position)).mImageId)
                        .placeholder(R.mipmap.ic_launcher)
                        .fit()
                        .centerCrop()
                        .into(img);
                return listItemView;
            }

            //Chose file
            if (listItemView == null) listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_file, parent, false);

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(Objects.requireNonNull(getItem(position)).mName);
            return listItemView;
        }
    }

    private class Item {
        String mFileName;               // actual file name
        String mName;                   // file name without extension if there was one
        Class mClass;
        int mImageId;

        Item(String fileName) {
            mFileName = fileName;
            mName = fileName.endsWith(".txt") ? fileName.substring(0, fileName.length() - 4) : fileName;
            Timber.v("Item set!");
        }

        Item(String fileName, Class myClass, int imageId) {
            mFileName = fileName;
            mName = fileName.endsWith(".txt") ? fileName.substring(0, fileName.length() - 4) : fileName;
            mClass = myClass;
            mImageId = imageId;
            Timber.v("Item set!");
        }

        public String getName() {
            return mFileName;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}