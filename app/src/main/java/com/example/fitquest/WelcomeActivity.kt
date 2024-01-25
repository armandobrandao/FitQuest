package com.example.fitquest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fitquest.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.signUpButton.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
