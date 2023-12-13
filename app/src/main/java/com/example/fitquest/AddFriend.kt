package com.example.fitquest

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFriend() {
    var searchQuery by remember { mutableStateOf("") }

    // Sample list of users for demonstration
    val userList = listOf(
        User("Harry Philip", R.drawable.profile_image),
        User("Jane Smith", R.drawable.profile_image),
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Back",
            modifier = Modifier.height(40.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Add Friends", fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color.Black)

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

        userList.forEach { user ->
            UserListItem(user = user)
        }
    }
}
@Composable
fun UserListItem(user: User) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Round profile image
            Image(
                painter = painterResource(id = user.profileImage),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // User's name
            Text(text = user.username, fontSize = 22.sp, color = Color.Black)

            Spacer(modifier = Modifier.width(8.dp))

            // Send Friend Request Button
            Button(
                onClick = {
                    // Handle send friend request button click
                    // You can perform the necessary actions here
                },
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(min = 80.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
            ) {
                Text("Add", color = Color.White)
            }
        }
    }
}

@Composable
fun ShareCode(name: String, code: String){
    // Get the context
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text("Share your code", fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(8.dp))

        // User Information Card
        OutlinedCard(
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
                Image(
                    painter = painterResource(id = R.drawable.profile_image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // User's name
                Text(text = name, fontSize = 22.sp, color = Color.Black)

                Spacer(modifier = Modifier.height(8.dp))

                //Qr code

                // Share Code
                Text(text = "Code:", fontSize = 22.sp, color = Color.Black)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = Color(0xFFE66353))
                        .align(Alignment.CenterHorizontally),
                )
                {
                    Text(
                        text = code, color = Color.White, fontSize = 30.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share Button
                Button(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, code) // Replace with the actual code
                            type = "text/plain"
                        }
                        // Use the context to start the activity
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFf3b4ac))
                ) {
                    Text("Share Code", fontSize = 25.sp)
                }
            }
        }
    }
}

data class User(val username: String, val profileImage: Int)  // Sample user data class

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriend( name: String, code: String ) {
    LazyColumn {
        item {
            SearchFriend()
            ShareCode(name = name, code = code)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddFriendPreview() {
    AddFriend(name = "John Doe", code= "123-456-789")
}
