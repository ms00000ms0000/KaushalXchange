package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AcquiredSkills : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var skillAdapter: SkillAdapter

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

    private val skillsList = mutableListOf<Skill>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acquired_skills)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        recyclerView = findViewById(R.id.recyclerViewAcquiredSkills)
        recyclerView.layoutManager = LinearLayoutManager(this)
        skillAdapter = SkillAdapter(skillsList)
        recyclerView.adapter = skillAdapter

        loadLocalSkills()
    }

    private fun loadLocalSkills() {
        val skillsSet = sharedPreferences.getStringSet("AcquiredSkills", emptySet()) ?: emptySet()
        skillsList.clear()

        nameList.forEachIndexed { index, name ->
            if (skillsSet.contains(name)) {
                skillsList.add(Skill(name, imageList[index]))
            }
        }
        skillAdapter.notifyDataSetChanged()
    }
}
