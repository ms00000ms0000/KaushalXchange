package com.example.kaushalxchange

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val feedbackText = findViewById<EditText>(R.id.editFeedback)
        val sendButton = findViewById<Button>(R.id.btnSendFeedback)

        sendButton.setOnClickListener {
            val message = feedbackText.text.toString().trim()

            if (message.isEmpty()) {
                Toast.makeText(this, "Please write your feedback", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("businesswallah01@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "App Feedback")
                putExtra(Intent.EXTRA_TEXT, message)
            }

            try {
                startActivity(Intent.createChooser(intent, "Send Feedback via"))
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
