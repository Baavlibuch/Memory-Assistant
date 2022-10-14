package com.memory_athlete.memoryassistant.main;

import android.util.Log;

import androidx.annotation.NonNull;

//import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

public class CrashlyticsLogTree extends Timber.Tree {
    private static final String CRASHLYTICS_KEY_PRIORITY = "priority";
    private static final String CRASHLYTICS_KEY_TAG = "tag";
    private static final String CRASHLYTICS_KEY_MESSAGE = "message";

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable throwable) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        } else if (priority == Log.INFO) {
            //Crashlytics.log(priority, tag, message);
            return;
        }

        if (throwable != null) {
            //Crashlytics.logException(throwable);
        } else {
            //Crashlytics.logException(new Exception(message));
        }
    }
}
