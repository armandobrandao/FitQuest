package com.example.fitquest

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class AuthManager(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()


    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        usersCollection.whereEqualTo("e-mail", email)
            .get()
            .addOnCompleteListener { queryTask ->
                if (queryTask.isSuccessful) {
                    val querySnapshot = queryTask.result
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        callback(false, "The email address is already in use by another account.")
                    } else {
                        if (password.length >= 6) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(activity) { task ->
                                    if (task.isSuccessful) {
                                        val signUpBefore = true
                                        signIn(email, password, signUpBefore) { signInSuccess, signInError ->
                                            if (signInSuccess) {
                                                callback(true, null)
                                            } else {
                                                callback(false, signInError ?: "Error signing in after sign-up")
                                            }
                                        }
                                    callback(true, null)
                                    } else {
                                        callback(false, task.exception?.message)
                                    }
                                }
                        } else {
                            callback(false, "Your password needs to be at least 6 characters long")
                        }
                    }
                } else {
                    callback(false, queryTask.exception?.message)
                }
            }
    }


    fun signIn(email: String, password: String, signUpBefore: Boolean, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        if (!signUpBefore) {
                            hasDailyQuestForToday(user.uid) { hasDailyQuest ->
                                if (!hasDailyQuest) {
                                    generateNewDailyQuest(user.uid) { newDailyQuest ->
                                        if (newDailyQuest != null) {
                                            checkAndCreateChallenges(user.uid) { challengeCreationSuccess ->
                                                if (challengeCreationSuccess) {
                                                    callback(true, null)
                                                } else {
                                                    callback(false, "Error creating challenges")
                                                }
                                            }
                                        } else {
                                            callback(false, "Error generating DailyQuest")
                                        }
                                    }

                                    updateLongestStreak(user.uid) { updateSuccess ->
                                        if (updateSuccess) {
                                            callback(true, null)
                                        } else {
                                            callback(false, "Error updating longest streak")
                                        }
                                    }
                                } else {
                                    callback(true, null)
                                }
                            }
                        }
                    } else {
                        callback(false, "Error retrieving user information")
                    }
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid.orEmpty()
    }

    private fun hasDailyQuestForToday(userId: String, callback: (Boolean) -> Unit) {
        val currentDate = getCurrentFormattedDateDaily()
        firestore.collection("users")
            .document(userId)
            .collection("dailyQuests")
            .whereEqualTo("date", currentDate)
            .whereEqualTo("quest", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                callback(!querySnapshot.isEmpty)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun signUpUser(
        name: String,
        username: String,
        gender: String,
        age: Int,
        weight: Double,
        height: Double,
        goal: String,
        motivation: String,
        pushUps: String,
        activityLevel: String,
        firstDay: String,
        trainingDays: String,
        sessionsOutside: String,
        joinDate: String,
        uniqueCode: String,
        profileImageUri: String?,
        callback: (Boolean, String?) -> Unit
    ) {
        val user = auth.currentUser
        val userProfile = UserProfile(
            id = user?.uid,
            username = username,
            fullName = name,
            profileImage = R.drawable.default_profile_image,
            gender = gender,
            age = age,
            weight = weight,
            height = height,
            goal = goal,
            motivation = motivation,
            pushUps = pushUps,
            firstDayOfWeek = firstDay,
            trainingDays = trainingDays,
            activityLevel = activityLevel,
            sessionsOutside = sessionsOutside,
            xp_total = 0,
            xp_level = 0,
            level = 0,
            joinDate = joinDate,
            longestStreak = 0,
            places = emptyList(),
            friends = emptyList(),
            friend_reqs = emptyList(),
            achievements = emptyList(),
            progress = 0,
            uniqueCode = uniqueCode,
            lastLoginDate = Calendar.getInstance().time,
            currentStreak = 0,
            profileImageUrl = "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/profile_images%2Fdefault_profile_image.jpg?alt=media&token=af298b29-c545-4d30-b56a-5331b6667348"
        )

        saveUserProfile(user?.uid, userProfile, callback)

        if (user != null) {
            hasDailyQuestForToday(user.uid) { hasDailyQuest ->
                if (!hasDailyQuest) {
                    generateNewDailyQuest(user.uid) { newDailyQuest ->
                        if (newDailyQuest != null) {

                            checkAndCreateChallenges(user.uid) { challengeCreationSuccess ->
                                if (challengeCreationSuccess) {
                                    callback(true, null)
                                } else {
                                    callback(false, "Error creating challenges")
                                }
                            }
                        } else {
                            callback(false, "Error generating DailyQuest")
                        }
                    }
                    updateLongestStreak(user.uid) { updateSuccess ->
                        if (updateSuccess) {
                            callback(true, null)
                        } else {
                            callback(false, "Error updating longest streak")
                        }
                    }
                } else {
                    callback(true, null)
                }
            }
        }
    }

    private fun saveUserProfile(
        userId: String?,
        userProfile: UserProfile,
        callback: (Boolean, String?) -> Unit
    ) {
        userId?.let {
            firestore.collection("users")
                .document(it)
                .set(userProfile)
                .addOnSuccessListener {
                    callback(true, null)
                }
                .addOnFailureListener { e ->
                    callback(false, "Error creating user profile: $e")
                }
        }
    }

    fun getCurrentUser(callback: (UserProfile?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                        if (userProfile != null) {
                            val currentLevelData = calculateUserLevel(userProfile.xp_total)
                            val currentLevelXpData = calculateUserLevelXp(userProfile.xp_total)
                            val currentUser = UserProfile(
                                id = user.uid,
                                username = userProfile.username,
                                fullName = userProfile.fullName,
                                profileImage = userProfile.profileImage,
                                gender = userProfile.gender,
                                age = userProfile.age,
                                weight = userProfile.weight,
                                height = userProfile.height,
                                goal = userProfile.goal,
                                motivation = userProfile.motivation,
                                pushUps = userProfile.pushUps,
                                activityLevel = userProfile.activityLevel,
                                firstDayOfWeek = userProfile.firstDayOfWeek,
                                trainingDays = userProfile.trainingDays,
                                sessionsOutside = userProfile.sessionsOutside,
                                xp_total = userProfile.xp_total,
                                xp_level = currentLevelXpData,
                                level = currentLevelData,
                                joinDate = userProfile.joinDate,
                                longestStreak = userProfile.longestStreak,
                                places = userProfile.places,
                                friends = userProfile.friends,
                                friend_reqs = userProfile.friend_reqs,
                                achievements = userProfile.achievements,
                                progress = userProfile.progress,
                                uniqueCode = userProfile.uniqueCode,
                                lastLoginDate = userProfile.lastLoginDate,
                                currentStreak = userProfile.currentStreak,
                                profileImageUrl = userProfile.profileImageUrl
                            )
                            callback(currentUser)
                        } else {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener { e ->
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    fun updateCurrentUserProfile(
        userId: String,
        userProfile: UserProfile,
        imageUri: Uri?,
        callback: (Boolean) -> Unit
    ) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("profile_images/${UUID.randomUUID()}")
        if(imageUri != null){
            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    userProfile.profileImageUrl = downloadUri.toString()

                    firestore.collection("users")
                        .document(userId)
                        .set(userProfile)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }
        } else {
            firestore.collection("users")
                .document(userId)
                .set(userProfile)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }
    }


    fun uploadPhotoToFirestore(isQuest: Boolean, imageUri: Uri?, userId: String, callback: (Uri?) -> Unit) {
        if (imageUri != null) {
            val storageRef = storage.reference
            val imageRef = storageRef.child("post_workout_images/${UUID.randomUUID()}")

            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    savePhotoReferenceToDailyQuest(isQuest, userId, downloadUri)

                    callback(downloadUri)
                } else {
                    callback(null)
                }
            }
        } else {
            callback(null)
        }
    }

    fun uploadPhotoToFirestorePlace(imageUri: Uri?, userId: String, checkpointName: String, challengeId :String, placeName: String, callback: (Uri?) -> Unit) {
        if (imageUri != null) {
            val storageRef = storage.reference
            val imageRef = storageRef.child("post_workout_images/${UUID.randomUUID()}")

            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    savePhotoReferenceToPlacesAndCheckpoint(userId, downloadUri, checkpointName, challengeId, placeName)

                    callback(downloadUri)
                } else {
                    callback(null)
                }
            }
        } else {
            callback(null)
        }
    }

    private fun savePhotoReferenceToDailyQuest(isQuest: Boolean, userId: String, photoUri: Uri?) {
        val currentDate = getCurrentFormattedDateDaily()

        var collection = "generatedWorkouts"
        if (isQuest) {
            collection = "dailyQuests"
        }

        firestore.collection("users")
            .document(userId)
            .collection(collection)
            .whereEqualTo("date", currentDate)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val dailyQuestDocument = querySnapshot.documents[0]

                    dailyQuestDocument.reference
                        .update("post_photo", photoUri.toString())
                        .addOnSuccessListener {

                            val lastWorkoutsRef = firestore.collection("users")
                                .document(userId)
                                .collection("lastWorkouts")
                                .document()

                            dailyQuestDocument.reference.get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val completedWorkout = documentSnapshot.toObject(WorkoutData::class.java)
                                    if (completedWorkout != null) {
                                        lastWorkoutsRef.set(completedWorkout)
                                            .addOnSuccessListener {

                                            }
                                            .addOnFailureListener {

                                            }
                                    }
                                }
                        }
                        .addOnFailureListener {

                        }
                } else {

                }
            }
            .addOnFailureListener {

            }
    }


    fun savePhotoReferenceToPlacesAndCheckpoint(
        userId: String,
        photoUri: Uri?,
        checkpointName: String,
        challengeId: String,
        placeName: String
    ) {
        if (photoUri != null) {

            savePhotoReferenceToPlaces(placeName, photoUri.toString())

            savePhotoReferenceToCheckpoint(userId, challengeId, checkpointName, photoUri.toString())
        }
    }

    private fun savePhotoReferenceToPlaces(placeName: String, photoUrl: String) {
        val placesCollectionRef = firestore.collection("places")

        val query = placesCollectionRef.whereEqualTo("name", placeName)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val placeData = documentSnapshot.toObject(PlaceData::class.java)
                val updatedPhotos = placeData?.photos?.toMutableList() ?: mutableListOf()
                updatedPhotos.add(photoUrl)

                documentSnapshot.reference.update("photos", updatedPhotos)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener {

                    }
            } else {

            }
        }
    }


    private fun savePhotoReferenceToCheckpoint(
        userId: String,
        challengeId: String,
        checkpointName: String,
        photoUrl: String
    ) {
        val challengesCollectionRef = firestore.collection("users").document(userId)
            .collection("challenges").document(challengeId)

        challengesCollectionRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val challengeData = documentSnapshot.toObject(ChallengeData::class.java)
                val checkpoints = challengeData?.checkpoints?.toMutableList() ?: mutableListOf()

                val updatedCheckpoints = checkpoints.map { checkpoint ->
                    if (checkpoint?.name == checkpointName) {
                        val updatedWorkout = checkpoint.workout?.copy(post_photo = photoUrl)
                        checkpoint.copy(workout = updatedWorkout)
                    } else {
                        checkpoint
                    }
                }

                challengesCollectionRef.update("checkpoints", updatedCheckpoints)
                    .addOnSuccessListener {
                        var updatedWorkout2: WorkoutData? = null
                        updatedCheckpoints.map { checkpoint ->
                            if(checkpoint?.name == checkpointName){
                                updatedWorkout2 = checkpoint.workout?.copy(title = checkpointName)
                            }
                        }

                        val lastWorkoutsRef = firestore.collection("users")
                            .document(userId)
                            .collection("lastWorkouts")
                            .document()

                        updatedWorkout2?.let { it1 ->
                            lastWorkoutsRef.set(it1)
                                .addOnSuccessListener {

                                }
                                .addOnFailureListener {

                                }
                        }
                    }
                    .addOnFailureListener {

                    }
            } else {

            }
        }
    }


    fun signOut(context : Context) {
        auth.signOut()
        val intent = Intent(context, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    fun calculateUserLevel(xp_total: Int): Int{
        val levelThreshold = 200
        val currentLevel = (xp_total / levelThreshold) + 1
        return currentLevel
    }

    fun calculateUserLevelXp(xp_total: Int): Int{
        val levelThreshold = 200
        val xpInCurrentLevel = xp_total % levelThreshold
        return xpInCurrentLevel
    }


    fun saveDailyQuestForUser(userId: String, dailyQuest: WorkoutData, callback: (Boolean) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("dailyQuests")
            .add(dailyQuest)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
    fun saveWorkoutForUser(userId: String, newWorkout: WorkoutData, callback: (Boolean) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .collection("generatedWorkouts")
            .add(newWorkout)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun generateNewDailyQuest(userId: String, callback: (WorkoutData?) -> Unit) {
        var currentUser: UserProfile? = null
        getCurrentUser { user ->
            if (user != null) {
                currentUser = user

            } else {

            }
        }

        firestore.collection("exercises")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val exercisesList = mutableListOf<ExerciseData>()

                for (document in querySnapshot.documents) {
                    val exercise = document.toObject(ExerciseData::class.java)
                    exercise?.let { exercisesList.add(it) }
                }

                if (exercisesList.size >= 2) {
                    exercisesList.shuffle()

                    val filteredExercises = exercisesList.filter { exercise ->
                        val matchCount = listOf(
                            currentUser?.gender,
                            currentUser?.goal,
                            currentUser?.motivation,
                            currentUser?.pushUps,
                            currentUser?.activityLevel
                        ).count { criteria ->
                            when (criteria) {
                                currentUser?.gender -> exercise.suitableGender.contains(criteria)
                                currentUser?.goal -> exercise.suitableGoals.contains(criteria)
                                currentUser?.motivation -> exercise.suitableMotivations.contains(criteria)
                                currentUser?.pushUps -> exercise.suitablePushUps.contains(criteria)
                                currentUser?.activityLevel -> exercise.suitableActivityLevels.contains(criteria)
                                else -> false
                            }
                        }

                        matchCount >= 1
                    }

                    val selectedExercises = filteredExercises.subList(0, 5)

                    val images = arrayOf(
                        "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Fabs.jpg?alt=media&token=f82d14ea-137b-4e16-8654-ea6a630e4637",
                        "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Ffullbody.jpg?alt=media&token=90c6ffb2-b0ec-4494-a3ba-6c836e9eeffd",
                        "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Fhiit.jpg?alt=media&token=bfe4564f-7151-4826-88ce-8798dfd89595",
                        "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Frunning.jpg?alt=media&token=27e73550-3854-4520-8c1b-8cd57f5239da",
                        "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Fweight%20lift.jpg?alt=media&token=81249036-cff5-407d-9afa-152399ef0c25",
                        "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Fweights.jpg?alt=media&token=2a4810de-aeb9-4c3e-ae61-c602774ed013",
                        "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Fyoga.jpg?alt=media&token=b623e637-edb7-4433-8dcf-6abd7bd8b296"
                    )

                    images.shuffle()

                    val newDailyQuest = WorkoutData(
                        title = "Daily Quest",
                        duration = "",
                        completed = false,
                        image = images[0],
                        exercises = selectedExercises,
                        date = getCurrentFormattedDateDaily(),
                        quest = true,
                        xp = 50
                    )

                    saveDailyQuestForUser(userId, newDailyQuest) { success ->
                        if (success) {
                            callback(newDailyQuest)
                        } else {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                callback(null)
            }
    }

    fun generateNewWorkout(
        selectedType: String?,
        callback: (WorkoutData?) -> Unit
    ) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            if (selectedType != null ) {
                firestore.collection("exercises")
                    .whereEqualTo("target", selectedType)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val exercisesList = mutableListOf<ExerciseData>()

                        for (document in querySnapshot.documents) {
                            val exercise = document.toObject(ExerciseData::class.java)
                            exercise?.let { exercisesList.add(it) }
                        }

                        if (exercisesList.size >= 3) {
                            exercisesList.shuffle()
                            val selectedExercises = exercisesList.subList(0, 3)

                            val newWorkout = WorkoutData(
                                title = "$selectedType Workout",
                                duration = "",
                                completed = false,
                                image = "",
                                exercises = selectedExercises,
                                date = getCurrentFormattedDateDaily(),
                                quest = false,
                                xp = 50
                            )

                            saveWorkoutForUser(userId, newWorkout) { success ->
                                if (success) {
                                    callback(newWorkout)
                                } else {
                                    callback(null)
                                }
                            }
                        } else {
                            callback(null)
                        }
                    }
                    .addOnFailureListener { e ->
                        callback(null)
                    }
            } else {
                callback(null)
            }
        }
    }

    private fun getCurrentFormattedDateDaily(): String {
        val currentTime = Calendar.getInstance().time
        return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(currentTime)
    }

    fun getDailyQuestForToday(callback: (WorkoutData?) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            val currentDate = getCurrentFormattedDateDaily()

            firestore.collection("users")
                .document(userId)
                .collection("dailyQuests")
                .whereEqualTo("date", currentDate)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val dailyQuestDocument = querySnapshot.documents[0]
                        val dailyQuestId = dailyQuestDocument.id
                        val dailyQuest = dailyQuestDocument.toObject(WorkoutData::class.java)

                        val workoutDataWithId = dailyQuest?.copy(id = dailyQuestId)
                        callback(workoutDataWithId)
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

    fun getLastWorkouts(callback: (List<WorkoutData>) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            firestore.collection("users")
                .document(userId)
                .collection("lastWorkouts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val lastWorkoutsList = mutableListOf<WorkoutData>()

                    for (document in querySnapshot) {
                        val workout = document.toObject(WorkoutData::class.java)
                        lastWorkoutsList.add(workout)
                    }
                    callback(lastWorkoutsList)
                }
                .addOnFailureListener { exception ->
                    callback(emptyList())
                }
        }
    }

    fun getChallengesForCurrentWeek(callback: (List<ChallengeData?>) -> Unit) {
        val currentDate = Calendar.getInstance().time
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            firestore.collection("users")
                .document(userId)
                .collection("challenges")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val challengesForCurrentWeek = querySnapshot.documents
                        .filter { document ->
                            val beginDate = document.getTimestamp("begin_date")?.toDate()
                            val endDate = document.getTimestamp("end_date")?.toDate()

                            beginDate != null && endDate != null && currentDate >= beginDate && currentDate <= endDate
                        }
                        .mapNotNull { document ->
                            val challengeId = document.id
                            document.toObject(ChallengeData::class.java)?.copy(id = challengeId)
                        }

                    callback(challengesForCurrentWeek)
                }
                .addOnFailureListener {
                    callback(emptyList())
                }
        } else {
            callback(emptyList())
        }
    }
    fun updateCheckpointAndChallenge(
        challengeId: String,
        checkpointName: String,
        callback: (Boolean) -> Unit
    ) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            val challengeRef = firestore.collection("users")
                .document(userId)
                .collection("challenges")
                .document(challengeId)

            firestore.runTransaction { transaction ->
                val challengeDoc = transaction.get(challengeRef)
                var completou = false

                if (challengeDoc.exists()) {
                    val challenge = challengeDoc.toObject(ChallengeData::class.java)

                    val updatedCheckpoints = challenge?.checkpoints?.map { checkpoint ->
                        if (checkpoint?.name == checkpointName) {
                            if (!checkpoint.completed) {
                                checkpoint.completed = true
                                checkpoint.workout!!.completed = true
                                completou = true

                            }
                        }
                        checkpoint
                    }
                    if (completou) {
                        challenge?.done_checkpoints = (challenge?.done_checkpoints ?: 0) + 1
                        if (updatedCheckpoints != null) {
                            challenge?.checkpoints = updatedCheckpoints
                        }
                        if (challenge != null) {
                            if (challenge.done_checkpoints == challenge.total_checkpoints) {
                                challenge.completed = true
                                updateXP(challenge.xp) { success ->
                                    if (success) {

                                    } else {

                                    }
                                }
                            }
                        }
                        transaction.set(challengeRef, challenge!!)
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            }
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    callback(false)
                }
        } else {
            callback(false)
        }
    }


    fun updateXP(xp: Int, callback: (Boolean) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            val userRef = firestore.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userProfile = document.toObject(UserProfile::class.java)

                        userProfile?.xp_total = userProfile?.xp_total?.plus(xp) ?: xp

                        userRef.set(userProfile!!)
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener {
                                callback(false)
                            }
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }

    fun updateDailyQuest(exercises: WorkoutData, isQuest: Boolean, callback: (Boolean) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            var collection = "generatedWorkouts"
            if(isQuest){
                collection = "dailyQuests"
            }
            val dailyQuestsRef = firestore.collection("users")
                .document(userId)
                .collection(collection)

            dailyQuestsRef.whereEqualTo("date", exercises.date).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val dailyQuestDoc = querySnapshot.documents[0]
                        val daily = dailyQuestDoc.toObject(WorkoutData::class.java)
                        daily?.completed = true

                        dailyQuestDoc.reference.set(daily!!)
                            .addOnSuccessListener {

                                callback(true)
                            }
                            .addOnFailureListener {

                                callback(false)
                            }

                        val xp = exercises.xp

                        updateXP(xp) { success ->
                            if (success) {
                                callback(true)
                            } else {
                                callback(false)
                            }
                        }
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }

    fun updatePlaces(place: PlaceData, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            val userRef = firestore.collection("users")
                .document(userId)

            firestore.runTransaction { transaction ->
                val userDoc = transaction.get(userRef)

                if (userDoc.exists()) {
                    val userProfile = userDoc.toObject(UserProfile::class.java)

                    val hasPlace = userProfile?.places?.any { it.name == place.name } ?: false

                    if (!hasPlace) {

                        userProfile?.places = userProfile?.places?.plus(place) ?: listOf(place)

                        if (userProfile != null) {
                            firestore.collection("users")
                                .document(userId)
                                .set(userProfile)
                                .addOnSuccessListener {
                                    callback(true)
                                }
                                .addOnFailureListener {
                                    callback(false)
                                }
                        }
                        true
                    } else {
                        true
                    }
                } else {
                    false
                }
            }
                .addOnSuccessListener {

                    callback(true)
                }
                .addOnFailureListener { e ->

                    callback(false)
                }
        } else {

            callback(false)
        }
    }

    fun getPlace(placeName: String?, callback: (PlaceData?) -> Unit) {
        if (placeName.isNullOrEmpty()) {
            callback(null)
            return
        }

        val placesCollectionRef = firestore.collection("places")

        val query = placesCollectionRef.whereEqualTo("name", placeName).limit(1)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents[0]
                val placeData = documentSnapshot.toObject(PlaceData::class.java)
                callback(placeData)
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun updateLongestStreak(userId: String, callback: (Boolean) -> Unit) {

        val user = auth.currentUser
        if (user != null) {
            firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)
                        if (userProfile != null) {
                            val currentDate = Date();
                            val lastLoginDate = userProfile.lastLoginDate

                            if (isOneDayBefore(lastLoginDate,currentDate)) {
                                val currentStreak = userProfile.currentStreak + 1
                                val longestStreak = maxOf(currentStreak, userProfile.longestStreak)

                                val updatedUser = userProfile.copy(
                                    currentStreak = currentStreak,
                                    longestStreak = longestStreak,
                                    lastLoginDate = currentDate
                                )

                                updateCurrentUserProfile(userId, updatedUser, null) {
                                    callback(it)
                                }
                            } else {
                                val updatedUser = userProfile.copy(
                                    currentStreak = 0,
                                    lastLoginDate = currentDate
                                )

                                updateCurrentUserProfile(userId, updatedUser, null) {
                                    callback(it)
                                }
                            }
                        } else {
                            callback(false)
                        }
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }


    private fun isOneDayBefore(date1: Date?, date2: Date): Boolean {

        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) - 1
    }

    private fun checkAndCreateChallenges(userId: String, callback: (Boolean) -> Unit) {
        var currentUser: UserProfile? = null
        hasChallengesForCurrentWeek(userId) { hasChallenges ->
            if (!hasChallenges) {
                val currentDate = Calendar.getInstance().time
                val endDate = getEndDateForCurrentWeek(currentDate)

                getCurrentUser { user ->
                    if (user != null) {
                        currentUser = user

                        createNewChallenges(userId, currentDate, endDate, user)
                    } else {
                    }}

                callback(true)
            } else {
                callback(true)
            }
        }
    }

    private fun hasChallengesForCurrentWeek(userId: String, callback: (Boolean) -> Unit) {
        val currentDate = Calendar.getInstance().time

        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val hasChallengesForCurrentWeek = querySnapshot.documents.any { document ->
                    val beginDate = document.getTimestamp("begin_date")?.toDate()
                    val endDate = document.getTimestamp("end_date")?.toDate()

                    if (beginDate != null && endDate != null) {
                        currentDate >= beginDate && currentDate <= endDate
                    } else {
                        false
                    }
                }

                callback(hasChallengesForCurrentWeek)
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    private fun getEndDateForCurrentWeek(currentDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_WEEK, 6)

        return calendar.time
    }

    private fun createNewChallenges(userId: String, startDate: Date, endDate: Date, user: UserProfile?) {
        var currentUser: UserProfile? = null
        getCurrentUser { user ->
            if (user != null) {
                currentUser = user

            } else {
            }
        }
        firestore.collection("exercises")
            .get()
            .addOnSuccessListener { exercisesQuerySnapshot ->
                val exercisesList = mutableListOf<ExerciseData>()
                val placesList = mutableListOf<PlaceData>()

                for (document in exercisesQuerySnapshot.documents) {
                    val exercise = document.toObject(ExerciseData::class.java)
                    exercise?.let { exercisesList.add(it) }
                }

                firestore.collection("places")
                    .get()
                    .addOnSuccessListener { placesQuerySnapshot ->
                        for (document in placesQuerySnapshot.documents) {
                            val place = document.toObject(PlaceData::class.java)
                            place?.let { placesList.add(it) }
                        }

                        if (exercisesList.size >= 3 && placesList.size >= 3) {
                            exercisesList.shuffle()

                            val filteredExercises = exercisesList.filter { exercise ->
                                val matchCount = listOf(
                                    currentUser?.gender,
                                    currentUser?.goal,
                                    currentUser?.motivation,
                                    currentUser?.pushUps,
                                    currentUser?.activityLevel
                                ).count { criteria ->
                                    when (criteria) {
                                        currentUser?.gender -> exercise.suitableGender.contains(criteria)
                                        currentUser?.goal -> exercise.suitableGoals.contains(criteria)
                                        currentUser?.motivation -> exercise.suitableMotivations.contains(criteria)
                                        currentUser?.pushUps -> exercise.suitablePushUps.contains(criteria)
                                        currentUser?.activityLevel -> exercise.suitableActivityLevels.contains(criteria)
                                        else -> false
                                    }
                                }

                                matchCount >= 1
                            }

                            placesList.shuffle()

                            val selectedPlaces = placesList.subList(0, 3)

                            val challengeWithCheckpoints = currentUser?.sessionsOutside?.let {
                                createChallengeWithCheckpoints(
                                    "Challenge w Checkpoints",
                                    startDate,
                                    endDate,
                                    listOf(
                                        filteredExercises.subList(0, 5),
                                        filteredExercises.subList(5, 10),
                                        filteredExercises.subList(10, 15)
                                    ),
                                    selectedPlaces,
                                    it.toInt()
                                )
                            }

                            val challengeWithoutCheckpoints1 = createChallengeWithoutCheckpoints(
                                "Challenge wo Checkpoints 1",
                                startDate,
                                endDate
                            )

                            val challengeWithoutCheckpoints2 = createChallengeWithoutCheckpoints(
                                "Challenge wo Checkpoints 2",
                                startDate,
                                endDate
                            )

                            if (user != null) {
                                if (user.friends.isNotEmpty()) {
                                    for (friend in user.friends.take(3)) {
                                        val challengeWithFriend =
                                            createChallengeWithoutCheckpointsWithFriends(
                                                "Challenge with Friend",
                                                startDate,
                                                endDate,
                                                friend
                                            )
                                        saveChallengeForUser(userId, challengeWithFriend)
                                    }
                                }
                            }

                            if (challengeWithCheckpoints != null) {
                                saveChallengeForUser(userId, challengeWithCheckpoints)
                            }
                            saveChallengeForUser(userId, challengeWithoutCheckpoints1)
                            saveChallengeForUser(userId, challengeWithoutCheckpoints2)
                        } else {
                        }
                    }
                    .addOnFailureListener {
                    }
            }
            .addOnFailureListener {
            }
    }
    private fun createChallengeWithCheckpoints(
        title: String,
        startDate: Date,
        endDate: Date,
        selectedExercisesList: List<List<ExerciseData>>,
        selectedPlaces: List<PlaceData>,
        sessionsOutside: Int
    ): ChallengeData {
        val checkpointsCount = sessionsOutside.coerceAtMost(selectedExercisesList.size)

        val checkpoints = selectedExercisesList.subList(0, checkpointsCount).mapIndexed { index, selectedExercises ->
            val checkpoint = CheckpointData(
                name = "Checkpoint ${index + 1}",
                place = selectedPlaces[index],
                completed = false,
                workout = WorkoutData(
                    title = "",
                    duration = "45 mins",
                    completed = false,
                    image = "https://firebasestorage.googleapis.com/v0/b/fitquest-5d322.appspot.com/o/daily%2Ffullbody.jpg?alt=media&token=61a68882-6c5b-4357-88b1-2d5d35d4df9e",
                    exercises = selectedExercises,
                    date = getCurrentFormattedDateDaily(),
                    quest = false,
                    xp = 50
                )
            )
            checkpoint
        }

        return ChallengeData(
            title = title,
            xp = 200,
            type = "Location",
            total_checkpoints = checkpointsCount,
            done_checkpoints = 0,
            checkpoints = checkpoints,
            begin_date = startDate,
            end_date = endDate,
            completed = false,
        )
    }


    private fun createChallengeWithoutCheckpoints(
        title: String,
        startDate: Date,
        endDate: Date
    ): ChallengeData {
        return ChallengeData(
            title = title,
            xp = 100,
            type = "Steps",
            begin_date = startDate,
            end_date = endDate,
            completed = false,
            description = "Complete 40 000 steps"
        )
    }

    private fun createChallengeWithoutCheckpointsWithFriends(
        title: String,
        startDate: Date,
        endDate: Date,
        friend: UserProfile
    ): ChallengeData {
        return ChallengeData(
            title = title,
            xp = 100,
            type = "Steps",
            begin_date = startDate,
            end_date = endDate,
            completed = false,
            description = "Complete 40 000 steps",
            friend = friend,
            group = true,
        )
    }

    private fun saveChallengeForUser(userId: String, challenge: ChallengeData) {
        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .add(challenge)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }


    fun fetchUserListFromFirestoreNotFriend(currentUser: UserProfile, callback: (List<UserProfile>) -> Unit) {
        firestore.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<UserProfile>()
                for (document in querySnapshot) {
                    val userProfile = document.toObject(UserProfile::class.java)

                    if (userProfile.uniqueCode != currentUser.uniqueCode &&
                        userProfile.uniqueCode !in currentUser.friends.map { it.uniqueCode }
                    ) {
                        userList.add(userProfile)
                    }
                }
                callback(userList)
            }
            .addOnFailureListener { exception ->
                callback(emptyList())
            }
    }

    fun fetchUserListFromFirestore(currentUser: UserProfile, callback: (List<UserProfile>) -> Unit) {
        firestore.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<UserProfile>()
                for (document in querySnapshot) {
                    val userProfile = document.toObject(UserProfile::class.java)

                    if (userProfile.uniqueCode != currentUser.uniqueCode) {
                        userList.add(userProfile)
                    }
                }
                callback(userList)
            }
            .addOnFailureListener { exception ->
                callback(emptyList())
            }
    }

    fun sendFriendRequest(currentUser: UserProfile, userFriend: UserProfile, callback: (Boolean) -> Unit) {
        val updatedFriendReqs = userFriend.friend_reqs.toMutableList().apply {
            add(currentUser)
        }

        firestore.collection("users")
            .whereEqualTo("uniqueCode", userFriend.uniqueCode)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val userDocument = querySnapshot.documents[0]
                    val userId = userDocument.id

                    firestore.collection("users")
                        .document(userId)
                        .update("friend_reqs", updatedFriendReqs)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener {
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    fun acceptFriendRequest(currentUser: UserProfile, friend: UserProfile, callback: (Boolean) -> Unit) {
        val updatedCurrentUserFriends = currentUser.friends.toMutableList().apply {
            add(friend)
        }

        val updatedCurrentUserFriendReqs = currentUser.friend_reqs.toMutableList().apply {
            remove(friend)
        }

        val updatedFriendFriends = friend.friends.toMutableList().apply {
            add(currentUser)
        }

        val updatedFriendFriendReqs = friend.friend_reqs.toMutableList().apply {
            remove(currentUser)
        }

        firestore.collection("users")
            .document(currentUser.id!!)
            .update("friends", updatedCurrentUserFriends, "friend_reqs", updatedCurrentUserFriendReqs)
            .addOnSuccessListener {
                firestore.collection("users")
                    .document(friend.id!!)
                    .update("friends", updatedFriendFriends, "friend_reqs", updatedFriendFriendReqs)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun deleteFriendRequest(currentUser: UserProfile, friend: UserProfile, callback: (Boolean) -> Unit) {
        val updatedCurrentUserFriendReqs = currentUser.friend_reqs.toMutableList().apply {
            remove(friend)
        }

        val updatedFriendFriendReqs = friend.friend_reqs.toMutableList().apply {
            remove(currentUser)
        }

        firestore.collection("users")
            .document(currentUser.id!!)
            .update("friend_reqs", updatedCurrentUserFriendReqs)
            .addOnSuccessListener {
                firestore.collection("users")
                    .document(friend.id!!)
                    .update("friend_reqs", updatedFriendFriendReqs)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    fun checkUsernameAvailability(username: String, callback: (Boolean, String?) -> Unit) {
        val usersCollection = FirebaseFirestore.getInstance().collection("users")

        usersCollection.whereEqualTo("username", username)
            .get()
            .addOnCompleteListener { queryTask ->
                if (queryTask.isSuccessful) {
                    val querySnapshot = queryTask.result

                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        callback(false, "Username is already taken")
                    } else {
                        callback(true, null)
                    }
                } else {
                    callback(false, queryTask.exception?.message)
                }
            }
    }
}