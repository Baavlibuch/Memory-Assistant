package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.compat.BuildConfig;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpace;
import com.memory_athlete.memoryassistant.recall.Recall;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        Log.i(LOG_TAG, "BuildConfig.DEBUG is " + BuildConfig.DEBUG);
        theme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
        e.putLong("last_opened", System.currentTimeMillis());
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "Last opened on" + System.currentTimeMillis());
        e.apply();
        ReminderUtils.scheduleReminder(this);
       // theme();
    }

    protected void theme(){
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme"), title = "";
        switch (theme){
            case "Dark":
                setTheme(R.style.dark);
                Log.i(LOG_TAG, String.valueOf(((ColorDrawable) (this.getWindow().getDecorView()).getBackground()).getColor()));
                (this.getWindow().getDecorView()).setBackgroundColor(0xff333333);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
                (this.getWindow().getDecorView()).setBackgroundColor(0xffeaeaea);
                title="<font color=#FFFFFF>";
        }
        setContentView(R.layout.activity_main);
        setTitle(Html.fromHtml(title + getString(R.string.app_name)));
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
        if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Adapter set!");
    }

    private void setList(ArrayList<Item> list) {
        //list.add(new Item(R.string.login, Login.class));
        list.add(new Item(R.string.learn, Learn.class));
        list.add(new Item(R.string.practice, Practice.class));
        list.add(new Item(R.string.recall, Recall.class));
        list.add(new Item(R.string.apply, Apply.class));
        list.add(new Item(R.string.my_space, MySpace.class));
        //list.add(new Item(R.string.reminders, ))
        list.add(new Item(R.string.preferences, Preferences.class));
        //list.add(new Item(R.string.get_pro, GetPro.class));
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "List set!");
    }

    private class Item {
        int mItem;
        Class mClass;

        Item(int itemName, Class class1) {
            mItem = itemName;
            mClass = class1;
            if (BuildConfig.DEBUG) Log.i(LOG_TAG, "Item set!");
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
}