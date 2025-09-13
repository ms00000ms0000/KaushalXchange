package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SkillICanTeachActivity : AppCompatActivity() {

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

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_ican_teach)

        sharedPreferences = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        //  Clear preferences if user is new (UID changed)
        val uid = auth.currentUser?.uid
        if (uid != null && sharedPreferences.getString("lastUid", "") != uid) {
            sharedPreferences.edit().clear().putString("lastUid", uid).apply()
        }

        recyclerView = findViewById(R.id.recyclerViewSkillICanTeach)
        recyclerView.layoutManager = LinearLayoutManager(this)
        skillAdapter = SkillAdapter(skillsList)
        recyclerView.adapter = skillAdapter

        loadLocalSkills()
        loadFirebaseSkills()
        syncTeachSkillsToFirestore()
    }

    private fun loadLocalSkills() {
        val skillsSet = sharedPreferences.getStringSet("SkillsICanTeach", emptySet()) ?: emptySet()
        skillsList.clear()

        // For new users, list is empty
        nameList.forEachIndexed { index, name ->
            if (skillsSet.contains(name)) {
                skillsList.add(Skill(name, imageList[index]))
            }
        }
        skillAdapter.notifyDataSetChanged()
    }

    private fun loadFirebaseSkills() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val firebaseSkills = document.get("skills_can_teach") as? List<String> ?: emptyList()
                skillsList.clear()

                // Only show unlocked skills
                nameList.forEachIndexed { index, name ->
                    if (firebaseSkills.contains(name)) {
                        skillsList.add(Skill(name, imageList[index]))
                    }
                }
                skillAdapter.notifyDataSetChanged()
            }
    }

    private fun syncTeachSkillsToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        val displayName = auth.currentUser?.displayName ?: "Unknown"

        val skillsSet = sharedPreferences.getStringSet("SkillsICanTeach", emptySet()) ?: emptySet()

        val data = mapOf(
            "uid" to uid,
            "displayName" to displayName,
            "skills_can_teach" to skillsSet.toList()
        )

        //  Overwrite completely to avoid old data
        firestore.collection("users").document(uid).set(data)
    }
}
