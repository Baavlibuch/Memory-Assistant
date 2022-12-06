package com.memory_athlete.memoryassistant;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.ContextCompat.checkSelfPermission;
import static com.memory_athlete.memoryassistant.Helper.APP_FOLDER;
import static com.memory_athlete.memoryassistant.TestHelper.waitForExecution;

import android.content.pm.PackageManager;
import android.os.Build;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.memory_athlete.memoryassistant.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;

@RunWith(AndroidJUnit4ClassRunner.class)
public class WithoutStoragePermissionInstrumentedUnitTests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    // If it fails, try running it again
    @Test
    public void checkPermissionsAndMakeDirectoryTest() throws IOException {
        waitForExecution();

        //if (checkSelfPermission(mActivityTestRule.getActivity(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        //    throw new RuntimeException("Has read permission");
        if (checkSelfPermission(mActivityTestRule.getActivity(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            throw new RuntimeException("Has write permission");
        File pDir = new File(APP_FOLDER + "Tests");
        boolean isDirectoryCreated = pDir.exists();
        try {
            if (!isDirectoryCreated) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    Files.createDirectories(pDir.toPath());
                else isDirectoryCreated = pDir.mkdirs();
            } else {
                if (pDir.delete())
                    throw new RuntimeException("Deleted the test directory!");
            }
            if (isDirectoryCreated) throw new RuntimeException("Directory created!");
        } catch (AccessDeniedException e) {
            // expected
        }

        waitForExecution();
    }
}
