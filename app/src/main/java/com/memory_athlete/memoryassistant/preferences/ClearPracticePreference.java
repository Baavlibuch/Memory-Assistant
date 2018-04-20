package com.memory_athlete.memoryassistant.preferences;

import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.AttributeSet;

import com.memory_athlete.memoryassistant.R;

import java.io.File;

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
                new File(path + resources.getString(R.string.practice))
        };

        for (File file : arr) {
            if (file.exists()) {
                deleteRecursive(file);
            }
        }
        (new File(path + resources.getString(R.string.practice))).mkdir();
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}
