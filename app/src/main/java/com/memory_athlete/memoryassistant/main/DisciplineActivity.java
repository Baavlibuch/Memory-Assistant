package com.memory_athlete.memoryassistant.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.memory_athlete.memoryassistant.Helper;
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

import static java.util.Objects.requireNonNull;

public class DisciplineActivity extends AppCompatActivity implements MySpaceFragment.TabTitleUpdater {
    boolean backPressed = false;

    private ArrayList<String> tabTitles = new ArrayList<>();
    Intent intent;                                      //Contains data sent to this activity
    ViewPager viewPager;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("entered onCreate()");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        intent = getIntent();
        theme(intent);
        int fragIndex = intent.getIntExtra("class", 0);
        Timber.v("fragIndex = " + fragIndex + "tabTitles.size() = " + tabTitles.size());
        tabTitles.add(getString(R.string.practice));
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(9);

        if (!Helper.mayAccessStorage(this)) {
            Toast.makeText(this, "Storage permissions are required", Toast.LENGTH_LONG).show();
            finish();
        }

        new LoadFragmentsAsyncTask().execute();
    }

    // Function to set the theme
    protected void theme(Intent intent) {
        String theme = requireNonNull(sharedPreferences.getString(getString(R.string.theme), "AppTheme"));
        switch (theme) {
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
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

    private class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private SimpleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            try {
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
            } catch (IllegalStateException e){
                throw new RuntimeException("IllegalStateException from ViewPager.populate() caused in DisciplineActivity.getItem()");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles.get(position);
        }

        @Override
        public int getCount() {
            if (!Helper.mayAccessStorage(DisciplineActivity.this)) return 1;
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

    protected class LoadFragmentsAsyncTask extends AsyncTask<Void, SimpleFragmentPagerAdapter,
            SimpleFragmentPagerAdapter> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (tabTitles.size() == 1 || !Helper.mayAccessStorage(DisciplineActivity.this))
                findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
        }

        @Override
        protected SimpleFragmentPagerAdapter doInBackground(Void... v) {
            if (Helper.mayAccessStorage(DisciplineActivity.this)) {
                int noOfMySpaceScreens = Integer.parseInt(Objects.requireNonNull(sharedPreferences
                        .getString(getString(R.string.no_my_space_frags), "1")));
                for (int i = 0; i < noOfMySpaceScreens; i++) {
                    if (noOfMySpaceScreens == 1) tabTitles.add(getString(R.string.my_space));
                    else tabTitles.add(getString(R.string.my_space) + " " + (i + 1));
                }
            Timber.v("tabTitles.size() = %s", tabTitles.size());
            }
            return new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        }

        @Override
        protected void onPostExecute(SimpleFragmentPagerAdapter adapter) {
            super.onPostExecute(adapter);
            viewPager.setAdapter(adapter);
            Timber.v("adapter set");

            TabLayout tabLayout = findViewById(R.id.sliding_tabs);
            tabLayout.setupWithViewPager(viewPager);
        }
    }
}