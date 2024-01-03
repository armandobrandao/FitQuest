package com.example.fitquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.fitquest.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    //private lateinit var firebaseAuth: FirebaseAuth
    private val authManager = AuthManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupPassword2.text.toString()

            if (password == confirmPassword) {
                authManager.signUp(email, password) { success, errorMessage ->
                    if (success) {
                        // Registration successful, you can handle it here
                        Log.d("SignUpActivity", "Registration successful")

                        // Redirect to the next activity, for example, WelcomeActivity
                        val intent = Intent(this@SignUpActivity, WelcomeActivity::class.java)
                        startActivity(intent)
                        finish() // Optional: finish this activity if you don't want the user to come back to it by pressing the back button
                    } else {
                        // Registration failed, handle the error or show a message to the user
                        errorMessage?.let {
                            // Display an error message
                        }
                    }
                }
            } else {
                // Passwords do not match, handle it accordingly
            }
        }

    }
}