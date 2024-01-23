package com.example.fitquest

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.fitquest.ui.theme.FitQuestTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import android.Manifest
import android.util.Log
import androidx.compose.ui.res.colorResource


@Composable
fun DailyQuestComplete(navController: NavController, listExercises: WorkoutData, isQuest: Boolean, authManager: AuthManager, userId: String, showButton:Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                    if (isQuest){
                        Text(
                            text = "Daily Quest Complete",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                        )
                    }else{
                        Text(
                            text = "Workout Complete",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                        )

                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    Text(
                        text = "Well done! Great job staying active!",
                        fontSize = 25.sp,
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    Text(
                        text = "You've earned:",
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${listExercises.xp} XP",
                            fontSize = 100.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(26.dp))
//                    Text(
//                        text = "This is your 25º FitQuest workout!",
//                        fontSize = 25.sp,
//                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
        if(showButton){
            // Placeholder CreateWorkoutButton
            TakePhotoButton { photoUri ->
                // Handle the captured photo URI here
                Log.d("DailyQuestComplete", "Photo captured: $photoUri")
                // If you want to upload the photo to Firestore, call the upload function here
                authManager.uploadPhotoToFirestore(isQuest, photoUri, userId) { downloadUri ->
                    // Handle the download URI if needed
                    Log.d("DailyQuestComplete", "Download URI: $downloadUri")
                    navController.navigate(Screens.Home.route)
                }
            }
        }
    }
}
@Composable
fun TakePhotoButton(onPhotoCaptured: (Uri?) -> Unit) {
    val context = LocalContext.current
    val file = context.createImageFile()

    var capturedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isTaken ->
        if (isTaken) {
            onPhotoCaptured(capturedImageUri)
        } else {
            Toast.makeText(context, "Photo capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            val uri = FileProvider.getUriForFile(
                context,
                "com.example.fitquest.provider",
                file
            )
            capturedImageUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(onClick = {
            val permissionCheckResult =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                // Request a permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        modifier = Modifier
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFFE66353)),
        shape = RoundedCornerShape(30.dp)
        ) {
            Text(text = "Take a Photo!", fontSize =20.sp,
                color = colorResource(id = R.color.lightModeColor)
            )
        }
    }
}

fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    Log.d("DailyQuesComplete", "image: $image")
    return image
}

//@Preview(showBackground = true)
//@Composable
//fun DailyQuestCompletePreview() {
//    FitQuestTheme {
//        DailyQuestComplete()
//    }
//}