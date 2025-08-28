package com.example.kaushalxchange

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kaushalxchange.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Signup : AppCompatActivity() {
    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signinButton.setOnClickListener {
            startActivity(Intent(this, LogIn::class.java))
            finish()
        }

        binding.registerButton.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val userName = binding.userName.text.toString().trim()
            val password = binding.password.text.toString()
            val repeatPassword = binding.repeatPassword.text.toString()

            if (email.isEmpty() || userName.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else if (password != repeatPassword) {
                Toast.makeText(this, "Repeat password must be same", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser!!.uid

                            // Save to Firestore
                            val userMap = hashMapOf(
                                "username" to userName,
                                "email" to email,
                                "profileImageUrl" to "" // empty initially
                            )
                            Firebase.firestore.collection("users").document(userId)
                                .set(userMap)

                            // Update FirebaseAuth profile with displayName
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build()
                            auth.currentUser!!.updateProfile(profileUpdates)

                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LogIn::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration Failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            super.onBackPressed()
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}
