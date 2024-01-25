package com.example.fitquest


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import java.time.Duration
import java.time.Instant


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChallengeItem(challenge: ChallengeData, isGroupChallenge: Boolean = false, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick.invoke() }
    ) {
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
            }
        }
        else{
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
                ChallengeProgress(challenge)
            }

        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekChallengesProgress(challengeList: List<ChallengeData?>) {
    val totalChallenges = challengeList.size
    val completedChallenges = challengeList.count { it!!.completed }
    val overallProgress = if (totalChallenges > 0) {
        (completedChallenges.toFloat() / totalChallenges) * 100
    } else {
        0f
    }

    LinearProgressIndicator(
        progress = overallProgress / 100,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .height(8.dp)
    )

    Spacer(modifier = Modifier.width(3.dp))
    Text(text = "${overallProgress.toInt()}%", fontSize = 10.sp)
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
                    .background(Color(0xFFE66353))
            ) {
                Text(
                    text = "Individual",
                    fontSize = 25.sp,
                    color = colorResource(id = R.color.lightModeColor)
                )
            }
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFE66353))
            ) {
                Text(
                    text = "Group",
                    fontSize = 25.sp,
                    color = colorResource(id = R.color.lightModeColor)
                )
            }
        }

        when (selectedTabIndex) {
            0 -> IndividualChallenges(navController, challenges)
            1 -> GroupChallenges(navController, challenges)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IndividualChallenges(navController: NavController, challenges : List<ChallengeData?>) {
    var overallProgress by remember { mutableStateOf(0f) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
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
                val totalChallenges = challenges.count { it?.group == false }
                val completedChallenges = challenges.count { it?.group == false && it?.completed == true }

                overallProgress = if (totalChallenges > 0) {
                    (completedChallenges.toFloat() / totalChallenges) * 100
                } else {
                    0f
                }

                LinearProgressIndicator(
                    progress = overallProgress / 100,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(16.dp)
                )

                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "${overallProgress.toInt()}%", fontSize = 10.sp)
            }

            val currentTime = Instant.now()
            val remainingTime = Duration.between(currentTime, challenges[0]?.end_date!!.toInstant())

            val daysLeft = remainingTime.toDays()
            val hoursLeft = remainingTime.toHours() % 24
            val minutesLeft = remainingTime.toMinutes() % 60

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(overallProgress).toInt()}% Completed",
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
            Spacer(modifier = Modifier.height(8.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    challenges.forEachIndexed { index, challenge ->
                        if (challenge != null && !challenge.group) {
                            ChallengeItem(challenge = challenge) {
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
    var overallProgress by remember { mutableStateOf(0f) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
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
                val totalChallenges = challenges.count { it?.group == true }
                val completedChallenges = challenges.count { it?.group == true && it?.completed == true }

                overallProgress = if (totalChallenges > 0) {
                    (completedChallenges.toFloat() / totalChallenges) * 100
                } else {
                    0f
                }

                LinearProgressIndicator(
                    progress = overallProgress / 100,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(16.dp)
                )

                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "${overallProgress.toInt()}%", fontSize = 10.sp)
            }

            val currentTime = Instant.now()
            val remainingTime = Duration.between(currentTime, challenges[0]?.end_date!!.toInstant())

            val daysLeft = remainingTime.toDays()
            val hoursLeft = remainingTime.toHours() % 24
            val minutesLeft = remainingTime.toMinutes() % 60

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(overallProgress).toInt()}% Completed",
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

            Spacer(modifier = Modifier.height(8.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                Column {
                    if (challenges.isEmpty() || challenges.none { it?.group == true }) {
                        Text(
                            text = "Nothing to see here... Add some friends!",
                            fontSize = 20.sp,
                        )
                    } else {
                        challenges.forEachIndexed { index, challenge ->
                            if (challenge != null && challenge.group) {
                                ChallengeItem(challenge = challenge, isGroupChallenge = true) {
                                    navController.navigate("${Screens.Challenge.route}/${challenge.title}")
                                }
                            }
                            if (index < challenges.size - 1 && challenge?.group == true) {
                                Divider()
                            }
                        }
                    }
                }

            }
        }
    }
}