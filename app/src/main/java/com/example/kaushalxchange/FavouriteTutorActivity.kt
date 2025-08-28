package com.example.kaushalxchange

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FavouriteTutorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_tutor)

        findViewById<TextView>(R.id.textFavouriteTutor).text = "Working on it..."
    }
}
