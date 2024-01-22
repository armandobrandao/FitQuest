package com.example.fitquest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun DailyQuest(quest: WorkoutData, navController: NavController) {
    val totalTimeInMinutes = calculateTotalTime(quest.exercises)
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .paint(
                            painter = painterResource(R.drawable.diverse_exercise),
                            contentScale = ContentScale.FillWidth
                        )
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
                // Box with title and stats on top of the image
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Daily Quest",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$totalTimeInMinutes mins | ${quest.exercises.size} exercises | 4x", // tem de ser gerados
                            fontSize = 14.sp
                        )
                        // Placeholder content (replace with your content)
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
                        ) {
                            Column {
                                quest.exercises.forEachIndexed { index, exercise ->
                                    ExerciseItem(exercise = exercise)
                                    if (index < quest.exercises.size - 1) {
                                        Divider()
                                    }
                                }
                            }
                        }
//                    // Placeholder CreateWorkoutButton
//                    StartWorkoutButton(navController)
                    }
                }
            }
        }
        // Bottom bar
        StartWorkoutButton(navController = navController, isQuest = true, isGen = false, checkpoint = null)
    }
}

// Function to calculate total time in minutes
fun calculateTotalTime(exercises: List<ExerciseData>): Int {
    // Calculate total time of exercises and breaks in seconds
    val totalTimeInSeconds = (exercises.sumBy { it.durationInSeconds } + (exercises.size - 1) * 30) * 4

    // Convert total time to minutes
    return totalTimeInSeconds / 60
}


//@Preview(showBackground = true)
//@Composable
//fun DailyQuestPreview() {
//    FitQuestTheme {
//        DailyQuest()
//    }
//}