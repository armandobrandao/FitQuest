package com.example.fitquest
data class WorkoutData(
    val id: String? = null,
    val title: String = "",
    val duration: String = "",
    var completed: Boolean = false,
    val image: String = "",
    val exercises: List<ExerciseData> = emptyList(),
    val date: String = "",
    val quest: Boolean = false,
    val xp : Int = 0,
    val post_photo: String? = null
)

