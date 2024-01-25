package com.example.fitquest

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
                Text(text = "Duration: ${exercise.durationInSeconds} secs")
            }
            Spacer(modifier = Modifier.width(46.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Box {
                    if ( exercise.imageResId.isNullOrEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
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
}

@Composable
fun StartWorkoutButton(navController: NavController, isQuest: Boolean, isGen: Boolean, checkpoint: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
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
                                text = "$totalTimeInMinutes mins | ${generatedWorkout.exercises.size} exercises | 3x",
                                fontSize = 14.sp
                            )
                        }
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
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
        StartWorkoutButton(navController, isQuest = false, isGen = true, checkpoint = null)
    }
}