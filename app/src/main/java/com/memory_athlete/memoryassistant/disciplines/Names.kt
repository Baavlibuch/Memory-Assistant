package com.memory_athlete.memoryassistant.disciplines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.memory_athlete.memoryassistant.R
import com.memory_athlete.memoryassistant.R.raw.first
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class Names : WordDisciplineFragment() {
    private val mFirstName = ArrayList<String>()
    private val mLastName = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        speechSpeedMultiplier = 1.5f
        (rootView.findViewById<View>(R.id.no_of_values) as EditText).hint =
                getString(R.string.enter) + " " + getString(R.string.nm)
        return rootView
    }

    //Read files and make a list of names
    override fun createDictionary() {
        var dict: BufferedReader? = null                 //Reads a line from the file

        try {
            dict = BufferedReader(InputStreamReader(resources.openRawResource(first)))
            var first = dict.readLine()
            while (first != null) {
                mFirstName.add(first.substring(0, 1) + first.substring(1).toLowerCase())//All were in caps
                first = dict.readLine()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            dict!!.close()
        } catch (e: IOException) {
            Timber.e("File not closed")
        }

        dict = null

        try {
            dict = BufferedReader(InputStreamReader(resources.openRawResource(R.raw.last)))
            var last = dict.readLine()
            while (last != null) {
                // if(last.length()>1)
                mLastName.add(last.substring(0, 1) + last.substring(1).toLowerCase())
                last = dict.readLine()
            }

        } catch (e: IOException) {
            e.printStackTrace()
            // } catch (StringIndexOutOfBoundsException e){
            //   Log.e(LOG_TAG, "error" + mLastName.size());
        }

        try {
            dict!!.close()
        } catch (e: IOException) {
            Timber.e("File not closed")
        }

    }

    override fun backgroundArray(): ArrayList<*>? {
        try {
            //String textString = "";
            var stringBuilder = StringBuilder()
            val rand = Random()
            val arrayList = ArrayList<String>()
            var n: Int

            var i = 0
            while (i < a[NO_OF_VALUES]) {
                n = rand.nextInt(mFirstName.size)
                stringBuilder.append(mFirstName[n]).append(" ")
                n = rand.nextInt(mLastName.size)
                stringBuilder.append(mLastName[n]).append("\n")
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
                    + "caused in Names.backgroundArray")
        }
    }
}