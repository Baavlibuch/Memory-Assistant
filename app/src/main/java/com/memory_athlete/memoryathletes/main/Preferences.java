package com.memory_athlete.memoryathletes.main;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.Toast;

import com.memory_athlete.memoryathletes.R;
import com.memory_athlete.memoryathletes.reminders.TimePreference;

public class Preferences extends AppCompatActivity {
    String theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme");
        String title="";
        switch (theme){
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
                title="<font color=#FFFFFF>";
        }
        setContentView(R.layout.activity_preferences);
        setTitle(Html.fromHtml(title + getString(R.string.preferences)));
    }

    public static class MemoryPreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.periodic)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.theme)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.location_wise)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.transit)));
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).
                            getString(preference.getKey(), "22:30"));
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
                preference.setSummary(stringValue);
            }
            return true;
        }

    }
        @Override
        public void onStop() {
            super.onStop();
            if(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(getString(R.string.theme), "AppTheme") != theme)
            Toast.makeText(this, "You might need to restart the app for changes to take effect", Toast.LENGTH_SHORT).show();
        }
}
