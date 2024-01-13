package com.example.fitquest

import Questionary
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
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fitquest.databinding.EditInfoBinding
import com.example.fitquest.databinding.UserSignUpBinding

class EditInfo : AppCompatActivity() {

    private lateinit var binding: EditInfoBinding
    private val authManager = AuthManager(this)

    private lateinit var textViewFirstTitle: TextView
    private lateinit var TextName: EditText
    private lateinit var TextUsername: EditText
    private lateinit var textViewDescription: TextView
    private lateinit var spinnerGender: Spinner // Add this line
    private lateinit var buttonContinue: Button
    private lateinit var textViewnameLabel: TextView
    private lateinit var textViewUsernameLabel: TextView
    private lateinit var textViewAgeLabel: TextView
    private lateinit var editAge: EditText
    private lateinit var textViewHeightLabel: TextView
    private lateinit var editHeight: EditText
    private lateinit var textViewWeightLabel: TextView
    private lateinit var editWeight: EditText
    private lateinit var textViewGenderLabel: TextView
    private lateinit var errorMessageTextView: TextView



    private lateinit var buttonBack: Button
    private lateinit var buttonSubmitQuestionary: Button
    private lateinit var textViewQuestionaryTitle: TextView
    private lateinit var radioGroupGoal: RadioGroup
    private lateinit var radioGroupMotivation: RadioGroup
    private lateinit var radioGroupPushUps: RadioGroup
    private lateinit var radioGroupActivityLevel: RadioGroup
    private lateinit var spinnerFirstDay: Spinner
    private lateinit var radioGroupTrainingDays: RadioGroup
    private lateinit var textSessionsOutside : TextView
    private lateinit var radioGroupSessionsOutside: RadioGroup
    private lateinit var textGoalLabel: TextView
    private lateinit var textMotivationLabel: TextView
    private lateinit var textPushUpsLabel: TextView
    private lateinit var textActivityLevelLabel: TextView
    private lateinit var textWeeklyGoalLabel: TextView
    private lateinit var textTrainingDays: TextView
    private lateinit var textFirstDay: TextView
    private lateinit var errorMessageSubmitTextView : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //First
        textViewFirstTitle = findViewById(R.id.textViewTitle)
        textViewnameLabel = findViewById(R.id.textViewnameLabel)
        TextName = findViewById(R.id.editTextName)
        textViewUsernameLabel = findViewById(R.id.textViewUsernameLabel)
        TextUsername = findViewById(R.id.editTextUsername)
        textViewAgeLabel = findViewById(R.id.textViewAgeLabel)
        editAge = findViewById(R.id.editAge)
        textViewHeightLabel = findViewById(R.id.textViewHeightLabel)
        editHeight = findViewById(R.id.editHeight)
        textViewWeightLabel = findViewById(R.id.textViewWeightLabel)
        editWeight = findViewById(R.id.editWeight)
        textViewGenderLabel = findViewById(R.id.textViewGenderLabel)
        spinnerGender = findViewById(R.id.spinnerGender)
        buttonContinue = findViewById(R.id.buttonContinue)
        errorMessageTextView = findViewById(R.id.errorMessageTextView)



        // Set up gender spinner
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapterGender = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapterGender

        //Questionary
        buttonBack = findViewById(R.id.buttonBack)
        textViewQuestionaryTitle = findViewById(R.id.textViewTitle2)
        textGoalLabel = findViewById(R.id.textGoalLabel)
        radioGroupGoal = findViewById(R.id.radioGroupGoal)
        textMotivationLabel = findViewById(R.id.textMotivationLabel)
        radioGroupMotivation = findViewById(R.id.radioGroupMotivation)
        textPushUpsLabel = findViewById(R.id.textPushUpsLabel)
        radioGroupPushUps = findViewById(R.id.radioGroupPushUps)
        textActivityLevelLabel = findViewById(R.id.textActivityLevelLabel)
        radioGroupActivityLevel = findViewById(R.id.radioGroupActivityLevel)
        textWeeklyGoalLabel = findViewById(R.id.textWeeklyGoalLabel)
        textTrainingDays = findViewById(R.id.textTrainingDays)
        textFirstDay = findViewById(R.id.textFirstDay)
        spinnerFirstDay = findViewById(R.id.spinnerFirstDay)
        radioGroupTrainingDays = findViewById(R.id.radioGroupTrainingDays)
        radioGroupSessionsOutside = findViewById(R.id.radioGroupSessionsOutside)
        textSessionsOutside = findViewById(R.id.textSessionsOutside)
        buttonSubmitQuestionary = findViewById(R.id.buttonSubmit)
        errorMessageSubmitTextView = findViewById(R.id.errorMessageSubmitTextView)



