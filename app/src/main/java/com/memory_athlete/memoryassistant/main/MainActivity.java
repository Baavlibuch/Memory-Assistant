package com.memory_athlete.memoryassistant.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.memory_athlete.memoryassistant.data.Helper;
import com.memory_athlete.memoryassistant.mySpace.MySpace;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import timber.log.Timber;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.widget.Toast.makeText;


public class MainActivity extends AppCompatActivity {
    boolean backPressed = false;
    private final int REQUEST_STORAGE_ACCESS = 444;

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
        Helper.theme(this, MainActivity.this);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        firstStart();                           //TODO
        setAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(
                        getApplicationContext()).edit();
                e.putLong("last_opened", System.currentTimeMillis());
                Timber.v("Last opened on" + System.currentTimeMillis());
                e.apply();
                ReminderUtils.scheduleReminder(getApplicationContext());
            }
        };
        r.run();
    }

    void firstStart() {
        if (mayAccessStorage()) Helper.makeDirectory(Helper.APP_FOLDER);
        else return;
        //if (isExternalStorageWritable())
        //new Runnable() {
        //  @Override
        //public void run() {

        SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (s.getLong("last_opened", 0) == 0) {
            makeText(getApplicationContext(), R.string.confused, Toast.LENGTH_LONG)
                    .show();
            Timber.e("firstStart");
        } else {
            String filesDir = getFilesDir().getAbsolutePath() + File.separator + getString(R.string.my_space) + File.separator;
            String mySpaceDir = Helper.APP_FOLDER + File.separator + getString(R.string.my_space) + File.separator;
            Helper.makeDirectory(mySpaceDir);
            String folder;

            for (int i = 0; i < 6; i++) {
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
                    default:
                        continue;
                }
                Timber.v("Folder " + folder);
                File from = new File(filesDir + folder);

                if (from.exists()) {
                    File[] files = from.listFiles();
                    Helper.makeDirectory(mySpaceDir + folder);
                    try {
                        for (File f : files) {
                            File to = new File(mySpaceDir + folder + File.separator + f.getName());
                            copyFile(f, to);
                            f.delete();
                        }
                    } catch (IOException e) {
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                from.delete();
            }
            (new File(filesDir)).delete();
        }

//    }
//};

    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean mayAccessStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ACCESS);
        } else {
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ACCESS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_ACCESS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Helper.makeDirectory(Helper.APP_FOLDER);
            } else {
                Snackbar.make(findViewById(R.id.main_list), "The app might crash without these permissions",
                        Snackbar.LENGTH_SHORT).setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firstStart();
                    }
                });
            }
        }
    }


    public void setAdapter() {
        final ArrayList<Item> list = new ArrayList<>();
        setList(list);

        MainAdapter adapter = new MainAdapter(this, list);
        ListView listView = findViewById(R.id.main_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                Item item = list.get(position);
                Intent intent = new Intent(MainActivity.this, item.mClass);
                if (item.mItem == R.string.apply)
                    intent.putExtra(getString(R.string.apply), getString(R.string.apply));
                intent.putExtra(Helper.TYPE, item.mItem);
                startActivity(intent);
            }
        });
        Timber.v("Adapter set!");
    }

    private void setList(ArrayList<Item> list) {
        //list.add(new Item(R.string.login, Login.class));
        list.add(new Item(R.string.learn, R.drawable.learn, Learn.class));
        list.add(new Item(R.string.practice, R.drawable.practice, Practice.class));
        list.add(new Item(R.string.recall, R.drawable.recall, RecallSelector.class));
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
                listItemView = LayoutInflater.from(getContext()).inflate(R.layout.category //item_main
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