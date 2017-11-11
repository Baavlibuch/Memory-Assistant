package com.memory_athlete.memoryassistant.lessons;

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

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.mySpace.MySpaceFragment;

import java.util.ArrayList;

import timber.log.Timber;

public class ImplementLesson extends AppCompatActivity implements MySpaceFragment.TabTitleUpdater {
    private ArrayList<String> tabTitles = new ArrayList<>();
    Intent intent;
    ViewPager viewPager;

    @Override
    public void tabTitleUpdate(String title) {
        if (title.equals(getString(R.string.my_space))) title += " " + viewPager.getCurrentItem();
        Timber.v("updating tabTitles");
        tabTitles.set(viewPager.getCurrentItem(), title);
        TabLayout slidingTabs = (TabLayout) findViewById(R.id.sliding_tabs);
        slidingTabs.getTabAt(viewPager.getCurrentItem()).setText(title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("entered onCreate()");
        intent = getIntent();
        theme(intent);

        tabTitles.add(getString(R.string.apply));
        for (int i = 0; i < Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.no_my_space_frags), "1")); i++)
            tabTitles.add("MySpace " + (i + 1));
        if (tabTitles.size() == 1) findViewById(R.id.sliding_tabs).setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(9);
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
                    LessonFragment lessonFragment = new LessonFragment();
                    lessonFragment.setArguments(bundle);
                    return lessonFragment;
                default:
                    return new MySpaceFragment();
                /*default :
                    finish();
                    Timber.w("couldn't open lessonFragment");
                    return null;*/
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
        int cur = viewPager.getCurrentItem();
        if (cur != 0) {
            String tag = "android:switcher:" + R.id.viewpager + ":" + cur;
            MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment.fragListViewId == 0 || fragment.fragListViewId == fragment.MIN_DYNAMIC_VIEW_ID)
                viewPager.setCurrentItem(0, true);
            else fragment.back();
            return;
        }
        for (int i = 1; i < tabTitles.size(); i++) {
            String tag = "android:switcher:" + R.id.viewpager + ":" + i;
            MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (fragment == null) continue;
            if (!fragment.save()) {
                viewPager.setCurrentItem(i, true);
                return;
            }
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
            for (int i = 1; i < tabTitles.size(); i++) {
                String tag = "android:switcher:" + R.id.viewpager + ":" + i;
                MySpaceFragment fragment = (MySpaceFragment) getSupportFragmentManager().findFragmentByTag(tag);
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