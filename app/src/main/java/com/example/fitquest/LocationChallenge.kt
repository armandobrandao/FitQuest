package com.example.fitquest

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.content.Context
import android.location.Location
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState

data class Place(
    val name: String,
    val done: Boolean,
    val lat: Double,
    val long: Double,
    val prog: Int
    )

val samplePlaces = listOf(
    Place("Jardim do Torel", true, lat = 38.718838, long =  -9.141320, 0),
    Place("Príncipe Real", false, lat = 38.7167, long= -9.1483, 0),
    Place("Parque Eduardo VII", false, lat= 38.728298, long= -9.152683, 0),
)


@Composable
fun MapsActivity(navController: NavController, challenge: ChallengeData) {
    Box(
        modifier = Modifier
            .height(500.dp)
            .background(Color.Transparent)
    ) {
        var properties by remember {
            mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
        }

        var contextCurrent = LocalContext.current

        // Location-related variables
        var currentLocation by remember { mutableStateOf<LatLng?>(null) }
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(contextCurrent)
        val requestPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    getCurrentLocation(contextCurrent, fusedLocationClient) { location ->
                        currentLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(0.0, 0.0), 12f)
        }

        val currentLocationState = MarkerState(position = currentLocation ?: LatLng(0.0, 0.0))

        LaunchedEffect(Unit) {
            val context = contextCurrent
            if (hasLocationPermission(context)) {
                getCurrentLocation(context, fusedLocationClient) { location ->
                    currentLocation = LatLng(location.latitude, location.longitude)
                    // Move the camera to the current location immediately
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation!!, 10f)
                    cameraPositionState.move(cameraUpdate)
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val placesFromChallenge = getPlacesFromChallenge(challenge)

            GoogleMapWithMarkers(
                places = placesFromChallenge,
                properties = properties,
                currentLocation = currentLocation,
                cameraPositionState = cameraPositionState,
                focusCheckpoint = null,
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

fun getPlacesFromChallenge(challenge: ChallengeData): List<PlaceData> {
    val places = mutableListOf<PlaceData>()

    challenge.checkpoints?.forEach { checkpoint ->
        if (checkpoint != null) {
            checkpoint.place?.let { place ->
                places.add(place)
            }
        }
    }

    return places
}


@Composable
fun GoogleMapWithMarkers(
    places: List<PlaceData>,
    properties: MapProperties,
    currentLocation: LatLng?,
    cameraPositionState: CameraPositionState,
    focusCheckpoint: PlaceData?, // Add the focusCheckpoint parameter
    onMarkerClick: (PlaceData) -> Unit
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties
    ) {
        // Show markers for each place
        places.forEach { place ->
            val markerState = MarkerState(position = LatLng(place.lat, place.long))
            Marker(
                title = place.name,
                state = markerState
            )
        }

        // Show current location marker if available
        if (currentLocation != null) {
            val currentLocationState = MarkerState(position = currentLocation)
            Marker(
                title = "Current Location",
                state = currentLocationState,
            )
        }

        // Show marker for the focused checkpoint
        focusCheckpoint?.let { checkpoint ->
            val focusedCheckpointState = MarkerState(position = LatLng(checkpoint.lat, checkpoint.long))
            Marker(
                title = checkpoint.name,
                state = focusedCheckpointState,
            )
        }
    }
}


//MARKER APARECER NA CURRENT lOCATION
//@Composable
//fun MapsActivity() {
//    Column(
//        modifier = Modifier
//        .height(500.dp)
//    ) {
//
//
//        var properties by remember {
//            mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
//        }
//
//        var contextCurrent = LocalContext.current
//
//        // Location-related variables
//        var currentLocation by remember { mutableStateOf<LatLng?>(null) }
//        val fusedLocationClient: FusedLocationProviderClient =
//            LocationServices.getFusedLocationProviderClient(contextCurrent)
//        val requestPermissionLauncher =
//            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
//                if (isGranted) {
//                    getCurrentLocation(contextCurrent, fusedLocationClient) { location ->
//                        currentLocation = LatLng(location.latitude, location.longitude)
//                    }
//                }
//            }
//
//        val cameraPositionState = rememberCameraPositionState {
//            position = CameraPosition.fromLatLngZoom(currentLocation ?: LatLng(0.0, 0.0), 12f)
//        }
//
//        val currentLocationState = MarkerState(position = currentLocation ?: LatLng(0.0, 0.0))
//
//        LaunchedEffect(Unit) {
//            val context = contextCurrent
//            if (hasLocationPermission(context)) {
//                getCurrentLocation(context, fusedLocationClient) { location ->
//                    currentLocation = LatLng(location.latitude, location.longitude)
//                    // Move the camera to the current location immediately
//                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation!!, 12f)
//                    cameraPositionState.move(cameraUpdate)
//                }
//            } else {
//                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//            }
//        }
//
//        Box(Modifier.fillMaxSize()) {
//            GoogleMap(
//                modifier = Modifier.fillMaxSize(),
//                cameraPositionState = cameraPositionState,
//                properties = properties
//            ) {
//                Marker(
//                    title = "Current Location",
//                    state = currentLocationState
//                )
//            }
//            // Remove the button, as we're now setting the initial location
//        }
//    }
//}

fun hasLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request permissions here
        return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            onLocationReceived(location)
        }
    }
}


@Composable
fun CheckpointItem(checkpoint: CheckpointData, checkpointNumber: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick.invoke() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Checkpoint $checkpointNumber", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))

                // Display check mark if the checkpoint is done
                if (checkpoint.isCompleted) {
                    Image(
                        painter = painterResource(id = R.drawable.check_mark),
                        contentDescription = "Check mark icon",
                        modifier = Modifier.size(24.dp) // Adjust the size of the icon as needed
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                checkpoint.place?.let { Text(text = it.name) }
            }
        }
    }
}

@Composable
fun InfoSection(navController: NavController, challenge: ChallengeData) {
    // Extract checkpoint information from the challenge
    val completedCheckpoints = challenge.done_checkpoints?.toFloat() ?: 0f
    val totalCheckpoints = challenge.total_checkpoints ?: 0

    // Calculate progress percentage
    val progress = if (totalCheckpoints > 0) {
        completedCheckpoints.toFloat() / totalCheckpoints
    } else {
        0f
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
            Text(text = challenge.title, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = challenge.xp.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "${challenge.done_checkpoints}/${challenge.total_checkpoints} checkpoints done")
    }

    Spacer(modifier = Modifier.height(8.dp))
    // Placeholder content (replace with your content)
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            challenge.checkpoints.forEachIndexed { index, checkpoint ->
                if (checkpoint != null) {
                    CheckpointItem(checkpoint = checkpoint, checkpointNumber = index + 1, onClick = {
                        // Navigate to friend's profile
                        navController.navigate("${Screens.Checkpoint.route}/${challenge.title}/${checkpoint.name}")
                    })
                }
                if (index < challenge.checkpoints.size - 1) {
                    Divider()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationChallenge(navController: NavController, challenge: ChallengeData) {
    LazyColumn {
        item {
            // Map section
            MapsActivity(navController, challenge)
        }
        stickyHeader {
            // Info section
            InfoSection(navController, challenge)
        }
    }
}