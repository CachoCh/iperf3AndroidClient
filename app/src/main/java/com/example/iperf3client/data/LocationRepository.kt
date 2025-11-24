package com.example.iperf3client.data

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import com.example.iperf3client.utils.LocationTracker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LocationRepository(context: Context) {
    private val _locationFlow = MutableStateFlow(Location(""))

    var locationFlow: StateFlow<Location> = _locationFlow.asStateFlow()
    val tracker = LocationTracker(context) { location ->
        _locationFlow.update { location }
    }

    companion object {
        @Volatile
        private var instance: LocationRepository? = null
        fun getInstance(application: Context) =
            instance ?: synchronized(this) {
                instance ?: LocationRepository(application).also { instance = it }
            }
    }
}