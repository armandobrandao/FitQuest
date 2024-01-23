package com.example.fitquest

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.FirebaseFirestore

// Sample list of users for demonstration
val searchList = listOf(
    User("Evan Sheeran", R.drawable.profile_image, "@evan"),
    User("Taylor Smith", R.drawable.profile_image, "@taylor"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriend(navController: NavController, currentUser: UserProfile, userList: List<UserProfile>, authManager: AuthManager) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // Filter the user list based on the search query
    val filteredUserList = if (searchQuery.isNotBlank()) {
        isSearching = true
        userList.filter { user ->
            user.fullName.contains(searchQuery, ignoreCase = true) ||
            user.username.contains(searchQuery, ignoreCase = true) ||
            user.uniqueCode.contains(searchQuery, ignoreCase = true)
        }
    } else {
        isSearching = false
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(text = "Add Friends", fontWeight = FontWeight.Bold, fontSize = 27.sp) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            label = { Text("Search for a friend") },
            trailingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Perform search action
                }
            )
        )

        if (isSearching) {
            // Display the filtered user list
            filteredUserList.forEach { user ->
                UserListItem(currentUser = currentUser, user = user, onClick = {
                    // Navigate to friend's profile
                    navController.navigate("${Screens.Friend.route}/${user.username}")
                }, authManager = authManager)
            }
        }
    }
}

// Function to fetch the user list from Firestore


@Composable
fun UserListItem(currentUser: UserProfile, user: UserProfile, onClick: () -> Unit, authManager: AuthManager) {
    var friendRequestSent by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Round profile image
            Image(
                painter = rememberImagePainter(
                    data = user.profileImageUrl,
                    builder = {
                        crossfade(false)
//                        placeholder(R.drawable.default_profile_image)
                    }
                ),
                contentScale = ContentScale.Crop,
                contentDescription = "${user.fullName}'s profile photo",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            // User's name
            Text(text = user.fullName, fontSize = 15.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.weight(1f))

            val isInFriendReq = user.friend_reqs.any { it.uniqueCode == currentUser.uniqueCode }
            val isInMyFriendReq = currentUser.friend_reqs.any { it.uniqueCode == user.uniqueCode }

            // Send Friend Request Button
            Button(
                onClick = {
                    Log.d("AddFriend", "currentUser: $currentUser")
                    Log.d("AddFriend", "user: $user")
                    authManager.sendFriendRequest(currentUser, user) { success ->
                        if (success) {
                            // Update the friendRequestSent variable
                            friendRequestSent = true
                        }
                    }
                },
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(min = 80.dp),
                colors = ButtonDefaults.buttonColors(if (friendRequestSent || isInFriendReq || isInMyFriendReq) Color.Gray else Color(0xFFE66353))
            ) {
                Text(if (friendRequestSent || isInFriendReq || isInMyFriendReq) "Sent" else "Add")
            }
        }
    }
}


@Composable
fun ShareCode(user: UserProfile){
    // Get the context
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text("Share your code", fontWeight = FontWeight.Bold, fontSize = 25.sp)

        Spacer(modifier = Modifier.height(8.dp))

        // User Information Card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    // Handle card click
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Round profile image
                Box {
                    // Loading indicator
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
//                                placeholder(R.drawable.default_profile_image)
                            }
                        ),
                        contentScale = ContentScale.Crop,
                        contentDescription = "${user.fullName}'s profile photo",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User's name
                Text(text = user.fullName, fontSize = 22.sp)

                Spacer(modifier = Modifier.height(8.dp))

                //Qr code

                // Share Code
                Text(text = "Code:", fontSize = 22.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = Color(0xFFE66353))
                        .align(Alignment.CenterHorizontally),
                )
                {
                    Text(
                        text = user.uniqueCode, fontSize = 30.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share Button
                Button(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, user.uniqueCode) // Replace with the actual code
                            type = "text/plain"
                        }
                        // Use the context to start the activity
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
                ) {
                    Text("Share Code", fontSize = 25.sp,color = colorResource(id = R.color.lightModeColor))
                }
            }
        }
    }
}

data class User(val username: String, val profileImage: Int, val userUnder: String)  // Sample user data class

@Composable
fun AddFriend(user: UserProfile, navController: NavController, usersList: List<UserProfile>, authManager: AuthManager) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            SearchFriend(navController, user, usersList, authManager)
            ShareCode(user)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AddFriendPreview() {
//    AddFriend(name = "John Doe", code= "123-456-789")
//}
