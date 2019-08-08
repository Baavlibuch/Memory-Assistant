package com.memory_athlete.memoryassistant;

import android.content.SharedPreferences;
import android.widget.ListView;

import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.memory_athlete.memoryassistant.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkArrayAdapterElementCount() {
        onView(withId(R.id.main_list)).check(new ListViewItemCountAssertion(7));
    }

    // run twice
    @Test
    public void checkColorTest() {
        String gray = "ff424242";
        String white = "ffffffff";
        String pitch = "ff000000";

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivityTestRule.getActivity());
        String[] themes = mActivityTestRule.getActivity().getResources().getStringArray(R.array.themes);

        CardView cardView = (CardView) ((ListView) mActivityTestRule.getActivity().findViewById(R.id.main_list))
                .getChildAt(0);
        int color = cardView.getCardBackgroundColor().getDefaultColor();

        switch (sharedPreferences.getString(mActivityTestRule.getActivity().getString(R.string.theme), "AppTheme")) {
            case "Night":
                assertEquals(gray, Integer.toHexString(color));
                break;
            case "Dark":
                assertEquals(gray, Integer.toHexString(color));
                break;
            case "Light (Default)":
                assertEquals(white, Integer.toHexString(color));
                break;
            default:
                assertEquals(white, Integer.toHexString(color));
                break;
        }

        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(mActivityTestRule.getActivity().getString(R.string.theme), themes[new Random().nextInt(3)]);
        e.apply();
    }

    @Test
    public void valuesTest(){
        String[] themes = mActivityTestRule.getActivity().getResources().getStringArray(R.array.themes);
        assertEquals(themes[0], "Light (Default)");
        assertEquals(themes[1], "Dark");
        assertEquals(themes[2], "Night");

    }
}