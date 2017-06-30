package com.maniksejwal.memoryathletes.main;

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

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.disciplines.BinaryDigits;
import com.maniksejwal.memoryathletes.disciplines.Cards;
import com.maniksejwal.memoryathletes.disciplines.Colours;
import com.maniksejwal.memoryathletes.disciplines.Equations;
import com.maniksejwal.memoryathletes.disciplines.Foods;
import com.maniksejwal.memoryathletes.disciplines.Places;
import com.maniksejwal.memoryathletes.disciplines.RandomDates;
import com.maniksejwal.memoryathletes.disciplines.RandomLetters;
import com.maniksejwal.memoryathletes.disciplines.RandomNames;
import com.maniksejwal.memoryathletes.disciplines.RandomNumbers;
import com.maniksejwal.memoryathletes.disciplines.RandomWords;

import java.util.ArrayList;

import static com.maniksejwal.memoryathletes.R.drawable.pic;

public class Practice extends AppCompatActivity {

    private static String TAG = "Position::--";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        setTitle("Practice");
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
        disc.add(new Category(R.string.e, pic, Cards.class, false, false));
        disc.add(new Category(R.string.b, pic, RandomNumbers.class, true, false, 1));
        disc.add(new Category(R.string.k, pic, RandomLetters.class, true, false));
        disc.add(new Category(R.string.f, pic, BinaryDigits.class, true, false));
        disc.add(new Category(R.string.c, pic, RandomWords.class, false, true));
        disc.add(new Category(R.string.d, pic, RandomNames.class, false, true));
        disc.add(new Category(R.string.g, pic, Places.class, false, true));
        disc.add(new Category(R.string.a, pic, Equations.class, false, false));
        disc.add(new Category(R.string.h, pic, RandomDates.class, false, false));
        disc.add(new Category(R.string.i, pic, Foods.class, false, false));
        disc.add(new Category(R.string.j, pic, Colours.class, false, false));
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

            TextView txt = (TextView) listView.findViewById(R.id.text);
            ImageView img = (ImageView) listView.findViewById(R.id.image);
            txt.setText(cat.mNameId);
            img.setImageResource(cat.mImageId);
            return listView;
        }
    }
}
