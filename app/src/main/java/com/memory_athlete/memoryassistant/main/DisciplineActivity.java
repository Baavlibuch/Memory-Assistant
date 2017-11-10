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
import android.view.View;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.MakeList;
import com.memory_athlete.memoryassistant.disciplines.BinaryDigits;
import com.memory_athlete.memoryassistant.disciplines.Cards;
import com.memory_athlete.memoryassistant.disciplines.Letters;
import com.memory_athlete.memoryassistant.disciplines.Names;
import com.memory_athlete.memoryassistant.disciplines.Numbers;
import com.memory_athlete.memoryassistant.disciplines.Places;
import com.memory_athlete.memoryassistant.disciplines.Words;
import com.memory_athlete.memoryassistant.mySpace.MySpaceFragment;

import java.util.ArrayList;

import timber.log.Timber;

public class DisciplineActivity extends AppCompatActivity {
    boolean backPressed = false;

    private ArrayList<String> tabTitles = new ArrayList<>();
    Intent intent;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("entered onCreate()");
        intent = getIntent();
        theme(intent);
        int fragIndex = intent.getIntExtra("class", 0);
        Timber.v("fragIndex = " + fragIndex + "tabTitles.size() = " + tabTitles.size());
        tabTitles.add(getString(MakeList.makePracticeFrags()[fragIndex - 1]));
        for (int i = 0; i < Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.no_my_space_frags), "1")); i++)
            tabTitles.add("MySpace " + (i + 1));
        if (tabTitles.size() == 1) findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
        Timber.v("tabTitles.size() = " + tabTitles.size());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        Timber.v("adapter set");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

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
        if (header != 0)
            setTitle(getString(header));
        else setTitle(intent.getStringExtra("headerString"));

        Timber.v("theme = " + theme);
        setContentView(R.layout.activity_experiment);
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
            return tabTitles.size();
        }

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, true);
            return;
        }
        for (int i = 1; i < tabTitles.size(); i++) {
            String tag = "android:switcher:" + R.id.viewpager + ":" + i;
            MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment==null) continue;
            if (!fragment.save()) {
                viewPager.setCurrentItem(i, true);
                return;
            }
        }
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.double_back_to_exit), false) && !backPressed) {
            backPressed = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }
}