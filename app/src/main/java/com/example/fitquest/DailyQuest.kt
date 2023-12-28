package com.example.fitquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitquest.ui.theme.FitQuestTheme


@Composable
fun DailyQuest(navController: NavHostController) {
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
            ElevatedCard (
                modifier = Modifier
                    .fillMaxSize()
            ){
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
                        text = "20 mins | 5 exercises | 4x", // tem de ser gerados
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
//fun DailyQuestPreview() {
//    FitQuestTheme {
//        DailyQuest()
//    }
//}