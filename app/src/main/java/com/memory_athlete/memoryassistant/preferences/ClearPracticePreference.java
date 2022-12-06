package com.memory_athlete.memoryassistant.preferences;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.memory_athlete.memoryassistant.Helper;
import com.memory_athlete.memoryassistant.R;

import java.io.File;
import java.util.Objects;

/**
 * Created by Manik on 10/09/17.
 */

public class ClearPracticePreference extends Preference {

    public ClearPracticePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onClick() {
        super.onClick();
        String path = getContext().getFilesDir() + File.separator;
        Resources resources = getContext().getResources();
        // purge everything from filesDir
        File[] arr = {
                new File(path + resources.getString(R.string.digits)),
                new File(path + resources.getString(R.string.equations)),
                new File(path + resources.getString(R.string.numbers)),
                new File(path + resources.getString(R.string.words)),
                new File(path + resources.getString(R.string.names)),
                new File(path + resources.getString(R.string.cards)),
                new File(path + resources.getString(R.string.binary)),
                new File(path + resources.getString(R.string.places_capital)),
                new File(path + resources.getString(R.string.dates)),
                new File(path + resources.getString(R.string.i)),
                new File(path + resources.getString(R.string.j)),
                new File(path + resources.getString(R.string.letters)),
                new File(path + resources.getString(R.string.practice)),
                new File(Helper.APP_FOLDER + File.separator + resources.getString(R.string.practice))
        };
        for (File file : arr)
            if (file.exists()) deleteRecursive(file);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (!(checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                !(checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
            return;
        if (fileOrDirectory.isDirectory())
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles()))
                deleteRecursive(child);

        //noinspection ResultOfMethodCallIgnored
        fileOrDirectory.delete();
    }

}
