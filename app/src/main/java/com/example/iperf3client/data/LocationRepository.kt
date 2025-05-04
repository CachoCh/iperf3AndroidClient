package com.example.iperf3client.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
import android.location.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationRepository (context : Context) {
    private var locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
    private val _locationFlow = MutableStateFlow( Location(""))

    var locationFlow: StateFlow<Location> = _locationFlow.asStateFlow()

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation() {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var location: Location? = null

        if (isGpsEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        if (location == null && isNetworkEnabled) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        if (location != null) {
            _locationFlow.value = location
        }
    }

    companion object {
        @Volatile private var instance: LocationRepository? = null
        fun getInstance(application: Context) =
            instance ?: synchronized(this) {
                instance ?: LocationRepository(application).also { instance = it }
            }
    }
}