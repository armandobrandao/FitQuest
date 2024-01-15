package com.example.fitquest

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun NavGraph (navController: NavHostController, authManager: AuthManager){
    var currentUser by remember { mutableStateOf<UserProfile?>(null) }
    var userKey by remember { mutableStateOf(0) }
    LaunchedEffect(authManager, userKey) {
        authManager.getCurrentUser { user ->
            if (user != null) {
                currentUser = user
                // Increment the key to trigger a re-execution of LaunchedEffect
                userKey++
            } else {
                // Handle the case where the user is null
            }
        }
    }
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
            WeeklyChallenges(navController = navController)
        }
        composable(Screens.Profile.route) {
            Log.d("NavGraph", "Navigating to Profile")
            //Profile( user = Harry2, navController = navController)
            currentUser?.let {
                Profile(user = it, navController = navController)
            } ?: run {
                // Handle the case where currentUser is null
            }
        }
        composable(Screens.Notifications.route) {
            Notifications(navController = navController)
        }
        composable(Screens.AddFriends.route) {
            //AddFriend(user= Harry, navController = navController)
            currentUser?.let {
                AddFriend(user = it, navController = navController)
            } ?: run {
                // Handle the case where currentUser is null
            }
        }

        // Update the NavGraph composable
        composable("${Screens.Friend.route}/{friendUsername}") { backStackEntry ->
            val friendUsername = backStackEntry.arguments?.getString("friendUsername")
            if (friendUsername != null) {
                val friend = sampleFriends.find { it.username == friendUsername }
                val request = requestList.find { it.username == friendUsername }
                val search = searchList.find { it.username == friendUsername }
                    Log.d("NavGraph", "Navigating to FriendProfile for $friendUsername")
                if (friend != null) {
                    Friend(user = friend, navController = navController)
                }
                if (request != null){
                    Friend(user = request, navController = navController)
                }
                if (search != null) {
                    Friend(user = search, navController = navController)
                }else {
                    // Handle the case where friend is null (e.g., username not found)
                }
            } else {
                // Handle the case where friendUsername is null or not provided
            }
        }

        composable("${Screens.DailyQuest.route}/{questTitle}") { backStackEntry ->
            val questTitle = backStackEntry.arguments?.getString("questTitle")
            if (questTitle != null) {
                val quest = sampleDailyQuests.find { it.title == questTitle }
                Log.d("NavGraph", "Navigating to DailyQuest for $questTitle")
                if (quest != null) {
                    DailyQuest(navController = navController)
                }else {
                    // Handle the case where friend is null (e.g., username not found)
                }
            } else {
                // Handle the case where friendUsername is null or not provided
            }
        }
        composable(Screens.GenerateWorkout.route) {
            GenerateWorkout(navController = navController)
        }

        composable(Screens.CheckpointComplete.route) {
            CheckpointComplete(navController = navController)
        }

        composable("${Screens.LocationChallenge.route}/{challengeName}") { backStackEntry ->
            val challengeName = backStackEntry.arguments?.getString("challengeName")
            if (challengeName != null) {
                val challenge = sampleChallenges.find { it.name == challengeName }
                Log.d("NavGraph", "Navigating to DailyQuest for $challengeName")
                if (challenge != null) {
                    LocationChallenge(navController = navController, challenge)
                }else {
                    // Handle the case where friend is null (e.g., username not found)
                }
            } else {
                // Handle the case where friendUsername is null or not provided
            }
        }

        composable("${Screens.Checkpoint.route}/{checkpointName}") { backStackEntry ->
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")
            if (checkpointName != null) {
                val checkpoint = samplePlaces.find { it.name == checkpointName }
                Log.d("NavGraph", "Navigating to DailyQuest for $checkpointName")
                if (checkpoint != null) {
                    Checkpoint(navController = navController, checkpoint)
                }else {
                    // Handle the case where friend is null (e.g., username not found)
                }
            } else {
                // Handle the case where friendUsername is null or not provided
            }
        }

        composable("${Screens.CountdownPage.route}/{listExercises}/{numSets}") { backStackEntry ->
            val listExercises = backStackEntry.arguments?.getString("listExercises")?.let {
                // Logic to convert the string into a List<Exercise>
                // Example: it.split(",").map { exerciseString -> parseExerciseFromString(exerciseString) }
                sampleExercises
            } ?: emptyList()

            val numSets = backStackEntry.arguments?.getString("numSets")?.toIntOrNull() ?: 1

            CountdownPage(
                navController = navController,
                listExercises = listExercises,
                numSets = numSets
            )
        }

        composable("${Screens.Exercise.route}/{listExercises}/{numSets}") { backStackEntry ->
            val listExercises = backStackEntry.arguments?.getString("listExercises")?.let {
                // Logic to convert the string into a List<Exercise>
                // Example: it.split(",").map { exerciseString -> parseExerciseFromString(exerciseString) }
                sampleExercises
            } ?: emptyList()

            val numSets = backStackEntry.arguments?.getString("numSets")?.toIntOrNull() ?: 1

            Exercise(
                navController = navController,
                listExercises = listExercises,
                numSets = numSets
            )
        }

        composable("${Screens.FinishedWorkout.route}/{listExercises}") { backStackEntry ->
            val listExercises = backStackEntry.arguments?.getString("listExercises")?.let {
                // Logic to convert the string into a List<Exercise>
                // Example: it.split(",").map { exerciseString -> parseExerciseFromString(exerciseString) }
                sampleExercises
            } ?: emptyList()

            FinishedWorkout(
                navController = navController,
                listExercises = listExercises
            )
        }
        composable(Screens.DailyQuestComplete.route) {
            DailyQuestComplete(navController = navController)
        }

        composable(Screens.SignUpUser.route) {
            Log.d("NavGraph", "Navigating to SignUpUser")
            SignUpUser()
        }
    }
}

val Harry = User("Harry Philip", R.drawable.profile_image, "@harry")
val Harry2 = UserProfile("@harry","Harry Philip", R.drawable.profile_image)


