package com.example.fitquest

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph (navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route
    )
    {
        composable(Screens.Home.route) {
            Log.d("NavGraph", "Navigating to Home")
            Homepage(navController = navController)
        }
        composable(Screens.Workout.route) {
            Log.d("NavGraph", "Navigating to Workout")
            Workouts(navController)
        }
        composable(Screens.Challenges.route) {
            Log.d("NavGraph", "Navigating to Challenges")
            Challenge(navController = navController)
        }
        composable(Screens.Profile.route) {
            Log.d("NavGraph", "Navigating to Profile")
            DailyQuest(navController = navController)
        }
        composable(Screens.Notifications.route) {
            Notifications(navController = navController)
        }
    }
}


