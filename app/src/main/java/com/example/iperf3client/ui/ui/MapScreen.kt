package com.example.iperf3client.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMapViewWithLifecycle
import com.utsman.osmandcompose.rememberOverlayManagerState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
fun MapScreen(
    testResults: List<String>
) {
    Column(modifier = Modifier.fillMaxWidth()) {

            OsmdroidMapView(Modifier.weight(10f))

    }
}


//https://stackoverflow.com/questions/76161027/android-jetpack-compose-open-street-map-conflict-with-tabrow
@Composable
fun OsmdroidMapView(weight: Modifier) {
    var mapView = rememberMapViewWithLifecycle()
    mapView.clipToOutline = true


    // define camera state
    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(-6.3970066, 106.8224316)
        zoom = 12.0 // optional, default is 5.0
    }

    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    val overlayManagerState = rememberOverlayManagerState()

    SideEffect {
        mapProperties = mapProperties
            .copy(isTilesScaledToDpi = true)
            .copy(tileSources = TileSourceFactory.MAPNIK)
            .copy(isEnableRotationGesture = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }


    OpenStreetMap(
        modifier = Modifier.height(200.dp),
        cameraState = cameraState,
        properties = mapProperties,
        overlayManagerState = overlayManagerState,
        /*onFirstLoadListener = {
            val copyright = CopyrightOverlay(context)
            overlayManagerState.overlayManager.add(copyright)
        }*/
    ) {

    }
}




