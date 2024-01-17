package com.example.fitquest


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

//TEM DE SER ALTERADOS PARA FAZER DISPLAY DOS VALORES CORRETOS
data class Challenge(
    val name: String,
    val xp: Int,
    val checkpoints: String,
)

val sampleChallenges = listOf(
    Challenge("Urban Explorer", 200, "1/3"),
    Challenge("Insane Walkers", 150, "2/3"),
    Challenge("On Fire!", 100, "3/3")
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

// Calculate remaining time
@RequiresApi(Build.VERSION_CODES.O)
val currentDay = LocalDate.now().dayOfWeek
@RequiresApi(Build.VERSION_CODES.O)
val timeUntilMidnight = Duration.between(LocalTime.now(), LocalTime.MAX).toMinutes()

// Sample completion value (replace this with your completion logic)
val completion = 0.33f

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompletionInfo(completion: Float, currentDay: DayOfWeek, timeUntilMidnight: Long) {
    // Calculate remaining days, hours, and minutes
    val daysLeft = DayOfWeek.SUNDAY.value - currentDay.value
    val hoursLeft = timeUntilMidnight / 60
    val minutesLeft = timeUntilMidnight % 60

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${(completion * 100).toInt()}% Completed",
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = when {
                daysLeft > 0 -> "$daysLeft ${if (daysLeft > 1) "days" else "day"} left"
                hoursLeft > 0 -> "$hoursLeft ${if (hoursLeft > 1) "hours" else "hour"} left"
                else -> "$minutesLeft ${if (minutesLeft > 1) "minutes" else "minute"} left"
            },
            fontWeight = FontWeight.Bold
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChallengeItem(challenge: ChallengeData, isGroupChallenge: Boolean = false, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick.invoke() }
    ) {
        // Title and XP Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = challenge.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text =  challenge.xp.toString() + " XP", fontWeight = FontWeight.Bold)
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
                if(challenge.done_checkpoints != null) {
                    Text(text = challenge.done_checkpoints.toString() + "/" + challenge.total_checkpoints.toString() + " checkpoints done")
                }
                else{
                    Text(text= challenge.description.toString())
                }
                Spacer(modifier = Modifier.weight(1f))
//                Image(
//                    painter = painterResource(id = (challenge as GroupChallenge).profileImage),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .size(50.dp)
//                        .clip(CircleShape)
//                )
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
                if(challenge.done_checkpoints != null) {
                    Text(text = challenge.done_checkpoints.toString() + "/" + challenge.total_checkpoints.toString() + " checkpoints done")
                }else{
                    Text(text= challenge.description.toString())
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(challenge.done_checkpoints != null) {
                ChallengeCheckpointProgress(challenge)
            }
            else{
                // TODO funcao para ver quando o progresso não é por checkpoints
                ChallengeProgress(challenge)
            }

        }
    }
}

@Composable
fun ChallengeCheckpointProgress(challenge: ChallengeData) {
    val progress = challenge.done_checkpoints?.toFloat() ?: 0f
    val totalCheckpoints = challenge.total_checkpoints ?: 0

    val percentage = if (totalCheckpoints > 0) {
        ((progress / totalCheckpoints) * 100).toInt()
    } else {
        0
    }

    LinearProgressIndicator(
        progress = progress / totalCheckpoints,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .height(8.dp)
    )

    Spacer(modifier = Modifier.width(3.dp))
    Text(text = "$percentage%", fontSize = 10.sp)
}

@Composable
fun ChallengeProgress(challenge: ChallengeData) {
    val progress = challenge.goal?.toFloat() ?: 0f


//    val percentage = if (totalCheckpoints > 0) {
//        ((progress / totalCheckpoints) * 100).toInt()
//    } else {
//        0
//    }

//    LinearProgressIndicator(
//        progress = progress / totalCheckpoints,
//        color = MaterialTheme.colorScheme.primary,
//        modifier = Modifier
//            .height(8.dp)
//    )

    Spacer(modifier = Modifier.width(3.dp))
//    Text(text = "$percentage%", fontSize = 10.sp)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyChallenges(navController: NavHostController, challenges : List<ChallengeData?>) {
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
            0 -> IndividualChallenges(navController, challenges)
            1 -> GroupChallenges(navController, challenges)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IndividualChallenges(navController: NavController, challenges : List<ChallengeData?>) {
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
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 2.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = "33% Completed", fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.weight(1f))
//                Text(text = "3 days", fontWeight = FontWeight.Bold)
//            }

            CompletionInfo(completion, currentDay, timeUntilMidnight)
            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder content (replace with your content)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Column {
                    challenges.forEachIndexed { index, challenge ->
                        if (challenge != null) {
                            ChallengeItem(challenge = challenge) {
                                // Navigate to friend's profile
                                if(challenge.done_checkpoints != null) {
                                    navController.navigate("${Screens.LocationChallenge.route}/${challenge.title}")
                                }else{
                                    navController.navigate("${Screens.Challenge.route}/${challenge.title}")
                                }
                            }
                        }
                        if (index < challenges.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroupChallenges(navController: NavController, challenges : List<ChallengeData?>) {
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
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal= 16.dp, vertical = 2.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = "33% Completed", fontWeight = FontWeight.Bold)
//                Spacer(modifier = Modifier.weight(1f))
//                Text(text = "3 days", fontWeight = FontWeight.Bold)
//            }
            CompletionInfo(completion, currentDay, timeUntilMidnight)

            Spacer(modifier = Modifier.height(8.dp))
            // Placeholder content (replace with your content)
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Column {
                    challenges.forEachIndexed { index, challenge ->
                        if (challenge != null && challenge.isGroup) {
                            ChallengeItem(challenge = challenge, isGroupChallenge = true) {
                                // Navigate to friend's profile
                                navController.navigate("${Screens.LocationChallenge.route}/${challenge.title}")
                            }
                        }
                        if (index < challenges.size - 1) {
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