        // Set up Spinner for days of the week
        val adapterDay = ArrayAdapter.createFromResource(
            this, R.array.days_of_week, android.R.layout.simple_spinner_item
        )
        adapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFirstDay.adapter = adapterDay


        // Handle Continue button click
        buttonContinue.setOnClickListener {
            // Additional checks and validations if needed

            // Hide initial elements
            buttonContinue.visibility = View.GONE
            spinnerGender.visibility = View.GONE
            textViewFirstTitle.visibility = View.GONE
            textViewnameLabel.visibility = View.GONE
            TextName.visibility = View.GONE
            textViewUsernameLabel.visibility = View.GONE
            TextUsername.visibility = View.GONE
            textViewAgeLabel.visibility = View.GONE
            editAge.visibility = View.GONE
            textViewHeightLabel.visibility = View.GONE
            editHeight.visibility =View.GONE
            textViewWeightLabel.visibility = View.GONE
            editWeight.visibility = View.GONE
            textViewGenderLabel.visibility = View.GONE
            errorMessageTextView.visibility = View.GONE

            // Show questionary elements
            buttonBack.visibility = View.VISIBLE
            textViewQuestionaryTitle.visibility = View.VISIBLE
            buttonSubmitQuestionary.visibility = View.VISIBLE
            textGoalLabel.visibility = View.VISIBLE
            radioGroupGoal.visibility = View.VISIBLE
            textMotivationLabel.visibility = View.VISIBLE
            radioGroupMotivation.visibility = View.VISIBLE
            textPushUpsLabel.visibility = View.VISIBLE
            radioGroupPushUps.visibility = View.VISIBLE
            textActivityLevelLabel.visibility = View.VISIBLE
            radioGroupActivityLevel.visibility = View.VISIBLE
            textWeeklyGoalLabel.visibility = View.VISIBLE
            textTrainingDays.visibility = View.VISIBLE
            textFirstDay.visibility = View.VISIBLE
            spinnerFirstDay.visibility = View.VISIBLE
            radioGroupTrainingDays.visibility = View.VISIBLE
            radioGroupSessionsOutside.visibility = View.VISIBLE
            textSessionsOutside.visibility = View.VISIBLE

        }

        buttonBack.setOnClickListener {
            // Hide questionary elements
            buttonBack.visibility = View.GONE
            textViewQuestionaryTitle.visibility = View.GONE
            buttonSubmitQuestionary.visibility = View.GONE
            textGoalLabel.visibility = View.GONE
            radioGroupGoal.visibility = View.GONE
            textMotivationLabel.visibility = View.GONE
            radioGroupMotivation.visibility = View.GONE
            textPushUpsLabel.visibility = View.GONE
            radioGroupPushUps.visibility = View.GONE
            textActivityLevelLabel.visibility = View.GONE
            radioGroupActivityLevel.visibility = View.GONE
            textWeeklyGoalLabel.visibility = View.GONE
            textTrainingDays.visibility = View.GONE
            textFirstDay.visibility = View.GONE
            spinnerFirstDay.visibility = View.GONE
            radioGroupTrainingDays.visibility = View.GONE
            radioGroupSessionsOutside.visibility = View.GONE
            textSessionsOutside.visibility = View.GONE

            // Show initial elements
            buttonContinue.visibility = View.VISIBLE
            spinnerGender.visibility = View.VISIBLE
            textViewFirstTitle.visibility = View.VISIBLE
            textViewnameLabel.visibility = View.VISIBLE
            TextName.visibility = View.VISIBLE
            textViewUsernameLabel.visibility = View.VISIBLE
            TextUsername.visibility = View.VISIBLE
            textViewAgeLabel.visibility = View.VISIBLE
            editAge.visibility = View.VISIBLE
            textViewHeightLabel.visibility = View.VISIBLE
            editHeight.visibility =View.VISIBLE
            textViewWeightLabel.visibility = View.VISIBLE
            editWeight.visibility = View.VISIBLE
            textViewGenderLabel.visibility = View.VISIBLE
            errorMessageTextView.visibility = View.VISIBLE
        }

