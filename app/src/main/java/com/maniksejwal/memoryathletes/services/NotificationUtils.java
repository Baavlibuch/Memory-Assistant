package com.maniksejwal.memoryathletes.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.maniksejwal.memoryathletes.R;
import com.maniksejwal.memoryathletes.main.MainActivity;

import static com.maniksejwal.memoryathletes.R.string.app_name;
import static com.maniksejwal.memoryathletes.R.string.periodic;

/**
 * Created by Manik on 26/08/17.
 */

public abstract class NotificationUtils {
    private static final int WATER_REMINDER_PENDING_INTENT_ID = 3417;
    private static final String LOG_TAG = "\tNotificationUtils : ";

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, WATER_REMINDER_PENDING_INTENT_ID, startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        return BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
    }

    public static void createNotification(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String text = sharedPreferences.getString("mText", "Text not found");
        Log.d(LOG_TAG, text);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                //.setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(app_name))
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getText(periodic)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }
        NotificationManager notificationManager = (NotificationManager)
                (context.getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManager.notify(WATER_REMINDER_PENDING_INTENT_ID, notificationBuilder.build());
        Log.v(LOG_TAG, "createNotification() complete");
    }

}
