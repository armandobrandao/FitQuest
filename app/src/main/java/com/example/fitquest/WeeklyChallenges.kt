package com.example.fitquest


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

//TEM DE SER ALTERADOS PARA FAZER DISPLAY DOS VALORES CORRETOS
data class Challenge(
    val name: String,
    val xp: Int,
    val checkpoints: String,
)

val sampleChallenges = listOf(
    Challenge("Core and Cardio", 200, "1/3"),
    Challenge("Strength Training", 150, "2/3"),
    Challenge("Flexibility Workout", 100, "3/3")
)

data class GroupChallenge(
    val name: String,
    val xp: Int,
    val checkpoints: String,
    val profileImage: Int
)

val sampleGroupChallenges = listOf(
    GroupChallenge("Core and Cardio", 200, "1/3", R.drawable.profile_image),
    GroupChallenge("Strength Training", 150, "2/3", R.drawable.profile_image),
)


@Composable
fun ChallengeItem(challenge: Any, isGroupChallenge: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title and XP Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = if (challenge is Challenge) challenge.name else (challenge as GroupChallenge).name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "200 XP", fontWeight = FontWeight.Bold)
        }

        if (isGroupChallenge) {
            // Additional GroupChallenge specific UI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "1/3 checkpoints done")
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = (challenge as GroupChallenge).profileImage),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }
        }
        else{
            // Checkpoints Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "1/3 checkpoints done" )
            }
        }

        Row(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = 0.3f,
                color = Color(0xFFE66353),
                modifier = Modifier
                    .height(8.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "33%", fontSize = 10.sp)
        }


    }
}

@Composable
fun WeeklyChallenges(navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val colorText =
        if (isSystemInDarkTheme()) Color.White
        else Color.Black

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
                    fontSize = 25.sp,
                    color= colorText
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
                    fontSize = 25.sp,
                    color= colorText
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp) // Adjust this value based on your bottom navigation bar height
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                Text(
                    text = "Weekly Challenges",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = 0.3f,
                    color = Color(0xFFE66353),
                    modifier = Modifier
                        .fillMaxWidth() // Adjust the width here
                        .padding(start = 16.dp, end = 16.dp)
                        .height(16.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "33% Completed", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "3 days", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder content (replace with your content)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Column {
                    sampleChallenges.forEachIndexed { index, challenge ->
                        ChallengeItem(challenge = challenge)
                        if (index < sampleChallenges.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun GroupChallenges() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp) // Adjust this value based on your bottom navigation bar height
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ){
                Text(
                    text = "Weekly Challenges",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                LinearProgressIndicator(
                    progress = 0.3f,
                    color = Color(0xFFE66353),
                    modifier = Modifier
                        .fillMaxWidth() // Adjust the width here
                        .padding(start= 16.dp, end= 16.dp)
                        .height(16.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal= 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "33% Completed", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "3 days", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder content (replace with your content)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Column {
                    sampleGroupChallenges.forEachIndexed { index, challenge ->
                        ChallengeItem(challenge = challenge, isGroupChallenge = true)
                        if (index < sampleGroupChallenges.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun WeeklyChallengesPreview() {
//    FitQuestTheme {
//        WeeklyChallenges()
//    }
//}