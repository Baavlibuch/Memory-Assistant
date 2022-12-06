package com.memory_athlete.memoryassistant.main;

import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.disciplines.BinaryDigits;
import com.memory_athlete.memoryassistant.disciplines.Cards;
import com.memory_athlete.memoryassistant.disciplines.Dates;
import com.memory_athlete.memoryassistant.disciplines.DisciplineFragment;
import com.memory_athlete.memoryassistant.disciplines.Letters;
import com.memory_athlete.memoryassistant.disciplines.Names;
import com.memory_athlete.memoryassistant.disciplines.Numbers;
import com.memory_athlete.memoryassistant.disciplines.Places;
import com.memory_athlete.memoryassistant.disciplines.Words;
import com.memory_athlete.memoryassistant.mySpace.MySpaceFragment;

import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

public class DisciplineActivity extends AppCompatActivity implements MySpaceFragment.TabTitleUpdater {
    boolean backPressed = false;
    private static ArrayList<String> tabTitles;
    static Intent intent;                                      //Contains data sent to this activity
    static ViewPager viewPager;
    private static TabLayout tabLayout;
    private static boolean mayAccessStorage;
    static String noOfMySpaceFrags;
    static String mySpace;

    private static SharedPreferences sharedPreferences;
    private static FragmentManager fragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("entered onCreate()");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        intent = getIntent();

        theme(intent);
        tabLayout = findViewById(R.id.sliding_tabs);
        mayAccessStorage = Helper.mayAccessStorage(this);
        noOfMySpaceFrags = getString(R.string.no_my_space_frags);
        mySpace = getString(R.string.my_space);
        fragManager = getSupportFragmentManager();

        int fragIndex = intent.getIntExtra("class", 0);
        Timber.v("fragIndex = %s", fragIndex);
        tabTitles = new ArrayList<>();
        tabTitles.add(getString(R.string.practice));
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(9);

