package com.memory_athlete.memoryassistant.mySpace;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.memory_athlete.memoryassistant.BuildConfig;
import com.memory_athlete.memoryassistant.R;
import com.memory_athlete.memoryassistant.reminders.ReminderUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import timber.log.Timber;

public class WriteFileFragment extends Fragment {
    private final static String LOG_TAG = "\tWriteFile: ";
    private String mName;
    String path;

    /*OnImageClickListener mCallback;

    public interface OnImageClickListener {
        void onImageSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnImageClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnImageClickListener");
        }
    }*/

    public WriteFileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.activity_write_file, container, false);
        return rootView;
    }


   /* {

        fileName = intent.getStringExtra("fileName");
        if (intent.getBooleanExtra("name", true)) {
            ((EditText) findViewById(R.id.f_name)).setText(getTitle().toString());
            StringBuilder text = new StringBuilder();

        Bundle bundle = getArguments();
        fileName=bundle.getString("fileName");
        mName =bundle.getString("mHeader");

        ((EditText) rootView.findViewById(R.id.f_name)).setText(mName);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(
                    fileName + File.separator + mName + ".txt")));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
            ((EditText) rootView.findViewById(R.id.my_space_editText)).setText(text);
            //findViewById(R.id.saveFAB).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
            //finish();
        }

        return rootView;
    }

    public boolean save(View rootView) {
        String string = ((EditText) rootView.findViewById(R.id.my_space_editText)).getText().toString();
        String dirPath = fileName;
        String fname = fileName + File.separator + mName + ".txt";
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "fname = " + fname);
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
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                e.putLong(fname, System.currentTimeMillis());
                if (BuildConfig.DEBUG)
                    Log.v(LOG_TAG, fname + "made at " + System.currentTimeMillis());
                e.apply();
                ReminderUtils.mySpaceReminder(getActivity(), fname);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(getActivity(),
                "Couldn't find the parent directory!", Toast.LENGTH_SHORT).show();
        if (BuildConfig.DEBUG) Log.v(LOG_TAG, "fileName = " + fileName);
        return true;
    }


    void back(View rootView){
        rootView.findViewById(R.id.backFAB).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.backFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    //{
        //fileName = intent.getStringExtra("fileName");
      //  intent.getStringExtra()
    //}

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //switch (item.getItemId()) {
        //    case R.id.action_delete:
        File file = new File(fileName + File.separator + getTitle().toString() + ".txt");
        finish();
        return !file.exists() || file.delete();
    }
    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
    }

    protected void theme(Intent intent) {
        String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.theme), "AppTheme"), title = "";

        switch (theme) {
            case "Dark":
                setTheme(R.style.dark);
                break;
            case "Night":
                setTheme(R.style.pitch);
                (this.getWindow().getDecorView()).setBackgroundColor(0xff000000);
                break;
            default:
                setTheme(R.style.light);
                title = "<font color=#FFFFFF>";
        }
        setContentView(R.layout.activity_write_file);
        String header = intent.getStringExtra("mHeader");
        if (header == null) header = "New";
        //header = header.substring(0, header.length() - 4);
        setTitle(Html.fromHtml(title + header));
    }*/
/*
    public boolean save(View rootView) {
        String string = ((EditText) rootView.findViewById(R.id.my_space_editText)).getText().toString();
        String fname = ((EditText) rootView.findViewById(R.id.f_name)).getText().toString();
        if (fname.length() == 0) {
            if (!name) {
                Timber.d("using getActivity() in fragment for Toast context");
                Toast.makeText(getActivity(), "please enter a name", Toast.LENGTH_SHORT).show();
                name = true;
                return false;
            }
            Toast.makeText(getActivity(), "Didn't save nameless file", Toast.LENGTH_SHORT).show();
            return true;
        }
        String dirPath = fileName;
        fname = fileName + File.separator + fname + ".txt";
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "fname = " + fname);
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
                Timber.d("using getActivity() in fragment for Toast context");
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor e = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                e.putLong(fname, System.currentTimeMillis());
                if (BuildConfig.DEBUG) {
                    Timber.v(fname + "made at " + System.currentTimeMillis());
                }
                e.apply();
                ReminderUtils.mySpaceReminder(getActivity(), fname);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
            }
        } else Toast.makeText(getActivity(),
                "Couldn't find the parent directory!", Toast.LENGTH_SHORT).show();
        Timber.v("fileName = " + fileName);
        return true;
    }
    }*/
}
