package com.example.fitquest

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitquest.ui.theme.FitQuestTheme


@Composable
fun DailyQuestComplete() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .paint(
                    painter = painterResource(R.drawable.diverse_exercise),
                    contentScale = ContentScale.FillWidth
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.left_arrow),
                contentDescription = "Back Arrow",
                modifier = Modifier
                    .height(32.dp)
                    .padding(start = 8.dp, top= 10.dp)
                    .clickable {
                        // Handle back button click
                        // You can perform the necessary actions here
                    }
            )
        }
        // Box with title and stats on top of the image
        Box(
            modifier = Modifier
                .shadow(12.dp, shape = RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Daily Quest Complete",
                    fontWeight = FontWeight.Bold,
                    fontSize = 35.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Well done! Great job staying active!",
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                    Text(
                        text = "You've earned:",
                        fontSize = 25.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "20 XP",
                            fontSize = 100.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(64.dp))
                    Text(
                        text = "This is your 25º FitQuest workout!",
                        fontSize = 25.sp,
                        color = Color.Black,
                    )
                }
                // Placeholder CreateWorkoutButton
                TakePhotoButton(context = LocalContext.current)
            }
        }
    }
}

@Composable
fun TakePhotoButton(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                // Open the camera app
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                context.startActivity(intent)
            },
            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFED8F83)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Take Photo!", color = Color.Black, fontSize = 25.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyQuestCompletePreview() {
    FitQuestTheme {
        DailyQuestComplete()
    }
}