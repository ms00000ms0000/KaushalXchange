package com.example.kaushalxchange

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TrashActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var emptyBtn: Button
    private lateinit var prefs: SharedPreferences
    private lateinit var adapter: TrashAdapter
    private val trashedItems: MutableList<ActiveCourse> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        listView = findViewById(R.id.trash_list)
        emptyBtn = findViewById(R.id.btn_empty_trash)
        prefs = getSharedPreferences("KaushalXChangePrefs", MODE_PRIVATE)

        adapter = TrashAdapter(this, trashedItems,
            onRestoreClick = { course -> restoreCourse(course) }
        )
        listView.adapter = adapter

        emptyBtn.setOnClickListener {
            emptyTrash()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTrash()
    }

    private fun loadTrash() {
        trashedItems.clear()
        val set = prefs.getStringSet("TrashCourses", emptySet()) ?: emptySet()

        set.forEach { serialized ->
            val parts = serialized.split("||", limit = 3)
            if (parts.size >= 3) {
                val studentName = parts[0].trim()
                val wantList = parseToList(parts[1].trim())
                val canList = parseToList(parts[2].trim())
                trashedItems.add(ActiveCourse(studentName, wantList, canList))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun restoreCourse(course: ActiveCourse) {
        val editor = prefs.edit()

        // Remove from Trash
        val trashSet = prefs.getStringSet("TrashCourses", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        trashSet.remove(serializeCourse(course))
        editor.putStringSet("TrashCourses", trashSet)

        // Add back to ActiveCourses
        val activeSet = prefs.getStringSet("ActiveCourses", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        activeSet.add(serializeCourse(course))
        editor.putStringSet("ActiveCourses", activeSet)

        editor.apply()

        Toast.makeText(this, "${course.studentName}'s course restored", Toast.LENGTH_SHORT).show()
        loadTrash()
    }

    private fun emptyTrash() {
        prefs.edit().remove("TrashCourses").apply()
        trashedItems.clear()
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Trash emptied", Toast.LENGTH_SHORT).show()
    }

    private fun serializeCourse(course: ActiveCourse): String {
        return "${course.studentName}||${course.skills_want_to_learn.joinToString(",")}||${course.skills_can_teach.joinToString(",")}"
    }

    private fun parseToList(raw: String): List<String> {
        val s = raw.trim()
        if (s.isEmpty()) return emptyList()

        return if (s.startsWith("[") && s.endsWith("]")) {
            s.removePrefix("[")
                .removeSuffix("]")
                .split(",")
                .map { it.trim().trim('"', '\'').trim() }
                .filter { it.isNotEmpty() }
        } else {
            s.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        }
    }
}
