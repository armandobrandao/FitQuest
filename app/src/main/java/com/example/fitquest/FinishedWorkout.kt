package com.example.fitquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitquest.ui.theme.FitQuestTheme

@Composable
fun FinishedWorkout() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .paint(
                    painter = painterResource(R.drawable.diverse_exercise),
                    contentScale = ContentScale.FillWidth
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.left_arrow),
                contentDescription = "Back Arrow",
                modifier = Modifier
                    .height(32.dp)
                    .padding(start = 8.dp, top = 10.dp)
                    .clickable {
                        // Handle back button click
                        // You can perform the necessary actions here
                    }
            )
        }
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
                    text = "Finished Workout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp,
                    color = Color.Black,
                )
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
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "00:27:08",
                            fontSize = 20.sp,
                            color = Color.Black,
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
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "216 kcal",
                            fontSize = 20.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Exercises Performed", // tem de ser gerados
                    color = Color.Black,
                    fontSize = 25.sp
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
                ContinueWorkoutButton()
            }
        }
    }
}

@Composable
fun ContinueWorkoutButton() {
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
            Text("Continue", color = Color.Black, fontSize = 25.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinishedWorkoutPreview() {
    FitQuestTheme {
        FinishedWorkout()
    }
}