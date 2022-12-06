package com.memory_athlete.memoryassistant;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;

import timber.log.Timber;

/**
 * Created by Manik on 15/07/17.
 */

public class Helper {
    public static final String APP_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Memory Assistant/";
    public static final int REQUEST_STORAGE_ACCESS = 555;

    public static final String TYPE = "type";
    public static final String RAW_RESOURCE_ID_KEY = "rawResourceID";

    public final static int LOWER_CASE = 0;             // a = 97
    // final static int UPPER_CASE = 1;             // A = 65       // unused
    public final static int MIXED_CASE = 2;             // 65 + r + c * 32             c == 0 or 1

    public static int[] makeCards() {
        return new int[]{
                R.drawable.s2,
                R.drawable.s3,
                R.drawable.s4,
                R.drawable.s5,
                R.drawable.s6,
                R.drawable.s7,
                R.drawable.s8,
                R.drawable.s9,
                R.drawable.s10,
                R.drawable.sj,
                R.drawable.sq,
                R.drawable.sk,
                R.drawable.sa,
                R.drawable.h2,
                R.drawable.h3,
                R.drawable.h4,
                R.drawable.h5,
                R.drawable.h6,
                R.drawable.h7,
                R.drawable.h8,
                R.drawable.h9,
                R.drawable.h10,
                R.drawable.hj,
                R.drawable.hq,
                R.drawable.hk,
                R.drawable.ha,
                R.drawable.d2,
                R.drawable.d3,
                R.drawable.d4,
                R.drawable.d5,
                R.drawable.d6,
                R.drawable.d7,
                R.drawable.d8,
                R.drawable.d9,
                R.drawable.d10,
                R.drawable.dj,
                R.drawable.dq,
                R.drawable.dk,
                R.drawable.da,
                R.drawable.c2,
                R.drawable.c3,
                R.drawable.c4,
                R.drawable.c5,
                R.drawable.c6,
                R.drawable.c7,
                R.drawable.c8,
                R.drawable.c9,
                R.drawable.c10,
                R.drawable.cj,
                R.drawable.cq,
                R.drawable.ck,
                R.drawable.ca
        };
    }

    public static int[] makeSuits() {
        return new int[]{R.drawable.s, R.drawable.h, R.drawable.d, R.drawable.c};
    }

    public static String[] suitDescriptions() {
        return new String[]{"Spades", "Hearts", "Diamonds", "Clubs"};
    }

    public static String[] makeCardString() {
        return new String[]{
                "2 of Spades",
                "3 of Spades",
                "4 of Spades",
                "5 of Spades",
                "6 of Spades",
                "7 of Spades",
                "8 of Spades",
                "9 of Spades",
                "10 of Spades",
                "Jack of Spades",
                "Queen of Spades",
                "King of Spades",
                "Ace of Spades",
                "2 of Hearts",
                "3 of Hearts",
                "4 of Hearts",
                "5 of Hearts",
                "6 of Hearts",
                "7 of Hearts",
                "8 of Hearts",
                "9 of Hearts",
                "10 of Hearts",
                "Jack of Hearts",
                "Queen of Hearts",
                "King of Hearts",
                "Ace of Hearts",
                "2 of Diamonds",
                "3 of Diamonds",
                "4 of Diamonds",
                "5 of Diamonds",
                "6 of Diamonds",
                "7 of Diamonds",
                "8 of Diamonds",
                "9 of Diamonds",
                "10 of Diamonds",
                "Jack of Diamonds",
                "Queen of Diamonds",
                "King of Diamonds",
                "Ace of Diamonds",
                "2 of Clubs",
                "3 of Clubs",
                "4 of Clubs",
                "5 of Clubs",
                "6 of Clubs",
                "7 of Clubs",
                "8 of Clubs",
                "9 of Clubs",
                "10 of Clubs",
                "Jack of Clubs",
                "Queen of Clubs",
                "King of Clubs",
                "Ace of Clubs"
        };
    }


    public static void fixBug(Context context) {
        Toast.makeText(context, "There was an error. It will be fixed shortly",
                Toast.LENGTH_SHORT).show();
    }

    public static void theme(Context context, Activity activity) {
        String theme = PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.theme), "AppTheme");
        String[] themes = context.getResources().getStringArray(R.array.themes);
        if (themes[1].equals(theme)) {
            context.setTheme(R.style.dark);
        } else if (themes[2].equals(theme)) {
            context.setTheme(R.style.pitch);
            (activity.getWindow().getDecorView()).setBackgroundColor(0xff000000);
        } else {
            context.setTheme(R.style.light);
        }

    }

    // custom themes - Cards, Lessons, LessonFragment, ImplementLesson, RecallCards, DisciplineActivity

    // return true if directory was created successfully. returns false if it fails (when storage permissions are not granted)
    public static boolean makeDirectory(String path, Context context) {
        File pDir = new File(path);
        boolean isDirectoryCreated = pDir.exists();
        if (!isDirectoryCreated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Files.createDirectories(pDir.toPath());
                } catch (AccessDeniedException e){
                    // No write permissions
                    Timber.i(e);
                    return false;
                } catch (FileSystemException e) {
                    // No space left
                    Timber.i(e);
                    new Handler(context.getMainLooper()).post(() ->
                            Toast.makeText(context,
                                    R.string.storage_full, Toast.LENGTH_SHORT).show()
                    );
                    return false;
                } catch (IOException e) {
                    // Check whether there is another unknown cause;
                    throw new RuntimeException(e);
                }
                return true;
            } else {
                // checked later for reliability
                //noinspection ResultOfMethodCallIgnored
                pDir.mkdirs();
            }
        }
        // Build.VERSION.SDK_INT < Build.VERSION_CODES.O
        return pDir.exists();
    }

    public static boolean externalStorageNotWritable() {
        String state = Environment.getExternalStorageState();
        return !Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean mayAccessStorage(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        requestPermissions((Activity) context, new String[]{READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ACCESS);
        return false;
    }

    public static void clickableViewAnimation(View view, Context context) {
        view.setClickable(true);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        view.setBackgroundResource(outValue.resourceId);
    }

    public static float oneDpAsPixels(int dp, Context context){
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
