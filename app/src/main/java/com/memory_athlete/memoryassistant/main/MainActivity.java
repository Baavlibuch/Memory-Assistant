package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.memory_athlete.memoryassistant.BuildConfig;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpace;
import com.memory_athlete.memoryassistant.recall.Recall;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        theme();
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));
        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
        e.putLong("last_opened", System.currentTimeMillis());
        Timber.v(LOG_TAG, "Last opened on" + System.currentTimeMillis());
        e.apply();
        ReminderUtils.scheduleReminder(this);
       // mTheme();
    }

    void theme(){
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme");
        switch (theme){
            case "Dark":
                setTheme(R.style.dark);
                Log.i(LOG_TAG, String.valueOf(((ColorDrawable) (this.getWindow().getDecorView()).getBackground()).getColor()));
  //              (this.getWindow().getDecorView()).setBackgroundColor(0xff333333);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
//                (this.getWindow().getDecorView()).setBackgroundColor(0xffeaeaea);
        }
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
        list.add(new Item(R.string.learn, R.drawable.learn, Learn.class));
        list.add(new Item(R.string.practice, R.drawable.practice, Practice.class));
        list.add(new Item(R.string.recall, R.drawable.recall, Recall.class));
        list.add(new Item(R.string.apply, R.drawable.implement, Implement.class));
        list.add(new Item(R.string.my_space, R.drawable.my_space, MySpace.class));
        list.add(new Item(R.string.preferences, R.drawable.preferences, Preferences.class));
        //list.add(new Item(R.string.reminders, ))
        //list.add(new Item(R.string.get_pro, GetPro.class));
        Timber.v("List set!");
    }

    private class Item {
        int mItem, mImageId;
        Class mClass;

        Item(int itemName, int im, Class class1) {
            mItem = itemName;
            mClass = class1;
            mImageId = im;
            Timber.v("Item set!");
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
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.category //main_item
                        ,parent, false);
            }

            TextView textView = listItemView.findViewById(R.id.text);
            textView.setText(getString(getItem(position).mItem));
            ImageView img = listItemView.findViewById(R.id.image);
            Picasso
                    .with(getApplicationContext())
                    .load(getItem(position).mImageId)
                    .placeholder(R.mipmap.launcher_ic)
                    .fit()
                    .centerCrop()
                    //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                    .into(img);

            return listItemView;
        }

    }
}