package com.memory_athlete.memoryassistant.main;

import android.content.Context;
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
import android.widget.TextView;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Practice extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.theme(this, Practice.this);
        setContentView(R.layout.activity_practice);
        setTitle(getString(R.string.practice));
        final ArrayList<Category> disc = new ArrayList<>();
        setList(disc);
        DisciplineAdapter discipline = new DisciplineAdapter(this, disc);
        ListView disciplineList = findViewById(R.id.disciplines);
        disciplineList.setAdapter(discipline);
        disciplineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Category cat = disc.get(position);
                Intent intent = new Intent(Practice.this, DisciplineActivity.class);
                intent.putExtra("class", cat.mClass);
                intent.putExtra("hasSpinner", cat.hasSpinner);
                intent.putExtra("hasAsyncTask", cat.hasAsyncTask);
                intent.putExtra("nameID", cat.mNameId);
                intent.putExtra("spinnerContent", cat.mDifferentString);
                startActivity(intent);
            }
        });
    }

    private void setList(ArrayList<Category> disc) {
        disc.add(new Category(R.string.numbers, R.drawable.numbers, 1, true, false, 1));
        disc.add(new Category(R.string.words, R.drawable.words, 2, false, true));
        disc.add(new Category(R.string.names, R.drawable.names, 3, false, true));
        disc.add(new Category(R.string.places_capital, R.drawable.places, 4, false, true));
        disc.add(new Category(R.string.cards, R.drawable.cards, 5, false, false));
        disc.add(new Category(R.string.dates, R.drawable.dates, 8, false, true));
        disc.add(new Category(R.string.binary, R.drawable.binary, 6, true, false));
        disc.add(new Category(R.string.letters, R.drawable.letters, 7, true, false));
        //disc.add(new Category(R.string.a, R.drawable.equations, Equations.class, false, false));
        //disc.add(new Category(R.string.i, R.drawable.foods, Foods.class, false, false));
        //disc.add(new Category(R.string.j, R.drawable.colours, Colours.class, false, false));
    }

    private class Category {
        private int mNameId;
        private int mImageId;
        private int mDifferentString=0;
        private int mClass;
        private boolean hasSpinner;
        private boolean hasAsyncTask;

        Category(int nm, int im, int classId, boolean spinner, boolean async){
            mNameId = nm;
            mImageId = im;
            mClass = classId;
            hasSpinner = spinner;
            hasAsyncTask = async;
        }

        Category(int nm, int im, int classId, boolean spinner, boolean async, int num){
            mNameId = nm;
            mImageId = im;
            mClass = classId;
            hasSpinner = spinner;
            hasAsyncTask = async;
            mDifferentString = num;
        }
    }

    private class DisciplineAdapter extends ArrayAdapter<Category> {
        DisciplineAdapter(Context context, ArrayList<Category> cats) {
            super(context, 0, cats);
        }

        @Override
        public View getView(int position, View listView, ViewGroup parent) {
            Category cat = getItem(position);
            if (listView == null) {
                listView = LayoutInflater.from(getContext()).inflate(R.layout.category, null, true);
            }

            TextView txt = listView.findViewById(R.id.text);
            ImageView img = listView.findViewById(R.id.image);
            txt.setText(cat.mNameId);
            Picasso
                    .with(getApplicationContext())
                    .load(cat.mImageId)
                    .placeholder(R.mipmap.launcher_ic)
                    .fit()
                    .centerCrop()
                    //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                    .into(img);
            //img.setImageResource(cat.mImageId);
            return listView;
        }
    }
}
