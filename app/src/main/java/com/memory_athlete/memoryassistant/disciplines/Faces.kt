package com.memory_athlete.memoryassistant.disciplines

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.memory_athlete.memoryassistant.R
import com.memory_athlete.memoryassistant.recall.RecallComplex
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class Faces : WordDisciplineFragment() {
    private lateinit var faces: Array<String>

    private val mFirstName = ArrayList<String>()
    private val mLastName = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        (rootView.findViewById<View>(R.id.no_of_values) as EditText).hint =
                getString(R.string.enter) + " " + getString(R.string.images)

        mRecallClass = RecallComplex::class.java
        hasSpeech = false
        rootView.findViewById<View>(R.id.speech_check_box).visibility = View.GONE
        return rootView
    }

    override fun createDictionary() {
        Names.readNames(mFirstName, mLastName, resources)
    }

    override fun backgroundArray(): ArrayList<*>? {
        val arrayList = ArrayList<RandomObject>(a[NO_OF_VALUES])
        val rand = Random()
        var n: Int
        var f: Int
        var l: Int

        for (i in 0 until a[NO_OF_VALUES]) {
            f = rand.nextInt(mFirstName.size)
            l = rand.nextInt(mLastName.size)
            // n = rand.nextInt()

            arrayList.add(RandomObject(f, l, i))

            if (a[RUNNING] == FALSE) break
        }

        return arrayList
    }

    @SuppressLint("InflateParams")
    override fun getMyView(convertView: View?, textSize: Int, item: Any?, context: Context?): View? {
        var linearLayout = convertView as LinearLayout?
        val randomItem = item as RandomObject
        if (linearLayout == null) {
            linearLayout = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_category, null, true) as LinearLayout
            linearLayout.layoutParams = AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
        }

        val face = linearLayout.findViewById<ImageView>(R.id.face)
        val name = linearLayout.findViewById<TextView>(R.id.name)

        val s = mFirstName.get(randomItem.firstName) + " " + mLastName.get(randomItem.lastName)
        name.text = s

        Picasso
                //.setLoggingEnabled(true)
                .with(getContext())
                .load(activity.obbDir.path + File.separator + "faces" + File.separator + faces[randomItem.face])
                .placeholder(com.memory_athlete.memoryassistant.R.drawable.sa)
                .fit()
                //.centerInside()                 // or .centerCrop() to avoid a stretched imageÒ
                .into(face)

        return linearLayout
    }

    override fun startRandomAdapter(list: ArrayList<*>): RandomAdapter {
        return RandomAdapter(activity, list, 18)
    }
}
class RandomObject(val firstName: Int, val lastName: Int, val face: Int)