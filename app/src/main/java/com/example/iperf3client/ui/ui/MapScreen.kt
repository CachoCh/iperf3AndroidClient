package com.example.iperf3client.ui.ui

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.iperf3client.viewmodels.TestViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


@Composable
fun MapScreen(
    testViewModel: TestViewModel
) {
    val resultsLocation by testViewModel.resultsLocation.collectAsState()
    Column(modifier = Modifier.fillMaxWidth()) {
            OsmdroidMapView(resultsLocation)
    }
}


//https://stackoverflow.com/questions/76161027/android-jetpack-compose-open-street-map-conflict-with-tabrow
@Composable
fun OsmdroidMapView(resultsLocation: List<Location>) {

    val context = LocalContext.current


    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->

            var mapView = MapView(context).apply{
                setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
                setBuiltInZoomControls(true)
                setMultiTouchControls(true)
                clipToOutline = true
                controller.setZoom(15.0)
                Configuration.getInstance().userAgentValue = "CACHO"
            }
            addExistingMarkers(mapView, resultsLocation)
            mapView
        },
        update = { view ->
            // Code to update or recompose the view goes here
            // Since geoPoint is read here, the view will recompose whenever it is updated
            var startPoint = GeoPoint(47.278493, 8.582961)
            addMarker(view, startPoint)
            if(resultsLocation.isNotEmpty()) {
                addMarker(view, resultsLocation.get(resultsLocation.size - 1))
                startPoint = GeoPoint(resultsLocation.get(resultsLocation.size - 1))
                view.controller.setCenter((startPoint)) //TODO replace with last result geopoint
            }
            view.controller.animateTo(startPoint)
            //var mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
            //view.controller.animateTo(mMyLocationOverlay.myLocation)
            //view.controller.setZoom(6.0)
        }
    )


}

private fun addExistingMarkers(mapView: MapView, resultsLocation: List<Location>) {
    for(location in resultsLocation){
        addMarker(mapView, location)
    }
}

private fun addMarker(mapView: MapView, location:Location) {
    val startMarker = Marker(mapView)
    startMarker.setPosition(GeoPoint(location))
    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

    mapView.overlays.add(startMarker)
}

private fun addMarker(mapView: MapView, location: GeoPoint) {
    val startMarker = Marker(mapView)
    startMarker.setPosition(GeoPoint(location))
    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    startMarker.setTextIcon("HELLO")

    //startMarker.setInfoWindow()
    mapView.overlays.add(startMarker)
}




