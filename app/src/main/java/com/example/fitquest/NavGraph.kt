package com.example.fitquest

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph (navController: NavHostController){
    NavHost(
        navController = navController,
        startDestination = "Home"
    )
    {
        composable("Home") {
            Log.d("NavGraph", "Navigating to Home")
            Homepage()
        }
        composable("Workout") {
            Log.d("NavGraph", "Navigating to Workout")
            AddFriend("Maria","123")
        }
        composable("Challenges") {
            Log.d("NavGraph", "Navigating to Challenges")
            Homepage()
        }
        composable("Profile") {
            Log.d("NavGraph", "Navigating to Profile")
            Homepage()
        }
    }
}


