package com.example.fitquest

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

class AuthManager(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // If sign-up is successful, the signIn function will take care of creating a new DailyQuest
                    signIn(email, password) { signInSuccess, signInError ->
                        if (signInSuccess) {
                            // Callback with sign-up success
                            callback(true, null)
                        } else {
                            // Callback with sign-up failure
                            callback(false, signInError ?: "Error signing in after sign-up")
                        }
                    }
                } else {
                    // Callback with sign-up failure
                    callback(false, task.exception?.message)
                }
            }
    }


    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Check if the user already has a DailyQuest for the current day
                        hasDailyQuestForToday(user.uid) { hasDailyQuest ->
                            if (!hasDailyQuest) {
                                // If no DailyQuest exists for the current day, generate a new one
                                generateNewDailyQuest(user.uid) { newDailyQuest ->
                                    if (newDailyQuest != null) {
                                        // Handle the generated DailyQuest, for example, save it to Firestore or use it as needed
                                        // ...
                                        Log.d("New daily quest", "$newDailyQuest")
                                        // Callback with sign-in success
                                        callback(true, null)
                                    } else {
                                        // Handle the case when there is an issue generating the DailyQuest
                                        callback(false, "Error generating DailyQuest")
                                    }
                                }
                            } else {
                                // If a DailyQuest already exists for the current day, proceed without generating a new one
                                callback(true, null)
                            }
                        }
                    } else {
                        // Handle the case when the user is null
                        callback(false, "Error retrieving user information")
                    }
                } else {
                    // Callback with sign-in failure
                    callback(false, task.exception?.message)
                }
            }
    }

    // Add this function to your AuthManager class
    private fun hasDailyQuestForToday(userId: String, callback: (Boolean) -> Unit) {
        // Get the current date
        val currentDate = getCurrentFormattedDateDaily()

        firestore.collection("users")
            .document(userId)
            .collection("dailyQuests")
            .whereEqualTo("date", currentDate)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if there is a DailyQuest for the current day
                callback(!querySnapshot.isEmpty)
            }
            .addOnFailureListener {
                // Handle the failure to check for existing DailyQuests in Firestore
                callback(false)
            }
    }

    fun signUpUser(
        name: String,
        username: String,
        gender: String,
        age: Int,
        weight: Double,
        height: Double,
        goal: String,
        motivation: String,
        pushUps: String,
        activityLevel: String,
        firstDay: String,
        trainingDays: String,
        sessionsOutside: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val user = auth.currentUser
        val userProfile = UserProfile(
            username = username,
            fullName = name,
            profileImage = R.drawable.default_profile_image,
            gender = gender,
            age = age,
            weight = weight,
            height = height,
            goal = goal,
            motivation = motivation,
            pushUps = pushUps,
            firstDayOfWeek = firstDay,
            trainingDays = trainingDays,
            activityLevel = activityLevel,
            sessionsOutside = sessionsOutside,
            xp_total = 0,
            xp_level = 0,
            level = 0,
            joinDate = getCurrentFormattedDate(), // Replace this function with your date formatting logic
            longestStreak = 0,
            places = 0,
            friends = emptyList(),
            achievements = emptyList(),
            progress = 0,
            uniqueCode = generateUniqueCode(username),

        )

        saveUserProfile(user?.uid, userProfile, callback)
    }


    private fun getCurrentFormattedDate(): String {
        // Replace this with your date formatting logic
        val currentTime = Calendar.getInstance().time
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentTime)
    }

    private fun saveUserProfile(
        userId: String?,
        userProfile: UserProfile,
        callback: (Boolean, String?) -> Unit
    ) {
        userId?.let {
            firestore.collection("users")
                .document(it)
                .set(userProfile)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, "Error creating user profile: $e")
                }
        }
    }

    fun getCurrentUser(callback: (UserProfile?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                        if (userProfile != null) {
                            val currentLevelData = calculateUserLevel(userProfile.xp_total)
                            val currentLevelXpData = calculateUserLevelXp(userProfile.xp_total)
                            val currentUser = UserProfile(
                                username = userProfile.username,
                                fullName = userProfile.fullName,
                                profileImage = userProfile.profileImage,
                                gender = userProfile.gender,
                                age = userProfile.age,
                                weight = userProfile.weight,
                                height = userProfile.height,
                                goal = userProfile.goal,
                                motivation = userProfile.motivation,
                                pushUps = userProfile.pushUps,
                                activityLevel = userProfile.activityLevel,
                                firstDayOfWeek = userProfile.firstDayOfWeek,
                                trainingDays = userProfile.trainingDays,
                                sessionsOutside = userProfile.sessionsOutside,
                                xp_total = userProfile.xp_total,
                                xp_level = currentLevelXpData,
                                level = currentLevelData,
                                joinDate = userProfile.joinDate,
                                longestStreak = userProfile.longestStreak,
                                places = userProfile.places,
                                friends = userProfile.friends,
                                achievements = userProfile.achievements,
                                progress = userProfile.progress,
                                uniqueCode = userProfile.uniqueCode,
                            )
                            updateCurrentUserProfile(user.uid, currentUser) {
                                if (it) {
                                    callback(currentUser)
                                } else {
                                    callback(null)
                                }
                            }
                        } else {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener { e ->
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    private fun updateCurrentUserProfile(userId: String, userProfile: UserProfile, callback: (Boolean) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .set(userProfile)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun generateUniqueCode(username: String): String {
        val random = SecureRandom()
        val salt = random.nextInt().absoluteValue.toString()

        // Concatenate username and randomness
        val combinedData = "$username$salt"

        // Create a hash of the combined data
        val hashedCode = hashString(combinedData)

        // Return a portion of the hash as the unique code
        return hashedCode.substring(0, 8) // You can adjust the length as needed
    }

    fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.fold(StringBuilder()) { str, byte -> str.append(byte.toString(16).padStart(2, '0')) }.toString()
    }

    fun calculateUserLevel(xp_total: Int): Int{
        val levelThreshold = 200
        val currentLevel = (xp_total / levelThreshold) + 1
        return currentLevel
    }

    fun calculateUserLevelXp(xp_total: Int): Int{
        val levelThreshold = 200
        val xpInCurrentLevel = xp_total % levelThreshold
        return xpInCurrentLevel
    }


    // Add this function to your AuthManager class
    fun saveDailyQuestForUser(userId: String, dailyQuest: DailyQuest, callback: (Boolean) -> Unit) {
        Log.d("AuthManager", "Entra no saveDailyQuestForUser")
        firestore.collection("users")
            .document(userId)
            .collection("dailyQuests")
            .add(dailyQuest)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Update generateNewDailyQuest function
    fun generateNewDailyQuest(userId: String, callback: (DailyQuest?) -> Unit) {
        Log.d("AuthManager", "Entra no generateNewDailyQuest")

        // Assuming you have a collection named "exercises" in your Firestore
        firestore.collection("exercises")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val exercisesList = mutableListOf<ExerciseData>()

                // Convert documents to Exercise objects
                for (document in querySnapshot.documents) {
                    val exercise = document.toObject(ExerciseData::class.java)
                    exercise?.let { exercisesList.add(it) }
                }

                // Ensure that there are at least 2 exercises in the collection
                if (exercisesList.size >= 2) {
                    // Shuffle the list and select the first two exercises
                    exercisesList.shuffle()
                    val selectedExercises = exercisesList.subList(0, 2)
                    Log.d("AuthManager", "selectedExercises, $selectedExercises")

                    // Create a new DailyQuest with the selected exercises
                    val newDailyQuest = DailyQuest(
                        title = "Generated Daily Quest",
                        duration = "45 mins", // You can adjust this as needed
                        isCompleted = false,
                        image = R.drawable.pilates, // Replace with the appropriate image
                        exercises = selectedExercises,
                        date = getCurrentFormattedDateDaily()
                    )

                    Log.d("AuthManager", "newDailyQuest, $newDailyQuest")


                    // Save the DailyQuest to the user's document
                    saveDailyQuestForUser(userId, newDailyQuest) { success ->
                        if (success) {
                            callback(newDailyQuest)
                        } else {
                            // Handle the case when there is an issue saving the DailyQuest
                            callback(null)
                        }
                    }
                } else {
                    // Handle the case when there are not enough exercises in the collection
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                // Handle the failure to retrieve exercises from Firestore
                callback(null)
            }
    }
    private fun getCurrentFormattedDateDaily(): String {
        // Replace this with your date formatting logic
        val currentTime = Calendar.getInstance().time
        return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(currentTime)
    }
}