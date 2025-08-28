package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class LikedSkillsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_skills)

        prefs = getSharedPreferences("LikedSkills", MODE_PRIVATE)
        val likedSkillsList = findViewById<ListView>(R.id.listLikedSkills)

        val skills = prefs.all.keys.toList()
        likedSkillsList.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, skills)
    }

    override fun onResume() {
        super.onResume()
        val likedSkillsList = findViewById<ListView>(R.id.listLikedSkills)
        val skills = prefs.all.keys.toList()
        likedSkillsList.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, skills)
    }
}
