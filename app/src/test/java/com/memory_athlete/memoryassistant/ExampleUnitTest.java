package com.memory_athlete.memoryassistant;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void parseDecimalFromHexString(){
        String hex = "999999";
        assertEquals(10066329, Integer.parseInt(hex, 16));
    }

    @Test
    public void charSequenceToString(){
        String s = "qwerty";
        //noinspection UnnecessaryLocalVariable
        CharSequence c = s;
        //noinspection CastCanBeRemovedNarrowingVariableType
        assertEquals(s, (String) c);
    }
}