package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MyLearningWishlist : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var listView: ListView
    private lateinit var listAdapter: ListAdapter
    private val dataArrayList = ArrayList<ListData?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_learning_wishlist)

        // Same prefs name used by ListAdapter hearts
        prefs = getSharedPreferences("LikedSkills", MODE_PRIVATE)

        listView = findViewById(R.id.listLikedSkills)

        loadLikedSkills()

        listAdapter = ListAdapter(this, dataArrayList)
        listView.adapter = listAdapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh when coming back
        loadLikedSkills()
        listAdapter.notifyDataSetChanged()
    }

    private fun loadLikedSkills() {
        dataArrayList.clear()

        // Master list
        val nameList = arrayOf(
            "Python", "Java", "C", "HTML", "CSS",
            "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva"
        )
        val imageList = intArrayOf(
            R.drawable.python,
            R.drawable.java,
            R.drawable.c,
            R.drawable.html,
            R.drawable.css,
            R.drawable.javascript,
            R.drawable.word,
            R.drawable.excel,
            R.drawable.powerpoint,
            R.drawable.canva
        )

        // Add only liked ones
        for (i in nameList.indices) {
            if (prefs.getBoolean(nameList[i], false)) {
                dataArrayList.add(ListData(nameList[i], imageList[i]))
            }
        }
    }
}
