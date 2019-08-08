package com.memory_athlete.memoryassistant;

import android.preference.SwitchPreference;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.memory_athlete.memoryassistant.main.Preferences;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.matcher.PreferenceMatchers.withKey;
import static androidx.test.espresso.matcher.PreferenceMatchers.withTitle;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4ClassRunner.class)
public class PreferenceActivityTests {
    @Rule
    public ActivityTestRule<Preferences> mActivityTestRule
            = new ActivityTestRule<>(Preferences.class);

    // Doesn't work, ignore
    @Test
    public void justToggleSwitchPreferencesTest() {
        int[] titles = new int[]{R.string.remind, R.string.bottom_tabs, R.string.preceding_zeros,
                R.string.single_card, R.string.shuffle_decks, R.string.double_back_to_exit};
        for (int i : titles)
            withKey(mActivityTestRule.getActivity().getString(i));

        mActivityTestRule.getActivity();

        SwitchPreference preference = new SwitchPreference(getInstrumentation().getContext());
        assertThat(preference, withKey(mActivityTestRule.getActivity().getString(R.string.remind)));
        assertThat(preference, withTitle(R.string.bottom_tabs));
    }
}
