package com.example.kaushalxchange

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kaushalxchange.databinding.ActivityLogInBinding
import com.google.firebase.auth.FirebaseAuth
import android.text.InputType

class LogIn : AppCompatActivity() {
    private val binding: ActivityLogInBinding by lazy {
        ActivityLogInBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast

    override fun onStart() {
        super.onStart()
        // You can check here if user already logged in, then redirect to Home
         if (auth.currentUser != null) {
             startActivity(Intent(this, Home::class.java))
             finish()
         }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Show/Hide Password checkbox
        binding.showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.password.setSelection(binding.password.text.length) // keep cursor at end
        }

        binding.loginButton.setOnClickListener {
            val userName = binding.userName.text.toString()
            val password = binding.password.text.toString()

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fill required details", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(userName, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Welcome back", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, Home::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Sign in failed:${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Forgot password
        binding.forgotPassword.setOnClickListener {
            val emailInput = android.widget.EditText(this)
            emailInput.hint = "Enter your registered email"

            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Reset Password")
                .setMessage("A password reset link will be sent to your email.")
                .setView(emailInput)
                .setPositiveButton("Send") { _, _ ->
                    val email = emailInput.text.toString().trim()
                    if (email.isEmpty()) {
                        Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
            finish()
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
