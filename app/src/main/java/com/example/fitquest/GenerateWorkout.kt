package com.example.fitquest

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxScopeInstance.align
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ColumnScopeInstance.align
//import androidx.compose.foundation.layout.RowScopeInstance.align
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBox(
    label: String,
    options: List<String> = emptyList(),
    onValueChange: (String) -> Unit // Add a callback to update the state
) {
    var selectedOption by remember { mutableStateOf(options.firstOrNull()) }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(text = label, fontSize = 18.sp)
        if (options.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    selectedOption?.let {
                        TextField(
                            value = it,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    selectedOption = item
                                    onValueChange(item) // Update the state with the original value
                                    expanded = false
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                TextField(
                    value = selectedOption ?: "",
                    onValueChange = {
                        selectedOption = it
                        onValueChange(it) // Update the state with the original value
                    },
                    label = { Text(text = label) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Handle done action if needed
                        }
                    ),
                    modifier = Modifier
                        .background(Color.Gray) // Adjust the background color as needed
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateWorkout(navController: NavHostController, authManager: AuthManager) {
    var showError by remember { mutableStateOf(false) }
    val typeOptions = listOf("Cardio", "Back", "Abs", "Legs", "Shoulders", "Full Body", "Arms", "Chest")
    var selectedType by remember { mutableStateOf(typeOptions.firstOrNull()) }

//    var selectedDuration by remember { mutableStateOf("") }

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
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
                // Box with title and stats on top of the image
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 70.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        // Top Bar
                        Text(text = "Create Workout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 27.sp
                        )

                        InputBox(
                            label = "Type",
                            options = typeOptions,
                            onValueChange = { selectedType = it }
                        )

//                    InputBox(
//                        label = "Duration (m)",
//                        onValueChange = { selectedDuration = it }
//                    )


                    }
                }
            }
        }

        // Generate Workout Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(70.dp), // Adjust the height as needed
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom // Align the content to the bottom
        ) {
            Button(
                onClick = {
                    if (selectedType.isNullOrBlank()) {
                        // Display error message if type or duration is not selected
                        showError = true

                    } else {
                        // Navigate to the next screen if both type and duration are selected
                        navController.navigate("${Screens.GeneratedWorkout.route}/$selectedType")
                    }

                },
                modifier = Modifier
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFE66353))
            ) {

                Text("Generate Workout", fontSize = 20.sp,color = colorResource(id = R.color.lightModeColor))
            }
        }
    }

    if (showError) {
        // You can replace this with any UI element you want for displaying the error
        Text(
            "Please select the workout duration",
            modifier = Modifier
                .offset(y = 306.dp) // Adjust the vertical offset as needed (negative value to move upwards)
                .padding(8.dp),

            color = Color.Red
        )

    }
}



//@Preview(showBackground = true)
//@Composable
//fun GenerateWorkoutPreview() {
//    GenerateWorkout()
//}

