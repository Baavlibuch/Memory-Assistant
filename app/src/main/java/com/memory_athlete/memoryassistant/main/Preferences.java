package com.memory_athlete.memoryassistant.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.NotificationReceiver;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.language.SettingLanguage;
import com.memory_athlete.memoryassistant.preferences.TimeDialog;
import com.memory_athlete.memoryassistant.preferences.TimePreference;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import timber.log.Timber;

//import com.memory_athlete.memoryassistant.preferences.TimeDialog;

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

    public static class MemoryPreferenceFragment extends PreferenceFragmentCompat implements
            Preference.OnPreferenceChangeListener {


        int changeCount = 0;

//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            Timber.v("onCreate() started");
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.settings_main);
//
//            bindPreferenceSummaryToValue(findPreference(getString(R.string.periodic)));
//            bindPreferenceToast(findPreference(getString(R.string.speech_rate)));
//            bindPreferenceToast(findPreference("Change Language"));
//            //bindPreferenceSummaryToValue(findPreference(getString(R.string.mTheme)));
//            //bindPreferenceSummaryToValue(findPreference(getString(R.string.location_wise)));
//            //bindPreferenceSummaryToValue(findPreference(getString(R.string.transit)));
//            Timber.v("onCreate() complete");
//        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.settings_main);

            bindPreferenceSummaryToValue(Objects.requireNonNull(findPreference(getString(R.string.periodic))));
            bindPreferenceToast(Objects.requireNonNull(findPreference(getString(R.string.speech_rate))));
            bindPreferenceToast(Objects.requireNonNull(findPreference("Change Language")));

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
        public void onDisplayPreferenceDialog(Preference preference)
        {
            DialogFragment dialogFragment = null;
            if (preference instanceof TimePreference)
            {
                dialogFragment = new TimeDialog();
                Bundle bundle = new Bundle(1);
                bundle.putString("key", preference.getKey());
                dialogFragment.setArguments(bundle);
            }

            if (dialogFragment != null)
            {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(Objects.requireNonNull(this.getFragmentManager()), "android.support.v7.preference.PreferenceFragment.DIALOG");
            }
            else
            {
                super.onDisplayPreferenceDialog(preference);
            }
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

                SharedPreferences Preference = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireActivity());
                String language = Preference.getString("Change Language","");

                SettingLanguage sl = new SettingLanguage();
                String string_to_locale = sl.setLang(stringValue);
                LocaleHelper.setLocale(getActivity(), string_to_locale);

                SharedPreferences shrd = requireActivity().getSharedPreferences("LANGUAGE",MODE_PRIVATE);
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

                int minutess = Integer.parseInt(minutes);

                Toast.makeText(getActivity(), stringValue, Toast.LENGTH_SHORT).show();

                preference.setSummary(stringValue);

                Calendar calendar = Calendar.getInstance();
                if(meridian.equals(" pm")){
                    hour = hour + 12;
                }

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minutess);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTime().compareTo(new Date()) < 0)
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                Intent intent = new Intent(getActivity(), NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

                }

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

    //for notifying everyday
    public void myAlarm(int hour, int minutes, String meridian) {

        Calendar calendar = Calendar.getInstance();
        if(meridian.equals("pm")){
            hour = hour + 12;
        }

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        }

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }



}