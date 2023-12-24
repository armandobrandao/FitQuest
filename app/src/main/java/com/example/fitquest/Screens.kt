package com.example.fitquest

//NavController

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Workout : Screens("workout")
    object Challenges : Screens("challenges")
    object Profile : Screens("profile")
    object Notifications : Screens("notifications")
    object AddFriends : Screens("addFriends")
    object Friend : Screens("friend")


}