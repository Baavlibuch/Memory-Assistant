package com.memory_athlete.memoryassistant.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.lessons.Lessons;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Learn extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.theme(this, Learn.this);
        setContentView(R.layout.activity_learn);
        setTitle(getString(R.string.learn));
        setAdapter();
    }

    // setting the adapter
    public void setAdapter() {
        final ArrayList<Item> list = new ArrayList<>();

        setList(list);

        LearnAdapter adapter = new LearnAdapter(this, list);
        ListView listView = findViewById(R.id.learn_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = list.get(position);
            Intent intent = new Intent(getApplicationContext(), item.mClass);
            intent.putExtra("mHeader", item.mItem);
            intent.putExtra(Helper.RAW_RESOURCE_ID_KEY, item.mFile);
            intent.putExtra("mWebView", item.mWebView);
            intent.putExtra("list", true);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    // sets the list method of loci, the perfect association,...
    private void setList(ArrayList<Item> list) {
        list.add(new Item(R.string.method_of_loci, R.drawable.method_of_loci, Lessons.class, R.raw.lesson_method_of_loci));
        list.add(new Item(R.string.associations, R.drawable.perfect_association, Lessons.class, R.raw.lesson_perfect_association));
        list.add(new Item(R.string.major_system, R.drawable.major_system, Lessons.class, R.raw.lesson_major_system));
        list.add(new Item(R.string.pao, R.drawable.pao, Lessons.class, R.raw.lesson_pao));
        list.add(new Item(R.string.wardrobe_method, R.drawable.wardrobe_method, Lessons.class, R.raw.lesson_wardrobes));
        list.add(new Item(R.string.language, R.drawable.vocabulary, Lessons.class, R.raw.lesson_language));
        list.add(new Item(R.string.equations, R.drawable.equations, Lessons.class, R.raw.lesson_equations));
        list.add(new Item(R.string.checkout, R.drawable.theres_more, Lessons.class, R.raw.checkout));
        //list.add(new Item(R.string.checkout, Lessons.class, R.raw.Important, true));
    }

    // data about each item
    private class Item {
        int mItem, mFile, mImageId;
        Class mClass;
        boolean mWebView = false;

        Item(int item, int im, Class class1, int file) {
            mItem = item;
            mClass = class1;
            mFile = file;
            mImageId = im;
        }

        /*Item(int item, Class class1, int file, boolean webView) {
            mItem = item;
            mClass = class1;
            mFile = file;
            mWebView = webView;
        }*/
    }

    // defining the adapter which is going to take the list of items for displaying
    private class LearnAdapter extends ArrayAdapter<Item> {

        LearnAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, View listItemView, @NonNull ViewGroup parent) {
            if (listItemView == null) listItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_category, null, true);

            Item item = getItem(position);
            assert item != null;
            TextView textView = listItemView.findViewById(R.id.text);
            textView.setText(item.mItem);
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
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
