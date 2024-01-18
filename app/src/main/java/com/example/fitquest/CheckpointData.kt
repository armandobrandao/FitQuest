package com.example.fitquest

import java.time.Duration

data class CheckpointData(
    val id: String? = null,
    val name: String = "",
    val place: PlaceData? = null,
    var isCompleted: Boolean = false,
    val workout: WorkoutData? = null,
    )

// Exemplo de Checkpoit
// CheckpointData(name = "Checkpoint 1", place = Place(), isCompleted = false, exercises = [ExerciseData(), ExerciseData()], duration = 40)