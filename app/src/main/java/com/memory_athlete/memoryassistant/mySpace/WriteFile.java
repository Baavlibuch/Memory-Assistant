package com.memory_athlete.memoryassistant.mySpace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.language.LocaleHelper;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import timber.log.Timber;

public class WriteFile extends AppCompatActivity {
    private boolean name = false;
    String path;
    String oldName = null;
    boolean deleted = false;
    EditText searchEditText;
    EditText mySpaceEditText;
    int searchIndex;                                    // index in string to start searching from

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Helper.theme(this, WriteFile.this);
        setContentView(R.layout.activity_write_file);
        String header = intent.getStringExtra("mHeader");
        if (header == null) header = "New";
        else oldName = header;
        //header = header.substring(0, header.length() - 4);
        setTitle(header);

        mySpaceEditText = findViewById(R.id.my_space_editText);
        searchEditText = findViewById(R.id.search_edit_text);

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
                mySpaceEditText.setText(text);
                //findViewById(R.id.saveFAB).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //intent.getStringExtra()

        // reset search index to zero whenever text changes
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    searchIndex = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(searchEditText);
                return true;
            }
            return false;
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Timber.d(getTitle().toString());
                File file = new File(path + File.separator + getTitle().toString() + ".txt");
                deleted = true;
                finish();
                return !file.exists() || file.delete();
            case R.id.dont_save:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case android.R.id.home:
                if (save()) NavUtils.navigateUpFromSameTask(this);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // hide searchEditText if it is visible
        if (searchEditText.getVisibility() == View.VISIBLE) {
            searchEditText.setVisibility(View.GONE);
            return;
        }
        // save() returns false if save was rejected to notify the user at most once.
        if (save()) super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!deleted) save();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean save() {
        String string = mySpaceEditText.getText().toString();
        String fname = ((EditText) findViewById(R.id.f_name)).getText().toString();
        if (fname.length() == 0 && string.length() == 0) {
            if (!name) {
                ((EditText) findViewById(R.id.f_name)).setError("please enter a name");
                findViewById(R.id.f_name).requestFocus();
                //Toast.makeText(this, "please enter a name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            }
            Toast.makeText(this, R.string.nameless_file, Toast.LENGTH_SHORT).show();
            return true;
        }
        String dirPath = path;
        if (fname.length() > 250) {
            if (name) return true;

            Toast.makeText(this, "Try again with a shorter name", Toast.LENGTH_SHORT).show();
            name = true;
            return false;
        }

        if (oldName != null && !fname.equals(oldName)) {
            File from = new File(path + File.separator + oldName + ".txt");
            if (from.exists()) {
                File to = new File(path + File.separator + fname + ".txt");
                from.renameTo(to);
            }
        }

        fname = path + File.separator + fname + ".txt";
        Timber.v("fname = %s", fname);
        if (!Helper.mayAccessStorage(this)) {
            if (name) {
                Timber.i(getString(R.string.storage_permissions));
                Toast.makeText(this, R.string.storage_permissions,
                        Toast.LENGTH_SHORT).show();
                return true;
            }

            name = true;
            return false;
        }
        if (Helper.externalStorageNotWritable()) {
            Timber.i("externalStorageNotWritable");
            Toast.makeText(this, R.string.check_storage, Toast.LENGTH_SHORT).show();
            if (name) return true;

            name = true;
            return false;
        }
        if (Helper.makeDirectory(dirPath, getApplicationContext())) {
            try {
                FileOutputStream outputStream = new FileOutputStream(new File(fname));
                outputStream.write(string.getBytes());
                outputStream.close();

                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(this).edit();
                editor.putLong(fname, System.currentTimeMillis());
                Timber.v(fname + "made at " + System.currentTimeMillis());
                editor.apply();

                //Data input = new Data.Builder().putString("fpath",fname).build();
                ReminderUtils.mySpaceReminder(this, fname);

            } catch (Exception e) {
                Timber.e(e);
                Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }
        }
        Timber.v("fileName = %s", path);
        return true;
    }

    // returns true if found
    private boolean search(String stringToSearch) {
        if (stringToSearch.equals("")) return false;                            // don't search

        stringToSearch = stringToSearch.toLowerCase();
        String fullText = mySpaceEditText.getText().toString().toLowerCase();
        boolean hasText = fullText.contains(stringToSearch);
        Timber.d("hasText = %s", hasText);
        if (hasText) {
            searchIndex = fullText.indexOf(stringToSearch, searchIndex);        // index in string
            // -1 : not found. Happens after the last
            if (searchIndex == -1) {
                searchIndex = 0;
                Toast.makeText(this, R.string.search_from_start, Toast.LENGTH_SHORT).show();
                return false;
            }

            // scroll to location
            int lineNumber = mySpaceEditText.getLayout().getLineForOffset(searchIndex);
            int totalLines = mySpaceEditText.getLayout().getLineCount();
            int editTextViewBottom = findViewById(R.id.my_space_editText).getBottom();
            ((ScrollView) findViewById(R.id.my_space_scroll_view))
                    .smoothScrollTo(0, editTextViewBottom * (lineNumber-1) / totalLines);
            // highlight
            mySpaceEditText.setSelection(searchIndex, searchIndex + stringToSearch.length());
            mySpaceEditText.requestFocus();

            searchIndex++;
            return true;
        }
        Toast.makeText(getApplicationContext(), R.string.not_found, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void search(View view) {
        String stringToSearch = searchEditText.getText().toString();
        searchIndex++;
        searchEditText.setVisibility(View.VISIBLE);
        if (!search(stringToSearch))
            searchEditText.requestFocus();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
