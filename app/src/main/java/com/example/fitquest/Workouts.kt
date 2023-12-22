package com.example.fitquest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitquest.ui.theme.FitQuestTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


data class Workout(
    val name: String,
    val duration: String,
    val calories: String,
    val imageResId: Int
)

val sampleWorkouts = listOf(
    Workout("Core and Cardio", "45:00", "315 kcal", R.drawable.abs_exercise),
    Workout("Core and Cardio", "45:00", "315 kcal", R.drawable.abs_exercise),
    Workout("Core and Cardio", "45:00", "315 kcal", R.drawable.abs_exercise),

)

@Composable
fun WorkoutItem(workout: Workout) {
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
                Text(text = workout.name)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Duration: ${workout.duration}")
                Text(text = "Calories: ${workout.calories}")
            }
            Spacer(modifier = Modifier.width(46.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Image(
                    painter = painterResource(id = workout.imageResId),
                    contentDescription = "Image of: ${workout.name}",
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
fun CreateWorkoutButton() {
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
            Text("Create Workout", fontSize = 25.sp)
        }
    }
}


@Composable
fun Workouts(navController: NavHostController) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp) // Adjust this value based on your bottom navigation bar height
    )  {
        item {
            Header() // da Homepage
            Spacer(modifier = Modifier.height(16.dp)) // Adjust the height as needed
            Text(
                text = "Last Workouts",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                modifier = Modifier.padding(8.dp)
            )
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp)
//                    .shadow(12.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Column {
                    sampleWorkouts.forEachIndexed { index, workout ->
                        WorkoutItem(workout = workout)
                        if (index < sampleWorkouts.size - 1) {
                            Divider()
                        }
                    }
                }
            }
            CreateWorkoutButton()
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun WorkoutsPreview() {
//    FitQuestTheme {
//        Workouts(navController= NavHostController)
//    }
//}