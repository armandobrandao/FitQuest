package com.example.fitquest

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBox(
    label: String,
    options: List<String> = emptyList(),
    keyboardType: KeyboardType = KeyboardType.Text
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
                    },
                    label = { Text(text = label) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
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
fun GenerateWorkout(navController: NavHostController) {
    val apiKey = "Tng/CZGSkgzfSCEV+DbTyw==fsY8JbSZOC11ObD4"

    // Make the API request
    var apiResponse by remember { mutableStateOf<String?>(null) }

    // Mapping for display values
    val difficultyOptionsMap = mapOf(
        "beginner" to "Beginner",
        "intermediate" to "Intermediate",
        "expert" to "Expert"
    )

    val typeOptionsMap = mapOf(
        "cardio" to "Cardio",
        "olympic_weightlifting" to "Olympic Weightlifting",
        "plyometrics" to "Plyometrics"
    )

    val muscleOptionsMap = mapOf(
        "abdominals" to "Abdominals",
        "abductors" to "Abductors",
        "adductors" to "Adductors"
    )

    LazyColumn {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                TopAppBar(
                    title = {
                        Text(text = "Create Workout", fontWeight = FontWeight.Bold, fontSize = 27.sp)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                // Intensity Input
                val intensityOptions = listOf("beginner", "intermediate", "expert")
                val selectedDifficulty by remember { mutableStateOf(intensityOptions.firstOrNull()) }
                InputBox(label = "Intensity", options = intensityOptions.map { difficultyOptionsMap[it] ?: it })

                // Location Input
                val typeOptions = listOf("cardio", "olympic_weightlifting", "plyometrics")
                val selectedType by remember { mutableStateOf(typeOptions.firstOrNull()) }
                InputBox(label = "Type", options = typeOptions.map { typeOptionsMap[it] ?: it })

                // Duration Input
                val selectedDuration by remember { mutableStateOf("") }
                InputBox(label = "Duration", keyboardType = KeyboardType.Number)

                // Type Input
                val muscleOptions = listOf("abdominals", "abductors", "adductors")
                val selectedMuscle by remember { mutableStateOf(muscleOptions.firstOrNull()) }
                InputBox(label = "Type", options = muscleOptions.map { muscleOptionsMap[it] ?: it })

                // Generate Workout Button
                Button(
                    onClick = {
                        // Handle Generate Workout button click
                        val response = WorkoutAPI.makeApiRequest(
                            selectedMuscle,
                            selectedDifficulty,
                            selectedType,
                            selectedDuration,
                            apiKey,
                        )
                        apiResponse = response
                        println(response)
                    },
                    modifier = Modifier
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFED8F83))
                ) {
                    Text("Generate Workout", fontSize = 20.sp)
                }

                // Display API response
                apiResponse?.let { response ->
                    Text("API Response: $response", fontSize = 16.sp)
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GenerateWorkoutPreview() {
//    GenerateWorkout()
//}