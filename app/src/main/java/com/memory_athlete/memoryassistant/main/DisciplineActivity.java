package com.memory_athlete.memoryassistant.main;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.Helper;
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

import timber.log.Timber;

public class DisciplineActivity extends AppCompatActivity implements MySpaceFragment.TabTitleUpdater {
    boolean backPressed = false;

    private ArrayList<String> tabTitles = new ArrayList<>();
    Intent intent;                                      //Contains data sent to this activity
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("entered onCreate()");
        intent = getIntent();
        theme(intent);
        int fragIndex = intent.getIntExtra("class", 0);
        Timber.v("fragIndex = " + fragIndex + "tabTitles.size() = " + tabTitles.size());
        tabTitles.add(getString(R.string.practice));
        int noOfMySpaceScreens = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(
                this).getString(getString(R.string.no_my_space_frags), "1"));
        for (int i = 0; i < noOfMySpaceScreens; i++) {
            if(noOfMySpaceScreens == 1) tabTitles.add(getString(R.string.my_space));
            else tabTitles.add(getString(R.string.my_space) + " " + (i + 1));
        }
        if (tabTitles.size() == 1 || !Helper.mayAccessStorage(this))
            findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
        Timber.v("tabTitles.size() = " + tabTitles.size());
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(9);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        Timber.v("adapter set");

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Function to set the theme
    protected void theme(Intent intent) {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme");
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

        Timber.v("theme = " + theme);
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.bottom_tabs), false))
            setContentView(R.layout.activity_view_pager_bottom_tab);
        else setContentView(R.layout.activity_view_pager);
    }

    @Override
    public void tabTitleUpdate(String title) {
        if (title.equals(getString(R.string.my_space))) title += " " + viewPager.getCurrentItem();
        Timber.v("updating tabTitles");
        tabTitles.set(viewPager.getCurrentItem(), title);
        TabLayout slidingTabs = findViewById(R.id.sliding_tabs);
        slidingTabs.getTabAt(viewPager.getCurrentItem()).setText(title);
    }

    private class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

        private SimpleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
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
                            throw new RuntimeException("wrong practice");
                    }
                    fragment.setArguments(bundle);
                    return fragment;
                default:
                    return new MySpaceFragment();
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
            MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment.fragListViewId == 0 || fragment.fragListViewId == fragment.MIN_DYNAMIC_VIEW_ID)
                viewPager.setCurrentItem(0, true);
            else fragment.back();
            return;
        } else {
            DisciplineFragment fragment = (DisciplineFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (!fragment.reset()) return;
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
        DisciplineFragment frag = (DisciplineFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (frag.a.get(frag.RUNNING) == frag.TRUE) {
            //Stop the generation of the random list if it were being generated
            frag.a.set(frag.RUNNING, frag.FALSE);
            return;
        }

        //Check if sudden exit is enabled
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.double_back_to_exit), false) && !backPressed) {
            backPressed = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            String tag = "android:switcher:" + R.id.viewpager + ":" + 0;
            DisciplineFragment frag = (DisciplineFragment) getSupportFragmentManager().
                    findFragmentByTag(tag);
            if (frag.a.get(frag.RUNNING) == frag.TRUE) frag.a.set(frag.RUNNING, frag.FALSE);
            //Stopped the generation of the random list

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
}