package com.example.fitquest
data class WorkoutData(
    val title: String = "",
    val duration: String = "",
    val isCompleted: Boolean = false,
    val image: Int = 0,
    val exercises: List<ExerciseData> = emptyList(),
    val date: String = "",
    val isQuest: Boolean = false
)
