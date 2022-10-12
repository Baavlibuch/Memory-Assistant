package com.memory_athlete.memoryassistant.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.LocaleHelper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.SettingLanguage;
import com.memory_athlete.memoryassistant.preferences.TimePreference;

import java.util.Objects;

import timber.log.Timber;

public class Preferences extends AppCompatActivity {
    String mTheme;
    static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTheme = sharedPreferences.getString(getString(R.string.theme), "AppTheme");
        Helper.theme(this, Preferences.this);
        setContentView(R.layout.activity_preferences);
        setTitle(getString(R.string.preferences));
    }

    public static class MemoryPreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceChangeListener {
        int changeCount = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            Timber.v("onCreate() started");
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            bindPreferenceSummaryToValue(findPreference(getString(R.string.periodic)));
            bindPreferenceToast(findPreference(getString(R.string.speech_rate)));
            bindPreferenceToast(findPreference("Change Language"));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.mTheme)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.location_wise)));
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.transit)));
            Timber.v("onCreate() complete");
        }

        private void bindPreferenceToast(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference,
                    sharedPreferences.getString(preference.getKey(), "0.25"));
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference,
                    sharedPreferences.getString(preference.getKey(), "22:30"));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            Timber.v("preference changed");
            String stringValue = value.toString();

            if (Objects.equals(preference.getKey(), getString(R.string.speech_rate))) {
                Timber.d("changeCount = %s", changeCount);
                if (changeCount++ == 1) Toast.makeText(getActivity(),
                        R.string.speech_rate_changed_message, Toast.LENGTH_LONG).show();

            }
            else if (Objects.equals(preference.getKey(), "Change Language")){

                SharedPreferences Preference = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
                String language = Preference.getString("Change Language","");

                SettingLanguage sl = new SettingLanguage();
                String string_to_locale = sl.setLang(stringValue);
                LocaleHelper.setLocale(getActivity(), string_to_locale);

                SharedPreferences shrd = getActivity().getSharedPreferences("LANGUAGE",MODE_PRIVATE);
                SharedPreferences.Editor editor = shrd.edit();
                editor.putString("str",stringValue);
                editor.apply();

                //Toast.makeText(getActivity(),stringValue+" Language selected",Toast.LENGTH_SHORT).show();

                if(!language.equals(stringValue)){
                    Toast.makeText(getActivity(), "Language changed. Please restart the app", Toast.LENGTH_SHORT).show();
                }
            }

            else if (preference instanceof TimePreference) {
                int min = Integer.parseInt(stringValue.substring(stringValue.indexOf(":") + 1));
                int hour = Integer.parseInt(stringValue.substring(0, stringValue.indexOf(":")));
                String meridian = (hour < 12) ? " am" : " pm";
                String minutes = (min < 10) ? 0 + String.valueOf(min) : String.valueOf(min);
                if (hour > 12) hour -= 12;
                else if (hour == 0) hour = 12;

                stringValue = hour + " : " + minutes + meridian;
                preference.setSummary(stringValue);

            } else Timber.d("Preference key = " + preference.getKey() +
                    "\nPreference title = " + preference.getTitle());

            return true;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!Objects.equals(PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.theme), "AppTheme"), mTheme))
            Toast.makeText(this, "Please restart the app", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}