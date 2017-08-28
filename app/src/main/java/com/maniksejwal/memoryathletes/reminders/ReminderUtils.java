package com.maniksejwal.memoryathletes.reminders;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.services.ReminderJobService;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Manik on 26/08/17.
 */

public class ReminderUtils {
    private static final long MIN = 60000;
    private static final long HOUR = MIN * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final int REMINDER_INTERVAL_HOURS = 1;
    private static final int REMINDER_INTERVAL_SECS = (int) TimeUnit.HOURS.toSeconds(
            REMINDER_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = 5 * 60;
    private static final String REMINDER_JOB_TAG = "hydration_reminder_tag";
    private static boolean sInitialised;
    private static final String LOG_TAG = "\tReminderUtils: ";

    synchronized public static void scheduleReminder(@NonNull final Context context) {
        Log.v(LOG_TAG, String.valueOf(sInitialised));
        int diff = next(context);
        Log.v(LOG_TAG, "Next notification is after " + diff + "seconds");
        //Bundle bundle = new Bundle();
        //bundle.putString("mText", mText);
        //if (sInitialised) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Log.v(LOG_TAG, "dispatcher created");
        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(ReminderJobService.class)
                .setTag(REMINDER_JOB_TAG)
                //.setConstraints()
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(diff, diff + 20))
                //REMINDER_INTERVAL_SECS - SYNC_FLEXTIME_SECONDS,
                //REMINDER_INTERVAL_SECS + SYNC_FLEXTIME_SECONDS))
                //.setExtras(bundle)
                .setReplaceCurrent(true)
                .build();
        Log.v(LOG_TAG, "Job built");
        dispatcher.schedule(constraintReminderJob);
        Log.v(LOG_TAG, "dispatcher scheduled");
        sInitialised = true;
        Log.v(LOG_TAG, "reminder set");
    }

    private static int next(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long lastOpened = sharedPreferences.getLong("last_opened", System.currentTimeMillis());
        Log.v(LOG_TAG, "last opened - " + String.valueOf(lastOpened));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String time = preferences.getString(context.getString(R.string.periodic), "11:00");
        Log.v(LOG_TAG, "reminder time - " + time);
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        Log.v(LOG_TAG, hour + " " + minutes);
        //int gap=min*60+
        long cur = System.currentTimeMillis();
        long diff = cur - lastOpened;
        Calendar rightNow = Calendar.getInstance();
        int hr = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        String text="";

        if (diff / DAY < 1) {
            text = "Time to practice!";
            diff = (hour * 60 + minutes) - (hr * 60 + min);
        } else if (diff / DAY < 2) {
            text = "You haven't practiced for a day!";
            diff = (hour * 60 + minutes) - (hr * 60 + min);
        } else if (diff / DAY < 7) {
            text = "You haven't practiced for" + diff / DAY + " days!";
            diff = (hour * 60 + minutes) - (hr * 60 + min) + 2 * (diff / 1000) / 60;
        } else if ((diff / WEEK) % 8 == 0) {
            text = "You haven't practiced for " + diff / (DAY * 7) + " weeks!";
            diff = (hour * 60 + minutes) - (hr * 60 + min) + 2 * (diff / 1000) / 60;
        }

        if (diff < 0) diff += DAY / 1000;
        diff *= 60;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mText", text);
        editor.apply();
        return (int) diff;
    }
}
