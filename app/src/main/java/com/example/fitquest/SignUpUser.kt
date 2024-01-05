package com.example.fitquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitquest.databinding.UserSignUpBinding

class SignUpUser : AppCompatActivity() {

    private lateinit var binding: UserSignUpBinding
    private val authManager = AuthManager(this)

    private lateinit var TextName: EditText
    private lateinit var TextUsername: EditText
    private lateinit var spinnerActivityLevel: Spinner
    private lateinit var textViewDescription: TextView
    private lateinit var numberPickerSessionsOutside: NumberPicker
    private lateinit var buttonSubmit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TextName = findViewById(R.id.editTextName)
        TextUsername = findViewById(R.id.editTextUsername)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel)
        textViewDescription = findViewById(R.id.textViewDescription)

        // Initialize NumberPicker
        numberPickerSessionsOutside = findViewById(R.id.numberPickerSessionsOutside)
        numberPickerSessionsOutside.minValue = 0
        numberPickerSessionsOutside.maxValue = 7
        numberPickerSessionsOutside.wrapSelectorWheel = true // This allows wrapping from 0 to 7


        // Setting up the Spinner
        val activityLevels = arrayOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, activityLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivityLevel.adapter = adapter

        // Spinner item selection listener
        spinnerActivityLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Display the description based on the selected activity level
                val description = when (position) {
                    0 -> "Sedentary: Little to no exercise."
                    1 -> "Lightly Active: Light exercise or sports 1-3 days a week."
                    2 -> "Moderately Active: Moderate exercise or sports 3-5 days a week."
                    3 -> "Very Active: Hard exercise or sports 6-7 days a week."
                    else -> ""
                }

                // Update TextView to display the description
                textViewDescription.text = description
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        buttonSubmit.setOnClickListener {
            val name = TextName.text.toString()
            val username = TextUsername.text.toString()
            val sessionsOutside = numberPickerSessionsOutside.value
            val gender = when {
                findViewById<RadioButton>(R.id.radioButtonMale).isChecked -> "Male"
                findViewById<RadioButton>(R.id.radioButtonFemale).isChecked -> "Female"
                findViewById<RadioButton>(R.id.radioButtonOther).isChecked -> "Other"
                else -> ""
            }
            val age = findViewById<EditText>(R.id.editTextAge).text.toString().toIntOrNull()
            val weight = findViewById<EditText>(R.id.editTextWeight).text.toString().toDoubleOrNull()
            val height = findViewById<EditText>(R.id.editTextHeight).text.toString().toDoubleOrNull()
            val activityLevel = spinnerActivityLevel.selectedItem.toString()

            if (name.isNotBlank() && username.isNotBlank() && gender.isNotBlank() && age != null && weight != null && height != null) {
                // All required fields are filled

                // Call the signUpUser function with the additional values
                authManager.signUpUser(name, username, gender, age, weight, height, activityLevel, sessionsOutside) { success, errorMessage ->
                    if (success) {
                        Log.d("SignUpUser", "User criado com sucesso")

                        val intent = Intent(this@SignUpUser, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        errorMessage?.let {
                            // Your existing error handling logic
                            textViewDescription.text = "Erro ao criar o usuário"
                            textViewDescription.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                // Show an error message if any of the required fields are empty
                textViewDescription.text = "Please fill in all required fields"
                textViewDescription.visibility = View.VISIBLE
            }
        }

    }
}
