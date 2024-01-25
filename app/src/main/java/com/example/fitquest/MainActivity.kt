package com.example.fitquest

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitquest.ui.theme.FitQuestTheme

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCount: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    lateinit var authManager: AuthManager

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager(this)

        /*
            val serviceIntent = Intent(this, StepCountingService::class.java)
            startService(serviceIntent)
        */

        setContent {
            FitQuestTheme {
                val navController = rememberNavController()

                val items = listOf(
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        hasNews = false,
                    ),
                    BottomNavigationItem(
                        title = "Workout",
                        selectedIcon = Icons.Filled.Add,
                        unselectedIcon = Icons.Outlined.Add,
                        hasNews = false,
                    ),
                    BottomNavigationItem(
                        title = "Challenges",
                        selectedIcon = Icons.Filled.LocationOn,
                        unselectedIcon = Icons.Outlined.LocationOn,
                        hasNews = false,
                    ),
                    BottomNavigationItem(
                        title = "Profile",
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        hasNews = false,
                    )
                )

                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route ?: ""

                val shouldShowNavigationBar = currentDestination in listOf(
                    Screens.Home.route,
                    Screens.Workout.route,
                    Screens.Challenges.route,
                    Screens.Profile.route
                )


                Scaffold(
                    modifier = Modifier.background(color = Color.White),
                    bottomBar = {
                        if (shouldShowNavigationBar) {
                            NavigationBar {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index
                                            var titulo = item.title

                                            val destinationRoute = when (titulo) {
                                                "Home" -> Screens.Home.route
                                                "Workout" -> Screens.Workout.route
                                                "Challenges" -> Screens.Challenges.route
                                                else -> Screens.Profile.route
                                            }
                                            navController.navigate(destinationRoute)
                                        },
                                        label = {
                                            Text(text = item.title)
                                        },
                                        alwaysShowLabel = false,
                                        icon = {
                                            BadgedBox(
                                                badge = {
                                                    if (item.badgeCount != null) {
                                                        Badge {
                                                            Text(text = item.badgeCount.toString())
                                                        }
                                                    } else if (item.hasNews) {
                                                        Badge()
                                                    }
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = if (index == selectedItemIndex) {
                                                        item.selectedIcon
                                                    } else item.unselectedIcon,
                                                    contentDescription = item.title
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) {
                    NavGraph(navController = navController,authManager = authManager )
                }
            }
        }
    }

}

