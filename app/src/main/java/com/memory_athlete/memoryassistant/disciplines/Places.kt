package com.memory_athlete.memoryassistant.disciplines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.memory_athlete.memoryassistant.R
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class Places : WordDisciplineFragment() {
    private val mPlace = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        speechSpeedMultiplier = 1.5f
        (rootView.findViewById<View>(R.id.no_of_values) as EditText).hint =
                getString(R.string.enter) + " " + getString(R.string.places_small)
        return rootView
    }

    override fun reset(): Boolean {
        rootView.findViewById<View>(R.id.group).visibility = View.GONE
        return super.reset()
    }

    override fun backgroundArray(): ArrayList<*>? {
        try{
        var stringBuilder = StringBuilder()
        val rand = Random()
        val arrayList = ArrayList<String>()
        var n: Int

        var i = 0
        while (i < a[NO_OF_VALUES]) {
            n = rand.nextInt(mPlace.size)
            stringBuilder.append(mPlace[n]).append(" \n")
            if (++i % 20 == 0) {
                arrayList.add(stringBuilder.toString())
                stringBuilder = StringBuilder()
            }
            if (a[RUNNING] == FALSE) break
        }
        arrayList.add(stringBuilder.toString())
        return arrayList
        } catch (e: IllegalStateException) {
            throw RuntimeException("IllegalStateException from ViewPager.populate() "
                    + "caused in Places.backgroundArray")
        }
    }

    //Read files and make a list of places
    override fun createDictionary() {
        val files = intArrayOf(R.raw.cities, R.raw.countries, R.raw.waterfalls,
                R.raw.mountains, R.raw.lakes, R.raw.islands, R.raw.heritage, R.raw.rivers)
        for (fileID in files) {
            var dict: BufferedReader? = null
            try {
                dict = BufferedReader(InputStreamReader(
                        resources.openRawResource(fileID)))
                var place = dict.readLine()
                while (place != null) {
                    mPlace.add(place)
                    place = dict.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                dict!!.close()
            } catch (e: IOException) {
                Timber.e("File not closed")
            }
        }
    }
}