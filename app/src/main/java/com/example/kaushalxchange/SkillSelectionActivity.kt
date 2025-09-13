package com.example.kaushalxchange

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL

class SkillSelectionActivity : AppCompatActivity() {

    private lateinit var skillsListView: ListView
    private lateinit var startLearningBtn: Button
    private lateinit var startTestBtn: Button
    private var selectedSkill: String? = null

    private var skillsFromIntent: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_selection)

        skillsListView = findViewById(R.id.skills_list)
        startLearningBtn = findViewById(R.id.btn_start_learning)
        startTestBtn = findViewById(R.id.btn_start_test)

        // Load actual skills passed from ActiveCourses
        skillsFromIntent = intent.getStringArrayListExtra("skills_can_teach")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_single_choice,
            skillsFromIntent ?: listOf("No skills available")
        )

        skillsListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        skillsListView.adapter = adapter

        skillsListView.setOnItemClickListener { _, _, position, _ ->
            selectedSkill = skillsFromIntent?.get(position)
            Toast.makeText(this, "Selected: $selectedSkill", Toast.LENGTH_SHORT).show()
        }

        startLearningBtn.setOnClickListener {
            if (selectedSkill != null) {
                startVideoCall(selectedSkill!!)
            } else {
                Toast.makeText(this, "Please select a skill", Toast.LENGTH_SHORT).show()
            }
        }

        startTestBtn.setOnClickListener {
            if (selectedSkill != null) {
                openTestActivity(selectedSkill!!)
            } else {
                Toast.makeText(this, "Please select a skill", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startVideoCall(skill: String) {
        try {
            val serverURL = URL("https://meet.jit.si") // Free Jitsi server
            val options = JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                .setRoom("KaushalXChange_${skill}_${System.currentTimeMillis()}")
                .setFeatureFlag("welcomepage.enabled", false)
                .build()

            JitsiMeetActivity.launch(this, options)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error starting video call", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openTestActivity(skill: String) {
        val intent = when (skill) {
            "Python" -> Intent(this, TestPython::class.java)
            "Java" -> Intent(this, TestJava::class.java)
            "C" -> Intent(this, TestC::class.java)
            "CSS" -> Intent(this, TestCss::class.java)
            "HTML" -> Intent(this, TestHtml::class.java)
            "JavaScript" -> Intent(this, TestJS::class.java)
            else -> {
                Toast.makeText(this, "No test available for $skill", Toast.LENGTH_SHORT).show()
                return
            }
        }
        startActivity(intent)
    }
}
