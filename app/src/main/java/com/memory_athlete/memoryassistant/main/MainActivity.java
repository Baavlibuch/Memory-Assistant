package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.BuildConfig;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.MakeList;
import com.memory_athlete.memoryassistant.mySpace.MySpace;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import timber.log.Timber;

import static android.widget.Toast.makeText;


public class MainActivity extends AppCompatActivity {
    boolean backPressed = false;

    @Override
    public void onBackPressed() {
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.double_back_to_exit), false) && !backPressed) {
            backPressed = true;
            makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        } else super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());
        MakeList.theme(this, MainActivity.this);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        firstStart();                           //TODO
        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Runnable(){
            @Override
            public void run() {
        SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        e.putLong("last_opened", System.currentTimeMillis());
        Timber.v("Last opened on" + System.currentTimeMillis());
        e.apply();
        ReminderUtils.scheduleReminder(getApplicationContext());
            }
        };
    }

    void firstStart() {
        new Runnable() {
            @Override
            public void run() {
                SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (s.getLong("last_opened", 0) != 0) return;

                makeText(getApplicationContext(), R.string.confused, Toast.LENGTH_LONG).show();

                /*String filesDir = getFilesDir().getAbsolutePath() + File.separator;
                File file = new File(filesDir + getString(R.string.my_space));
                boolean isDirectoryCreated = file.exists();
                if (!isDirectoryCreated) isDirectoryCreated = file.mkdir();
                if (!isDirectoryCreated) {
                    MakeList.fixBug(getApplicationContext());
                    throw new RuntimeException("couldn't create the MySpace directory");
                }
                file = new File(getFilesDir().getAbsolutePath() + File.separator
                        + getString(R.string.practice));
                isDirectoryCreated = file.exists();
                if (!isDirectoryCreated) isDirectoryCreated = file.mkdir();
                if (!isDirectoryCreated) {
                    MakeList.fixBug(getApplicationContext());
                    throw new RuntimeException("couldn't create the MySpace directory");
                }*/
            }
        };

/*
        Timber.d("deleting files");

        for (int i = 0; i < 19; i++) {
            switch (i) {
                case 0:
                    folder = getString(R.string.majors);
                    break;
                case 1:
                    folder = getString(R.string.ben);
                    break;
                case 2:
                    folder = getString(R.string.wardrobes);
                    break;
                case 3:
                    folder = getString(R.string.lists);
                    break;
                case 4:
                    folder = getString(R.string.words);
                    break;
                case 5:
                    folder = getString(R.string.digits);
                    break;
                case 7:
                    folder = getString(R.string.equations);
                    break;
                case 8:
                    folder = getString(R.string.numbers);
                    break;
                case 9:
                    folder = getString(R.string.words);
                    break;
                case 10:
                    folder = getString(R.string.names);
                    break;
                case 11:
                    folder = getString(R.string.cards);
                    break;
                case 12:
                    folder = getString(R.string.binary);
                    break;
                case 13:
                    folder = getString(R.string.places_capital);
                    break;
                case 14:
                    folder = getString(R.string.h);
                    break;
                case 15:
                    folder = getString(R.string.i);
                    break;
                case 16:
                    folder = getString(R.string.j);
                    break;
                case 17:
                    folder = getString(R.string.letters);
                    break;
                case 18:
                    folder = getString(R.string.practice);
                    break;
                default:
                    continue;
            }

            File from = new File(filesDir + folder);
            if (from.exists()) {
                if (!from.delete()) {
                    Timber.e(from.getAbsolutePath() + " not deleted");
                }
            }
        }*/
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
        Timber.v("Adapter set!");
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
                        , parent, false);
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