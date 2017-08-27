package com.maniksejwal.memoryathletes.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.maniksejwal.memoryathletes.mySpace.MySpace;
import com.maniksejwal.memoryathletes.openFile.Recall;
import com.maniksejwal.memoryathletes.reminders.ReminderUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAdapter();

    }

    public void setAdapter() {
        final ArrayList<Item> list = new ArrayList<>();
        setList(list);

        MainAdapter adapter = new MainAdapter(this, list);
        ListView listView = (ListView) findViewById(R.id.main_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = list.get(position);
                Intent intent = new Intent(MainActivity.this, item.mClass);
                if (item.mItem == R.string.apply)
                    intent.putExtra(getString(R.string.apply), getString(R.string.apply));
                startActivity(intent);
            }
        });
        Log.i(LOG_TAG, "Adapter set!");
    }

    private void setList(ArrayList<Item> list) {
        list.add(new Item(R.string.login, Login.class));
        list.add(new Item(R.string.learn, Learn.class));
        list.add(new Item(R.string.practice, Practice.class));
        list.add(new Item(R.string.recall, Recall.class));
        list.add(new Item(R.string.apply, Apply.class));
        list.add(new Item(R.string.my_space, MySpace.class));
        //list.add(new Item(R.string.reminders, ))
        list.add(new Item(R.string.preferences, Preferences.class));
        list.add(new Item(R.string.get_pro, GetPro.class));
        Log.i(LOG_TAG, "List set!");
    }

    private class Item {
        int mItem;
        Class mClass;

        Item(int itemName, Class class1) {
            mItem = itemName;
            mClass = class1;
            Log.i(LOG_TAG, "Item set!");
        }
    }

    private class MainAdapter extends ArrayAdapter<Item> {

        MainAdapter(Activity context, ArrayList<Item> words) {
            super(context, 0, words);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.main_item,
                        parent, false);
            }

            TextView textView = listItemView.findViewById(R.id.main_textView);
            textView.setText(getString(getItem(position).mItem));

            return listItemView;
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
        e.putLong("last_opened", System.currentTimeMillis());
        Log.v(LOG_TAG, "Last opened on" + System.currentTimeMillis());
        e.apply();
        ReminderUtils.scheduleReminder(this);
    }
}