package com.example.fitquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.fitquest.databinding.ActivityWelcomeBinding
import com.example.fitquest.databinding.UserSignUpBinding

class SignUpUser : AppCompatActivity() {

    private lateinit var binding: UserSignUpBinding
    private val authManager = AuthManager(this)

    private lateinit var TextName: EditText
    private lateinit var TextUsername: EditText
    private lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val errorMessageTextView = binding.errorMessageTextView
        val spinner: Spinner = findViewById(R.id.spinnerGender)
        val genderOptions = arrayOf("Male", "Female","Other")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        TextName = findViewById(R.id.editTextName)
        TextUsername = findViewById(R.id.editTextUsername)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val name = TextName.text.toString()
            val username = TextUsername.text.toString()
            val selectedGender = spinner.selectedItem.toString()

            authManager.signUpUser(name, username) { success, errorMessage ->
                if (success) {
                    Log.d("SignUpUser", "User criado com sucesso")

                    val intent = Intent(this@SignUpUser, MainActivity::class.java)

                    // Para direcionar para um questionario diferente dependendo do genero

                   // val intent = when (selectedGender) {
                   //     "Male" -> Intent(this@SignUpUser, MaleActivity::class.java)
                   //     "Female" -> Intent(this@SignUpUser, FemaleActivity::class.java)
                   //     else -> Intent(this@SignUpUser, OtherActivity::class.java)
                   // }
                    startActivity(intent)
                    finish()
                } else {
                    errorMessage?.let {
                        errorMessageTextView.text = "Erro ao criar o user"
                        errorMessageTextView.visibility = View.VISIBLE
                    }


                }

            }
        }
    }
}