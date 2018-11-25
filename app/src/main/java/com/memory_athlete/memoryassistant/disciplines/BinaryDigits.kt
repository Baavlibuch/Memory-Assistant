package com.memory_athlete.memoryassistant.disciplines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.memory_athlete.memoryassistant.R
import com.memory_athlete.memoryassistant.R.string.tab
import java.util.*

class BinaryDigits : DisciplineFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (rootView.findViewById<View>(R.id.no_of_values) as EditText).hint =
                getString(R.string.enter) + " " + getString(R.string.st)
        return rootView
    }

    override fun backgroundString(): String {
        try {
            //String textString = "";
            val stringBuilder = StringBuilder()
            val rand = Random()
            var n: Int

            for (i in 0 until a[NO_OF_VALUES] / a[GROUP_SIZE]) {
                for (j in 0 until a[GROUP_SIZE]) {
                    n = rand.nextInt(2)
                    stringBuilder.append(n)
                }
                stringBuilder.append(getString(tab)).append("   ") //tab is the delimiter used in recall
                if (a[RUNNING] == FALSE) break
            }
            return stringBuilder.toString()
        } catch (e: IllegalStateException) {
            throw RuntimeException("IllegalStateException from ViewPager.populate() "
                    + "caused in BinaryDigits.backgroundString")
        }
    }
}
