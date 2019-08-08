package com.memory_athlete.memoryassistant;

import android.content.Context;
import android.content.Intent;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.memory_athlete.memoryassistant.main.DisciplineActivity;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CardsInstrumentedUnitTests {

    @Rule
    public ActivityTestRule<DisciplineActivity> activityTestRule =
            new ActivityTestRule<DisciplineActivity>(DisciplineActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();

                    // R.string.cards, R.drawable.cards, 5, false, false
                    Intent intent = new Intent(targetContext, DisciplineActivity.class);
                    intent.putExtra("class", 5);
                    intent.putExtra("hasSpinner", false);
                    intent.putExtra("hasAsyncTask", false);
                    intent.putExtra("nameID", R.string.cards);
                    intent.putExtra("spinnerContent", 0);
                    return intent;
                }

            };

}
