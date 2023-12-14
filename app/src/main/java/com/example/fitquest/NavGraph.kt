package com.example.fitquest

import android.os.Build
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
        composable("Home") { Homepage() }
        composable("Workout") { AddFriend("Maria","123") }
        composable("Challenges") { Homepage() }
        composable("Profile") { Homepage() }
    }
}


