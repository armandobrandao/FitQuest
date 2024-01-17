package com.example.fitquest

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

class AuthManager(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // If sign-up is successful, the signIn function will take care of creating a new DailyQuest
                    signIn(email, password) { signInSuccess, signInError ->
                        if (signInSuccess) {
                            // Callback with sign-up success
                            callback(true, null)
                        } else {
                            // Callback with sign-up failure
                            callback(false, signInError ?: "Error signing in after sign-up")
                        }
                    }
                } else {
                    // Callback with sign-up failure
                    callback(false, task.exception?.message)
                }
            }
    }


    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Check if the user already has a DailyQuest for the current day
                        hasDailyQuestForToday(user.uid) { hasDailyQuest ->
                            if (!hasDailyQuest) {
                                // If no DailyQuest exists for the current day, generate a new one
                                generateNewDailyQuest(user.uid) { newDailyQuest ->
                                    if (newDailyQuest != null) {
                                        // Handle the generated DailyQuest, for example, save it to Firestore or use it as needed
                                        // ...
                                        Log.d("New daily quest", "$newDailyQuest")

                                        // Check for existing challenges and create them if needed
                                        checkAndCreateChallenges(user.uid) { challengeCreationSuccess ->
                                            if (challengeCreationSuccess) {
                                                // Callback with sign-in success
                                                callback(true, null)
                                            } else {
                                                // Handle the case when there is an issue creating challenges
                                                callback(false, "Error creating challenges")
                                            }
                                        }
                                    } else {
                                        // Handle the case when there is an issue generating the DailyQuest
                                        callback(false, "Error generating DailyQuest")
                                    }
                                }

                                updateLongestStreak(user.uid) { updateSuccess ->
                                    if (updateSuccess) {
                                        // Callback with sign-in success
                                        callback(true, null)
                                    } else {
                                        // Callback with update failure
                                        callback(false, "Error updating longest streak")
                                    }
                                }
                            } else {
                                // If a DailyQuest already exists for the current day, proceed without generating a new one

                                // Check for existing challenges and create them if needed
//                                checkAndCreateChallenges(user.uid) { challengeCreationSuccess ->
//                                    if (challengeCreationSuccess) {
//                                        // Callback with sign-in success
//                                        callback(true, null)
//                                    } else {
//                                        // Handle the case when there is an issue creating challenges
//                                        callback(false, "Error creating challenges")
//                                    }
//                                }
                                callback(true, null)
                            }
                        }
                    } else {
                        // Handle the case when the user is null
                        callback(false, "Error retrieving user information")
                    }
                } else {
                    // Callback with sign-in failure
                    callback(false, task.exception?.message)
                }
            }
    }

    private fun hasDailyQuestForToday(userId: String, callback: (Boolean) -> Unit) {
        // Get the current date
        val currentDate = getCurrentFormattedDateDaily()

        firestore.collection("users")
            .document(userId)
            .collection("dailyQuests")
            .whereEqualTo("date", currentDate)
            .whereEqualTo("quest", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if there is a DailyQuest for the current day
                callback(!querySnapshot.isEmpty)
            }
            .addOnFailureListener {
                // Handle the failure to check for existing DailyQuests in Firestore
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
        callback: (Boolean, String?) -> Unit
    ) {
        val user = auth.currentUser
        val userProfile = UserProfile(
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
            joinDate = getCurrentFormattedDate(), // Replace this function with your date formatting logic
            longestStreak = 0,
            places = 0,
            friends = emptyList(),
            achievements = emptyList(),
            progress = 0,
            uniqueCode = generateUniqueCode(username),
            lastLoginDate = Calendar.getInstance().time,
            currentStreak = 0,
        )

        saveUserProfile(user?.uid, userProfile, callback)
    }


    private fun getCurrentFormattedDate(): String {
        // Replace this with your date formatting logic
        val currentTime = Calendar.getInstance().time
        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentTime)
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
                                achievements = userProfile.achievements,
                                progress = userProfile.progress,
                                uniqueCode = userProfile.uniqueCode,
                                lastLoginDate = userProfile.lastLoginDate,
                                currentStreak = userProfile.currentStreak,
                            )
                            updateCurrentUserProfile(user.uid, currentUser) {
                                if (it) {
                                    callback(currentUser)
                                } else {
                                    callback(null)
                                }
                            }
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

    private fun updateCurrentUserProfile(userId: String, userProfile: UserProfile, callback: (Boolean) -> Unit) {
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

    fun signOut() {
        auth.signOut()
    }

    fun generateUniqueCode(username: String): String {
        val random = SecureRandom()
        val salt = random.nextInt().absoluteValue.toString()

        // Concatenate username and randomness
        val combinedData = "$username$salt"

        // Create a hash of the combined data
        val hashedCode = hashString(combinedData)

        // Return a portion of the hash as the unique code
        return hashedCode.substring(0, 8) // You can adjust the length as needed
    }

    fun hashString(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.fold(StringBuilder()) { str, byte -> str.append(byte.toString(16).padStart(2, '0')) }.toString()
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
        Log.d("AuthManager", "Entra no saveDailyQuestForUser")
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

    fun saveExercise(exercise: ExerciseData, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("exercises")
                .add(exercise)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            // Handle the case where the user is not authenticated
            callback(false)
        }
    }

    fun savePlace(place: PlaceData, callback: (Boolean) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            firestore.collection("places")
                .add(place)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            // Handle the case where the user is not authenticated
            callback(false)
        }
    }

    fun generateNewDailyQuest(userId: String, callback: (WorkoutData?) -> Unit) {
        Log.d("AuthManager", "Entra no generateNewDailyQuest")

        // Assuming you have a collection named "exercises" in your Firestore
        firestore.collection("exercises")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val exercisesList = mutableListOf<ExerciseData>()

                // Convert documents to Exercise objects
                for (document in querySnapshot.documents) {
                    val exercise = document.toObject(ExerciseData::class.java)
                    exercise?.let { exercisesList.add(it) }
                }

                // Ensure that there are at least 2 exercises in the collection
                if (exercisesList.size >= 2) {
                    // Shuffle the list and select the first two exercises
                    exercisesList.shuffle()
                    val selectedExercises = exercisesList.subList(0, 2)
                    Log.d("AuthManager", "selectedExercises, $selectedExercises")

                    // Create a new DailyQuest with the selected exercises
                    val newDailyQuest = WorkoutData(
                        title = "Generated Daily Quest",
                        duration = "45 mins", // You can adjust this as needed
                        isCompleted = false,
                        image = R.drawable.pilates, // Replace with the appropriate image
                        exercises = selectedExercises,
                        date = getCurrentFormattedDateDaily(),
                        isQuest = true,
                        xp = 50
                    )
                    // Save the DailyQuest to the user's document
                    saveDailyQuestForUser(userId, newDailyQuest) { success ->
                        if (success) {
                            callback(newDailyQuest)
                        } else {
                            // Handle the case when there is an issue saving the DailyQuest
                            callback(null)
                        }
                    }
                } else {
                    // Handle the case when there are not enough exercises in the collection
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                // Handle the failure to retrieve exercises from Firestore
                callback(null)
            }
    }
    private fun getCurrentFormattedDateDaily(): String {
        // Replace this with your date formatting logic
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
                        val dailyQuest = querySnapshot.documents[0].toObject(WorkoutData::class.java)
                        callback(dailyQuest)
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

    fun getChallengesForCurrentWeek(callback: (List<ChallengeData?>) -> Unit) {
        val currentDate = Calendar.getInstance().time
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid

            // Get challenges for the current week
            firestore.collection("users")
                .document(userId)
                .collection("challenges")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // Filter challenges based on the current week
                    val challengesForCurrentWeek = querySnapshot.documents
                        .filter { document ->
                            val beginDate = document.getTimestamp("begin_date")?.toDate()
                            val endDate = document.getTimestamp("end_date")?.toDate()

                            beginDate != null && endDate != null && currentDate >= beginDate && currentDate <= endDate
                        }
                        .mapNotNull { document ->
                            document.toObject(ChallengeData::class.java)
                        }

                    callback(challengesForCurrentWeek)
                }
                .addOnFailureListener {
                    // Handle the failure to retrieve challenges from Firestore
                    callback(emptyList())
                }
        }else{
            callback(emptyList())
        }
    }

    //Sempre que faz log in faz check da streak!
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
                            Log.d("currentDate", "$currentDate")
                            val lastLoginDate = userProfile.lastLoginDate
                            Log.d("lastLoginDate", "$lastLoginDate")

                            //Se o lastLoginDate for 1 dia antes da currentdate
                            if (isOneDayBefore(lastLoginDate,currentDate)) {
                                // User logged in today, update the streak
                                val currentStreak = userProfile.currentStreak + 1
                                val longestStreak = maxOf(currentStreak, userProfile.longestStreak)

                                val updatedUser = userProfile.copy(
                                    currentStreak = currentStreak,
                                    longestStreak = longestStreak,
                                    lastLoginDate = currentDate
                                )

                                updateCurrentUserProfile(userId, updatedUser) {
                                    callback(it)
                                }
                            } else {
                                // User didn't log in today, reset the streak
                                val updatedUser = userProfile.copy(
                                    currentStreak = 0,
                                    lastLoginDate = currentDate
                                )

                                updateCurrentUserProfile(userId, updatedUser) {
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

        // Check if date1 is one day before date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) - 1
    }

    private fun checkAndCreateChallenges(userId: String, callback: (Boolean) -> Unit) {
        // Check if the user already has challenges for the current week
        hasChallengesForCurrentWeek(userId) { hasChallenges ->
            if (!hasChallenges) {
                // If no challenges for the current week, create new challenges
                val currentDate = Calendar.getInstance().time
                val endDate = getEndDateForCurrentWeek(currentDate)

                createNewChallenges(userId, currentDate, endDate)

                // Callback with success
                callback(true)
            } else {
                // Challenges already exist for the current week, no need to create new ones
                callback(true)
            }
        }
    }

    private fun hasChallengesForCurrentWeek(userId: String, callback: (Boolean) -> Unit) {
        val currentDate = Calendar.getInstance().time

        // Check if there are challenges for the current week
        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Iterate through the challenges and check if any is for the current week
                val hasChallengesForCurrentWeek = querySnapshot.documents.any { document ->
                    val beginDate = document.getTimestamp("begin_date")?.toDate()
                    val endDate = document.getTimestamp("end_date")?.toDate()

                    if (beginDate != null && endDate != null) {
                        currentDate >= beginDate && currentDate <= endDate
                    } else {
                        // Handle the case where begin_date or end_date is null
                        false
                    }
                }

                callback(hasChallengesForCurrentWeek)
            }
            .addOnFailureListener {
                // Handle the failure to check for existing challenges in Firestore
                callback(false)
            }
    }


    private fun getEndDateForCurrentWeek(currentDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_WEEK, 6) // Add 6 days to get the end of the week

        return calendar.time
    }

    private fun createNewChallenges(userId: String, startDate: Date, endDate: Date) {
        // Fetch exercises and places from the database
        firestore.collection("exercises")
            .get()
            .addOnSuccessListener { exercisesQuerySnapshot ->
                val exercisesList = mutableListOf<ExerciseData>()
                val placesList = mutableListOf<PlaceData>()

                // Convert documents to Exercise objects
                for (document in exercisesQuerySnapshot.documents) {
                    val exercise = document.toObject(ExerciseData::class.java)
                    exercise?.let { exercisesList.add(it) }
                }

                // Fetch places from the database
                firestore.collection("places")
                    .get()
                    .addOnSuccessListener { placesQuerySnapshot ->
                        // Convert documents to Place objects
                        for (document in placesQuerySnapshot.documents) {
                            val place = document.toObject(PlaceData::class.java)
                            place?.let { placesList.add(it) }
                        }

                        // Ensure that there are at least 3 exercises and 3 places in the collections
                        if (exercisesList.size >= 3 && placesList.size >= 3) {
                            // Shuffle the lists
                            exercisesList.shuffle()
                            placesList.shuffle()

                            // Select the first three exercises and places
                            val selectedExercises = exercisesList.subList(0, 3)
                            val selectedPlaces = placesList.subList(0, 3)

                            // Create challenges with checkpoints
                            val challengeWithCheckpoints = createChallengeWithCheckpoints(
                                "Challenge w Checkpoints",
                                startDate,
                                endDate,
                                selectedExercises,
                                selectedPlaces
                            )

                            // Create challenges without checkpoints
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

                            // Save challenges to the user's document
                            saveChallengeForUser(userId, challengeWithCheckpoints)
                            saveChallengeForUser(userId, challengeWithoutCheckpoints1)
                            saveChallengeForUser(userId, challengeWithoutCheckpoints2)
                        } else {
                            // Handle the case when there are not enough exercises or places in the collections
                        }
                    }
                    .addOnFailureListener {
                        // Handle the failure to retrieve places from Firestore
                    }
            }
            .addOnFailureListener {
                // Handle the failure to retrieve exercises from Firestore
            }
    }
    private fun createChallengeWithCheckpoints(
        title: String,
        startDate: Date,
        endDate: Date,
        selectedExercises: List<ExerciseData>,
        selectedPlaces: List<PlaceData>
    ): ChallengeData {
        // Create 3 checkpoints with associated exercises and places
        val checkpoints = (1..3).map { checkpointNumber ->
            val checkpoint = CheckpointData(
                name = "Checkpoint $checkpointNumber",
                place = selectedPlaces[checkpointNumber - 1],
                isCompleted = false,
                workout = WorkoutData(
                    title = "",
                    duration = "45 mins", // You can adjust this as needed
                    isCompleted = false,
                    image = R.drawable.pilates, // Replace with the appropriate image
                    exercises = selectedExercises,
                    date = getCurrentFormattedDateDaily(),
                    isQuest = false, // Set to false for challenges
                    xp = 50
                )
            )
            checkpoint
        }

        return ChallengeData(
            title = title,
            xp = 200,
            type = "Location",
            total_checkpoints = 3,
            done_checkpoints = 0,
            checkpoints = checkpoints,
            begin_date = startDate,
            end_date = endDate,
            isCompleted = false,
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
            isCompleted = false,
            description = "Complete 40 000 steps"
        )
    }

    private fun saveChallengeForUser(userId: String, challenge: ChallengeData) {
        firestore.collection("users")
            .document(userId)
            .collection("challenges")
            .add(challenge)
            .addOnSuccessListener {
                // Challenge saved successfully
            }
            .addOnFailureListener {
                // Failed to save challenge
            }
    }

    private fun getEndDate(startDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_YEAR, 7) // Add 7 days for a week-long challenge
        return calendar.time
    }


}