package com.memory_athlete.memoryassistant.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

//import hugo.weaving.DebugLog;

public class Practice extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.theme(this, Practice.this);
        setContentView(R.layout.activity_practice);
        setTitle(getString(R.string.practice));
        final ArrayList<Discipline> disciplines = new ArrayList<>();
        setList(disciplines);
        DisciplineAdapter discipline = new DisciplineAdapter(this, disciplines);
        ListView disciplineList = findViewById(R.id.main_list);
        disciplineList.setAdapter(discipline);
        //@DebugLog
        disciplineList.setOnItemClickListener((parent, view, position, id) -> {
            Discipline disc = disciplines.get(position);
            Intent intent = new Intent(Practice.this, DisciplineActivity.class);
            intent.putExtra("class", disc.mClass);
            intent.putExtra("hasSpinner", disc.hasSpinner);
            intent.putExtra("hasAsyncTask", disc.hasAsyncTask);
            intent.putExtra("nameID", disc.mNameId);
            intent.putExtra("spinnerContent", disc.mDifferentString);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (!Helper.mayAccessStorage(this)) {
            Snackbar.make(findViewById(R.id.main_list),
                    "Storage permissions are required", Snackbar.LENGTH_LONG)
                    .setAction("Grant", view -> {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    })
                    .show();
        }
    }

    // sets the list numbers,words,...
    private void setList(ArrayList<Discipline> disc) {
        disc.add(new Discipline(R.string.numbers, R.drawable.numbers, 1, true, false, 1));
        disc.add(new Discipline(R.string.words, R.drawable.vocabulary, 2, false, true));
        disc.add(new Discipline(R.string.names, R.drawable.names, 3, false, true));
        disc.add(new Discipline(R.string.places_capital, R.drawable.places, 4, false, true));
        disc.add(new Discipline(R.string.cards, R.drawable.cards, 5, false, false));
        disc.add(new Discipline(R.string.dates, R.drawable.dates, 8, false, true));
        disc.add(new Discipline(R.string.binary, R.drawable.binary, 6, true, false));
        disc.add(new Discipline(R.string.letters, R.drawable.letters, 7, true, false));
        //disc.add(new Discipline(R.string.a, R.drawable.equations, Equations.class, false, false));
        //disc.add(new Discipline(R.string.i, R.drawable.foods, Foods.class, false, false));
        //disc.add(new Discipline(R.string.j, R.drawable.colours, Colours.class, false, false));
    }

    // data about each item
    private class Discipline {
        private int mNameId;
        private int mImageId;
        private int mDifferentString=0;
        private int mClass;
        private boolean hasSpinner;
        private boolean hasAsyncTask;

        Discipline(int nm, int im, int classId, boolean spinner, boolean async){
            mNameId = nm;
            mImageId = im;
            mClass = classId;
            hasSpinner = spinner;
            hasAsyncTask = async;
        }

        Discipline(int nm, int im, int classId, boolean spinner, boolean async, int num){
            mNameId = nm;
            mImageId = im;
            mClass = classId;
            hasSpinner = spinner;
            hasAsyncTask = async;
            mDifferentString = num;
        }
    }

    // defining the adapter which is going to take the list of items for displaying
    private class DisciplineAdapter extends ArrayAdapter<Discipline> {
        DisciplineAdapter(Context context, ArrayList<Discipline> cats) {
            super(context, 0, cats);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, View listView, @NonNull ViewGroup parent) {
            if (listView == null) listView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_category, null, true);

            Discipline cat = getItem(position);
            TextView txt = listView.findViewById(R.id.text);
            ImageView img = listView.findViewById(R.id.image);
            txt.setText(Objects.requireNonNull(cat).mNameId);
            Picasso
                    .get()
                    .load(cat.mImageId)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                    .into(img);
            //img.setImageResource(cat.mImageId);
            return listView;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
