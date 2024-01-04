package com.example.fitquest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitquest.databinding.ActivityWelcomeBinding
import com.example.fitquest.databinding.UserSignUpBinding

class SignUpUser : AppCompatActivity() {

    private lateinit var binding: UserSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}