package com.example.fitquest

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AuthManager(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun signUpUser(
        name: String,
        username: String,
        gender: String,
        age: Int,
        weight: Double,
        height: Double,
        activityLevel: String,
        sessionsOutside: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        val user = auth.currentUser
        val userProfile = UserProfile(
            username = username,
            fullName = name,
            gender = gender,
            age = age,
            weight = weight,
            height = height,
            activityLevel = activityLevel,
            sessionsOutside = sessionsOutside,
            xp = 0,
            level = 0,
            joinDate = getCurrentFormattedDate(), // Replace this function with your date formatting logic
            longestStreak = 0,
            places = 0,
            friends = emptyList(),
            achievements = emptyList()
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

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}