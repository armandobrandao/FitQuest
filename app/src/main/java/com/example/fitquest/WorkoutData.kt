package com.example.fitquest
data class WorkoutData(
    val id: String? = null,
    val title: String = "",
    val duration: String = "",
    val isCompleted: Boolean = false,
    val image: Int = 0,
    val exercises: List<ExerciseData> = emptyList(),
    val date: String = "",
    val isQuest: Boolean = false,
    val xp : Int = 0,
)

// TODO: ADICIONAR UMA CENA PARA GUARDAR A FOTO PARA MOSTRAR NOS LAST WORKOUTS
