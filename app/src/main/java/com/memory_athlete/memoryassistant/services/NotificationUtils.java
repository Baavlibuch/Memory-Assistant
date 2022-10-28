package com.memory_athlete.memoryassistant.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.main.MainActivity;

import java.util.Objects;
import java.util.Random;

import timber.log.Timber;

/**
 * Created by Manik on 26/08/17.
 */

public class NotificationUtils {
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

    private static void createChannel(Context context, String channelId){
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return;
        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(channelId, channelId, importance);

        mNotificationManager.createNotificationChannel(mChannel);
    }

    public static void createNotification(Context context) {
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.remind), false)) return;
        Timber.d("creating notification");
        String channelId = context.getString(R.string.reminders);
        createChannel(context, channelId);

        String text = text(context);
        Timber.d(text);
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notif_launcher)
                .setContentTitle(text)
                //.addAction(R.drawable.ic_notif_launcher, "Don't disturb me", contentIntent(context))
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true)
                .build();
        //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        //.setLargeIcon(largeIcon(context))
        //.setContentText(text)
        //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))

        NotificationManager notificationManager = (NotificationManager)
                (context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(PERIODIC_REMINDER_PENDING_INTENT_ID, notification);
        Timber.v("createNotification() complete");
    }

    private static String text(Context context) {
        long lastOpened = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong("last_opened", System.currentTimeMillis());
        Timber.v("last opened - " + String.valueOf(lastOpened));
        String time = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(context.getString(R.string.periodic), "22:30");
        Timber.v("reminder time - " + time);

        int hour = Integer.parseInt(time.substring(0, Objects.requireNonNull(time).indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        Timber.v(hour + " " + minutes);
        long cur = System.currentTimeMillis();
        long diff = cur - lastOpened;

        if (diff / DAY < 2) return "Time to practice";
        //if (diff / DAY < 2) return "You haven't practiced for a day";
        return "You haven't practiced for a week";
    }

    public static void createMySpaceNotification(Context context, String fPath) {
        if (!PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.remind), false)) return;
        String channelId = context.getString(R.string.my_space) + " " + context.getString(R.string.reminders);
        createChannel(context, channelId);

        String text = mySpaceText(context, fPath);
        Timber.d(text);
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notif_launcher)
                //.addAction(R.drawable.ic_notif_launcher, "Don't disturb me", contentIntent(context))
                .setContentTitle("My Space")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(contentIntent(context))
                //.setPriority(Notification.PRIORITY_LOW)
                .setAutoCancel(true)
                .build();
        //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        //.setLargeIcon(largeIcon(context))
        //.setContentText(text)
        NotificationManager notificationManager = (NotificationManager)
                (context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(new Random().nextInt(), notification);
        Timber.v("createNotification() complete");
    }

    private static String mySpaceText(Context context, String fPath) {
        long lastOpened = PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(fPath, System.currentTimeMillis());
        Timber.v("fPath was created at " + String.valueOf(lastOpened));
        String time = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(context.getString(R.string.periodic), "22:30");
        Timber.v("reminder time - " + time);

        int hour = Integer.parseInt(time.substring(0, Objects.requireNonNull(time).indexOf(":")));
        int minutes = Integer.parseInt(time.substring(time.indexOf(":") + 1));
        Timber.v(hour + " " + minutes);
        long cur = System.currentTimeMillis();
        //long diff = cur - lastOpened;

        fPath = fPath.substring(fPath.lastIndexOf('/') + 1, fPath.length() - 4);
        Timber.v("fName = " + fPath);

        return "Consider revising " + fPath;
    }
}
