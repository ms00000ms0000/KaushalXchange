package com.example.kaushalxchange

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ActiveCourses : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ActiveCourseAdapter
    private lateinit var prefs: SharedPreferences
    private val activeCourses: MutableList<ActiveCourse> = mutableListOf()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_courses)

        prefs = getSharedPreferences("KaushalXChangePrefs", Context.MODE_PRIVATE)

        recyclerView = findViewById(R.id.recycler_active_courses)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ActiveCourseAdapter(activeCourses,
            onQuitClick = { course -> quitCourse(course) },
            onStartClick = { course -> openSkillSelection(course) }
        )
        recyclerView.adapter = adapter

        val trashBtn: ImageButton = findViewById(R.id.btn_open_trash)
        trashBtn.setOnClickListener {
            startActivity(Intent(this, TrashActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadLocalCourses()
        listenForFirestoreActiveCourses()
    }

    override fun onPause() {
        super.onPause()
        listener?.remove()
        listener = null
    }

    private fun loadLocalCourses() {
        activeCourses.clear()
        val set = prefs.getStringSet("ActiveCourses", emptySet()) ?: emptySet()
        set.forEach { serialized ->
            val parts = serialized.split("||", limit = 3)
            if (parts.size == 3) {
                val studentName = parts[0]
                val wantList = parseToList(parts[1])
                val canList = parseToList(parts[2])
                activeCourses.add(ActiveCourse(studentName, wantList, canList))
            }
        }
        adapter.update(activeCourses.toMutableList())
    }

    private fun listenForFirestoreActiveCourses() {
        val uid = auth.currentUser?.uid ?: return
        listener = db.collection("users")
            .document(uid)
            .collection("ActiveCourses")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val set = mutableSetOf<String>()
                    snapshot.documents.forEach { doc ->
                        val name = doc.getString("studentName") ?: return@forEach
                        val wantList = doc.get("skills_want_to_learn") as? List<*> ?: emptyList<Any>()
                        val canList = doc.get("skills_can_teach") as? List<*> ?: emptyList<Any>()

                        val serialized = buildString {
                            append(name)
                            append("||")
                            append(wantList.joinToString(","))
                            append("||")
                            append(canList.joinToString(","))
                        }
                        set.add(serialized)
                    }
                    prefs.edit().putStringSet("ActiveCourses", set).apply()
                    loadLocalCourses()
                }
            }
    }

    private fun quitCourse(course: ActiveCourse) {
        val serialized = serializeCourse(course)

        // remove from local active
        val activeSet = prefs.getStringSet("ActiveCourses", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        activeSet.remove(serialized)

        // add to trash
        val trashSet = prefs.getStringSet("TrashCourses", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        trashSet.add(serialized)

        prefs.edit()
            .putStringSet("ActiveCourses", activeSet)
            .putStringSet("TrashCourses", trashSet)
            .apply()

        // remove from Firestore
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).collection("ActiveCourses")
            .document(course.studentName)
            .delete()

        loadLocalCourses()
    }

    private fun serializeCourse(course: ActiveCourse): String {
        return "${course.studentName}||${course.skills_want_to_learn.joinToString(",")}||${course.skills_can_teach.joinToString(",")}"
    }

    private fun parseToList(raw: String): List<String> {
        if (raw.isBlank()) return emptyList()
        return raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun openSkillSelection(course: ActiveCourse) {
        val intent = Intent(this, SkillSelectionActivity::class.java)
        intent.putStringArrayListExtra("skills_can_teach", ArrayList(course.skills_can_teach))
        startActivity(intent)
    }
}
