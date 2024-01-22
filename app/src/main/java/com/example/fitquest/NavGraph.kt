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
    var dailyQuest by remember { mutableStateOf<WorkoutData?>(null) }
    var challenges by remember { mutableStateOf<List<ChallengeData?>>(emptyList()) }
    var usersList by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var placeData by remember { mutableStateOf<PlaceData?>(null) }
    var generatedWorkout by remember { mutableStateOf<WorkoutData?>(null) }


    LaunchedEffect(authManager, userKey) {
        authManager.getCurrentUser { user ->
            if (user != null) {
                currentUser = user
                // Increment the key to trigger a re-execution of LaunchedEffect
                Log.d("NavGraph", "$currentUser")
                userKey++
            } else {
                // Handle the case where the user is null
            }
        }
        authManager.getDailyQuestForToday { result ->
            dailyQuest = result
        }
        authManager.getChallengesForCurrentWeek { result ->
            challenges = result
        }

        currentUser?.let {
            authManager.fetchUserListFromFirestore(it) { fetchedUserList ->
                usersList = fetchedUserList
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
            dailyQuest?.let {
                Homepage(navController = navController, dailyQuest= dailyQuest!!)
            } ?: run {
                // Handle the case where dailyQuest is null
            }

        }
        composable(Screens.Workout.route) {
            Log.d("NavGraph", "Navigating to Workout")
            Workouts(navController)
        }
        composable(Screens.Challenges.route) {
            Log.d("NavGraph", "Navigating to Challenges")
            challenges?.let {
                WeeklyChallenges(navController = navController, challenges = challenges)
            } ?: run {
                // Handle the case where challenges is null
            }
        }
        composable(Screens.Profile.route) {
            Log.d("NavGraph", "Navigating to Profile")
            //Profile( user = Harry2, navController = navController)
            currentUser?.let {
                Profile(user = it, navController = navController, authManager = authManager)
            } ?: run {
                // Handle the case where currentUser is null
            }
        }
        composable(Screens.Notifications.route) {
            currentUser?.let {
                Notifications(user = it, navController = navController, authManager)
            } ?: run {
                // Handle the case where currentUser is null
            }
        }

        composable(Screens.AddFriends.route) {
            //AddFriend(user= Harry, navController = navController)
            currentUser?.let {
                AddFriend(user = it, navController = navController, usersList, authManager)
            } ?: run {
                // Handle the case where currentUser is null
            }
        }

        // Update the NavGraph composable
        composable("${Screens.Friend.route}/{friendUsername}") { backStackEntry ->
            val friendUsername = backStackEntry.arguments?.getString("friendUsername")
            if (friendUsername != null) {
//                val friend = sampleFriends.find { it.username == friendUsername }
//                val request = requestList.find { it.username == friendUsername }
                val search = usersList.find { it.username == friendUsername }
                Log.d("NavGraph", "Navigating to FriendProfile for $friendUsername")
                Log.d("NavGraph", "search $search")

//                if (friend != null) {
//                    Friend(user = friend, navController = navController)
//                }
//                if (request != null){
//                    Friend(user = request, navController = navController)
//                }
                if (search != null) {
                    Log.d("NavGraph", "Entra no search != null ")
                    currentUser?.let { Friend(user = search, currentUser = it, navController = navController) }
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
                // Retrieve the DailyQuest based on the title
                val quest = dailyQuest
                Log.d("NavGraph", "Navigating to DailyQuest for $questTitle")
                if (quest != null) {
                    // Pass the retrieved DailyQuest to the DailyQuest composable
                    DailyQuest(quest = quest, navController = navController)
                } else {
                    // Handle the case where the retrieved DailyQuest is null
                }
            } else {
                // Handle the case where questTitle is null or not provided
            }
        }
        composable(Screens.GenerateWorkout.route) {
            GenerateWorkout(navController = navController, authManager = authManager)
        }

        composable("${Screens.GeneratedWorkout.route}/{selectedType}/{selectedDuration}") { backStackEntry ->
            val selectedType = backStackEntry.arguments?.getString("selectedType")
            val selectedDuration = backStackEntry.arguments?.getString("selectedDuration")
            var canEnter by remember { mutableStateOf(false) }
            LaunchedEffect(selectedType) {
                authManager.generateNewWorkout(
                    selectedType = selectedType,
                    selectedDuration = selectedDuration
                ) { result ->
                    if (result != null) {
                        // Handle the generated workout (e.g., navigate to a new screen)
                        Log.d("GENERATE", "Generated Workout: $result")
                        generatedWorkout = result
                        canEnter = true
                    } else {
                        // Handle the case when the workout generation fails
                        Log.d("GENERATE", "Failed to generate workout")
                    }
                }
            }
            if(canEnter) {
                GeneratedWorkout(navController = navController, generatedWorkout = generatedWorkout)
            }
        }

        composable("${Screens.CheckpointComplete.route}/{checkpointName}") { backStackEntry ->
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")
            val checkpoint = challenges
                .flatMap { it?.checkpoints.orEmpty() }
                .find { it?.name == checkpointName }

            if (checkpoint != null) {
                // Get the challengeId from the parent challenge
                val challengeId = challenges
                    .find { it?.checkpoints?.contains(checkpoint) == true }
                    ?.id

                LaunchedEffect(challengeId) {
                    if (challengeId != null && checkpointName != null) {
                        authManager.updateCheckpointAndChallenge(challengeId, checkpointName
                        ) { success ->
                            if (success) {

                            } else {

                            }
                        }
                    }
                }
                LaunchedEffect(checkpointName){
                    checkpoint.place?.let {
                        authManager.updatePlaces(it) { success ->
                            if (success) {
                                Log.d("NavGraph","deu certo")
                            } else {

                            }
                        }
                        val placeName = checkpoint.place?.name
                        authManager.getPlace(placeName) { result ->
                            if (result != null) {
                                placeData = result
                                Log.d("NavGraph", "Place data retrieved successfully: $placeData")
                                // Handle the retrieved placeData as needed
                            } else {
                                Log.d("NavGraph", "Failed to retrieve place data.")
                                // Handle the case where placeData is null
                            }
                        }
                    }
                }
                // Pass the exercises from the checkpoint to the CheckpointComplete composable
                checkpoint.workout?.let { workout ->
                    val challenge = challenges.find { it?.checkpoints?.contains(checkpoint) == true }

                    val placeName = checkpoint.place?.name
                    if (challenge != null) {
                        currentUser?.id?.let {
                            if (checkpointName != null) {
                                if (challengeId != null) {
                                    CheckpointComplete(
                                        navController = navController,
                                        workout = workout,
                                        challenge = challenge,
                                        authManager = authManager,
                                        userId = it,
                                        checkpointName = checkpointName,
                                        challengeId = challengeId,
                                        placeName = placeName,
                                        placeData = placeData
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }



        composable("${Screens.Challenge.route}/{challengeName}") { backStackEntry ->
            val challengeName = backStackEntry.arguments?.getString("challengeName")
            if (challengeName != null) {
                val challenge = challenges.find { it?.title == challengeName }
                Log.d("NavGraph", "Navigating to DailyQuest for $challengeName")
                if (challenge != null) {
                    Challenge(navController = navController, challenge)
                } else {
                    // Handle the case where the challenge is null (e.g., challenge not found)
                }
            } else {
                // Handle the case where challengeName is null or not provided
            }

        }

        composable("${Screens.LocationChallenge.route}/{challengeName}") { backStackEntry ->
            val challengeName = backStackEntry.arguments?.getString("challengeName")
            if (challengeName != null) {
                val challenge = challenges.find { it?.title == challengeName }
                Log.d("NavGraph", "Navigating to DailyQuest for $challengeName")
                if (challenge != null) {
                    LocationChallenge(navController = navController, challenge)
                } else {
                    // Handle the case where the challenge is null (e.g., challenge not found)
                }
            } else {
                // Handle the case where challengeName is null or not provided
            }
        }


        composable("${Screens.Checkpoint.route}/{challengeTitle}/{checkpointName}") { backStackEntry ->
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")
            val challengeTitle = backStackEntry.arguments?.getString("challengeTitle")

            if (checkpointName != null && challengeTitle != null) {
                val challenge = challenges.find { it!!.title == challengeTitle }
                val checkpoint = challenge?.checkpoints?.find { it!!.name == checkpointName }
                Log.d("NavGraph", "Navigating to Checkpoint for $checkpointName")
                if (checkpoint != null) {
                    Log.d("NavGraph", "Entra no if para navegar")
                    Checkpoint(navController = navController, checkpoint)
                } else {
                    // Handle the case where checkpoint is null (e.g., checkpoint not found)
                }
            } else {
                // Handle the case where checkpointName or challengeTitle is null or not provided
            }
        }



        composable("${Screens.CountdownPage.route}/{numSets}/{isQuest}/{checkpointName}") { backStackEntry ->
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")
            val numSets = backStackEntry.arguments?.getString("numSets")?.toIntOrNull() ?: 1
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()
            Log.d("NavGraph COUNTDOWNPAGE", "isQuest, $isQuest")
            if(isQuest != null) {
                if(isQuest) {
                    // Retrieve the DailyQuest based on the title
                    val exercises = dailyQuest
                    Log.d("NavGraph", "Navigating to DailyQuest for $exercises")
                    if (exercises != null) {
                        Log.d("NAVGRAPH", "exercises, $exercises")
                        // Pass the retrieved DailyQuest to the DailyQuest composable
                        CountdownPage(
                            navController = navController,
                            exercises = exercises,
                            numSets = numSets,
                            isQuest = true,
                            checkpointName = null
                        )
                    } else {

                        // Handle the case where the retrieved DailyQuest is null
                    }
                } else {
                    // Look for the checkpoint in challenges and get exercises
                    val checkpoint = challenges
                        .flatMap { it?.checkpoints.orEmpty() }
                        .find { it?.name == checkpointName }

                    if (checkpoint != null) {
                        // Pass the exercises from the checkpoint to the CountdownPage composable
                        checkpoint.workout?.let {
                            CountdownPage(
                                navController = navController,
                                exercises = it,
                                numSets = numSets,
                                isQuest = false,
                                checkpointName = checkpointName
                            )
                        }
                    } else {
                        // Handle the case when checkpoint is not found
                    }
                }
            }else{

            }
        }

        composable("${Screens.Exercise.route}/{numSets}/{isQuest}/{checkpointName}") { backStackEntry ->
            val numSets = backStackEntry.arguments?.getString("numSets")?.toIntOrNull() ?: 1
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")
            Log.d("NavGraph EXERCISE", "isQuest, $isQuest")

            if(isQuest != null) {
                if (isQuest) {
                    // Retrieve the DailyQuest based on the title
                    val exercises = dailyQuest
                    Log.d("NavGraph", "Navigating to DailyQuest for $exercises")
                    if (exercises != null) {
                        // Pass the retrieved DailyQuest to the DailyQuest composable
                        Exercise(
                            navController = navController,
                            listExercises = exercises,
                            numSets = numSets,
                            isQuest = true,
                            checkpointName = null

                        )
                    } else {
                        // Handle the case where the retrieved DailyQuest is null
                    }
                } else {
                    // Look for the checkpoint in challenges and get exercises
                    val checkpoint = challenges
                        .flatMap { it?.checkpoints.orEmpty() }
                        .find { it?.name == checkpointName }

                    if (checkpoint != null) {
                        // Pass the exercises from the checkpoint to the CountdownPage composable
                        checkpoint.workout?.let {
                            Exercise(
                                navController = navController,
                                listExercises = it,
                                numSets = numSets,
                                isQuest = false,
                                checkpointName = checkpointName
                            )
                        }
                    }
                }
            }
        }

        composable("${Screens.FinishedWorkout.route}/{isQuest}") { backStackEntry ->
//            val workout = backStackEntry.arguments?.getString("listExercises")
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()

            if(isQuest != null) {
                    if(isQuest) {
                        // Retrieve the DailyQuest based on the title
                        val exercises = dailyQuest
                        Log.d("NavGraph", "Navigating to DailyQuest for $exercises")
                        if (exercises != null) {
                            // Pass the retrieved DailyQuest to the DailyQuest composable
                            FinishedWorkout(
                                navController = navController,
                                listExercises = exercises,
                                isQuest = true
                            )
                        } else {
                            // Handle the case where the retrieved DailyQuest is null
                        }
                    } else {

                    }
            }else{

            }

        }
        composable("${Screens.DailyQuestComplete.route}/{isQuest}") { backStackEntry ->
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()

            if(isQuest != null) {
                if(isQuest) {
                    // Retrieve the DailyQuest based on the title
                    val exercises = dailyQuest
                    LaunchedEffect(Unit){
                        Log.d("NavGraph", "Entra no LauchedEffect")
                        if (exercises != null) {
                            authManager.updateDailyQuest(exercises) { success ->
                                if (success) {
                                    Log.d("NavGraph","deu certo")
                                } else {

                                }
                            }
                        }
                    }
                    Log.d("NavGraph", "Navigating to DailyQuest for $exercises")
                    if (exercises != null) {
                        // Pass the retrieved DailyQuest to the DailyQuest composable
                        currentUser?.id?.let {
                            DailyQuestComplete(
                                navController = navController,
                                listExercises = exercises,
                                isQuest = true,
                                authManager = authManager,
                                userId = it
                            )
                        }
                    } else {
                        // Handle the case where the retrieved DailyQuest is null
                    }
                } else {
                    // TODO: LIDAR QUANDO NAO É UM QUEST
                }
            }else{

            }
        }

        composable(Screens.SignUpUser.route) {
            Log.d("NavGraph", "Navigating to SignUpUser")
            SignUpUser()
        }
    }
}


