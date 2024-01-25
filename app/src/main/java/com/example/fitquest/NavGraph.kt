package com.example.fitquest

import android.os.Build
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
    var usersListNotFriend by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var usersList by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var placeData by remember { mutableStateOf<PlaceData?>(null) }
    var generatedWorkout by remember { mutableStateOf<WorkoutData?>(null) }
    var lastWorkouts by remember { mutableStateOf<List<WorkoutData>?>(null) }


    LaunchedEffect(authManager, userKey) {
        authManager.getCurrentUser { user ->
            if (user != null) {
                currentUser = user

                userKey++
            } else {

            }
        }
        authManager.getDailyQuestForToday { result ->
            dailyQuest = result
        }
        authManager.getChallengesForCurrentWeek { result ->
            challenges = result
        }

        currentUser?.let {
            authManager.fetchUserListFromFirestoreNotFriend(it) { fetchedUserList ->
                usersListNotFriend = fetchedUserList
            }
        }

        currentUser?.let {
            authManager.fetchUserListFromFirestore(it) { fetchedUserList ->
                usersList = fetchedUserList
            }
        }

        authManager.getLastWorkouts { result ->
            lastWorkouts = result
        }
    }
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route

    )
    {
        composable(Screens.Home.route) {
            dailyQuest?.let {
                Homepage(navController = navController, dailyQuest= dailyQuest!!)
            } ?: run {

            }

        }
        composable(Screens.Workout.route) {
            lastWorkouts?.let {
                Workouts(navController = navController, lastWorkouts = lastWorkouts!!)
            } ?: run {

            }
        }
        composable(Screens.Challenges.route) {
            challenges?.let {
                WeeklyChallenges(navController = navController, challenges = challenges)
            } ?: run {

            }
        }
        composable(Screens.Profile.route) {
            currentUser?.let {
                Profile(user = it, navController = navController, authManager = authManager)
            } ?: run {

            }
        }
        composable(Screens.Notifications.route) {
            currentUser?.let {
                Notifications(user = it, navController = navController, authManager)
            } ?: run {
            }
        }

        composable(Screens.AddFriends.route) {
            currentUser?.let {
                AddFriend(user = it, navController = navController, usersListNotFriend, authManager)
            } ?: run {
            }
        }

        composable("${Screens.Friend.route}/{friendUsername}") { backStackEntry ->
            val friendUsername = backStackEntry.arguments?.getString("friendUsername")
            if (friendUsername != null) {
                val search = usersList.find { it.username == friendUsername }

                if (search != null) {
                    currentUser?.let { Friend(user = search, currentUser = it, navController = navController, authManager) }
                }else {

                }
            } else {

            }
        }

        composable("${Screens.DailyQuest.route}/{questTitle}") { backStackEntry ->
            val questTitle = backStackEntry.arguments?.getString("questTitle")
            if (questTitle != null) {

                val quest = dailyQuest

                if (quest != null) {
                    if (quest.completed){
                        currentUser?.id?.let {
                            DailyQuestComplete(
                                navController = navController,
                                listExercises = quest,
                                isQuest = true,
                                authManager = authManager,
                                userId = it,
                                showButton = false
                            )
                        }
                    }else {
                        DailyQuest(quest = quest, navController = navController)
                    }
                } else {

                }
            } else {

            }
        }
        composable(Screens.GenerateWorkout.route) {
            GenerateWorkout(navController = navController, authManager = authManager)
        }

        composable("${Screens.GeneratedWorkout.route}/{selectedType}") { backStackEntry ->
            val selectedType = backStackEntry.arguments?.getString("selectedType")
            var canEnter by remember { mutableStateOf(false) }
            LaunchedEffect(selectedType) {
                authManager.generateNewWorkout(
                    selectedType = selectedType,
                ) { result ->
                    if (result != null) {

                        generatedWorkout = result
                        canEnter = true
                    } else {

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
                            } else {

                            }
                        }
                        val placeName = checkpoint.place?.name
                        authManager.getPlace(placeName) { result ->
                            if (result != null) {
                                placeData = result

                            } else {

                            }
                        }
                    }
                }

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
                if (challenge != null) {
                    Challenge(navController = navController, challenge)
                } else {

                }
            } else {

            }

        }

        composable("${Screens.LocationChallenge.route}/{challengeName}") { backStackEntry ->
            val challengeName = backStackEntry.arguments?.getString("challengeName")
            if (challengeName != null) {
                val challenge = challenges.find { it?.title == challengeName }
                if (challenge != null) {
                    LocationChallenge(navController = navController, challenge)
                } else {

                }
            } else {

            }
        }


        composable("${Screens.Checkpoint.route}/{challengeTitle}/{checkpointName}") { backStackEntry ->
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")
            val challengeTitle = backStackEntry.arguments?.getString("challengeTitle")

            if (checkpointName != null && challengeTitle != null) {
                val challenge = challenges.find { it!!.title == challengeTitle }
                val checkpoint = challenge?.checkpoints?.find { it!!.name == checkpointName }
                if (checkpoint != null) {
                    Checkpoint(navController = navController, checkpoint)
                } else {

                }
            } else {

            }
        }

        composable("${Screens.CountdownPage.route}/{numSets}/{isQuest}/{isGen}/{checkpointName}") { backStackEntry ->
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")
            val numSets = backStackEntry.arguments?.getString("numSets")?.toIntOrNull() ?: 1
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()
            val isGen = backStackEntry.arguments?.getString("isGen")?.toBoolean()

            if(isQuest != null) {
                if(isQuest) {

                    val exercises = dailyQuest

                    if (exercises != null) {

                        CountdownPage(
                            navController = navController,
                            exercises = exercises,
                            numSets = numSets,
                            isQuest = true,
                            isGen = false,
                            checkpointName = null
                        )
                    } else {

                    }
                } else {
                    if(isGen!!) {

                        val exercises = generatedWorkout
                        if (exercises != null) {
                            CountdownPage(
                                navController = navController,
                                exercises = exercises,
                                numSets = numSets,
                                isQuest = false,
                                isGen = true,
                                checkpointName = null
                            )
                        }else{
                        }
                    }else{
                        if (checkpointName != null) {

                            val checkpoint = challenges
                                .flatMap { it?.checkpoints.orEmpty() }
                                .find { it?.name == checkpointName }

                            if (checkpoint != null) {
                                checkpoint.workout?.let {
                                    CountdownPage(
                                        navController = navController,
                                        exercises = it,
                                        numSets = numSets,
                                        isQuest = false,
                                        isGen = false,
                                        checkpointName = checkpointName
                                    )
                                }
                            } else {

                            }
                        } else {

                        }
                    }
                }
            }else{

            }
        }

        composable("${Screens.Exercise.route}/{numSets}/{isQuest}/{isGen}/{checkpointName}") { backStackEntry ->
            val numSets = backStackEntry.arguments?.getString("numSets")?.toIntOrNull() ?: 1
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()
            val isGen = backStackEntry.arguments?.getString("isGen")?.toBoolean()
            val checkpointName = backStackEntry.arguments?.getString("checkpointName")

            if(isQuest != null) {
                if (isQuest) {

                    val exercises = dailyQuest

                    if (exercises != null) {

                        Exercise(
                            navController = navController,
                            listExercises = exercises,
                            numSets = numSets,
                            isQuest = true,
                            isGen = false,
                            checkpointName = null
                        )
                    } else {

                    }
                } else {
                    if (isGen!!) {
                        val exercises = generatedWorkout
                        if (exercises != null) {
                            Exercise(
                                navController = navController,
                                listExercises = exercises,
                                numSets = numSets,
                                isQuest = false,
                                isGen = true,
                                checkpointName = null
                            )
                        }
                    } else {
                        if (checkpointName != null) {
                            val checkpoint = challenges
                                .flatMap { it?.checkpoints.orEmpty() }
                                .find { it?.name == checkpointName }

                            if (checkpoint != null) {
                                checkpoint.workout?.let {
                                    Exercise(
                                        navController = navController,
                                        listExercises = it,
                                        numSets = numSets,
                                        isQuest = false,
                                        isGen = false,
                                        checkpointName = checkpointName
                                    )
                                }
                            }
                        } else {
                        }
                    }
                }
            }
        }

        composable("${Screens.FinishedWorkout.route}/{isQuest}") { backStackEntry ->
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()

            if(isQuest != null) {
                    if(isQuest) {

                        val exercises = dailyQuest

                        if (exercises != null) {

                            FinishedWorkout(
                                navController = navController,
                                listExercises = exercises,
                                isQuest = true,
                                isGen = false
                            )
                        } else {

                        }
                    } else {
                        val exercises = generatedWorkout
                        if (exercises != null) {
                            FinishedWorkout(
                                navController = navController,
                                listExercises = exercises,
                                isQuest = false,
                                isGen = true
                            )
                        }
                    }
            }else{

            }

        }
        composable("${Screens.DailyQuestComplete.route}/{isQuest}") { backStackEntry ->
            val isQuest = backStackEntry.arguments?.getString("isQuest")?.toBoolean()

            if(isQuest != null) {
                val exercises = dailyQuest
                LaunchedEffect(Unit){
                    if (exercises != null) {
                        authManager.updateDailyQuest(exercises, isQuest) { success ->
                            if (success) {
                            } else {

                            }
                        }
                    }
                }
                if (exercises != null) {
                    currentUser?.id?.let {
                        DailyQuestComplete(
                            navController = navController,
                            listExercises = exercises,
                            isQuest = isQuest,
                            authManager = authManager,
                            userId = it,
                            showButton = true
                        )
                    }
                } else {
                }
            }else{

            }
        }

        composable(Screens.SignUpUser.route) {
            SignUpUser()
        }
    }
}


