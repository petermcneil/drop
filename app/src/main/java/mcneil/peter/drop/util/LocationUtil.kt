package mcneil.peter.drop.util

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.activities.FINE_LOCATION
import pub.devrel.easypermissions.AfterPermissionGranted
import kotlin.math.abs

class LocationUtil(private val locationManager: LocationManager, val locationClient: FusedLocationProviderClient) : LocationCallback() {
    lateinit var lastKnownLocation: Location

    val locationRequest = LocationRequest.create()?.apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(FINE_LOCATION)
    fun onUpdate(locationListener: LocationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        lastKnownLocation = locationResult.locations.first()
        DropApp.locationUtil.locationClient.removeLocationUpdates(this)
    }

    fun fuzzyCheckLocation(location1: Location, location2: Location) : Boolean {
        val longDif = abs(location2.longitude - location1.longitude)
        val latDif = abs(location2.latitude - location1.latitude)

        return longDif > 0.000050 && latDif > 0.000050
    }

}