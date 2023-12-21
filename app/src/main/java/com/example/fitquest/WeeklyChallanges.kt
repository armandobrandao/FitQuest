package com.example.fitquest


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun WeeklyChallenges() {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color(0xFFE66353))
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFED8F83))
            ) {
                Text(
                    text = "Individual",
                    color = Color.Black,
                    fontSize = 25.sp,
                )
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFED8F83))
            ) {
                Text(
                    text = "Group",
                    color = Color.Black,
                    fontSize = 25.sp,
                )
            }
        }

        // Display content based on the selected tab
        when (selectedTabIndex) {
            0 -> IndividualChallenges()
            1 -> GroupChallenges()
        }
    }
}

@Composable
fun IndividualChallenges() {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(
            text = "Weekly Challanges",
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



@Composable
fun GroupChallenges() {
    // Implement the content for Group challenges
}

@Preview(showBackground = true)
@Composable
fun WeeklyChallengesPreview() {
    FitQuestTheme {
        WeeklyChallenges()
    }
}