package mcneil.peter.drop.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import mcneil.peter.drop.activities.LOCATION
import mcneil.peter.drop.model.Either
import pub.devrel.easypermissions.AfterPermissionGranted
import kotlin.math.abs

class LocationUtil(val locationManager: LocationManager, val locationClient: FusedLocationProviderClient) : LocationCallback() {
    private val TAG = this.javaClass.simpleName
    private lateinit var lastKnownLocation: Location
    val locationRequest: LocationRequest? = LocationRequest.create()?.apply {
        interval = 1000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION)
    fun updateLastKnownLocation() {
        locationClient.requestLocationUpdates(locationRequest, this, null)
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION)
    fun removeLocationUpdates() {
        locationClient.removeLocationUpdates(this)
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION)
    fun onUpdate(locationListener: LocationListener) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    fun showLocationOnMap(activity: Activity, map: GoogleMap) {
        try {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
                val locationResult = locationClient.lastLocation
                Log.d(TAG, "Trying to add on complete listener")
                locationResult.addOnCompleteListener(activity, MapListener(map))
            }

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }
    }

    fun getLastKnownLocation(): Either<String, Location> {
        return if(::lastKnownLocation.isInitialized) {
            Either.Left("Not initialised")
        } else {
            Either.Right(lastKnownLocation)
        }
    }

    fun setLastKnownLocation(loc : Location) {
        lastKnownLocation = loc
    }

    override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        lastKnownLocation = locationResult.locations.first()
        removeLocationUpdates()
    }

    fun fuzzyCheckLocation(location1: Location, location2: Location): Boolean {
        val longDif = abs(location2.longitude - location1.longitude)
        val latDif = abs(location2.latitude - location1.latitude)

        return longDif > 0.000050 && latDif > 0.000050
    }

}