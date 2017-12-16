package com.memory_athlete.memoryassistant.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.main.MainActivity;

import java.io.File;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Manik on 26/08/17.
 */

abstract class NotificationUtils {
    private static final int PERIODIC_REMINDER_PENDING_INTENT_ID = 3417;
    //private static final int MY_SPACE_REMINDER_PENDING_INTENT_ID;
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
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.remind), false)) return;
        String text = text(context);
        Timber.d(text);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notif_launcher)
                .setContentTitle(text)
                //.addAction(R.drawable.ic_notif_launcher, "Don't disturb me", contentIntent(context))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        //.setLargeIcon(largeIcon(context))
        //.setContentText(text)
        //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager)
                (context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(PERIODIC_REMINDER_PENDING_INTENT_ID, notificationBuilder.build());
        Timber.v("createNotification() complete");
    }

    private static String text(Context context) {
        long lastOpened = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong("last_opened", System.currentTimeMillis());
        Timber.v("last opened - " + String.valueOf(lastOpened));
        String time = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(context.getString(R.string.periodic), "22:30");
        Timber.v("reminder time - " + time);

        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        Timber.v(hour + " " + minutes);
        long cur = System.currentTimeMillis();
        long diff = cur - lastOpened;

        if (diff / DAY < 1) return "Time to practice";
        if (diff / DAY < 2) return "You haven't practiced for a day";
        return "You haven't practiced for a week";
    }

    static void createMySpaceNotification(Context context, String fPath) {
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.remind), false)) return;
        if (!new File(fPath).exists()) {
            Toast.makeText(context, fPath + " no longer exists", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = mySpaceText(context, fPath);
        Timber.d(text);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notif_launcher)
                //.addAction(R.drawable.ic_notif_launcher, "Don't disturb me", contentIntent(context))
                .setContentTitle("My Space")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        //.setLargeIcon(largeIcon(context))
        //.setContentText(text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager)
                (context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(new Random().nextInt(), notificationBuilder.build());
        Timber.v("createNotification() complete");
    }

    private static String mySpaceText(Context context, String fPath) {
        long lastOpened = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(fPath, System.currentTimeMillis());
        Timber.v("fPath was created at " + String.valueOf(lastOpened));
        String time = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(context.getString(R.string.periodic), "22:30");
        Timber.v("reminder time - " + time);

        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        Timber.v(hour + " " + minutes);
        long cur = System.currentTimeMillis();
        long diff = cur - lastOpened;

        fPath = fPath.substring(fPath.lastIndexOf('/') + 1, fPath.length() - 4);
        Timber.v("fName = " + fPath);

        if (diff / DAY < 2) return "You should revise " + fPath + " now";
        if (diff / DAY < 5) return "It's been a few days since you opened " + fPath;
        if (diff / WEEK < 2) return "You opened " + fPath + " a week ago. Consider revising";
        if (diff / MONTH < 1) return "You learned " + fPath + " a month ago. Consider revising";
        if (diff / MONTH < 6)
            return "It's been about 3 months since you created " + fPath + ". Consider revising";
        if (diff / MONTH < 12)
            return "It's been about 6 months since you created " + fPath + ". Consider revising";
        return "A year ago you created " + fPath + ". Consider revising";
    }
}
