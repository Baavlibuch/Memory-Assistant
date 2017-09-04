package com.memory_athlete.memoryathletes.mySpace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.memory_athlete.memoryathletes.R;
import com.memory_athlete.memoryathletes.reminders.ReminderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class WriteFile extends AppCompatActivity {
    private final static String LOG_TAG = "\tWriteFile: ";
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme"), title="";
        switch (theme){
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
                title="<font color=#FFFFFF>";
        }
        setContentView(R.layout.activity_write_file);
        Intent intent = getIntent();
        String header = intent.getStringExtra("mHeader");
        header = header.substring(0, header.length() - 4);
        setTitle(Html.fromHtml(title + header));
        path = intent.getStringExtra("path");
        if (intent.getBooleanExtra("name", true)) {
            ((EditText) findViewById(R.id.f_name)).setText(getTitle().toString());
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(
                        path + File.separator + getTitle().toString() + ".txt")));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                ((EditText) findViewById(R.id.my_space_editText)).setText(text);
                findViewById(R.id.saveFAB).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't load file", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        //intent.getStringExtra()
    }

    public void save(View view) {
        String string = ((EditText) findViewById(R.id.my_space_editText)).getText().toString();
        String fname = ((EditText) findViewById(R.id.f_name)).getText().toString();
        if (fname.length() == 0) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        String dirPath = path;
        fname = path + File.separator + fname + ".txt";
        Log.d(LOG_TAG, "fname = " + fname);
        File pDir = new File(dirPath);
        boolean isDirectoryCreated = pDir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = pDir.mkdir();
        }
        if (isDirectoryCreated) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(fname));
                outputStream.write(string.getBytes());
                outputStream.close();
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(this).edit();
                e.putLong(fname, System.currentTimeMillis());
                Log.v(LOG_TAG, fname + "made at " + System.currentTimeMillis());
                e.apply();
                ReminderUtils.mySpaceReminder(this, fname);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't save the file", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getApplicationContext(), "Couldn't find the parent directory!", Toast.LENGTH_SHORT).show();
        Log.v(LOG_TAG, "path = " + path);

    }
}
