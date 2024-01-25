package com.example.fitquest

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

data class Statistic(val value: String, val icon: Int, val title: String)

data class Achievement(val name: String, val icon: Int, val description: String)

@Composable
fun MainCard(user: UserProfile, authManager: AuthManager) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = user.fullName, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "@" + user.username, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Joined in " + user.joinDate.toString(), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Portugal", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Box {
                    if (user.profileImageUrl.isNullOrEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }
                    Image(
                        painter = rememberImagePainter(
                            data = user.profileImageUrl,
                            builder = {
                                crossfade(false)
                            }
                        ),
                        contentScale = ContentScale.Crop,
                        contentDescription = "${user.fullName}'s profile photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
            }
        }

        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal= 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Lvl. ${user.level}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "${user.xp_level}/200", fontWeight = FontWeight.Bold)
            }
            Row(
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = calculateProgress(user.xp_level, 200 ),
                    color = Color(0xFFE66353),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .height(16.dp)
                )
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val context = LocalContext.current
                Button(
                    colors = ButtonDefaults.buttonColors(Color(0xFFE66353)),
                    onClick = {
                        context.startActivity(Intent(context, EditInfo::class.java))
                    }) {
                    Text("Edit Profile",
                        color = colorResource(id = R.color.lightModeColor)
                    )
                }
                Spacer(modifier = Modifier.weight(0.5f))
                Button(
                    colors = ButtonDefaults.buttonColors(Color(0xFFE66353)),
                    onClick = {
                        authManager.signOut(context)
                        context.startActivity(Intent(context, WelcomeActivity::class.java))
                    }) {
                    Text("Log out",
                        color = colorResource(id = R.color.lightModeColor))
                }
            }
        }
    }
}

@Composable
fun StatisticsSection(user: UserProfile) {
    val statisticsList1 = listOf(
        Statistic((user.xp_total).toString(), R.drawable.xp, "Total XP"),
        Statistic((user.longestStreak).toString(), R.drawable.streak, "Longest Streak"),
    )

    val statisticsList2 = listOf(
        Statistic((user.friends.size).toString() , R.drawable.friends_total, "Friends"),
        Statistic((user.places.size).toString(), R.drawable.places, "Places"),
    )

    Column(modifier = Modifier.padding(horizontal=16.dp)) {
        Text("Statistics", fontWeight = FontWeight.Bold, fontSize = 21.sp)

        StatisticsRow(statisticsList1)
        Spacer(modifier = Modifier.height(16.dp))
        StatisticsRow(statisticsList2)
    }
}

@Composable
fun StatisticsRow(statisticsList: List<Statistic>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        statisticsList.forEach { statistic ->
            StatisticsContainer(statistic = statistic, modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun StatisticsContainer(statistic: Statistic, modifier: Modifier) {
    ElevatedCard(
        modifier = Modifier
            .padding(2.dp)
            .height(90.dp)
            .width(130.dp)
            .aspectRatio(1.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = statistic.icon),
                    contentDescription = statistic.title,
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = statistic.value, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(2.dp))
                Text(text = statistic.title, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun FriendsSection(navController: NavController, user: UserProfile) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("Friends", fontWeight = FontWeight.Bold, fontSize = 21.sp)
            Button(
                colors = ButtonDefaults.buttonColors(Color(0xFFE66353)),
                onClick = {
                    navController.navigate(Screens.AddFriends.route)
                }) {
                Text("Add Friends",
                    color = colorResource(id = R.color.lightModeColor))
            }
        }
        if (user.friends.isNotEmpty()) {
            Column {
                user.friends.forEachIndexed { index, friend ->
                    FriendItem(user = friend, onClick = {
                        navController.navigate("${Screens.Friend.route}/${friend.username}")
                    })
                }
            }
        }else{
            ElevatedCard(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                )  {
                        Text("You don´t have any friends yet, add some!", fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun FriendItem(user: UserProfile, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal=16.dp, vertical=8.dp)
            .clickable { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box {
                if (user.profileImageUrl.isNullOrEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.background)
                    )
                }
                Image(
                    painter = rememberImagePainter(
                        data = user.profileImageUrl,
                        builder = {
                            crossfade(false)
                        }
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = "${user.fullName}'s profile photo",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            }

            Text(text = user.username, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = "Arrow",
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}


@Composable
fun AchievementsSection(user: UserProfile){
    Column(modifier = Modifier.padding(horizontal=16.dp)) {
        Text("Achievements", fontWeight = FontWeight.Bold, fontSize = 21.sp)

        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
        ) {
            if(user.achievements.isNotEmpty()) {
                Column {
                    user.achievements.forEachIndexed { index, achievement ->
                        AchievementItem(achievement = achievement)
                        if (index < user.achievements.size - 1) {
                            Divider()
                        }
                    }
                }
            } else{
                Column (
                    modifier = Modifier
                        .padding(16.dp)
                ) {

                        Text("Your achievements will appear here", fontSize = 15.sp)

                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = achievement.icon),
                contentDescription = "${achievement.name}",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
            Column {
                Text(text = achievement.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(text = achievement.description, fontSize = 15.sp)
            }
        }
    }
}
@Composable
fun Profile(user: UserProfile, navController: NavController, authManager: AuthManager) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
    ){
        item {
            MainCard(user, authManager)
            Spacer(modifier = Modifier.height(16.dp))
            StatisticsSection(user)
            Spacer(modifier = Modifier.height(16.dp))
            FriendsSection(navController, user)
            AchievementsSection(user)
        }
    }
}
