package com.example.fitquest

data class ExerciseData (
    val name: String = "",
    val duration: String = "",
    val imageResId: Int = 0,
    val durationInSeconds: Int = 0,
    val suitableGender: List<String> = emptyList(),
    val suitableGoals: List<String> = emptyList(),
    val suitableMotivations: List<String> = emptyList(),
    val suitablePushUps: List<String> = emptyList(),
    val suitableActivityLevels: List<String> = emptyList(),
    val target: String = ""
)