        // Handle Sign Up and Questionary Submit button clicks
        buttonSubmitQuestionary.setOnClickListener {
            val name = TextName.text.toString()
            val username = TextUsername.text.toString()
            val gender = spinnerGender.selectedItem.toString()
            val age = editAge.text.toString().toIntOrNull()
            val weight = editWeight.text.toString().toDoubleOrNull()
            val height = editHeight.text.toString().toDoubleOrNull()

            if (name.isNotBlank() && username.isNotBlank() && gender.isNotBlank() && age != null && weight != null && height != null) {
                // All required fields are filled

                // Check if the questionnaire fields are filled
                val goalSelectedId = radioGroupGoal.checkedRadioButtonId
                val motivationSelectedId = radioGroupMotivation.checkedRadioButtonId
                val pushUpsSelectedId = radioGroupPushUps.checkedRadioButtonId
                val activityLevelSelectedId = radioGroupActivityLevel.checkedRadioButtonId
                val firstDaySelectedPosition = spinnerFirstDay.selectedItemPosition
                val trainingDaysValue = radioGroupTrainingDays.checkedRadioButtonId
                val sessionsOutsideValue = radioGroupSessionsOutside.checkedRadioButtonId

                if (goalSelectedId != -1 && motivationSelectedId != -1 && pushUpsSelectedId != -1
                    && activityLevelSelectedId != -1 && firstDaySelectedPosition != AdapterView.INVALID_POSITION) {

                    // All questionnaire fields are filled

                    // Retrieve the selected values from radio buttons and spinners
                    val goal = findViewById<RadioButton>(goalSelectedId).text.toString()
                    val motivation = findViewById<RadioButton>(motivationSelectedId).text.toString()
                    val pushUps = findViewById<RadioButton>(pushUpsSelectedId).text.toString()
                    val activityLevel = findViewById<RadioButton>(activityLevelSelectedId).text.toString()
                    val firstDay = resources.getStringArray(R.array.days_of_week)[firstDaySelectedPosition]
                    val trainingDays = findViewById<RadioButton>(trainingDaysValue).text.toString()
                    val sessionsOutside = findViewById<RadioButton>(sessionsOutsideValue).text.toString()


                    // Call the signUpUser function with the additional values
                    authManager.signUpUser(
                        name, username, gender, age, weight, height,
                        goal, motivation, pushUps, activityLevel, firstDay, trainingDays, sessionsOutside
                    ) { success, errorMessage ->
                        if (success) {
                            Log.d("SignUpUser", "User created successfully")

                            val intent = Intent(this@EditInfo, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            errorMessage?.let {
                                // Your existing error handling logic
                                errorMessageSubmitTextView.text = "Error creating user"
                                errorMessageSubmitTextView.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    // Show an error message if any of the questionnaire fields are empty
                    errorMessageSubmitTextView.text = "Please fill in all questionnaire fields"
                    errorMessageSubmitTextView.visibility = View.VISIBLE
                }
            } else {
                // Show an error message if any of the required fields are empty
                errorMessageSubmitTextView.text = "Please fill in all required fields"
                errorMessageSubmitTextView.visibility = View.VISIBLE
            }
        }


//        buttonSubmit.setOnClickListener {
//            val name = TextName.text.toString()
//            val username = TextUsername.text.toString()
//            val gender = spinnerGender.selectedItem.toString()
//            val age = findViewById<EditText>(R.id.editAge).text.toString().toIntOrNull()
//            val weight = findViewById<EditText>(R.id.editWeight).text.toString().toDoubleOrNull()
//            val height = findViewById<EditText>(R.id.editHeight).text.toString().toDoubleOrNull()
//
//            if (name.isNotBlank() && username.isNotBlank() && gender.isNotBlank() && age != null && weight != null && height != null) {
//                // All required fields are filled
//
//                // Call the signUpUser function with the additional values
//                authManager.signUpUser(name, username, gender, age, weight, height) { success, errorMessage ->
//                    if (success) {
//                        Log.d("SignUpUser", "User criado com sucesso")
//
//                        val intent = Intent(this@SignUpUser, Questionary::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        errorMessage?.let {
//                            // Your existing error handling logic
//                            textViewDescription.text = "Erro ao criar o usuário"
//                            textViewDescription.visibility = View.VISIBLE
//                        }
//                    }
//                }
//            } else {
//                // Show an error message if any of the required fields are empty
//                textViewDescription.text = "Please fill in all required fields"
//                textViewDescription.visibility = View.VISIBLE
//            }
//        }

    }
}
