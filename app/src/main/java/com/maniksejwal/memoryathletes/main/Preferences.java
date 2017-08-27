package com.maniksejwal.memoryathletes.main;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.reminders.TimePreference;

public class Preferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setTitle(getString(R.string.preferences));
    }

    public static class MemoryPreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.periodic)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.location_wise)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.transit)));
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).
                            getString(preference.getKey(), "10:0"));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof TimePreference) {
                int min = Integer.parseInt(stringValue.substring(stringValue.indexOf(":") + 1));
                int hour = Integer.parseInt(stringValue.substring(0, stringValue.indexOf(":")));
                String meridian = (hour < 12) ? " am" : " pm";
                String minutes = (min < 10) ? 0 + String.valueOf(min) : String.valueOf(min);
                if (hour > 12) {
                    hour -= 12;
                } else if(hour == 0){
                    hour = 12;
                }

                stringValue = hour + " : " + minutes + meridian;
                preference.setSummary("Remind me at " + stringValue);
            }
            return true;
        }
    }
}
