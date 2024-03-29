package com.example.fitquest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter

@Composable
fun HeaderSimple() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}


@Composable
fun WorkoutItem(workout: WorkoutData) {
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
                Text(text = workout.title)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.width(46.dp))
            Column(
                modifier = Modifier
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Box {
                    if (workout.post_photo.isNullOrEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                        )
                    }

                    Image(
                        painter = rememberImagePainter(
                            data = workout.post_photo,
                            builder = {
                                crossfade(true)
                            }
                        ),
                        contentDescription = "Image of: ${workout.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(MaterialTheme.shapes.medium)
                    )
                }
            }
        }
    }
}

@Composable
fun CreateWorkoutButton(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(bottom = 80.dp)
            .height(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(

            onClick = {
                navController.navigate(Screens.GenerateWorkout.route)
            },
            modifier = Modifier
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFE66353)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Create Workout", fontSize =20.sp,
                color = colorResource(id = R.color.lightModeColor)
            )
        }
    }
}


@Composable
fun Workouts(navController: NavHostController, lastWorkouts: List<WorkoutData>) {
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
                HeaderSimple()
                Text(
                    text = "Last Workouts",
                    fontWeight = FontWeight.Bold,
                    fontSize = 27.sp,
                    modifier = Modifier.padding(8.dp)
                )
                ElevatedCard(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Column {
                        if(lastWorkouts.isNotEmpty()) {
                            lastWorkouts.forEachIndexed { index, workout ->
                                WorkoutItem(workout = workout)
                                if (index < lastWorkouts.size - 1) {
                                    Divider()
                                }
                            }
                        }else{
                            Text(
                                text = "Your previous workouts will appear here",
                                fontSize = 18.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
        CreateWorkoutButton(navController)
    }
}