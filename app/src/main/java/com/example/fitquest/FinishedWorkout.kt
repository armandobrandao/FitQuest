package com.example.fitquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.fitquest.ui.theme.FitQuestTheme

@Composable
fun FinishedWorkout(navController: NavController, listExercises: WorkoutData, isQuest: Boolean, isGen: Boolean) {
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
                        .padding(bottom = 70.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        if(isQuest){
                            Text(
                                text = "Finished Daily Quest",
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                            )
                        }else{
                            Text(
                                text = "Finished Workout",
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Time:",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = calculateTotalDuration(listExercises.exercises),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Calories:",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "216 kcal",
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Exercises Performed", // tem de ser gerados
                            fontSize = 25.sp
                        )
                        // Placeholder content (replace with your content)
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
                        ) {
                            Column {
                                listExercises.exercises.forEachIndexed { index, exercise ->
                                    ExerciseItem(exercise = exercise)
                                    if (index < listExercises.exercises.size - 1) {
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Placeholder CreateWorkoutButton
        ContinueWorkoutButton(navController, listExercises, isQuest, isGen)
    }
}

fun calculateTotalDuration(exercises: List<ExerciseData>): String {
    val totalSecondsExercises = exercises.sumBy { it.durationInSeconds } + (exercises.size - 1) * 30
    val totalSeconds = totalSecondsExercises * 4
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun ContinueWorkoutButton(navController : NavController, listExercises: WorkoutData, isQuest: Boolean, isGen: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(80.dp), // Adjust the height as needed
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom // Align the content to the bottom
    ) {
        Button(
            onClick = {
                if(isQuest || isGen) {
                    navController.navigate("${Screens.DailyQuestComplete.route}/$isQuest")
                }else{
                    navController.navigate(Screens.Home.route)
                }
            },
            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFE66353)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Continue", fontSize = 20.sp,color = colorResource(id = R.color.lightModeColor))
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun FinishedWorkoutPreview() {
//    FitQuestTheme {
//        FinishedWorkout()
//    }
//}