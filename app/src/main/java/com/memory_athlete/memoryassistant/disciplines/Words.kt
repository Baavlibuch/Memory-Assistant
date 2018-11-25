package com.memory_athlete.memoryassistant.disciplines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.memory_athlete.memoryassistant.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

class Words : WordDisciplineFragment() {
    private val mDictionary = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        speechSpeedMultiplier = 3.5f
        (rootView.findViewById<View>(R.id.no_of_values) as EditText).hint =
                getString(R.string.enter) + " " + getString(R.string.words_small)
        return rootView
    }

    override fun backgroundArray(): ArrayList<*>? {
        try {
            var stringBuilder = StringBuilder()
            val rand = Random()
            val arrayList = ArrayList<String>()
            var n: Short

            var i = 0
            while (i < a[NO_OF_VALUES]) {
                n = rand.nextInt(mDictionary.size).toShort()
                stringBuilder.append(mDictionary[n.toInt()]).append("\n")
                if (++i % 20 == 0) {
                    arrayList.add(stringBuilder.toString())
                    stringBuilder = StringBuilder()
                }
                if (a[RUNNING] == FALSE) break
            }
            arrayList.add(stringBuilder.toString())
            return arrayList
        } catch (e: IllegalStateException) {
            throw RuntimeException("IllegalStateException from ViewPager.populate() " + "caused in Words.backgroundArray")
        }
    }

    override fun createDictionary() {
        var dict: BufferedReader? = null

        try {
            dict = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.words)))
            var word = dict.readLine()
            while (word != null) {
                mDictionary.add(word)
                word = dict.readLine()
            }
        } finally {
            dict!!.close()
        }
    }
}