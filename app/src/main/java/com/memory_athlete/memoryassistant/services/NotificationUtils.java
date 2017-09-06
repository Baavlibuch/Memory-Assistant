package com.memory_athlete.memoryassistant.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.compat.BuildConfig;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.main.MainActivity;

import static com.memory_athlete.memoryassistant.R.string.app_name;

/**
 * Created by Manik on 26/08/17.
 */

abstract class NotificationUtils {
    private static final String LOG_TAG = "\tNotificationUtils : ";
    private static final int PERIODIC_REMINDER_PENDING_INTENT_ID = 3417;
    private static final int MY_SPACE_REMINDER_PENDING_INTENT_ID = 3418;
    private static final long MIN = 60000;
    private static final long HOUR = MIN * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long MONTH = WEEK * 4;

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, PERIODIC_REMINDER_PENDING_INTENT_ID, startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    static void createNotification(Context context) {

        String text = text(context);
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, text);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(largeIcon(context))
                .setSmallIcon(R.mipmap.launcher_ic)
                .setContentTitle(text)
                //.setContentText(text)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager)
                (context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(PERIODIC_REMINDER_PENDING_INTENT_ID, notificationBuilder.build());
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "createNotification() complete");
    }

    private static String text(Context context) {
        long lastOpened = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong("last_opened", System.currentTimeMillis());
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "last opened - " + String.valueOf(lastOpened));
        String time = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(context.getString(R.string.periodic), "22:30");
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "reminder time - " + time);

        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, hour + " " + minutes);
        long cur = System.currentTimeMillis();
        long diff = cur - lastOpened;

        if (diff / DAY < 1) return "Time to practice!";
        if (diff / DAY < 2) return "You haven't practiced for a day!";
        return "You haven't practiced for a week!";
    }

    static void createMySpaceNotification(Context context, String fname) {
        String text = mySpaceText(context, fname);
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, text);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                //.setLargeIcon(largeIcon(context))
                .setSmallIcon(R.mipmap.launcher_ic)
                .setContentTitle(context.getString(app_name))
                //.setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager)
                (context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(MY_SPACE_REMINDER_PENDING_INTENT_ID, notificationBuilder.build());
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "createNotification() complete");
    }

    private static String mySpaceText(Context context, String fname) {
        long lastOpened = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(fname, System.currentTimeMillis());
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "fname was created at " + String.valueOf(lastOpened));
        String time = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(context.getString(R.string.periodic), "22:30");
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "reminder time - " + time);

        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, hour + " " + minutes);
        long cur = System.currentTimeMillis();
        long diff = cur - lastOpened;


        if (diff / DAY < 2) return "It's time to revise " + fname;
        if (diff / DAY < 5) return "It's been a few days since you learned " + fname;
        if (diff / WEEK < 2) return "You learned " + fname + " a week ago. Consider revising.";
        if (diff / MONTH < 1) return "You learned " + fname + " a month ago. Consider revising.";
        if (diff / MONTH < 6)
            return "It's been about 3 months since you created " + fname + ". You should revise now!";
        if (diff / MONTH < 12)
            return "It's been about 6 months since you created " + fname + ". You should revise now!";
        return "A year ago you created " + fname + "! Revise it now and you'll never forget it!";
    }
}
