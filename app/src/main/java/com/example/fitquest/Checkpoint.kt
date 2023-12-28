package com.example.fitquest

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Overlay
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
@Composable
fun MapSection(navController: NavController, focusCheckpoint: Place? = null) {
    Box(
        modifier = Modifier
            .height(500.dp)
            .background(Color.Transparent)
    ) {
        // GoogleMapWithMarker
        var properties by remember {
            mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
        }

        var contextCurrent = LocalContext.current

        var checkpointCoords = focusCheckpoint?.let { LatLng(it.lat, focusCheckpoint.long) }
        // Location-related variables
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(contextCurrent)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(checkpointCoords ?: LatLng(0.0, 0.0), 12f)
        }

        LaunchedEffect(Unit) {
            val context = contextCurrent
            if (hasLocationPermission(context)) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(checkpointCoords!!, 14f)
                cameraPositionState.move(cameraUpdate)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            GoogleMapWithMarker(
                properties = properties,
                cameraPositionState = cameraPositionState,
                focusCheckpoint = focusCheckpoint,
            ) { place ->
                // Handle marker click, you can navigate or show more details
                // about the clicked place
            }
        }

        // TopAppBar with just the back arrow
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.White) // Set a background color for visibility

        ) {
            IconButton(
                onClick = { navController.popBackStack() },
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        }
    }
}

@Composable
fun GoogleMapWithMarker(
    properties: MapProperties,
    cameraPositionState: CameraPositionState,
    focusCheckpoint: Place?, // Add the focusCheckpoint parameter
    onMarkerClick: (Place) -> Unit
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties
    ) {
        // Show marker for the focused checkpoint
        focusCheckpoint?.let { checkpoint ->
            val focusedCheckpointState =
                MarkerState(position = LatLng(checkpoint.lat, checkpoint.long))
            Marker(
                title = checkpoint.name,
                state = focusedCheckpointState,
            )
        }
    }
}



@Composable
fun CheckpointSection(navController: NavController, checkpoint: Place){
    // Calculate progress percentage
    val progress = checkpoint.prog.toFloat()

    Column(
        modifier = Modifier
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = checkpoint.name, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = progress,
            color = Color(0xFFE66353),
            modifier = Modifier
                .height(10.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = "${(progress * 100).toInt()}%", fontSize = 10.sp)
    }
    Column(
        modifier = Modifier
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "20 mins | 5 exercises | 4x", // tem de ser gerados
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.weight(1f))
        }

    }

    Spacer(modifier = Modifier.height(8.dp))

    // Placeholder content (replace with your content)
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            sampleExercises.forEachIndexed { index, exercise ->
                ExerciseItem(exercise = exercise)
                if (index < sampleExercises.size - 1) {
                    Divider()
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Checkpoint(navController: NavController, checkpoint: Place) {
    LazyColumn {
        item {
            MapSection(navController, focusCheckpoint = checkpoint)
        }
        item {
           CheckpointSection(navController, checkpoint)
        }
        item {
            StartWorkoutButton()
        }

    }
}