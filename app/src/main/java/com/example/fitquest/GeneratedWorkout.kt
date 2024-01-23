package com.example.fitquest

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter


//data class Exercise(
//    val name: String,
//    val duration: String,
//    val imageResId: String,
//    val durationInSeconds: Int,
//    val suitableGender: List<String>,
//    val suitableGoals: List<String>,
//    val suitableMotivations: List<String>,
//    val suitablePushUps: List<String>,
//    val suitableActivityLevels: List<String>,
//    val target: String = ""
//)

//val sampleExercises = listOf(
//    ExerciseData("Abdominais", "1 set of 12 reps", R.drawable.abs_exercise, 60, listOf(""), listOf(""), listOf(), listOf(), listOf(), "Abs"),
//    ExerciseData("Flexoes", "1 set of 10 reps", R.drawable.abs_exercise, 60, listOf(""), listOf(""), listOf(), listOf(), listOf(), "Chest"),
//    ExerciseData("Core and Cardio", "1:00", R.drawable.abs_exercise, 60, listOf(""), listOf(""), listOf(), listOf(), listOf(), "Core"),
//    ExerciseData("Another Exercise", "0:45", R.drawable.abs_exercise, 45, listOf(""), listOf(""), listOf(), listOf(), listOf(), "Legs"),
    // Add more exercises as needed
//)

//val sampleExercises2 = listOf(
//    Exercise("Abdominais", "0:20", "R.drawable.abs_exercise", 20, listOf()),
//    Exercise("Flexoes", "0:20", "R.drawable.abs_exercise", 20, listOf()),
//    Exercise("Another Exercise", "0:20", "R.drawable.another_exercise", 20, listOf())
//    // Add more exercises as needed
//)


var numSets = 3

@Composable
fun ExerciseItem(exercise: ExerciseData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = exercise.name)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Duration: ${exercise.durationInSeconds}")
            }
            Spacer(modifier = Modifier.width(46.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = exercise.imageResId,
                        builder = {
                            crossfade(false)
                        }
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Image of: ${exercise.name}",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

@Composable
fun StartWorkoutButton(navController: NavController, isQuest: Boolean, isGen: Boolean, checkpoint: String?) {
    Log.d("StartWorkoutButton", "isQuest, $isQuest")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(70.dp), // Adjust the height as needed
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom // Align the content to the bottom
    ) {
        Button(
            onClick = {
                navController.navigate("${Screens.CountdownPage.route}/$numSets/$isQuest/$isGen/$checkpoint")
            },
            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFE66353)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Start", fontSize = 20.sp,color = colorResource(id = R.color.lightModeColor))
        }
    }
}


@Composable
fun GeneratedWorkout(navController: NavHostController, generatedWorkout: WorkoutData?) {
    val totalTimeInMinutes = generatedWorkout?.let { calculateTotalTime(it.exercises) }
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
                        if (generatedWorkout != null) {
                            Text(
                                text = "${generatedWorkout.title}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (generatedWorkout != null) {
                            Text(
                                text = "$totalTimeInMinutes mins | ${generatedWorkout.exercises.size} exercises | 4x",
                                fontSize = 14.sp
                            )
                        }
                        // Placeholder content (replace with your content)
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
                        ) {
                            Column {
                                if (generatedWorkout != null) {
                                    generatedWorkout.exercises.forEachIndexed { index, exercise ->
                                        ExerciseItem(exercise = exercise)
                                        if (index < generatedWorkout.exercises.size - 1) {
                                            Divider()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Placeholder CreateWorkoutButton
        StartWorkoutButton(navController, isQuest = false, isGen = true, checkpoint = null)
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GeneratedWorkoutPreview() {
//    FitQuestTheme {
//        GeneratedWorkout()
//    }
//}