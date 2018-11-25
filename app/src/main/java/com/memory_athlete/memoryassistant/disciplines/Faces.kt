package com.memory_athlete.memoryassistant.disciplines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.memory_athlete.memoryassistant.R
import com.memory_athlete.memoryassistant.recall.RecallComplex
import java.io.File
import java.util.*

class Faces : ComplexDisciplineFragment() {
    internal var faces: Array<String>? = null
    internal var randomList: ArrayList<Int>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (rootView.findViewById<View>(R.id.no_of_values) as EditText).hint = getString(R.string.enter) + " " + getString(R.string.images)

        mRecallClass = RecallComplex::class.java
        hasSpeech = false
        rootView.findViewById<View>(R.id.speech_check_box).visibility = View.GONE
        return rootView
    }

    override fun createDictionary() {
        val obbDir = activity.obbDir
        val f = File(obbDir.path + File.separator + "faces")
        faces = f.list()
    }

    override fun backgroundArray(): ArrayList<*>? {
        val indexList = ArrayList<Int>(a[NO_OF_VALUES])
        val arrayList = ArrayList<Int>(a[NO_OF_VALUES])
        val rand = Random()
        var n: Int

        for (i in 0 until a[NO_OF_VALUES]) indexList.add(i)

        for (i in 0 until a[NO_OF_VALUES]) {
            n = rand.nextInt(indexList.size)
            arrayList.add(indexList[n])
            indexList.removeAt(n)
            if (a[RUNNING] == FALSE) break
        }

        return arrayList
    }
}
