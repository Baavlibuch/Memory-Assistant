package com.memory_athlete.memoryassistant.disciplines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.memory_athlete.memoryassistant.Helper
import com.memory_athlete.memoryassistant.R
import timber.log.Timber
import java.util.*

class Letters : DisciplineFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (rootView.findViewById<View>(R.id.no_of_values) as EditText).hint = getString(R.string.enter) + " " + getString(R.string.st)
        Timber.v("Activity Created")
        return rootView
    }

    override fun backgroundString(): String {
        try {
            val stringBuilder = StringBuilder()
            val rand = Random()
            val letterCaseRand = Random()
            val letterCase = Integer.parseInt(sharedPreferences.getString(getString(R.string.letter_case), "0")!!)
            val letterA = if (letterCase == Helper.LOWER_CASE) 97 else 65
            val mixed = if (letterCase == Helper.MIXED_CASE) 1 else 0

            var s = StringBuilder()
            for (i in 0 until a[NO_OF_VALUES] / a[GROUP_SIZE]) {
                for (j in 0 until a[GROUP_SIZE]) {
                    val c = (rand.nextInt(26) + letterA +
                            letterCaseRand.nextInt(2) * 32 * mixed).toChar()
                    Timber.v("value of c = $c")
                    // add whitespace to ensure proper indentation
                    if (c != 'm' && c != 'w' && c != 'M' && c != 'W') s.append(" ")
                    if (c == 'i' || c == 'j' || c == 'l' || c == 't' || c == 'f' || c == 'I') s.append(" ")
                    // append to list
                    stringBuilder.append(c.toString())
                    // break if stopped
                    if (a[RUNNING] == FALSE) break
                }
                stringBuilder.append(s).append(getString(R.string.tab)).append("   ")
                s = StringBuilder()
            }
            Timber.v(stringBuilder.toString())
            return stringBuilder.toString()
        } catch (e: IllegalStateException) {
            throw RuntimeException("IllegalStateException from ViewPager.populate() " + "caused in BinaryDigits.backgroundString")
        }
    }
}