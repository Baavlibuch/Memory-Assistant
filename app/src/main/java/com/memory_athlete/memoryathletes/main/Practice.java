package com.memory_athlete.memoryathletes.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.memory_athlete.memoryathletes.R;
import com.memory_athlete.memoryathletes.disciplines.BinaryDigits;
import com.memory_athlete.memoryathletes.disciplines.Cards;
import com.memory_athlete.memoryathletes.disciplines.Colours;
import com.memory_athlete.memoryathletes.disciplines.Dates;
import com.memory_athlete.memoryathletes.disciplines.Equations;
import com.memory_athlete.memoryathletes.disciplines.Foods;
import com.memory_athlete.memoryathletes.disciplines.Letters;
import com.memory_athlete.memoryathletes.disciplines.Names;
import com.memory_athlete.memoryathletes.disciplines.Numbers;
import com.memory_athlete.memoryathletes.disciplines.Places;
import com.memory_athlete.memoryathletes.disciplines.Words;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Practice extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme"), title="";
        switch (theme){
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
                title="<font color=#FFFFFF>";
        }
        setContentView(R.layout.activity_practice);
        setTitle(Html.fromHtml(title+getString(R.string.practice)));
        final ArrayList<Category> disc = new ArrayList<>();
        setList(disc);

        DisciplineAdapter discipline = new DisciplineAdapter(this, disc);
        ListView disciplineList = (ListView) findViewById(R.id.disciplines);
        disciplineList.setAdapter(discipline);
        disciplineList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Category cat = disc.get(position);
                Intent intent = new Intent(Practice.this, cat.mClass);
                intent.putExtra("hasSpinner", cat.hasSpinner);
                intent.putExtra("hasAsyncTask", cat.hasAsyncTask);
                intent.putExtra("nameID", cat.mNameId);
                intent.putExtra("spinnerContent", cat.mDifferentString);
                startActivity(intent);
            }
        });
    }

    private void setList(ArrayList<Category> disc) {
        disc.add(new Category(R.string.b, R.drawable.numbers, Numbers.class, true, false, 1));
        disc.add(new Category(R.string.c, R.drawable.words, Words.class, false, true));
        disc.add(new Category(R.string.d, R.drawable.names, Names.class, false, true));
        disc.add(new Category(R.string.g, R.drawable.places, Places.class, false, true));
        disc.add(new Category(R.string.e, R.drawable.cards, Cards.class, false, false));
        disc.add(new Category(R.string.f, R.drawable.binary, BinaryDigits.class, true, false));
        disc.add(new Category(R.string.k, R.drawable.letters, Letters.class, true, false));
        disc.add(new Category(R.string.a, R.drawable.equations, Equations.class, false, false));
        disc.add(new Category(R.string.h, R.drawable.dates, Dates.class, false, false));
        disc.add(new Category(R.string.i, R.drawable.foods, Foods.class, false, false));
        disc.add(new Category(R.string.j, R.drawable.colours, Colours.class, false, false));
    }

    private class Category {
        private int mNameId;
        private int mImageId;
        private int mDifferentString=0;
        private Class mClass;
        private boolean hasSpinner;
        private boolean hasAsyncTask;

        Category(int nm, int im, Class<?> cid, boolean spinner, boolean async){
            mNameId = nm;
            mImageId = im;
            mClass = cid;
            hasSpinner = spinner;
            hasAsyncTask = async;
        }

        Category(int nm, int im, Class<?> cid, boolean spinner, boolean async, int num){
            mNameId = nm;
            mImageId = im;
            mClass = cid;
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
