package com.example.fitquest

import android.net.Uri
import java.util.Date

data class UserProfile(
    val id: String? = null,
    val username: String = "",
    val fullName: String = "",
    val profileImage: Int = R.drawable.default_profile_image,
    val gender: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val goal: String = "", // New field from the questionnaire
    val motivation: String = "", // New field from the questionnaire
    val pushUps: String = "", // New field from the questionnaire
    val activityLevel: String = "", // New field from the questionnaire
    val firstDayOfWeek: String = "", // New field from the questionnaire
    val trainingDays: String = "", // New field from the questionnaire
    val sessionsOutside: String = "",
    var xp_total: Int = 0,
    val xp_level: Int = 0,
    val level: Int = 0,
    val joinDate: String = "",
    val longestStreak: Int = 0,
    var places: List<PlaceData> = emptyList(),
    val friends: List<UserProfile> = emptyList(),
    val friend_reqs: List<UserProfile> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val progress: Int = 0,
    val uniqueCode: String = "",
    val lastLoginDate: Date? = Date(), // Nullable Date for the last login date
    val currentStreak: Int = 0,
    var profileImageUrl: String? = null,
)
