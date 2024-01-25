package com.example.fitquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter

data class Notification(
    val title: String,
    val text: String,
)

val sampleNotifications = listOf(
    Notification("INSANE WALKERS", "Get ready for a walking experience like never before! \uD83C\uDF1F Our newest group challenge, INSANE WALKERS, is here to elevate your fitness game! Rally your squad, gear up those kicks, and let the walking madness begin!"),
    Notification("URBAN EXPLORER", "\uD83C\uDFD9️ Get Ready for an Adventure! \uD83C\uDFC3\u200D♂️ URBAN EXPLORER Challenge is Here!\n" +
            "Embark on a fitness journey like never before with our brand-new challenge – URBAN EXPLORER! \uD83C\uDF06 Lace up your sneakers, because this challenge is all about exploring the city while staying fit and having a blast!"),
    )

@Composable
fun NotificationItem(notification: Notification) {
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
                Text(text = notification.title, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Duration: ${notification.text}")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequests(navController: NavHostController, currentUser: UserProfile, authManager: AuthManager) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(text = "Notifications", fontWeight = FontWeight.Bold, fontSize = 27.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Text("Friend Requests", fontWeight = FontWeight.Bold, fontSize = 25.sp)

        Spacer(modifier = Modifier.height(8.dp))
        if (currentUser.friend_reqs.isNotEmpty()) {
            currentUser.friend_reqs.forEach { user ->
                FriendsRequestListItem(currentUser = currentUser, user = user, onClick = {
                    navController.navigate("${Screens.Friend.route}/${user.username}")
                }, authManager = authManager)
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
                    Text("Your friend requests will appear here", fontSize = 15.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        NotificationsContainer()
    }
}
@Composable
fun ClickableButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(4.dp)
        )
    }
}
@Composable
fun NotificationsContainer(){
    Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 25.sp)

    Spacer(modifier = Modifier.height(8.dp))
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            sampleNotifications.forEachIndexed { index, notification ->
                NotificationItem(notification = notification)
                if (index < sampleNotifications.size - 1) {
                    Divider()
                }
            }
        }
    }
}
@Composable
fun FriendsRequestListItem(currentUser: UserProfile, user: UserProfile, onClick: () -> Unit,  authManager: AuthManager) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick()}
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            Text(text = user.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ClickableButton(
                    text = "Add",
                    onClick = {
                        authManager.acceptFriendRequest(currentUser, user) { success ->
                            if (success) {

                            } else {

                            }
                        }
                    },
                    backgroundColor = Color(0xFFE66353)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableButton(
                    text = "Delete",
                    onClick = {
                        authManager.deleteFriendRequest(currentUser, user) { success ->
                            if (success) {

                            } else {

                            }
                        }
                    },
                    backgroundColor = Color.Gray
                )
            }
        }
    }
}


@Composable
fun Notifications (user: UserProfile, navController: NavHostController, authManager: AuthManager) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            FriendRequests(navController, user, authManager)
        }
    }
}