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
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.fitquest.ui.theme.FitQuestTheme


data class Exercise(
    val name: String,
    val duration: String,
    val imageResId: Int,
    val durationInSeconds: Int
)

val sampleExercises = listOf(
    Exercise("Abdominais", "1:00",  R.drawable.abs_exercise, 60),
    Exercise("Flexoes", "1:00",  R.drawable.abs_exercise,60),
    Exercise("Core and Cardio", "1:00",  R.drawable.abs_exercise,60),
    Exercise("Core and Cardio", "1:00",  R.drawable.abs_exercise,60),
    Exercise("Core and Cardio", "1:00",  R.drawable.abs_exercise,60),
    Exercise("Core and Cardio", "1:00",  R.drawable.abs_exercise,60),
    Exercise("Core and Cardio", "1:00",  R.drawable.abs_exercise,60),
    )

val sampleExercises2 = listOf(
    Exercise("Abdominais", "0:20",  R.drawable.abs_exercise, 5),
    Exercise("Flexoes", "0:20",  R.drawable.abs_exercise,5),
//    Exercise("Core and Cardio", "0:20",  R.drawable.abs_exercise,10),
)

var numSets = 2

@Composable
fun ExerciseItem(exercise: Exercise) {
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
                Text(text = "Duration: ${exercise.duration}")
            }
            Spacer(modifier = Modifier.width(46.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = painterResource(id = exercise.imageResId),
                    contentDescription = "Image of: ${exercise.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}

@Composable
fun StartWorkoutButton(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                navController.navigate("${Screens.CountdownPage.route}/$sampleExercises2/$numSets")
            },

            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFED8F83)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Start", fontSize = 25.sp)
        }
    }
}

@Composable
fun GeneratedWorkout(navController: NavHostController) {
    LazyColumn {
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
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
                        text = "Abs Workout",       // tem de ser gerado
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "20 mins | 5 exercises | 4x",        // tem de ser gerados
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
                            sampleExercises.forEachIndexed { index, exercise ->
                                ExerciseItem(exercise = exercise)
                                if (index < sampleExercises.size - 1) {
                                    Divider()
                                }
                            }
                        }
                    }
                    // Placeholder CreateWorkoutButton
                    StartWorkoutButton(navController)
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun GeneratedWorkoutPreview() {
//    FitQuestTheme {
//        GeneratedWorkout()
//    }
//}