package com.example.fitquest

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Exercise(navController: NavController, listExercises: List<ExerciseData>, numSets: Int) {
    var currentSet by remember { mutableStateOf(1) }
    var currentExercise by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(true) }
    var timerValue by remember { mutableStateOf(listExercises[currentExercise].durationInSeconds + 1) }
    var isBreak by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(1f) }

    Log.d("EX FORA","$currentExercise" )

    if (timerRunning) {
        // Countdown timer for exercise
        LaunchedEffect(currentExercise) {
            val exerciseTimer = object : CountDownTimer((timerValue * 1000).toLong(), 1000) {
                val timerFull = timerValue
                override fun onTick(millisUntilFinished: Long) {
                    timerValue = (millisUntilFinished / 1000).toInt()
                    // Calculate progress based on elapsed time
                    val elapsed = (timerFull * 1000 - millisUntilFinished).toFloat()

                    // Calculate the constant decrease per second
                    val decreasePerSecond = 1 / (timerFull * 1000).toFloat()

                    // Calculate progress by subtracting the constant decrease per second
                    progress = (elapsed * decreasePerSecond)
                    Log.d("SET onTick 1","$currentSet" )
                    Log.d("EX onTick 1","$currentExercise" )
                }

                override fun onFinish() {
                    if(currentExercise == listExercises.size-1 && currentSet >= numSets ){
                        navController.navigate("${Screens.FinishedWorkout.route}/$listExercises")
                    }else {
                        // Exercise timer completed, start the break timer
                        isBreak = true
                        timerValue = 2 + 1 // break time
                        timerRunning = true

                        Log.d("SET onFinish 1","$currentSet" )
                        Log.d("EX onFinish 1","$currentExercise" )
                    }
                }
            }

            exerciseTimer.start()

//            // Cleanup logic
//            onDispose {
//                exerciseTimer.cancel()
//            }
        }
    }

    if (timerRunning && isBreak) {
        // Countdown timer for break
        LaunchedEffect(isBreak) {
            val breakTimer = object : CountDownTimer((timerValue * 1000).toLong(), 1000) {
                val timerFull = timerValue
                override fun onTick(millisUntilFinished: Long) {
                    timerValue = (millisUntilFinished / 1000).toInt()
                    val elapsed = (timerFull * 1000 - millisUntilFinished).toFloat()

                    // Calculate the constant decrease per second
                    val decreasePerSecond = 1 / (timerFull * 1000).toFloat()

                    // Calculate progress by subtracting the constant decrease per second
                    progress = (elapsed * decreasePerSecond)
                    Log.d("SET onTick 2","$currentSet" )
                    Log.d("EX onTick 2","$currentExercise" )
                }

                override fun onFinish() {
                    // Break timer completed, check if it's the last set and navigate accordingly
                    isBreak = false
                    currentExercise++

                    if (currentExercise >= listExercises.size) {
                        // Move to the next set
                        currentSet++

                        if (currentSet > numSets) {
                            // All sets are completed, navigate or handle completion
                            navController.navigate("${Screens.FinishedWorkout.route}/$listExercises")
                        } else {
                            // Reset exercise index for the new set
                            currentExercise = 0
                            Log.d("SET onFinish 2.1","$currentSet" )
                            Log.d("EX onFinish 2.1","$currentExercise" )
                        }
                    } else {
                        timerValue = listExercises[currentExercise].durationInSeconds + 1
                        timerRunning = true

                        Log.d("SET onFinish 2.2","$currentSet" )
                        Log.d("EX onFinish 2.2","$currentExercise" )
                    }
                }
            }

            breakTimer.start()

//            // Cleanup logic
//            onDispose {
//                breakTimer.cancel()
//            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Exercise ${currentExercise + 1} out of ${listExercises.size}",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$currentSet of $numSets Sets",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier.width(300.dp)
            ) {
                Text(
                    text = listExercises[currentExercise].name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(id = listExercises[currentExercise].imageResId.toInt()),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(300.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Time left", fontSize = 25.sp, fontWeight = FontWeight.Bold)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .padding(8.dp)
            ) {
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE66353)
                )

                // Centered text inside the circular shape
                Text(
                    text = "$timerValue",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

        // Timer controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    timerRunning = !timerRunning
                },
                modifier = Modifier.padding(end = 16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
            ) {
                Text(
                    text = if (timerRunning) "Pause" else "Play",
                    fontWeight = FontWeight.Bold
                )
            }
        }

//            Button(
//                onClick = {
//                    currentExercise++
//                    isBreak = false
//                    timerValue = listExercises[currentExercise].durationInSeconds
//                    timerRunning = true
//                },
//                colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
//            ) {
//                Text(
//                    text = "Next",
//                    fontWeight = FontWeight.Bold
//                )
//            }

    }

}


//@Preview(showBackground = true)
//@Composable
//fun ExercisePagePreview() {
//    val sampleExercises = listOf(
//        Exercise("Core and Cardio", "1:00", R.drawable.abs_exercise, 60),
//        Exercise("Legs and Glutes", "0:45", R.drawable.abs_exercise, 45),
//        Exercise("Upper Body", "1:30", R.drawable.abs_exercise, 90)
//    )
//
//    Exercise(navController = rememberNavController(), listExercises = sampleExercises, numSets = 3)
//}