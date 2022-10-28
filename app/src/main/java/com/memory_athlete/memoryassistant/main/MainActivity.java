package com.memory_athlete.memoryassistant.main;

import static android.widget.Toast.makeText;
import static com.memory_athlete.memoryassistant.Helper.REQUEST_STORAGE_ACCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.memory_athlete.memoryassistant.language.BaseActivity;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpace;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    boolean backPressed = false;
    // sharedPreferences object points to a file containing key-value pairs
    private SharedPreferences sharedPreferences;

    // create a menu bar on the first page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // when clicked on Privacy Policy, go to the PrivacyPolicy activity which is a menu item of menu bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.privacy_policy_menu) {
            startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
            //throw new RuntimeException("Testing crash");
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // close the MainActivity on back press
    @Override
    public void onBackPressed() {
        if (sharedPreferences.getBoolean(getString(R.string.double_back_to_exit), false)
                && !backPressed) {
            backPressed = true;
            makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        } else super.onBackPressed();
    }

    // displays the main content on the first page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashlyticsLogTree());
        }
        if (BuildConfig.DEBUG) Timber.plant(new Timber.DebugTree());

        Helper.theme(this, MainActivity.this);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences getShared = getSharedPreferences("LANGUAGE", MODE_PRIVATE);
        String value = getShared.getString("str", null);
        if (value != null) {
            Toast.makeText(MainActivity.this,value+" language", Toast.LENGTH_SHORT).show();

        } else {
            Intent intent = new Intent(MainActivity.this, BaseActivity.class);
            startActivity(intent);
        }

        setAdapter();

        firstStart();

        Helper.mayAccessStorage(this);
        if (!verifyInstallerId() && !BuildConfig.DEBUG) {
            makeText(this, R.string.dl_from_play, Toast.LENGTH_LONG).show();
            FirebaseAnalytics.getInstance(this).logEvent(
                    "release_app_not_installed_form_play", null);
        }
        if (!Locale.getDefault().getLanguage().equals("en"))
            makeText(this, R.string.faulty_translations, Toast.LENGTH_LONG).show();
        LocaleListCompat appLocale = LocaleListCompat.forLanguageTags("xx-YY");
        //AppCompatDelegate.setApplicationLocales(appLocale);
    }

    // resuming the MainActivity again
    @Override
    protected void onResume() {
        super.onResume();
        new Thread(() -> {
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putLong("last_opened", System.currentTimeMillis());
            Timber.v("Last opened on %s", System.currentTimeMillis());
            e.apply();
            ReminderUtils.scheduleReminder(getApplicationContext());
        }).start();
    }

    // checks if the MainActivity is running
    void firstStart() {
        if (sharedPreferences.getLong("last_opened", 0) != 0) return;
        makeText(getApplicationContext(), R.string.confused, Toast.LENGTH_LONG).show();
        Timber.d("firstStart");
    }

    // verifies the installer id for checking if app is installed from play store
    boolean verifyInstallerId() {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        // The package name of the app that has installed your app
        //final String installer = getPackageManager().getInstallerPackageName(getPackageName());
        // true if your app has been downloaded from Play Store
        //return installer != null && validInstallers.contains(installer);

        String installerPackageName = "";
        try {
            String packageName = getPackageName();
            PackageManager pm = getPackageManager();
            installerPackageName =  pm.getInstallerPackageName(packageName);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                InstallSourceInfo info = pm.getInstallSourceInfo(packageName);
                if (info != null) {
                    installerPackageName = info.getInstallingPackageName();
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(MainActivity.this,"PackageManager exception", Toast.LENGTH_SHORT).show();
        }

        //Toast.makeText(MainActivity.this,installerPackageName, Toast.LENGTH_SHORT).show();

        // true if your app has been downloaded from Play Store
        return installerPackageName != null && validInstallers.contains(installerPackageName);


    }

    // ask user's permission for phone read and write storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_ACCESS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Helper.makeDirectory(Helper.APP_FOLDER, getApplicationContext());
            } else {
                Snackbar.make(findViewById(R.id.main_list), R.string.storage_permissions,
                        Snackbar.LENGTH_SHORT).setAction("Grant", view -> firstStart());
            }
        }
    }

    // displays the list learn, practice, recall,... on the first page, when clicked on a list item, go to that Activity
    public void setAdapter() {
        final List<Item> list = setList();
        MainAdapter adapter = new MainAdapter(this, list);
        ListView listView = findViewById(R.id.main_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Item item = list.get(position);
            Intent intent = new Intent(MainActivity.this, item.mClass);
            if (item.mItem == R.string.apply)
                intent.putExtra(getString(R.string.apply), getString(R.string.apply));
            intent.putExtra(Helper.TYPE, item.mItem);
            startActivity(intent);
        });
        Timber.v("Adapter set!");
    }

    // sets the list learn, practice, recall,...
    private List<Item> setList() {
        return Arrays.asList(
                new Item(R.string.learn, R.drawable.learn, Learn.class),
                new Item(R.string.practice, R.drawable.practice, Practice.class),
                new Item(R.string.recall, R.drawable.recall, RecallSelector.class),
                new Item(R.string.apply, R.drawable.implement, Implement.class),
                new Item(R.string.my_space, R.drawable.my_space, MySpace.class),
                new Item(R.string.preferences, R.drawable.preferences, Preferences.class),
                new Item(R.string.get_pro, R.drawable.get_pro, Contribute.class));
        //list.add(new Item(R.string.reminders,))
        //Timber.v("List set!");
    }

    // data about each item
    private class Item {
        int mItem, mImageId;
        Class mClass;

        Item(int itemName, int im, Class class1) {
            mItem = itemName;
            mClass = class1;
            mImageId = im;
        }
    }


    // defining the adapter which is going to take the list of items for displaying
    private class MainAdapter extends ArrayAdapter<Item> {

        MainAdapter(Activity context, List<Item> words) {
            super(context, 0, words);
        }

        @SuppressLint("InflateParams")  // passing null as rootView
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_category, null, true);

            TextView textView = convertView.findViewById(R.id.text);
            textView.setText(getString(Objects.requireNonNull(getItem(position)).mItem));
            ImageView img = convertView.findViewById(R.id.image);
            Picasso
                    .get()
                    .load(Objects.requireNonNull(getItem(position)).mImageId)
                    .placeholder(R.mipmap.ic_launcher)
                    .fit()
                    .centerCrop()
                    //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                    .into(img);

            return convertView;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}

