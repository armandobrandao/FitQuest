package com.example.fitquest

import androidx.navigation.NavController
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fitquest.ui.theme.FitQuestTheme

@Composable
fun RoundedImageCard(imageResource: Int) {
    Card(
        modifier = Modifier
            .height(250.dp)
            .width(200.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = painterResource(imageResource),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun PhotosSection(){
    // Replace the placeholder data with your actual image resources
    val imageList = listOf(
        R.drawable.principe_real,
        R.drawable.principe_real,
        R.drawable.principe_real
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(imageList) { imageResource ->
            RoundedImageCard(imageResource)
        }
    }
}
@Composable
fun CheckpointComplete(navController: NavController, workout: WorkoutData, challenge: ChallengeData, authManager: AuthManager, userId: String) {
    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .paint(
                        painter = painterResource(R.drawable.diverse_exercise),
                        contentScale = ContentScale.FillWidth
                    )
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }
            // Box with title and stats on top of the image
            ElevatedCard (
                modifier = Modifier
                    .fillMaxSize()
            ){
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Checkpoint Complete",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    Text(
                        text = "Well done! Great job staying active!",
                        fontSize = 25.sp,
                    )
                    Spacer(modifier = Modifier.height(26.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${challenge.done_checkpoints} / ${challenge.total_checkpoints}",
                            fontSize = 100.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.check_mark),
                            contentDescription = "Check mark icon",
                            modifier = Modifier.size(100.dp) // Adjust the size of the icon as needed
                        )
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    Text(
                        text = "Share this moment!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PhotosSection()

                    Spacer(modifier = Modifier.weight(1f))
                    // Placeholder CreateWorkoutButton
                    TakePhotoButton { photoUri ->
                        // Handle the captured photo URI here
                        Log.d("DailyQuestComplete", "Photo captured: $photoUri")
                        // If you want to upload the photo to Firestore, call the upload function here
                        authManager.uploadPhotoToFirestore(photoUri, userId) { downloadUri ->
                            // Handle the download URI if needed
                            Log.d("DailyQuestComplete", "Download URI: $downloadUri")
                        }
                    }
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DailyQuestCompletePreview() {
//    FitQuestTheme {
//        DailyQuestComplete()
//    }
//}