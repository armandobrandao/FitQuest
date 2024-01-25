package com.example.fitquest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.fitquest.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val errorMessageTextView = binding.errorMessageTextView

        authManager = AuthManager(this)

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            authManager.signIn(email, password, signUpBefore = false) { success, errorMessage ->
                if (success) {
                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    errorMessage?.let {
                        errorMessageTextView.text = "Incorrect email or password. Please try again."
                        errorMessageTextView.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.signupRedirectText.setOnClickListener {
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
