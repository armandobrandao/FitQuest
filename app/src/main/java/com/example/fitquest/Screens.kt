package com.example.fitquest

//NavController

sealed class Screens(val route: String) {
    object Home : Screens("Home")
    object Workout : Screens("Workout")
    object Challenges : Screens("Challenges")
    object Profile : Screens("Profile")

}