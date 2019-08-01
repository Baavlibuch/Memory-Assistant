package com.memory_athlete.memoryassistant;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

/**
 * Created by Manik on 15/07/17.
 */

public class Helper {
    public static final String APP_FOLDER = Environment.getExternalStorageDirectory().toString()
            + "/Memory Assistant/";
    private static final int REQUEST_STORAGE_ACCESS = 555;

    //App constants
    public static final String TYPE = "type";

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

//    public static int[] makePracticeFrags() {
//        int[] frags = new int[7];
//        frags[0] = R.string.numbers;
//        frags[1] = R.string.words;
//        frags[2] = R.string.names;
//        frags[3] = R.string.places_capital;
//        frags[4] = R.string.cards;
//        frags[5] = R.string.binary;
//        frags[6] = R.string.letters;
//        return frags;
//    }

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
        switch (theme) {
            case "Dark":
                context.setTheme(R.style.dark);
                break;
            case "Night":
                context.setTheme(R.style.pitch);
                (activity.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                //(activity.col)
                break;
            default:
                context.setTheme(R.style.light);
        }
    }

    public static boolean makeDirectory(String path) {                  // return true if directory was created successfully. throws exception otherwise
        File pDir = new File(path);
        boolean isDirectoryCreated = pDir.exists();
        if (!isDirectoryCreated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Files.createDirectories(pDir.toPath());
                } catch (IOException e) {
                    try {                                               // second try
                        Files.createDirectories(pDir.toPath());
                    } catch (IOException e1) {
                        throw new RuntimeException(e1);                 // throw exception if fails twice
                    }
                }
                return true;
            } else isDirectoryCreated = pDir.mkdirs();
        }
        if (isDirectoryCreated) return true;
        isDirectoryCreated = pDir.mkdirs();                             // second try
        if (isDirectoryCreated) return true;
        throw new RuntimeException("Couldn't create the directory. Path = " + path);// throw exception if fails twice
    }

    public static boolean externalStorageNotWritable() {
        String state = Environment.getExternalStorageState();
        return !Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean mayAccessStorage(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;

        if (checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        //if (shouldShowRequestPermissionRationale((Activity) context, READ_EXTERNAL_STORAGE)) {
        requestPermissions((Activity) context, new String[]{READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ACCESS);
        //} else {
        //    requestPermissions((Activity) context, new String[]{READ_EXTERNAL_STORAGE,
        //            WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_ACCESS);
        //}
        return false;
    }

    public static void clickableViewAnimation(View view, Context context, ClickableType clickableType) {
        view.setClickable(true);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        view.setBackgroundResource(outValue.resourceId);
    }

    public enum ClickableType {SHORT, LONG}
}
