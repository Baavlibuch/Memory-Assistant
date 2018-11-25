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
    private var faces: Array<String>? = null
    internal var randomList: ArrayList<Int>? = null

    private val mFirstName = ArrayList<String>()
    private val mLastName = ArrayList<String>()

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

        Names.readNames(mFirstName, mLastName, resources)
    }

    override fun backgroundArray(): ArrayList<*>? {
        val imageIndexList = ArrayList<Int>(a[NO_OF_VALUES])
        val arrayList = ArrayList<RandomObject>(a[NO_OF_VALUES])
        val rand = Random()
        var n: Int
        var f: Int
        var l: Int

        for (i in 0 until a[NO_OF_VALUES]) imageIndexList.set(i, i)

        for (i in 0 until a[NO_OF_VALUES]) {
            f = rand.nextInt(mFirstName.size)
            l = rand.nextInt(mLastName.size)
            n = rand.nextInt(imageIndexList.size)

            arrayList.add(RandomObject(f, l, n))

            imageIndexList.removeAt(n)
            if (a[RUNNING] == FALSE) break
        }

        return arrayList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup, textSize: Int, item: Any?): View {
        return super.getView(position, convertView, parent, textSize, item)
    }
}

class RandomObject(firstName: Int, lastName: Int, face: Int)