        new LoadFragmentsAsyncTask().execute();
    }

    // Function to set the theme
    protected void theme(Intent intent) {
        String theme = requireNonNull(sharedPreferences.getString(getString(R.string.theme), "AppTheme"));
        String[] themes = getResources().getStringArray(R.array.themes);
        if (themes[1].equals(theme)) {
            setTheme(R.style.dark);
        } else if (themes[2].equals(theme)) {
            setTheme(R.style.pitch);
            (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
        } else {
            setTheme(R.style.light);
        }
        int header = intent.getIntExtra("mHeader", 0);
        if (header != 0) setTitle(getString(header));
        else setTitle(intent.getStringExtra("headerString"));

        Timber.v("theme = %s", theme);
        if (sharedPreferences.getBoolean(getString(R.string.bottom_tabs), false))
            setContentView(R.layout.activity_view_pager_bottom_tab);
        else setContentView(R.layout.activity_view_pager);
    }

    @Override
    public void tabTitleUpdate(String title) {
        if (title.equals(getString(R.string.my_space))) title += " " + viewPager.getCurrentItem();
        tabTitles.set(viewPager.getCurrentItem(), title);
        TabLayout slidingTabs = findViewById(R.id.sliding_tabs);
        requireNonNull(slidingTabs.getTabAt(viewPager.getCurrentItem())).setText(title);
    }

    private static class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private SimpleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // it takes the content according to the discipline like numbers, words, names,..
        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Bundle bundle = intent.getExtras();
                Fragment fragment;
                switch (intent.getIntExtra("class", 0)) {
                    case 1:
                        fragment = new Numbers();
                        break;
                    case 2:
                        fragment = new Words();
                        break;
                    case 3:
                        fragment = new Names();
                        break;
                    case 4:
                        fragment = new Places();
                        break;
                    case 5:
                        fragment = new Cards();
                        break;
                    case 6:
                        fragment = new BinaryDigits();
                        break;
                    case 7:
                        fragment = new Letters();
                        break;
                    case 8:
                        fragment = new Dates();
                        break;
                    default:
                        throw new RuntimeException("wrong practice, received "
                                + intent.getIntExtra("class", 0));
                }
                fragment.setArguments(bundle);
                return fragment;
            }
            return new MySpaceFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }

        @Override
        public int getCount() {
            if (!mayAccessStorage) return 1;
            return tabTitles.size();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = 1; i < tabTitles.size(); i++) {
            String tag = "android:switcher:" + R.id.viewpager + ":" + i;
            MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment == null) continue;
            fragment.save();
        }
    }

    @Override
    public void onBackPressed() {
        int cur = viewPager.getCurrentItem();
        String tag = "android:switcher:" + R.id.viewpager + ":" + cur;

        //go back in current fragment
        if (cur != 0) {
            MySpaceFragment mySpaceFragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (Objects.requireNonNull(mySpaceFragment).fragListViewId == 0 ||
                    mySpaceFragment.fragListViewId == mySpaceFragment.MIN_DYNAMIC_VIEW_ID)
                viewPager.setCurrentItem(0, true);
            else mySpaceFragment.back();
            return;
        } else {
            DisciplineFragment disciplineFragment = (DisciplineFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (!Objects.requireNonNull(disciplineFragment).reset()) return;
        }

        //Check if everything is saved
        for (int i = 1; i < tabTitles.size(); i++) {
            tag = "android:switcher:" + R.id.viewpager + ":" + i;
            MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment == null) continue;
            if (!fragment.save()) {
                viewPager.setCurrentItem(i, true);
                return;
            }
        }

        //Stop the loading
        tag = "android:switcher:" + R.id.viewpager + ":" + 0;
        DisciplineFragment disciplineFragment = (DisciplineFragment) getSupportFragmentManager().findFragmentByTag(tag);
        assert disciplineFragment != null;
        if (disciplineFragment.a.get(disciplineFragment.RUNNING) == disciplineFragment.TRUE) {
            //Stop the generation of the random list if it were being generated
            disciplineFragment.a.set(disciplineFragment.RUNNING, disciplineFragment.FALSE);
            return;
        }

        //Check if sudden exit is enabled
        if (sharedPreferences.getBoolean(getString(R.string.double_back_to_exit), false)
                && !backPressed) {
            backPressed = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            String tag = "android:switcher:" + R.id.viewpager + ":" + 0;
            DisciplineFragment disciplineFragment = (DisciplineFragment) getSupportFragmentManager().
                    findFragmentByTag(tag);

            if (Objects.requireNonNull(disciplineFragment).a.get(disciplineFragment.RUNNING) ==
                    disciplineFragment.TRUE)
                disciplineFragment.a.set(disciplineFragment.RUNNING, disciplineFragment.FALSE);
            // Stopped the generation of the random list

            for (int i = 1; i < tabTitles.size(); i++) {
                tag = "android:switcher:" + R.id.viewpager + ":" + i;
                MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager()
                        .findFragmentByTag(tag);
                if (fragment == null) continue;
                if (!fragment.save()) {
                    viewPager.setCurrentItem(i, true);
                    return true;
                    //super.onOptionsItemSelected(item)
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    static protected class LoadFragmentsAsyncTask extends AsyncTask<Void, Void, SimpleFragmentPagerAdapter> {

        @Override
        protected SimpleFragmentPagerAdapter doInBackground(Void... v) {
            if (mayAccessStorage) {
                int noOfMySpaceScreens = Integer.parseInt(Objects.requireNonNull(sharedPreferences
                        .getString(noOfMySpaceFrags, "1")));
                for (int i = 0; i < noOfMySpaceScreens; i++) {
                    if (noOfMySpaceScreens == 1) tabTitles.add(mySpace);
                    else tabTitles.add(mySpace + " " + (i + 1));
                }
                Timber.v("tabTitles.size() = %s", tabTitles.size());
            }
            return new SimpleFragmentPagerAdapter(fragManager);
        }

        @Override
        protected void onPostExecute(SimpleFragmentPagerAdapter adapter) {
            super.onPostExecute(adapter);
            if (tabTitles.size() == 1 || !mayAccessStorage)
                tabLayout.setVisibility(View.GONE);

            viewPager.setAdapter(adapter);
            Timber.v("adapter set");

            tabLayout.setupWithViewPager(viewPager);
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}