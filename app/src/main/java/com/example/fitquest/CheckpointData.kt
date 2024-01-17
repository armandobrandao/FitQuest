package com.example.fitquest

data class CheckpointData(
    val name: String = "",
    val place: PlaceData? = null,
    val isCompleted: Boolean = false,
    val exercises: List<ExerciseData> = emptyList(),
    )

// Exemplo de Checkpoit
// CheckpointData(name = "Checkpoint 1", place = Place(), isCompleted = false, exercises = [ExerciseData(), ExerciseData()])