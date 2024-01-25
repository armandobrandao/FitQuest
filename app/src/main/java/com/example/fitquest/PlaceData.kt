package com.example.fitquest

data class PlaceData(
    val name: String = "",
    val lat: Double = 0.0,
    val long: Double = 0.0,
    val photos: List<String> = emptyList(),
)