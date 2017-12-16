package com.memory_athlete.memoryassistant.reminders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.services.MySpaceJobService;
import com.memory_athlete.memoryassistant.services.ReminderJobService;

import java.util.Calendar;

import timber.log.Timber;

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

    synchronized public static void scheduleReminder(@NonNull final Context context) {
        int diff = next(context) * 60, diff1;
        if (diff < 0) diff += DAY;

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        for (int i = 0; i < 3; i++) {
            switch (i) {
                case 1:
                    diff1 = (DAY + diff);
                    break;
                case 2:
                    diff1 = (WEEK + diff);
                    break;
                default:
                    diff1 = diff;
            }

            dispatcher.cancel(REMINDER_JOB_TAG + i);
            Job constraintReminderJob = dispatcher.newJobBuilder()
                    .setService(ReminderJobService.class)
                    .setTag(REMINDER_JOB_TAG + i)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow(diff1, diff1 + HOUR))
                    .setReplaceCurrent(true)
                    .build();
            Timber.v("Notification is after " + diff1);
            //.setConstraints()
            //REMINDER_INTERVAL_SECS - SYNC_FLEXTIME_SECONDS,
            //REMINDER_INTERVAL_SECS + SYNC_FLEXTIME_SECONDS))
            //.setExtras(bundle)
            Timber.v("Job built");
            dispatcher.schedule(constraintReminderJob);
            Timber.v("dispatcher scheduled");
        }
        Timber.v("reminder set");
    }

    private static int next(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String time = preferences.getString(context.getString(R.string.periodic), "22:30");
        Timber.v("reminder time - " + time);
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        Timber.v(hour + " " + minutes);
        Calendar rightNow = Calendar.getInstance();
        int hr = rightNow.get(Calendar.HOUR_OF_DAY);
        int min = rightNow.get(Calendar.MINUTE);
        return (hour * 60 + minutes) - (hr * 60 + min);
    }

    synchronized public static void mySpaceReminder(@NonNull final Context context, String fname) {
        Bundle bundle = new Bundle();
        bundle.putString("fPath", fname);

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        int diff = next(context) * 60;

        for (int i = 0; i < 7; i++) {
            int diff1 = diff;
            switch (i) {
                case 0:
                    if (diff < 10 * HOUR) diff1 += DAY;
                    break;
                case 1:
                    diff1 += 3 * DAY;
                    break;
                case 2:
                    diff1 += WEEK;
                    break;
                case 3:
                    diff1 += MONTH;
                    break;
                case 4:
                    diff1 += 3 * MONTH;
                    break;
                case 5:
                    diff1 += 6 * MONTH;
                    break;
                case 6:
                    diff1 += 12 * MONTH;
            }

            Timber.v("MySpace notification is after " + diff1);
            Job constraintReminderJob = dispatcher.newJobBuilder()
                    .setService(MySpaceJobService.class)
                    .setTag(fname + i)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow(diff1 - 5 * MIN, diff1 + HOUR))
                    .setReplaceCurrent(true)
                    .setExtras(bundle)
                    .build();
            //.setConstraints()
            //REMINDER_INTERVAL_SECS - SYNC_FLEXTIME_SECONDS,
            //REMINDER_INTERVAL_SECS + SYNC_FLEXTIME_SECONDS))
            //.setExtras(bundle)
            Timber.v("Job built");
            dispatcher.schedule(constraintReminderJob);
            Timber.v("dispatcher scheduled");
        }
    }
}
