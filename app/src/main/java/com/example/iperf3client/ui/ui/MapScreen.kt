package com.example.iperf3client.ui.ui

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.iperf3client.viewmodels.SpeedMapMarker
import com.example.iperf3client.viewmodels.TestViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.collections.plus


@Composable
fun MapScreen(
    testViewModel: TestViewModel
) {

    Column(modifier = Modifier.fillMaxWidth()) {
        OsmdroidMapView(testViewModel)
    }
}


//https://stackoverflow.com/questions/76161027/android-jetpack-compose-open-street-map-conflict-with-tabrow
@Composable
fun OsmdroidMapView(testViewModel: TestViewModel) {
    val context = LocalContext.current
    val mapMarker by testViewModel.mapMarker.collectAsState()
    if (mapMarker.isEmpty()) return

    // Save center location and zoom level
    var mapCenter by rememberSaveable {mutableStateOf(mapMarker.last().location)}
    var zoomLevel by rememberSaveable {mutableStateOf(20.0)}


    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            var mapView = MapView(context).apply {
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                setBuiltInZoomControls(true)
                setMultiTouchControls(true)
                clipToOutline = true
                controller.setZoom(zoomLevel)
                controller.setCenter(mapCenter)
                Configuration.getInstance().userAgentValue = "CACHO"
            }
            Log.wtf("CACHO", "adding existing ${mapMarker.size} markers")
            addExistingMarkers(mapView, mapMarker)
            mapView
        },
        update = { view ->
            view.controller.setCenter((mapMarker.last().location))
            Log.wtf("CACHO", "map: ${mapMarker.last().location}  thr: ${mapMarker.last().throughput}")
            AddMarker(view, mapMarker.last().location, mapMarker.last().throughput)

            view.controller.animateTo(mapMarker.last().location)
        }
    )


}

fun addExistingMarkers(view: MapView, mapMarkers: List<SpeedMapMarker>) {
    for (SpeedMapMarker in mapMarkers){
        AddMarker(view, SpeedMapMarker.location, SpeedMapMarker.throughput)
    }
}

fun AddMarker(mapView: MapView, location: GeoPoint, x: Float) {
    //var x by rememberSaveable { mutableIntStateOf(0) }
    val startMarker = Marker(mapView)
    startMarker.setPosition(GeoPoint(location))
    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    startMarker.setTextIcon("Thr: $x")

    mapView.overlays.add(startMarker)
}






