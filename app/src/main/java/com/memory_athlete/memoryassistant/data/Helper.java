package com.memory_athlete.memoryassistant.data;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Created by Manik on 15/07/17.
 */

public class Helper {
    public static final String APP_FOLDER = Environment.getExternalStorageDirectory().toString()
            + "/Memory Assistant/";
    private final int REQUEST_STORAGE_ACCESS = 555;

    public static int[] makeCards() {
        int[] cards = new int[52];
        cards[0] = R.drawable.s2;
        cards[1] = R.drawable.s3;
        cards[2] = R.drawable.s4;
        cards[3] = R.drawable.s5;
        cards[4] = R.drawable.s6;
        cards[5] = R.drawable.s7;
        cards[6] = R.drawable.s8;
        cards[7] = R.drawable.s9;
        cards[8] = R.drawable.s10;
        cards[9] = R.drawable.sj;
        cards[10] = R.drawable.sq;
        cards[11] = R.drawable.sk;
        cards[12] = R.drawable.sa;
        cards[15] = R.drawable.h2;
        cards[13] = R.drawable.h3;
        cards[14] = R.drawable.h4;
        cards[16] = R.drawable.h5;
        cards[17] = R.drawable.h6;
        cards[18] = R.drawable.h7;
        cards[19] = R.drawable.h8;
        cards[20] = R.drawable.h9;
        cards[21] = R.drawable.h10;
        cards[22] = R.drawable.hj;
        cards[23] = R.drawable.hq;
        cards[24] = R.drawable.hk;
        cards[25] = R.drawable.ha;
        cards[26] = R.drawable.d2;
        cards[27] = R.drawable.d3;
        cards[28] = R.drawable.d4;
        cards[29] = R.drawable.d5;
        cards[30] = R.drawable.d6;
        cards[31] = R.drawable.d7;
        cards[32] = R.drawable.d8;
        cards[33] = R.drawable.d9;
        cards[34] = R.drawable.d10;
        cards[35] = R.drawable.dj;
        cards[36] = R.drawable.dq;
        cards[37] = R.drawable.dk;
        cards[38] = R.drawable.da;
        cards[39] = R.drawable.c2;
        cards[40] = R.drawable.c3;
        cards[41] = R.drawable.c4;
        cards[42] = R.drawable.c5;
        cards[43] = R.drawable.c6;
        cards[44] = R.drawable.c7;
        cards[45] = R.drawable.c8;
        cards[46] = R.drawable.c9;
        cards[47] = R.drawable.c10;
        cards[48] = R.drawable.cj;
        cards[49] = R.drawable.cq;
        cards[50] = R.drawable.ck;
        cards[51] = R.drawable.ca;
        return cards;
    }

    public static int[] makeSuits() {
        int[] suits = new int[4];
        suits[0] = R.drawable.s;
        suits[1] = R.drawable.h;
        suits[2] = R.drawable.d;
        suits[3] = R.drawable.c;
        return suits;
    }

    /*public static int[] makePracticeFrags() {
        int[] frags = new int[7];
        frags[0] = R.string.numbers;
        frags[1] = R.string.words;
        frags[2] = R.string.names;
        frags[3] = R.string.places_capital;
        frags[4] = R.string.cards;
        frags[5] = R.string.binary;
        frags[6] = R.string.letters;
        return frags;
    }*/

    public static String[] makeCardString() {
        String[] cards = new String[52];
        cards[0] = "2 of Spades";
        cards[1] = "3 of Spades";
        cards[2] = "4 of Spades";
        cards[3] = "5 of Spades";
        cards[4] = "6 of Spades";
        cards[5] = "7 of Spades";
        cards[6] = "8 of Spades";
        cards[7] = "9 of Spades";
        cards[8] = "10 of Spades";
        cards[9] = "Jack of Spades";
        cards[10] = "Queen of Spades";
        cards[11] = "King of Spades";
        cards[12] = "Ace of Spades";
        cards[15] = "2 of Hearts";
        cards[13] = "3 of Hearts";
        cards[14] = "4 of Hearts";
        cards[16] = "5 of Hearts";
        cards[17] = "6 of Hearts";
        cards[18] = "7 of Hearts";
        cards[19] = "8 of Hearts";
        cards[20] = "9 of Hearts";
        cards[21] = "10 of Hearts";
        cards[22] = "Jack of Hearts";
        cards[23] = "Queen of Hearts";
        cards[24] = "King of Hearts";
        cards[25] = "Ace of Hearts";
        cards[26] = "2 of Diamonds";
        cards[27] = "3 of Diamonds";
        cards[28] = "4 of Diamonds";
        cards[29] = "5 of Diamonds";
        cards[30] = "6 of Diamonds";
        cards[31] = "7 of Diamonds";
        cards[32] = "8 of Diamonds";
        cards[33] = "9 of Diamonds";
        cards[34] = "10 of Diamonds";
        cards[35] = "Jack of Diamonds";
        cards[36] = "Queen of Diamonds";
        cards[37] = "King of Diamonds";
        cards[38] = "Ace of Diamonds";
        cards[39] = "2 of Clubs";
        cards[40] = "3 of Clubs";
        cards[41] = "4 of Clubs";
        cards[42] = "5 of Clubs";
        cards[43] = "6 of Clubs";
        cards[44] = "7 of Clubs";
        cards[45] = "8 of Clubs";
        cards[46] = "9 of Clubs";
        cards[47] = "10 of Clubs";
        cards[48] = "Jack of Clubs";
        cards[49] = "Queen of Clubs";
        cards[50] = "King of Clubs";
        cards[51] = "Ace of Clubs";
        return cards;
    }


    public static void fixBug(Context context) {
        Toast.makeText(context, "Sorry there was an error. It will be fixed shortly", Toast.LENGTH_SHORT).show();
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

    public static boolean makeDirectory(String path) {
        File pDir = new File(path);
        boolean isDirectoryCreated = pDir.exists();
        if (!isDirectoryCreated)
            isDirectoryCreated = pDir.mkdir();
        if (isDirectoryCreated) return true;                //Write the file
        else throw new RuntimeException("Couldn't create the directory");
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean mayAccessStorage(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
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
}
