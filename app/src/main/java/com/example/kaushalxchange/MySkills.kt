package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MySkillsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private val nameList = arrayOf(
        "Python", "Java", "C", "HTML", "CSS",
        "JavaScript", "MS Word", "MS Excel", "Powerpoint", "Canva"
    )
    private val imageList = intArrayOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_skills)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)
        val skillsSet = sharedPreferences.getStringSet("MySkills", emptySet()) ?: emptySet()

        val skillsList = nameList.mapIndexedNotNull { index, name ->
            if (skillsSet.contains(name)) Skill(name, imageList[index]) else null
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMySkills)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SkillAdapter(skillsList)
    }
}
