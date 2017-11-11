package com.memory_athlete.memoryassistant.mySpace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.data.MakeList;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import timber.log.Timber;

public class WriteFile extends AppCompatActivity {
    private boolean name = false;
    String path, oldName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        MakeList.theme(this, WriteFile.this);
        setContentView(R.layout.activity_write_file);
        String header = intent.getStringExtra("mHeader");
        if (header == null) header = "New";
        else oldName = header;
        //header = header.substring(0, header.length() - 4);
        setTitle(header);

        path = intent.getStringExtra("fileName");
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
                //findViewById(R.id.saveFAB).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        //intent.getStringExtra()
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                File file = new File(path + File.separator + getTitle().toString() + ".txt");
                finish();
                return !file.exists() || file.delete();
            case android.R.id.home:
                save();
                NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (save()) super.onBackPressed();
    }

    public boolean save() {
        String string = ((EditText) findViewById(R.id.my_space_editText)).getText().toString();
        String fname = ((EditText) findViewById(R.id.f_name)).getText().toString();
        if (fname.length() == 0) {
            if (!name) {
                ((EditText) findViewById(R.id.f_name)).setError("please enter a name");
                findViewById(R.id.f_name).requestFocus();
                //Toast.makeText(this, "please enter a name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            }
            Toast.makeText(this, "Didn't save nameless file", Toast.LENGTH_SHORT).show();
            return true;
        }
        String dirPath = path;
        if (fname.length() > 250) {
            if (!name) {
                Toast.makeText(this, "Try again with a shorter name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            } else return true;
        }

        if (oldName != null && !fname.equals(oldName)){
            File from = new File(path + File.separator + oldName + ".txt");
            if (from.exists()) {
                File to = new File(path + File.separator + fname + ".txt");
                from.renameTo(to);
            }
        }

        fname = path + File.separator + fname + ".txt";
        Timber.v("fname = " + fname);
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

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putLong(fname, System.currentTimeMillis());
                Timber.v(fname + "made at " + System.currentTimeMillis());
                editor.apply();
                ReminderUtils.mySpaceReminder(this, fname);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(getApplicationContext(),
                R.string.try_again, Toast.LENGTH_SHORT).show();
        Timber.v("fileName = " + path);
        return true;
    }
}
