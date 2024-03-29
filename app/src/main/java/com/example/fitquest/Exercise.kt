package com.example.fitquest

import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Exercise(navController: NavController, listExercises: WorkoutData, numSets: Int, isQuest: Boolean, isGen: Boolean, checkpointName : String?) {
    var currentSet by remember { mutableStateOf(1) }
    var currentExercise by remember { mutableStateOf(0) }
    var timerRunning by remember { mutableStateOf(true) }
    var timerValue by remember { mutableStateOf(listExercises.exercises[currentExercise].durationInSeconds + 1) }
    var isBreak by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(1f) }

    if (timerRunning) {
        DisposableEffect (currentExercise) {
            val exerciseTimer = object : CountDownTimer((timerValue * 1000).toLong(), 1000) {
                val timerFull = timerValue
                override fun onTick(millisUntilFinished: Long) {
                    timerValue = (millisUntilFinished / 1000).toInt()
                    val elapsed = (timerFull * 1000 - millisUntilFinished).toFloat()

                    val decreasePerSecond = 1 / (timerFull * 1000).toFloat()

                    progress = (elapsed * decreasePerSecond)
                }

                override fun onFinish() {
                    if(currentExercise == listExercises.exercises.size-1 && currentSet >= numSets ){
                        if(isQuest || isGen) {
                            navController.navigate("${Screens.FinishedWorkout.route}/$isQuest")
                        }else{
                            navController.navigate("${Screens.CheckpointComplete.route}/$checkpointName")
                        }
                    }else {
                        isBreak = true
                        timerValue = 30 + 1
                        timerRunning = true
                    }
                }
            }

            exerciseTimer.start()

            onDispose {
                exerciseTimer.cancel()
            }
        }
    }

    if (timerRunning && isBreak) {
        DisposableEffect (isBreak) {
            val breakTimer = object : CountDownTimer((timerValue * 1000).toLong(), 1000) {
                val timerFull = timerValue
                override fun onTick(millisUntilFinished: Long) {
                    timerValue = (millisUntilFinished / 1000).toInt()
                    val elapsed = (timerFull * 1000 - millisUntilFinished).toFloat()

                    val decreasePerSecond = 1 / (timerFull * 1000).toFloat()

                    progress = (elapsed * decreasePerSecond)
                }

                override fun onFinish() {
                    isBreak = false
                    currentExercise++

                    if (currentExercise >= listExercises.exercises.size) {
                        currentSet++
                        currentExercise = 0

                        if (currentSet > numSets) {
                            navController.navigate("${Screens.FinishedWorkout.route}/$listExercises/$isQuest")
                        }

                    } else {
                        timerValue = listExercises.exercises[currentExercise].durationInSeconds + 1
                        timerRunning = true

                    }
                }
            }

            breakTimer.start()

            onDispose {
                breakTimer.cancel()
            }
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
                    text = "Exercise ${currentExercise + 1} out of ${listExercises.exercises.size}",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
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

                if(!isBreak) {
                    Text(
                        text = listExercises.exercises[currentExercise].name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box {
                        if (listExercises.exercises[currentExercise].imageResId.isNullOrEmpty()) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.background)
                            )
                        }
                        Image(
                            painter = rememberImagePainter(
                                data = listExercises.exercises[currentExercise].imageResId,
                                builder = {
                                    crossfade(false)
                                }
                            ),
                            contentScale = ContentScale.Crop,
                            contentDescription = "${listExercises.exercises[currentExercise].name}",
                            modifier = Modifier
                                .width(400.dp)
                                .height(300.dp)
                        )
                    }
                }else{
                    Text(
                        text = "Break",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = rememberImagePainter(
                            data = "https://firebasestorage.googleapis.com/v0/b/fitquest-5d322.appspot.com/o/exercises%2Fbreak.jpg?alt=media&token=adf21d9b-ac46-4050-9472-f85e0d38291d",
                            builder = {
                                crossfade(false)
                            }
                        ),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Exercise break",
                        modifier = Modifier
                            .width(400.dp)
                            .height(300.dp)
                    )

                }
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

                Text(
                    text = "$timerValue",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (timerRunning) {
                Button(
                    onClick = {
                        timerRunning = !timerRunning
                    },
                    modifier = Modifier.padding(end = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
                ) {
                    Text(
                        text = if (timerRunning) "Pause" else "Play",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.lightModeColor)
                    )
                }
            } else {
                Button(
                    onClick = {
                        timerRunning = !timerRunning
                    },
                    modifier = Modifier.padding(end = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
                ) {
                    Text(
                        text = if (timerRunning) "Pause" else "Play",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.lightModeColor)
                    )
                }
                Button(
                    onClick = {
                              navController.navigate(Screens.Home.route)
                    },
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text(
                        text = "Finish",
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.lightModeColor)
                    )
                }
            }
        }
    }
}