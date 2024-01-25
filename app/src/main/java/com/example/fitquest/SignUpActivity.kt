package com.example.fitquest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.fitquest.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val authManager = AuthManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val errorMessageTextView = binding.errorMessageTextView
        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupPassword2.text.toString()

            if (password == confirmPassword) {
                authManager.signUp(email, password) { success, errorMessage ->
                    if (success) {
                        val intent = Intent(this@SignUpActivity, SignUpUser::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        errorMessage?.let {
                            when (it) {
                                "The email address is already in use by another account." -> {
                                    errorMessageTextView.text = "Email is already in use"
                                    errorMessageTextView.visibility = View.VISIBLE
                                }

                                "Your password needs to be at least 6 characters long" -> {
                                    errorMessageTextView.text =
                                        "Your password needs to be at least 6 characters long"
                                    errorMessageTextView.visibility = View.VISIBLE
                                }



                            }
                        }
                    }
                }
            } else {
                errorMessageTextView.text = "Passwords do not match"
                errorMessageTextView.visibility = View.VISIBLE
            }
        }

    }
}
