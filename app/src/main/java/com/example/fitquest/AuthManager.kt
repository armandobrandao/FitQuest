package com.example.fitquest

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class AuthManager(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var renewalTask: ScheduledFuture<*>? = null


    init {
        // Verificar se há um token armazenado localmente e tentar fazer o login automaticamente
        val localAccessToken = getLocalAccessToken()
        if (localAccessToken != null) {
            // Existe um token armazenado localmente, tentar fazer o login
            signInWithToken(localAccessToken)
        } else {
            // Não existe um token localmente, iniciar a renovação automática
            //startTokenRenewal()
        }
    }

    private fun signInWithToken(token: String) {
        // Autenticar com o token diretamente (sem necessidade de email/senha)
        Log.d("AuthManager", "---------------- TESTE DE TOKEN --------------")
        Log.d("AuthManager", token)
        auth.signInWithCustomToken(token)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido com o token
                    // Agora, você pode prosseguir com qualquer lógica adicional
                    Log.d("AuthManager", "Login automático bem-sucedido com o token")
                } else {
                    // Tratar falha no login com o token
                    Log.e("AuthManager", "Falha no login automático com o token: ${task.exception?.message}")
                }
            }
    }

    //fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
    //    auth.createUserWithEmailAndPassword(email, password)
    //        .addOnCompleteListener(activity) { task ->
    //            if (task.isSuccessful) {
    //                // If sign-up is successful, the signIn function will take care of creating a new DailyQuest
    //                signIn(email, password) { signInSuccess, signInError ->
    //                    if (signInSuccess) {
    //                        // Callback with sign-up success
    //                        callback(true, null)
    //                    } else {
    //                        // Callback with sign-up failure
    //                        callback(false, signInError ?: "Error signing in after sign-up")
    //                    }
    //                }
    //            } else {
    //                // Callback with sign-up failure
    //                callback(false, task.exception?.message)
    //            }
    //        }
    //}



    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        // Check if the email is already in use using Firestore
        Log.d("AuthManager", "Entra no signUp")
        val usersCollection = FirebaseFirestore.getInstance().collection("users")
        Log.d("AuthManager", "usersCollection: $usersCollection")

        usersCollection.whereEqualTo("e-mail", email)
            .get()
            .addOnCompleteListener { queryTask ->
                if (queryTask.isSuccessful) {
                    val querySnapshot = queryTask.result
                    Log.d("querySnapshot", "$querySnapshot")
                    if (querySnapshot != null && !querySnapshot.isEmpty) {
                        // Email is already in use in Firestore, callback with failure
                        callback(false, "The email address is already in use by another account.")
                    } else {
                        // Check password length
                        if (password.length >= 6) {
                            // Password has more than 6 characters, proceed with Firebase Authentication
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(activity) { task ->
                                    Log.d("AuthManager", "entra na task do createUserWithEmailAndPassword")
                                    if (task.isSuccessful) {
                                        Log.d("AuthManager", "diz que a task é successful")
                                        signIn(email, password) { signInSuccess, signInError ->
                                            if (signInSuccess) {
                                                Log.d("ENTROU", "ENTROU")
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
                            // Password is too short, callback with failure
                            callback(false, "Your password needs to be at least 6 characters long")
                        }
                    }
                } else {
                    // Error occurred while checking email existence in Firestore, callback with failure
                    callback(false, queryTask.exception?.message)
                }
            }
    }

    private fun renewAccessToken(callback: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        user?.getIdToken(false)
            ?.addOnCompleteListener { idTokenTask ->
                if (idTokenTask.isSuccessful) {
                    // Novo token de acesso obtido com sucesso
                    val newAccessToken = idTokenTask.result?.token
                    // Salvar o novo token localmente
                    saveAccessTokenLocally(newAccessToken ?: "")
                    // Callback com o novo token de acesso
                    callback(true, newAccessToken)
                } else {
                    // Tratar falha na obtenção do novo token de acesso
                    callback(false, "Erro ao renovar o token de acesso")
                }
            }
    }

    // Renovação automatica do token
    private fun scheduleTokenRenewal() {
        renewalTask = executor.scheduleAtFixedRate({
            // Renovar o token a cada 24 horas
            renewAccessToken { _, _ ->
            }
        }, 0, 24, TimeUnit.HOURS)
    }

    private fun saveAccessTokenLocally(token: String) {
        // Salvar o token de acesso localmente usando SharedPreferences
        val sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("accessToken", token)
        editor.apply()
    }

    private fun getLocalAccessToken(): String? {
        // Obter o token de acesso armazenado localmente usando SharedPreferences
        val sharedPreferences = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("accessToken", null)
    }

    fun startTokenRenewal() {
        if (renewalTask == null || renewalTask?.isCancelled == true) {
            scheduleTokenRenewal()
        }
    }

    fun stopTokenRenewal() {
        renewalTask?.cancel(true)
    }


    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        Log.d("AuthManager", "entra no SignIn")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("AuthManager", "----- USER -----")
                    Log.d("AuthManager", "fas")

                    if (user != null) {
                        // Check if the user already has a DailyQuest for the current day
                        hasDailyQuestForToday(user.uid) { hasDailyQuest ->
                            if (!hasDailyQuest) {
                                Log.d("AuthManager", "antes da zona que meti em comentário")
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
                                Log.d("AuthManager", "passa a zona que meti em comentário")

                                updateLongestStreak(user.uid) { updateSuccess ->
                                    if (updateSuccess) {
                                        // Callback with sign-in success
                                        Log.d("AuthManager", "updateLongestStreak em updateSuccess")

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
                         renewAccessToken { renewSuccess, renewError ->
                            if (renewSuccess) {
                                Log.d("AuthManager", "------ TESTE1 -------")
                                // Callback com sucesso na renovação do token de acesso
                                callback(true, null)
                            } else {
                                Log.d("AuthManager", "------ TESTE2 -------")
                                // Callback com falha na renovação do token de acesso
                                callback(false, renewError ?: "Erro durante a renovação do token de acesso")
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
        Log.d("AuthManager", "entra no hasDailyQuestForToday")
        firestore.collection("users")
            .document(userId)
            .collection("dailyQuests")
            .whereEqualTo("date", currentDate)
            .whereEqualTo("quest", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Check if there is a DailyQuest for the current day
                Log.d("AuthManager", "entra no hasDailyQuestForToday addOnSuccessListener")
                callback(!querySnapshot.isEmpty)
            }
            .addOnFailureListener {
                // Handle the failure to check for existing DailyQuests in Firestore
                Log.d("AuthManager", "entra no hasDailyQuestForToday addOnFailureListener")

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
        Log.d("AuthManager", "user.uid")
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
            joinDate = joinDate, // Replace this function with your date formatting logic
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
    }


//    private fun getCurrentFormattedDate(): String {
//        // Replace this with your date formatting logic
//        val currentTime = Calendar.getInstance().time
//        return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentTime)
//    }

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
//        Log.d("AuthManager", "Entra no getCurrentUser")
        val user = auth.currentUser
//        Log.d("AuthManager", "user: $user")
        if (user != null) {
            firestore.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userProfile = documentSnapshot.toObject(UserProfile::class.java)
//                        Log.d("AuthManager", "userProfile: $userProfile")
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
//                            Log.d("AuthManager", "userProfile: $userProfile")
                            callback(currentUser)
//                            updateCurrentUserProfile(user.uid, currentUser, null) {
//                                if (it) {
//                                    callback(currentUser)
//                                } else {
//                                    callback(null)
//                                }
//                            }
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
        imageUri: Uri?, // Pass the image URI to the function
        callback: (Boolean) -> Unit
    ) {
        Log.d("AuthManager", "Entra no updateCurrentUserProfile")
        // Step 1: Upload the image to Firebase Storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("profile_images/${UUID.randomUUID()}") // Unique path for each image
        if(imageUri != null){
            val uploadTask = imageRef.putFile(imageUri)
//            Log.d("AuthManager", "uploadTask: $uploadTask")
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                Log.d("AuthManager", "Deve ter passado")
                // Continue with the task to get the download URL
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Step 2: Get the download URL and save it in UserProfile
                    val downloadUri = task.result
                    Log.d("AuthManager", "downloadUri: $downloadUri")
                    userProfile.profileImageUrl = downloadUri.toString()

                    // Step 3: Save the updated UserProfile in Firestore
                    firestore.collection("users")
                        .document(userId)
                        .set(userProfile)
                        .addOnSuccessListener {
                            Log.d("AuthManager", "entra na firestore.collection certa")
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
            Log.d("AuthManager", "entra no else do updateCurrentUserProfile")
            // If no image is selected, directly save the UserProfile in Firestore
            firestore.collection("users")
                .document(userId)
                .set(userProfile)
                .addOnSuccessListener {
                    Log.d("AuthManager", "entra no callback true do updateCurrentUserProfile")
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

                    // Save the reference to the user's subcollection dailyQuests document
                    savePhotoReferenceToDailyQuest(isQuest, userId, downloadUri)

                    // Return the download URI
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

                    // Save the reference to the user's subcollection dailyQuests document
//                    savePhotoReferenceToDailyQuest(userId, downloadUri)
                    savePhotoReferenceToPlacesAndCheckpoint(userId, downloadUri, checkpointName, challengeId, placeName)

                    // Return the download URI
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
        if(isQuest){
            collection = "dailyQuests"
        }

        Log.d("AuthManager", "coollection, $collection")
        Log.d("AuthManager", "isQuest, $isQuest")

        // Query the dailyQuests collection to get the document for today
        firestore.collection("users")
            .document(userId)
            .collection(collection)
            .whereEqualTo("date", currentDate)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val dailyQuestDocument = querySnapshot.documents[0]
                    Log.d("AuthManager", "dailyQuestDocument, $dailyQuestDocument")

                    // Update the dailyQuest document with the photo reference
                    dailyQuestDocument.reference
                        .update("post_photo", photoUri.toString())
                        .addOnSuccessListener {
                            // Successfully updated the dailyQuest document with the photo reference
                            Log.d("AuthManager", "Entra no success")
                        }
                        .addOnFailureListener {
                            // Handle the failure to update the dailyQuest document
                        }
                } else {
                    // Handle the case where there is no dailyQuest document for today
                }
            }
            .addOnFailureListener {
                // Handle the failure to query the dailyQuests collection
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
            // Save the photo reference to the Places collection
            savePhotoReferenceToPlaces(placeName, photoUri.toString())

            // Save the photo reference to the Challenge's Checkpoint
            savePhotoReferenceToCheckpoint(userId, challengeId, checkpointName, photoUri.toString())
        }
    }

    private fun savePhotoReferenceToPlaces(placeName: String, photoUrl: String) {
        val placesCollectionRef = firestore.collection("places")

        // Query for documents where the "name" field is equal to placeName
        val query = placesCollectionRef.whereEqualTo("name", placeName)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Assuming there is only one document with the specified placeName
                val documentSnapshot = querySnapshot.documents[0]
                val placeData = documentSnapshot.toObject(PlaceData::class.java)
                val updatedPhotos = placeData?.photos?.toMutableList() ?: mutableListOf()
                updatedPhotos.add(photoUrl)

                // Update the photos list in the PlaceData document
                documentSnapshot.reference.update("photos", updatedPhotos)
                    .addOnSuccessListener {
                        // Successfully updated the photos list in the PlaceData document
                    }
                    .addOnFailureListener {
                        // Handle the failure to update the photos list
                    }
            } else {
                // Handle the case where no document with the specified placeName is found
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

                // Update the checkpoints list in the ChallengeData document
                challengesCollectionRef.update("checkpoints", updatedCheckpoints)
                    .addOnSuccessListener {
                        // Successfully updated the checkpoints list in the ChallengeData document
                    }
                    .addOnFailureListener {
                        // Handle the failure to update the checkpoints list
                    }
            } else {
                // Handle the case where the ChallengeData document does not exist
            }
        }
    }


    fun signOut() {
        auth.signOut()
    }

//    fun generateUniqueCode(username: String): String {
//        val random = SecureRandom()
//        val salt = random.nextInt().absoluteValue.toString()
//
//        // Concatenate username and randomness
//        val combinedData = "$username$salt"
//
//        // Create a hash of the combined data
//        val hashedCode = hashString(combinedData)
//
//        val sanitizedHashedCode = hashedCode.replace("-", "")
//
//        val part1 = sanitizedHashedCode.substring(0, 2)
//        val part2 = sanitizedHashedCode.substring(2, 4)
//        val part3 = sanitizedHashedCode.substring(4, 6)
//
//        return "$part1-$part2-$part3"
//    }
//
//    fun hashString(input: String): String {
//        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
//        return bytes.fold(StringBuilder()) { str, byte -> str.append(byte.toString(16).padStart(2, '0')) }.toString()
//    }

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
    fun saveWorkoutForUser(userId: String, newWorkout: WorkoutData, callback: (Boolean) -> Unit) {
        Log.d("AuthManager", "Entra no saveWorkoutForUser")
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
                        completed = false,
                        image = "https://firebasestorage.googleapis.com/v0/b/fitquest2-2d61f.appspot.com/o/daily%2Fyoga.jpg?alt=media&token=b623e637-edb7-4433-8dcf-6abd7bd8b296", // Replace with the appropriate image
                        exercises = selectedExercises,
                        date = getCurrentFormattedDateDaily(),
                        quest = true,
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

    fun generateNewWorkout(
        selectedType: String?,
        selectedDuration: String?,
        callback: (WorkoutData?) -> Unit
    ) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            if (selectedType != null && selectedDuration != null) {
                // Assuming you have a collection named "exercises" in your Firestore
                firestore.collection("exercises")
//                    .whereEqualTo("target", selectedType) //TODO retirar isto de comentário
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val exercisesList = mutableListOf<ExerciseData>()

                        // Convert documents to Exercise objects
                        for (document in querySnapshot.documents) {
                            val exercise = document.toObject(ExerciseData::class.java)
                            exercise?.let { exercisesList.add(it) }
                        }

                        // Ensure that there are at least 3 exercises in the collection
                        if (exercisesList.size >= 3) {
                            // Shuffle the list and select the first three exercises
                            exercisesList.shuffle()
                            val selectedExercises = exercisesList.subList(0, 3)
                            Log.d("AuthManager", "selectedExercises: $selectedExercises")

                            // Create a new WorkoutData with the selected exercises
                            val newWorkout = WorkoutData(
                                title = "$selectedType Workout",
                                duration = selectedDuration,
                                completed = false,
                                image = "", // Replace with the appropriate image URL
                                exercises = selectedExercises,
                                date = getCurrentFormattedDateDaily(),
                                quest = false, // Adjust as needed
                                xp = 50 // Adjust as needed
                            )

                            // Save the WorkoutData to the user's document
                            saveWorkoutForUser(userId, newWorkout) { success ->
                                if (success) {
                                    callback(newWorkout)
                                } else {
                                    // Handle the case when there is an issue saving the WorkoutData
                                    callback(null)
                                }
                            }
                        } else {
                            // Handle the case when there are not enough exercises for the selected type
                            callback(null)
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle the failure to retrieve exercises from Firestore
                        callback(null)
                    }
            } else {
                // Handle the case when required inputs are missing
                callback(null)
            }
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
                        val dailyQuestDocument = querySnapshot.documents[0]
                        val dailyQuestId = dailyQuestDocument.id
                        val dailyQuest = dailyQuestDocument.toObject(WorkoutData::class.java)

                        // Create a new WorkoutData object with the dailyQuest's ID
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

    fun getGeneratedWorkoutForToday(callback: (WorkoutData?) -> Unit) {
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            val currentDate = getCurrentFormattedDateDaily()

            firestore.collection("users")
                .document(userId)
                .collection("generatedWorkouts")
                .whereEqualTo("date", currentDate)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val generatedWorkoutDocument = querySnapshot.documents[0]
                        val generatedWorkoutId = generatedWorkoutDocument.id
                        val generatedWorkout = generatedWorkoutDocument.toObject(WorkoutData::class.java)

                        // Create a new WorkoutData object with the dailyQuest's ID
                        val workoutDataWithId = generatedWorkout?.copy(id = generatedWorkoutId)
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
                            // Retrieve the challenge ID
                            val challengeId = document.id
                            // Convert the document to ChallengeData and include the ID
                            document.toObject(ChallengeData::class.java)?.copy(id = challengeId)
                        }

                    callback(challengesForCurrentWeek)
                }
                .addOnFailureListener {
                    // Handle the failure to retrieve challenges from Firestore
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

            // Get the reference to the user's challenge document
            val challengeRef = firestore.collection("users")
                .document(userId)
                .collection("challenges")
                .document(challengeId)

            // Update the checkpoint and challenge in a transaction
            firestore.runTransaction { transaction ->
                val challengeDoc = transaction.get(challengeRef)
                var completou = false

                // Check if the challenge document exists
                if (challengeDoc.exists()) {
                    val challenge = challengeDoc.toObject(ChallengeData::class.java)

                    // Find the checkpoint in the challenge
                    val updatedCheckpoints = challenge?.checkpoints?.map { checkpoint ->
                        if (checkpoint?.name == checkpointName) {
                            if(!checkpoint.completed){
                                checkpoint.completed = true
                                checkpoint.workout!!.completed = true
                                completou = true
                            }
                        }
                        checkpoint
                    }
                    if(completou) {
                        // Update the challenge with the modified checkpoints
                        challenge?.done_checkpoints = (challenge?.done_checkpoints ?: 0) + 1
                        if (updatedCheckpoints != null) {
                            challenge?.checkpoints = updatedCheckpoints
                        }
                        if (challenge != null) {
                            if(challenge.done_checkpoints == challenge.total_checkpoints){
                                challenge.completed = true
                                Log.d("AuthManager","Entra no if para tornar challege completed")
                                //chamar funcao e passar-lhe o xp para aumentar o xp_total
                                updateXP(challenge.xp) { success ->
                                    if (success) {
                                        Log.d("AuthManager","Entra no sucess do updateXP")
                                    } else {

                                    }
                                }
                            }
                        }
                        // Save the updated challenge back to Firestore
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
                    // Update successful
                    callback(true)
                }
                .addOnFailureListener { e ->
                    // Update failed
                    callback(false)
                }
        } else {
            // User is not signed in
            callback(false)
        }
    }

    fun updateXP(xp: Int, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
        Log.d("AuthManager","Entra no updateXP")

        if (user != null) {
            val userId = user.uid

            // Get the reference to the user's document
            val userRef = firestore.collection("users").document(userId)
            Log.d("AuthManager","userRef: $userRef")

            // Fetch the current user profile
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userProfile = document.toObject(UserProfile::class.java)
                        Log.d("AuthManager","userProfile: $userProfile")

                        // Update xp_total field
                        userProfile?.xp_total = userProfile?.xp_total?.plus(xp) ?: xp
                        Log.d("AuthManager","userProfile 2: $userProfile")

                        // Save the updated user profile back to Firestore
                        userRef.set(userProfile!!)
                            .addOnSuccessListener {
                                // Update successful
                                callback(true)
                            }
                            .addOnFailureListener {
                                // Update failed
                                callback(false)
                            }
                    } else {
                        // Document does not exist
                        callback(false)
                    }
                }
                .addOnFailureListener {
                    // Fetching the document failed
                    callback(false)
                }
        } else {
            // User is not signed in
            callback(false)
        }
    }

    fun updateDailyQuest(exercises: WorkoutData, isQuest: Boolean, callback: (Boolean) -> Unit) {
//        Log.d("AuthManager", "Entering updateDailyQuest")
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            var collection = "generatedWorkouts"
            if(isQuest){
                collection = "dailyQuests"
            }
            // Get the reference to the user's dailyQuests collection
            val dailyQuestsRef = firestore.collection("users")
                .document(userId)
                .collection(collection)
            Log.d("AuthManager", "dailyQuestsRef: $dailyQuestsRef")

            // Query the dailyQuests subcollection to find the document with matching date
            dailyQuestsRef.whereEqualTo("date", exercises.date).get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // If there is a matching document, update its isCompleted field to true
                        val dailyQuestDoc = querySnapshot.documents[0]
                        Log.d("AuthManager", "dailyQuestDoc: $dailyQuestDoc")

                        val daily = dailyQuestDoc.toObject(WorkoutData::class.java)
                        Log.d("AuthManager", "daily: $daily")
                        // Update isCompleted field
                        daily?.completed = true

                        // Save the updated document back to Firestore
                        dailyQuestDoc.reference.set(daily!!)
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener {
                                // Update failed
                                callback(false)
                            }

                        val xp = exercises.xp

                        // Update XP using the existing updateXP function
                        updateXP(xp) { success ->
                            if (success) {
                                Log.d("AuthManager", "Entra no success do updateXP")
                                callback(true)
                            } else {
                                callback(false)
                            }
                        }
                    } else {
                        // No matching document found
                        callback(false)
                    }
                }
                .addOnFailureListener {
                    // Query failed
                    callback(false)
                }
        } else {
            // User is not signed in
            callback(false)
        }
    }

    fun updatePlaces(place: PlaceData, callback: (Boolean) -> Unit) {
        val user = auth.currentUser
//        Log.d("AuthManager","entra na updatePlaces")
        if (user != null) {
            val userId = user.uid

            // Get the reference to the user's document
            val userRef = firestore.collection("users")
                .document(userId)

            // Update the places in a transaction
            firestore.runTransaction { transaction ->
                val userDoc = transaction.get(userRef)

                // Check if the user document exists
                if (userDoc.exists()) {
                    val userProfile = userDoc.toObject(UserProfile::class.java)
//                    Log.d("AuthManager","userProfile: $userProfile")
                    // Check if the place is already in the user's places
                    val hasPlace = userProfile?.places?.any { it.name == place.name } ?: false
//                    Log.d("AuthManager","hasPlace: $hasPlace")

                    if (!hasPlace) {
                        // Add the new place to the list
                        userProfile?.places = userProfile?.places?.plus(place) ?: listOf(place)
//                        Log.d("AuthManager","userProfile 2: $userProfile")
                        // Save the updated user profile back to Firestore
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
                        // Place already exists, no need to update
                        true
                    }
                } else {
                    // User document not found
                    false
                }
            }
                .addOnSuccessListener {
                    // Update successful
                    callback(true)
                }
                .addOnFailureListener { e ->
                    // Update failed
                    callback(false)
                }
        } else {
            // User is not signed in
            callback(false)
        }
    }

    fun getPlace(placeName: String?, callback: (PlaceData?) -> Unit) {
        if (placeName.isNullOrEmpty()) {
            callback(null)
            return
        }

        val placesCollectionRef = firestore.collection("places")

        // Query for documents where the "name" field is equal to placeName
        val query = placesCollectionRef.whereEqualTo("name", placeName).limit(1)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                // Assuming there is only one document with the specified placeName
                val documentSnapshot = querySnapshot.documents[0]
                val placeData = documentSnapshot.toObject(PlaceData::class.java)
                callback(placeData)
            } else {
                // Handle the case where no document with the specified placeName is found
                callback(null)
            }
        }.addOnFailureListener {
            // Handle any failure in fetching the place data
            callback(null)
        }
    }



    //Sempre que faz log in faz check da streak!
    fun updateLongestStreak(userId: String, callback: (Boolean) -> Unit) {
        Log.d("AuthManager", "entra no updateLongestStreak")

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

                                updateCurrentUserProfile(userId, updatedUser, null) {
                                    callback(it)
                                }
                            } else {
                                // User didn't log in today, reset the streak
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

                            // Select the first three places
                            val selectedPlaces = placesList.subList(0, 3)

                            // Create challenges with checkpoints
                            val challengeWithCheckpoints = createChallengeWithCheckpoints(
                                "Challenge w Checkpoints",
                                startDate,
                                endDate,
                                listOf(
                                    exercisesList.subList(0, 3),
                                    exercisesList.subList(3, 6),
                                    exercisesList.subList(6, 9)
                                ),
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
        selectedExercisesList: List<List<ExerciseData>>, // List of lists of exercises
        selectedPlaces: List<PlaceData>
    ): ChallengeData {
        // Create checkpoints with associated exercises and places
        val checkpoints = selectedExercisesList.mapIndexed { index, selectedExercises ->
            val checkpoint = CheckpointData(
                name = "Checkpoint ${index + 1}",
                place = selectedPlaces[index],
                completed = false,
                workout = WorkoutData(
                    title = "",
                    duration = "45 mins", // You can adjust this as needed
                    completed = false,
                    image = "https://firebasestorage.googleapis.com/v0/b/fitquest-5d322.appspot.com/o/daily%2Ffullbody.jpg?alt=media&token=61a68882-6c5b-4357-88b1-2d5d35d4df9e", // Replace with the appropriate image
                    exercises = selectedExercises,
                    date = getCurrentFormattedDateDaily(),
                    quest = false, // Set to false for challenges
                    xp = 50
                )
            )
            checkpoint
        }

        return ChallengeData(
            title = title,
            xp = 200,
            type = "Location",
            total_checkpoints = selectedExercisesList.size,
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

    fun fetchUserListFromFirestore(currentUser: UserProfile, callback: (List<UserProfile>) -> Unit) {
        // Replace "users" with the actual collection name in your Firestore
        firestore.collection("users").get()
            .addOnSuccessListener { querySnapshot ->
                val userList = mutableListOf<UserProfile>()
                for (document in querySnapshot) {
                    // Assuming you have a data class or model class named UserProfile to map the document data
                    val userProfile = document.toObject(UserProfile::class.java)

                    // Check if the user is not the current user and their uniqueCode is not in currentUser's friends
                    if (userProfile.uniqueCode != currentUser.uniqueCode &&
                        userProfile.uniqueCode !in currentUser.friends.map { it.uniqueCode }
                    ) {
                        userList.add(userProfile)
                    }
                }
                callback(userList)
            }
            .addOnFailureListener { exception ->
                // Handle the failure case, for now, using an empty list as a placeholder
                callback(emptyList())
            }
    }

    fun sendFriendRequest(currentUser: UserProfile, userFriend: UserProfile, callback: (Boolean) -> Unit) {
        // Add currentUser to userFriend's friend_reqs list
        val updatedFriendReqs = userFriend.friend_reqs.toMutableList().apply {
            add(currentUser)
        }

        // Update userFriend's friend_reqs in Firestore
        firestore.collection("users")
            .whereEqualTo("uniqueCode", userFriend.uniqueCode)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Assuming there is only one user with a uniqueCode (or you handle multiple results accordingly)
                    val userDocument = querySnapshot.documents[0]
                    val userId = userDocument.id

                    // Update friend_reqs for the found user
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
                    // User not found with the provided uniqueCode
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    fun acceptFriendRequest(currentUser: UserProfile, friend: UserProfile, callback: (Boolean) -> Unit) {
        // Add friend to currentUser's friends list
        val updatedCurrentUserFriends = currentUser.friends.toMutableList().apply {
            add(friend)
        }

        // Remove friend from currentUser's friend requests
        val updatedCurrentUserFriendReqs = currentUser.friend_reqs.toMutableList().apply {
            remove(friend)
        }

        // Add currentUser to friend's friends list
        val updatedFriendFriends = friend.friends.toMutableList().apply {
            add(currentUser)
        }

        // Remove currentUser from friend's friend requests
        val updatedFriendFriendReqs = friend.friend_reqs.toMutableList().apply {
            remove(currentUser)
        }

        // Update currentUser's friends and friend_reqs in Firestore
        firestore.collection("users")
            .document(currentUser.id!!)
            .update("friends", updatedCurrentUserFriends, "friend_reqs", updatedCurrentUserFriendReqs)
            .addOnSuccessListener {
                // Update friend's friends and friend_reqs in Firestore
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
        // Remove friend from currentUser's friend requests
        val updatedCurrentUserFriendReqs = currentUser.friend_reqs.toMutableList().apply {
            remove(friend)
        }

        // Remove currentUser from friend's friend requests
        val updatedFriendFriendReqs = friend.friend_reqs.toMutableList().apply {
            remove(currentUser)
        }

        // Update currentUser's friend_reqs in Firestore
        firestore.collection("users")
            .document(currentUser.id!!)
            .update("friend_reqs", updatedCurrentUserFriendReqs)
            .addOnSuccessListener {
                // Update friend's friend_reqs in Firestore
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
                        // Username is already taken in Firestore, callback with failure
                        callback(false, "Username is already taken")
                    } else {
                        // Username is not taken in Firestore, callback with success
                        callback(true, null)
                    }
                } else {
                    // Error occurred while checking username existence in Firestore, callback with failure
                    callback(false, queryTask.exception?.message)
                }
            }
    }


}