package com.example.fitquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitquest.ui.theme.FitQuestTheme


@Composable
fun DailyQuest() {
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
                    .padding(start = 8.dp, top= 10.dp)
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
                    text = "Daily Quest",
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "20 mins | 5 exercises | 4x", // tem de ser gerados
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
fun DailyQuestPreview() {
    FitQuestTheme {
        DailyQuest()
    }
}