package com.example.fitquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCardFriend(user: User, navController: NavController) {
    val addFriendIcon =
        if (isSystemInDarkTheme()) painterResource(id = R.drawable.add_user_2)
        else painterResource(id = R.drawable.add_user)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(text = "Friend Profile", fontWeight = FontWeight.Bold, fontSize = 27.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
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
                        .padding(horizontal = 16.dp) // Add padding here
                ) {
                    Text(text = user.username, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp)) // Add more space here

                    Text(text = user.userUnder, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(8.dp)) // Add more space here

                    Text(text = "Joined in June 2020", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(2.dp)) // Add more space here
                    Text(text = "Portugal", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp) // Add padding here
                ) {
                    Image(
                        painter = painterResource(id = user.profileImage),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }
            }
            Column {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Lvl 1", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "100/200", fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = 0.5f,
                        color = Color(0xFFE66353),
                        modifier = Modifier
                            .fillMaxWidth() // Adjust the width here
                            .padding(start = 16.dp, end = 16.dp)
                            .height(16.dp)
                    )
                }
            }
            // Add Friend Button Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { /* Handle add friend click here */ },
                    modifier = Modifier
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
                ) {
                    Image(
                        painter = addFriendIcon,
                        contentDescription = "Add Friend icon",
                        modifier = Modifier.size(20.dp) // Adjust the size of the icon as needed
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Add Friend", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
@Composable
fun Friend(user: User, navController: NavController) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
    ){
        item {
            MainCardFriend(user, navController)
            Spacer(modifier = Modifier.height(16.dp))
            StatisticsSection()
            Spacer(modifier = Modifier.height(16.dp))
            AchievementsSection()
        }
    }
}