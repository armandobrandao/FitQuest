package com.example.fitquest

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController


@Composable
fun CountdownPage(navController: NavController, exercises: WorkoutData, numSets: Int, isQuest: Boolean, isGen: Boolean, checkpointName: String?) {
    Log.d("CountdownPage", "Entra na CountdownPage")
    Log.d("CountdownPage", "exercises, $exercises")
    var secondsLeft by remember { mutableStateOf(3) }
    var secondsLeftDisplayed by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val countDownTimer = object : CountDownTimer((secondsLeft * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft = (millisUntilFinished / 1000).toInt()
                secondsLeftDisplayed = secondsLeft + 1
            }

            override fun onFinish() {
                // Countdown completed, navigate to another page
                navController.navigate("${Screens.Exercise.route}/$numSets/$isQuest/$isGen/$checkpointName")
            }
        }

        // Start the countdown
        countDownTimer.start()

        // Dispose of the timer when the CountdownPage is removed from the composition
        onDispose {
            countDownTimer.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "$secondsLeftDisplayed", fontSize = 160.sp, fontWeight = FontWeight.Bold)
    }
}