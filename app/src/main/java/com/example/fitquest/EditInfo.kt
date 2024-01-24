package com.example.fitquest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.fitquest.databinding.EditInfoBinding

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

    private lateinit var imageViewProfilePhoto: ImageView
    private lateinit var buttonSelectPhoto: Button

    private lateinit var currentUser : UserProfile
    private var selectedImageUri: Uri? = null

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

        imageViewProfilePhoto = findViewById(R.id.imageViewProfilePhoto)
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto)

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

        buttonSelectPhoto.setOnClickListener {
            // Handle photo selection here (open gallery, camera, etc.)
            // For simplicity, let's assume you're opening a gallery intent

            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }

        authManager.getCurrentUser { userProfile ->
            userProfile?.let {
                // Populate UI elements with user data
                currentUser = userProfile
                TextName.setText(userProfile.fullName)
                TextUsername.setText(userProfile.username)
                editAge.setText(userProfile.age.toString())
                editHeight.setText(userProfile.height.toString())
                editWeight.setText(userProfile.weight.toString())

                spinnerGender.setSelection(adapterGender.getPosition(userProfile.gender))

                // Load user's profile image using Coil
                val profileImageUri = Uri.parse(userProfile.profileImageUrl)
                imageViewProfilePhoto.load(profileImageUri) {
                    placeholder(R.drawable.default_profile_image)
                }

                val goalOptions = arrayOf("Lose weight", "Build muscle", "Maintain shape")  // Replace with your actual goal options
                val goalIndex = goalOptions.indexOf(userProfile.goal)

                if (goalIndex != -1) {
                    val radioButtonId = radioGroupGoal.getChildAt(goalIndex).id
                    radioGroupGoal.check(radioButtonId)
                }

                val motivationOptions = arrayOf("Feel confident", "Relieve stress", "Improve health", "Increase energy")
                val motivationIndex = motivationOptions.indexOf(userProfile.motivation)

                if (motivationIndex != -1) {
                    val radioButtonId = radioGroupMotivation.getChildAt(motivationIndex).id
                    radioGroupMotivation.check(radioButtonId)
                }

                val pushupOptions = arrayOf("3-5", "5-10 ", "At least 10")
                val pushupIndex = pushupOptions.indexOf(userProfile.pushUps)

                if (pushupIndex != -1) {
                    val radioButtonId = radioGroupPushUps.getChildAt(pushupIndex).id
                    radioGroupPushUps.check(radioButtonId)
                }

                val activityOptions = arrayOf("Sedentary", "Lightly active", "Moderately active", "Very active")
                val activityIndex = activityOptions.indexOf(userProfile.activityLevel)

                if (activityIndex != -1) {
                    val radioButtonId = radioGroupActivityLevel.getChildAt(activityIndex).id
                    radioGroupActivityLevel.check(radioButtonId)
                }

                val trainingDaysOptions = arrayOf("1", "2", "3", "4", "5", "6", "7")
                val trainingDaysIndex = trainingDaysOptions.indexOf(userProfile.trainingDays)

                if (trainingDaysIndex != -1) {
                    val radioButtonId = radioGroupTrainingDays.getChildAt(trainingDaysIndex).id
                    radioGroupTrainingDays.check(radioButtonId)
                }

                spinnerFirstDay.setSelection(adapterDay.getPosition(userProfile.firstDayOfWeek))

                val sessionsOutsideOptions = arrayOf("1", "2", "3", "4", "5", "6", "7")
                val sessionsOutsideIndex = sessionsOutsideOptions.indexOf(userProfile.sessionsOutside)

                if (sessionsOutsideIndex != -1) {
                    val radioButtonId = radioGroupSessionsOutside.getChildAt(sessionsOutsideIndex).id
                    radioGroupSessionsOutside.check(radioButtonId)
                }

            }
        }

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
            imageViewProfilePhoto.visibility = View.GONE
            buttonSelectPhoto.visibility = View.GONE

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
            imageViewProfilePhoto.visibility = View.VISIBLE
            buttonSelectPhoto.visibility = View.VISIBLE
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

                    val profileImageUri = selectedImageUri

                    // Create a UserProfile object
                    val updatedUserProfile = UserProfile(
                        username = username,
                        fullName = name,
                        gender = gender,
                        age = age,
                        weight = weight,
                        height = height,
                        goal = goal,
                        motivation = motivation,
                        pushUps = pushUps,
                        uniqueCode = currentUser.uniqueCode,
                        joinDate = currentUser.joinDate,
                        id = currentUser.id,
                        activityLevel = activityLevel,
                        firstDayOfWeek = firstDay,
                        trainingDays = trainingDays,
                        sessionsOutside = sessionsOutside,
                        profileImageUrl = profileImageUri.toString(),
                        xp_total = currentUser.xp_total,
                        xp_level = currentUser.xp_level,
                        level = currentUser.level,
                        longestStreak =currentUser.longestStreak,
                        places = currentUser.places,
                        friends = currentUser.friends,
                        friend_reqs = currentUser.friend_reqs,
                        achievements = currentUser.achievements,
                        progress = currentUser.progress,
                        lastLoginDate =currentUser.lastLoginDate,
                        currentStreak=currentUser.currentStreak,
                        steps =currentUser.steps,
                    )

                    // Call the updateCurrentUserProfile function with the UserProfile object
                    currentUser.id?.let { it1 ->
                        authManager.updateCurrentUserProfile(it1, updatedUserProfile, profileImageUri) { success ->
                            if (success) {
                                // Profile updated successfully
                                Log.d("UpdateUserProfile", "User profile updated successfully")

                                val intent = Intent(this@EditInfo, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // Handle update failure, show error message, etc.
                                errorMessageSubmitTextView.text = "Error updating user profile"
                                errorMessageSubmitTextView.visibility = View.VISIBLE
                            }
                        }
                    }

                    // Call the signUpUser function with the additional values
//                    authManager.signUpUser(
//                        name, username, gender, age, weight, height,
//                        goal, motivation, pushUps, activityLevel, firstDay, trainingDays, sessionsOutside,
//                        profileImageUri
//                    ) { success, errorMessage ->
//                        if (success) {
//                            Log.d("SignUpUser", "User created successfully")
//
//                            val intent = Intent(this@EditInfo, MainActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                        } else {
//                            errorMessage?.let {
//                                // Your existing error handling logic
//                                errorMessageSubmitTextView.text = "Error creating user"
//                                errorMessageSubmitTextView.visibility = View.VISIBLE
//                            }
//                        }
//                    }
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

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            Log.d("EditInfo", "selectedImageUri: $selectedImageUri")

            // Set the selected image to the ImageView
            imageViewProfilePhoto.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
