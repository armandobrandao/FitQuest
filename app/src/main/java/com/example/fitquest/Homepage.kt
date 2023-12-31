package com.example.fitquest

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitquest.ui.theme.FitQuestTheme
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Header(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp) // Set an appropriate height for the header
    ) {
        // Centered logo using a Column
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
        // Bell in the top-right corner
        Image(
            painter = painterResource(id = R.drawable.bell),
            contentDescription = "Notification Bell",
            modifier = Modifier
                .size(35.dp)
                .padding(end = 8.dp, top = 10.dp)
                .clickable {
                    // Handle bell click
                    // You can perform the necessary actions here
                    navController.navigate(Screens.Notifications.route)
                }
                .align(Alignment.TopEnd)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendario(close: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .padding(10.dp)
//            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
    ) {
        val currentDate = remember { LocalDate.now() }
        val startDate = remember { currentDate.minusDays(500) }
        val endDate = remember { currentDate.plusDays(500) }
        var selection by remember { mutableStateOf(currentDate) }
        val state = rememberWeekCalendarState(
            startDate = startDate,
            endDate = endDate,
            firstVisibleWeekDate = currentDate,
        )
        val visibleWeek = rememberFirstVisibleWeekAfterScroll(state)

        WeekCalendar(
            state = state,
            dayContent = { day ->
                if (day.date.isAfter(startDate) && day.date.isBefore(endDate.plusDays(1))) {
                    Day(day.date, isSelected = selection == day.date) { clicked ->
                        if (selection != clicked) {
                            selection = clicked
                        }
                    }
                }
            },
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private val dateFormatter = DateTimeFormatter.ofPattern("dd")

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun Day(date: LocalDate, isSelected: Boolean, onClick: (LocalDate) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(26.dp))
            .clickable { onClick(date) }
            .padding(4.dp)
            .background(if (isSelected) Color(0xFFE66353) else Color(0xFFf3b4ac)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
            )
            Box(
                modifier = Modifier
                    .size(24.dp) // Adjust the size of the circle as needed
                    .background(
                        color = if (isSystemInDarkTheme()) {
                            // Dark mode color
                            colorResource(id = R.color.darkModeColor)
                        } else {
                            // Light mode color
                            colorResource(id = R.color.lightModeColor)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dateFormatter.format(date),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun DailyProgress(steps: String, caloriesBurned: String, distance: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
    ) {
        // Title Row
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Daily Progress", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        }

        // Two rounded containers side by side (Steps and Calories Burned)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val stepsIconRes = R.drawable.running
            val caloriesIconRes = R.drawable.calories


            val stepsProgress = 0.7f
            val stepsGoal = 10000
            val caloriesProgress = 0.5f
            val caloriesGoal = 2000

            RoundedContainer("Steps", steps, stepsIconRes, stepsProgress, stepsGoal)
            Spacer(modifier = Modifier.width(16.dp))
            RoundedContainer("Calories Burned", caloriesBurned, caloriesIconRes, caloriesProgress, caloriesGoal)
        }

        // Another rounded container (Distance) in a separate row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val distanceIconRes = R.drawable.distance
            val distanceProgress = 0.3f
            val distanceGoal = 7
            RoundedContainer("Distance", distance, distanceIconRes, distanceProgress, distanceGoal)
        }
    }
}

@Composable
fun RoundedContainer(title: String, value: String, iconRes: Int, progress: Float, goal: Int) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .height(150.dp)
            .width(170.dp)
//            .clip(shape = RoundedCornerShape(16.dp))
//            .shadow(2.dp, shape = RoundedCornerShape(2.dp))
//            .background(Color.White)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = title,
                modifier = Modifier.size(40.dp) // Adjust the size of the icon as needed
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(text = "$value")

            Spacer(modifier = Modifier.height(2.dp))

            Row(modifier = Modifier
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .height(8.dp)
                        .width(90.dp),
                    color = Color(0xFFE66353)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = "$goal", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun DailyQuests(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        // Title Row
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Daily Quests", fontWeight = FontWeight.Bold, fontSize = 30.sp)
        }

        sampleDailyQuests.forEachIndexed { index, quest ->
            ClickableCardItem(quest = quest, onClick = {
                // Navigate to friend's profile
                navController.navigate("${Screens.DailyQuest.route}/${quest.title}")
            })
            if (index < sampleDailyQuests.size - 1) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

data class DailyQuest(
    val title: String,
    val difficulty: String,
    val duration: String,
    val image: Int
)

val sampleDailyQuests = listOf(
    DailyQuest("Pilates Session", "Medium", "20 mins",  R.drawable.pilates),
    DailyQuest("Cardio Session", "Medium", "30 mins",  R.drawable.pilates),
    )

@Composable
fun ClickableCardItem(quest: DailyQuest, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick.invoke() }
            .padding(8.dp)
            .height(140.dp)
            .fillMaxWidth()
    ) {
        // Image
        Image(
            painter = painterResource(id = quest.image), // Replace with your image resource
            contentDescription = quest.title, // Content description can be set to null for decorative images
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(26.dp))

        )

        // Text and Arrow
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Content
            Text(text = quest.title, fontSize = 20.sp, color = Color.White)
            Text(text = quest.difficulty, fontSize = 16.sp, color = Color.White)
            Text(text = quest.duration, fontSize = 16.sp, color = Color.White)

            // Spacer for layout adjustment
            Spacer(modifier = Modifier.weight(1f))

            // Arrow icon
            Icon(
                painter = painterResource(id = R.drawable.right_arrow), // Replace with your arrow icon resource
                contentDescription = "Arrow", // Content description can be set to null for decorative icons
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.End)
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Homepage(navController: NavController) {
    Log.d("Homepage", "Estou na home")
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp) // Adjust this value based on your bottom navigation bar height
    )  {
        item {
            Header(navController)
            Calendario()
            DailyProgress(steps = "3000 steps", caloriesBurned = "1200 cals", distance = "5 km")
            DailyQuests(navController)
        }
    }
}


//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun HomepagePreview() {
//    FitQuestTheme {
//        Homepage()
//    }
//}
