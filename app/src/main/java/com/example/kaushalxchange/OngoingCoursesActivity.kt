package com.example.kaushalxchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class OngoingCoursesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoing_courses)

        findViewById<TextView>(R.id.textOngoingCourses).text = "Currently no ongoing courses"
    }
}
