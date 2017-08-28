package com.maniksejwal.memoryathletes.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maniksejwal.memoryathletes.lessons.Lessons;
import com.maniksejwal.memoryathletes.R;

import java.util.ArrayList;


public class Learn extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        list.add(new Item(R.string.method_of_loci, Lessons.class, R.raw.lesson_method_of_loci));
        list.add(new Item(R.string.associations, Lessons.class, R.raw.lesson_perfect_association));
        list.add(new Item(R.string.major_system, Lessons.class, R.raw.lesson_major_system));
        list.add(new Item(R.string.pao, Lessons.class, R.raw.lesson_pao));
        list.add(new Item(R.string.equations, Lessons.class, R.raw.lesson_equations));
        list.add(new Item(R.string.derivations, Lessons.class, R.raw.lesson_derivations));

        list.add(new Item(R.string.checkout, Lessons.class, R.raw.checkout));
        //list.add(new Item(R.string.checkout, Lessons.class, R.raw.Important, true));
        //list.add(new Item(R.string.wardrobe_method, Lessons.class));
    }

    private class Item {
        int mItem, mFile;
        Class mClass;
        boolean mWebView = false;

        Item(int item, Class class1, int file) {
            mItem = item;
            mClass = class1;
            mFile = file;
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
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.main_item, null, true);
            }

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(getString(getItem(position).mItem));

            return listItemView;
        }
    }
}
