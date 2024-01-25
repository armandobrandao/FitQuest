package com.example.fitquest

data class CheckpointData(
    val id: String? = null,
    val name: String = "",
    val place: PlaceData? = null,
    var completed: Boolean = false,
    val workout: WorkoutData? = null,
    )