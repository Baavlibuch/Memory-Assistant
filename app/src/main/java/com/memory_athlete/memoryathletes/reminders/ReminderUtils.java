package com.memory_athlete.memoryathletes.reminders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.memory_athlete.memoryathletes.R;
import com.memory_athlete.memoryathletes.services.ReminderJobService;

import java.util.Calendar;

/**
 * Created by Manik on 26/08/17.
 */

public class ReminderUtils {
    private static final int MIN = 60;
    private static final int HOUR = MIN * 60;
    private static final int DAY = HOUR * 24;
    private static final int WEEK = DAY * 7;
    private static final int MONTH = WEEK * 4;
    //private static final int REMINDER_INTERVAL_HOURS = 1;
    //private static final int REMINDER_INTERVAL_SECS = (int) TimeUnit.HOURS.toSeconds(
    //      REMINDER_INTERVAL_HOURS);
    //private static final int SYNC_FLEXTIME_SECONDS = 5 * 60;
    private static final String REMINDER_JOB_TAG = "practice_time_";
    private static final String LOG_TAG = "\tReminderUtils: ";

    synchronized public static void scheduleReminder(@NonNull final Context context) {
        int diff = next(context) * 60, diff1;
        if (diff < 0) diff += DAY;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Log.v(LOG_TAG, "dispatcher created");
        String s = "";
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 1:
                    diff1 = (DAY + diff);
                    s = "day";
                    break;
                case 2:
                    diff1 = (WEEK + diff);
                    s = "week";
                    break;
                default:
                    diff1 = diff;
                    s = "seconds";
            }
            if (diff1 > HOUR) diff1 += HOUR;


            dispatcher.cancel(REMINDER_JOB_TAG + i);
            Job constraintReminderJob = dispatcher.newJobBuilder()
                    .setService(ReminderJobService.class)
                    .setTag(REMINDER_JOB_TAG + i)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow(diff1 - HOUR, diff1 + HOUR))
                    .setReplaceCurrent(true)
                    .build();
            Log.v(LOG_TAG, "Notification is after " + diff1 + " " + s);
            //.setConstraints()
            //REMINDER_INTERVAL_SECS - SYNC_FLEXTIME_SECONDS,
            //REMINDER_INTERVAL_SECS + SYNC_FLEXTIME_SECONDS))
            //.setExtras(bundle)
            Log.v(LOG_TAG, "Job built");
            dispatcher.schedule(constraintReminderJob);
            Log.v(LOG_TAG, "dispatcher scheduled");
        }
        Log.v(LOG_TAG, "reminder set");
    }

    private static int next(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String time = preferences.getString(context.getString(R.string.periodic), "22:30");
        Log.v(LOG_TAG, "reminder time - " + time);
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        Log.v(LOG_TAG, hour + " " + minutes);
        Calendar rightNow = Calendar.getInstance();
        int hr = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        return (hour * 60 + minutes) - (hr * 60 + min);
    }

    synchronized public static void mySpaceReminder(@NonNull final Context context, String fname) {
        Bundle bundle = new Bundle();
        bundle.putString("fname", fname);

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        int diff = next(context) * 60, diff1 = 0;
        String s = "";
        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0:
                    if (diff < 10 * HOUR) diff1 = diff + DAY;
                    s = "seconds";
                    break;
                case 1:
                    diff1 = diff + 3 * DAY;
                    s = "days";
                    break;
                case 2:
                    diff1 = diff + WEEK;
                    s = "week";
                    break;
                case 3:
                    diff1 = diff + MONTH;
                    s = "month";
                    break;
                case 4:
                    diff1 = diff + 3 * MONTH;
                    s = "months";
                    break;
                case 5:
                    diff1 = diff + 6 * MONTH;
                    s = "6 months";
                    break;
                case 6:
                    diff1 = diff + 12 * MONTH;
                    s = "year";
            }
            if (diff1 > HOUR) diff1 += HOUR;
            Log.v(LOG_TAG, "Notification is after " + diff1 + " " + s);

            Job constraintReminderJob = dispatcher.newJobBuilder()
                    .setService(ReminderJobService.class)
                    .setTag(fname + i)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow(diff1 - HOUR, diff1 + HOUR))
                    .setReplaceCurrent(true)
                    .setExtras(bundle)
                    .build();
            //.setConstraints()
            //REMINDER_INTERVAL_SECS - SYNC_FLEXTIME_SECONDS,
            //REMINDER_INTERVAL_SECS + SYNC_FLEXTIME_SECONDS))
            //.setExtras(bundle)
            Log.v(LOG_TAG, "Job built");
            dispatcher.schedule(constraintReminderJob);
            Log.v(LOG_TAG, "dispatcher scheduled");
        }
    }
}
