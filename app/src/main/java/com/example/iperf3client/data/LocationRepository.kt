package com.example.iperf3client.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationRepository (context : Context) {
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val _locationFlow = MutableStateFlow( Location(""))

    var locationFlow: StateFlow<Location> = _locationFlow.asStateFlow()


    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation() {

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    _locationFlow.value = location
                }
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