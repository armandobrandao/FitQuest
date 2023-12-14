package com.example.fitquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class SignInActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpRedirectText: TextView

    private val authManager = AuthManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
    }
    /*
        // Initialize views
        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)
        loginButton = findViewById(R.id.login_button)
        signUpRedirectText = findViewById(R.id.signupRedirectText)

        // Set click listener for login button
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Call your authentication logic using AuthManager
            authManager.signIn(email, password) { success, errorMessage ->
                if (success) {
                    // Authentication successful, navigate to the next screen or perform necessary actions
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Optional: Close the login activity to prevent going back
                } else {
                    // Authentication failed, display error message using Snackbar
                    Snackbar.make(loginButton, errorMessage.orEmpty(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // Set click listener for sign-up redirect text
        signUpRedirectText.setOnClickListener {
            // Navigate to the registration screen or activity
            // Example: startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
     */
}