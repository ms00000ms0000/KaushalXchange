package com.example.kaushalxchange

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kaushalxchange.databinding.ActivityDetailedBinding

class DetailedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name") ?: ""
        val image = intent.getIntExtra("image", R.drawable.python)
        binding.detailName.text = name
        binding.detailImage.setImageResource(image)

        binding.startAssessmentBtn.visibility = View.GONE

        binding.startModulesBtn.setOnClickListener {
            when {
                name.equals("Python", ignoreCase = true) ->
                    startActivity(Intent(this, PythonModulesActivity::class.java))
                name.equals("Java", ignoreCase = true) ->
                    startActivity(Intent(this, JavaModulesActivity::class.java))
                name.equals("C", ignoreCase = true) ->
                    startActivity(Intent(this, CModulesActivity::class.java))
                name.equals("HTML", ignoreCase = true) ->
                    startActivity(Intent(this, HtmlModulesActivity::class.java))
                name.equals("CSS", ignoreCase = true) ->
                    startActivity(Intent(this, CssModulesActivity::class.java))
                name.equals("JavaScript", ignoreCase = true) ->
                    startActivity(Intent(this, JSModulesActivity::class.java))
                else -> {
                    Toast.makeText(this, "Working on it — Modules coming soon", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
