package com.example.fitquest

data class UserProfile(
    val username: String = "",
    val fullName: String = "",
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
    val xp: Int = 0,
    val level: Int = 0,
    val joinDate: String = "",
    val longestStreak: Int = 0,
    val places: Int = 0,
    val friends: List<UserProfile> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
)