package com.example.fitquest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitquest.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFriend() {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier.height(40.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Text("Add Friends", fontWeight = FontWeight.Bold, fontSize = 25.sp)

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
                Text(text = "John Doe", fontSize = 22.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // QR Code
//                val qrCodeBitmap = generateQRCode("ABC123", 150, 150)
//                Image(
//                    bitmap = qrCodeBitmap,
//                    contentDescription = null,
//                    modifier = Modifier.size(80.dp)
//                )

                Spacer(modifier = Modifier.height(8.dp))

                // Share Code
                Text(text = "Code:", fontSize = 22.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color= Color(0xFFE66353))
                        .align(Alignment.CenterHorizontally)
                        .clip(shape = RoundedCornerShape(26.dp)),
                    )
                    {
                    Text(text = "123-456-789", color= Color.White, fontSize = 30.sp,
                        modifier = Modifier.align(Alignment.Center))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Share Button
                IconButton(
                    onClick = {
                        // Handle share button click
                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddFriendPreview() {
    AddFriend()
}
