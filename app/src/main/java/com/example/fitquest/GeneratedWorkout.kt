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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitquest.ui.theme.FitQuestTheme


data class Exercise(
    val name: String,
    val duration: String,
    val imageResId: Int
)

val sampleExercises = listOf(
    Exercise("Core and Cardio", "45:00",  R.drawable.abs_exercise),
    Exercise("Core and Cardio", "45:00",  R.drawable.abs_exercise),
    Exercise("Core and Cardio", "45:00",  R.drawable.abs_exercise),
    Exercise("Core and Cardio", "45:00",  R.drawable.abs_exercise),
    Exercise("Core and Cardio", "45:00",  R.drawable.abs_exercise),
    Exercise("Core and Cardio", "45:00",  R.drawable.abs_exercise),
    Exercise("Core and Cardio", "45:00",  R.drawable.abs_exercise),
    )

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
fun StartWorkoutButton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                // Handle the "Create Workout" button click
                // You can perform the necessary actions here
            },
            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFED8F83)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Start", color = Color.Black, fontSize = 25.sp)
        }
    }
}
@Composable
fun GeneratedWorkout() {
    Column {
        // Image at the top
        Image(
            painter = painterResource(id = R.drawable.diverse_exercise), // Replace with your image resource
            contentDescription = "illustration of people workoing out",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        // Box with title and stats on top of the image
        Box(
            modifier = Modifier
                .shadow(12.dp, shape = RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Abs Workout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "20 mins | 5 exercises | 4x",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                // Placeholder content (replace with your content)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .shadow(12.dp, shape = RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {
                    LazyColumn {
                        items(sampleExercises) { exercise ->
                            ExerciseItem(exercise = exercise)
                            Divider()
                        }
                    }
                }
                // Placeholder CreateWorkoutButton
                StartWorkoutButton()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GeneratedWorkoutPreview() {
    FitQuestTheme {
        GeneratedWorkout()
    }
}