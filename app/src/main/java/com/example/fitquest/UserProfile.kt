package com.example.fitquest

data class UserProfile(
    val username: String = "",
    val fullName: String = "",
    val gender: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val activityLevel: String = "",
    val sessionsOutside: Int = 0,
    val xp: Int = 0,
    val level: Int = 0,
    val joinDate: String = "",
    val longestStreak: Int = 0,
    val places: Int = 0,
    val friends: List<UserProfile>, //TODO: verificar se é msm UserProfile aqui ou se devemos criar outra coisa para os amigos
    val achievements: List<Achievement>,
)
