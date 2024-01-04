package com.example.fitquest

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

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

    fun signUpUser(name: String, username: String, callback: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        val userProfile = UserProfile(username, name)

        saveUserProfile(user?.uid, userProfile, callback)
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