package com.memory_athlete.memoryassistant;

import android.content.SharedPreferences;
import android.widget.ListView;

import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.memory_athlete.memoryassistant.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import timber.log.Timber;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTests {
    String theme;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setPreferencesAndFiles() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivityTestRule.getActivity());
        Timber.i("old theme = %s", sharedPreferences.getString(mActivityTestRule.getActivity().getString(R.string.theme), "AppTheme"));
        SharedPreferences.Editor e = sharedPreferences.edit();
        String[] themes = mActivityTestRule.getActivity().getResources().getStringArray(R.array.themes);

        int themeInt = new Random().nextInt(3);
        e.putString(mActivityTestRule.getActivity().getString(R.string.theme), themes[themeInt]);
        theme = themes[themeInt];
        e.apply();
        Timber.i("Theme = ", theme);
    }

    @Test
    public void checkArrayAdapterElementCount() {
        onView(withId(R.id.main_list)).check(new ListViewItemCountAssertion(7));
    }

    @Test
    public void checkColorTest() {
        String gray = "ff424242";
        String white = "ffffffff";
        String pitch = "ff000000";

        CardView cardView = (CardView) ((ListView) mActivityTestRule.getActivity().findViewById(R.id.main_list))
                .getChildAt(0);

        int color = cardView.getCardBackgroundColor().getDefaultColor();
        switch (theme) {
            case "Night":
                assertEquals(Integer.toHexString(color), gray);
                break;
            case "Dark":
                assertEquals(Integer.toHexString(color), gray);
                break;
            case "Light (Default)":
                assertEquals(Integer.toHexString(color), white);
                break;
            default:
                assertEquals(Integer.toHexString(color), white);
                break;
        }
    }
}