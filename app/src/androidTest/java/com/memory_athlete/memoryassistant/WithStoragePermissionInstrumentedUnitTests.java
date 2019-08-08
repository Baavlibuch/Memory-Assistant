package com.memory_athlete.memoryassistant;

import android.content.pm.PackageManager;
import android.os.Build;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.memory_athlete.memoryassistant.main.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static com.memory_athlete.memoryassistant.Helper.APP_FOLDER;

@RunWith(AndroidJUnit4ClassRunner.class)
public class WithStoragePermissionInstrumentedUnitTests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);

    @Test
    public void checkPermissionsAndMakeDirectoryTest() throws IOException {
        if (!(checkSelfPermission(mActivityTestRule.getActivity(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
            throw new RuntimeException("Doesn't have read permission");
        if (!(checkSelfPermission(mActivityTestRule.getActivity(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
            throw new RuntimeException("Doesn't have write permission");
        File pDir = new File(APP_FOLDER + "Tests");
        boolean isDirectoryCreated = pDir.exists();
        if (!isDirectoryCreated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                Files.createDirectories(pDir.toPath());
            else isDirectoryCreated = pDir.mkdirs();
        } else {
            if (!pDir.delete()) throw new RuntimeException("Couldn't delete the test directory");
        }
        if (!isDirectoryCreated) throw new RuntimeException("Couldn't create the directory!");
    }
}
