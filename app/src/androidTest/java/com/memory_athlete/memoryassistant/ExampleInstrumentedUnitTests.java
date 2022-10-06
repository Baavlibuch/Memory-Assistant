package com.memory_athlete.memoryassistant;

import static com.memory_athlete.memoryassistant.TestHelper.waitForExecution;

import android.content.Intent;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ExampleInstrumentedUnitTests {

    @Test
    public void printIntent() {
        Intent intent = new Intent();
        intent.putExtra("extra1", "extra value");
        intent.putExtra("extra2", "extra2 value");
        waitForExecution();
    }
}
