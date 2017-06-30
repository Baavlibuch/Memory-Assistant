package com.maniksejwal.memoryathletes.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.main.OpenFile.Recall;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAdapter();
    }

    public void setAdapter(){
        final ArrayList<Item> list = new ArrayList<>();
        setList(list);

        MainAdapter adapter = new MainAdapter(this, list);
        ListView listView = (ListView) findViewById(R.id.main_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = list.get(position);
                Intent intent = new Intent(MainActivity.this, item.mClass);
                startActivity(intent);
            }
        });
        Log.i(TAG, "Adapter set!");
    }

    private void setList(ArrayList<Item> list){
        list.add(new Item(R.string.login, Login.class));
        list.add(new Item(R.string.learn, Learn.class));
        list.add(new Item(R.string.practice, Practice.class));
        list.add(new Item(R.string.recall, Recall.class));
        list.add(new Item(R.string.preferences, Preferences.class));
        list.add(new Item(R.string.get_pro, GetPro.class));
        Log.i(TAG, "List set!");
    }

    private class Item {
        int mItem;
        Class mClass;

        Item(int item, Class class1) {
            mItem = item;
            mClass = class1;
            Log.i(TAG, "Item set!");
        }
    }

    private class MainAdapter extends ArrayAdapter<Item> {

        MainAdapter(Activity context, ArrayList<Item> words) {
            super(context, 0, words);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if(listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.main_item,
                        parent, false);
            }

            TextView textView = (TextView) listItemView.findViewById(R.id.main_textView);
            textView.setText(getString(getItem(position).mItem));

            return listItemView;
        }

    }
}