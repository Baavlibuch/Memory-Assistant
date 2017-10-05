package com.memory_athlete.memoryassistant.main;

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
import android.widget.TextView;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.MakeList;
import com.memory_athlete.memoryassistant.lessons.Lessons;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Learn extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MakeList.theme(this, Learn.this);
        setContentView(R.layout.activity_learn);
        setTitle(getString(R.string.learn));
        setAdapter();
    }

    public void setAdapter() {
        final ArrayList<Item> list = new ArrayList<>();
        setList(list);

        LearnAdapter adapter = new LearnAdapter(this, list);
        ListView listView = (ListView) findViewById(R.id.learn_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = list.get(position);
                Intent intent = new Intent(getApplicationContext(), item.mClass);
                intent.putExtra("mHeader", item.mItem);
                intent.putExtra("file", item.mFile);
                intent.putExtra("mWebView", item.mWebView);
                intent.putExtra("list", true);
                startActivity(intent);
            }
        });
    }

    private void setList(ArrayList<Item> list) {
        list.add(new Item(R.string.method_of_loci, R.drawable.method_of_loci, Lessons.class, R.raw.lesson_method_of_loci));
        list.add(new Item(R.string.associations, R.drawable.perfect_association, Lessons.class, R.raw.lesson_perfect_association));
        list.add(new Item(R.string.major_system, R.drawable.major_system, Lessons.class, R.raw.lesson_major_system));
        list.add(new Item(R.string.pao, R.drawable.pao, Lessons.class, R.raw.lesson_pao));
        list.add(new Item(R.string.wardrobe_method, R.drawable.wardrobe_method, Lessons.class, R.raw.lesson_wardrobes));
        list.add(new Item(R.string.vocab, R.drawable.vocabulary, Lessons.class, R.raw.lesson_vocabulary));
        list.add(new Item(R.string.equations, R.drawable.equations, Lessons.class, R.raw.lesson_equations));
        list.add(new Item(R.string.derivations, R.drawable.derive, Lessons.class, R.raw.lesson_derivations));
        list.add(new Item(R.string.checkout, R.drawable.theres_more, Lessons.class, R.raw.checkout));
        //list.add(new Item(R.string.checkout, Lessons.class, R.raw.Important, true));
    }

    private class Item {
        int mItem, mFile, mImageId;
        Class mClass;
        boolean mWebView = false;

        Item(int item, Class class1, int file) {
            mItem = item;
            mClass = class1;
            mFile = file;
        }

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

    private class LearnAdapter extends ArrayAdapter<Item> {

        LearnAdapter(Activity context, ArrayList<Item> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.category, null, true);
            }

            TextView textView = listItemView.findViewById(R.id.text);
            textView.setText(getItem(position).mItem);
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
    }
}
