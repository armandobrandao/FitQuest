package com.example.fitquest

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
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

val requestList = listOf(
    User("Lucy Bridgers", R.drawable.profile_image, "@lucy"),
    User("Phoebe Backer", R.drawable.profile_image, "@phoebe"),
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequests(navController: NavHostController, currentUser: UserProfile, authManager: AuthManager) {
    // Sample list of users for demonstration

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
                    // Navigate to friend's profile
                    Log.d("Notifications", "Deteta o onClick")
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
                    //                    .shadow(12.dp, shape = RoundedCornerShape(16.dp))
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
            .background(backgroundColor, shape = RoundedCornerShape(12.dp)) // Adjust corner radius here
            .clickable { onClick() }
            .padding(4.dp) // Adjust the internal padding here
    ) {
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.padding(4.dp) // Adjust the text padding here
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
//                            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
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
            horizontalArrangement = Arrangement.spacedBy(8.dp) // Adjust spacing here
        ) {
            // Round profile image
            Image(
                painter = painterResource(id = user.profileImage),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            // User's name
            Text(text = user.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            // Buttons container
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Send Friend Request Button
                ClickableButton(
                    text = "Add",
                    onClick = {
                        Log.d("Notifications", "Deteta o onClick Add")
                        // Handle send friend request button click
                        // You can perform the necessary actions here
                        authManager.acceptFriendRequest(currentUser, user) { success ->
                            if (success) {
                                // Handle the case when the friend request is accepted successfully
                            } else {
                                // Handle the case when there is an issue accepting the friend request
                            }
                        }
                    },
                    backgroundColor = Color(0xFFE66353)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableButton(
                    text = "Delete",
                    onClick = {
                        Log.d("Notifications", "Deteta o onClick Delete")
                        // Handle send friend request button click
                        // You can perform the necessary actions here
                        authManager.deleteFriendRequest(currentUser, user) { success ->
                            if (success) {
                                // Handle the case when the friend request is accepted successfully
                            } else {
                                // Handle the case when there is an issue accepting the friend request
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

//@Preview(showBackground = true)
//@Composable
//fun AddFriendPreview() {
//    AddFriend(name = "John Doe", code= "123-456-789")
//}
