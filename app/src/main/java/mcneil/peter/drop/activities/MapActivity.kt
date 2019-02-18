package mcneil.peter.drop.activities

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MapActivity : BaseActivity(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    private val TAG = this.javaClass.canonicalName
    private lateinit var map: GoogleMap

    private var showPermissionDeniedDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Google Map function - defines the map and updates the locationUtil
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN

        with(map) {
            uiSettings.isZoomControlsEnabled = true
        }
        updateLocation()
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(FINE_LOCATION)
    private fun updateLocation() {
        map.isMyLocationEnabled = true
        zoomToLocation()
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(FINE_LOCATION)
    fun zoomToLocation() {
//        val location = null
//
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 13f))
//
//        val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(17f)
//            .bearing(90f).tilt(40f).build()
//
//        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            zoomToLocation()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    /**
     *  Easy Permissions makes it easy to work with API 23+ permissions
     */
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        showPermissionDeniedDialog = true
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        locationUtil.onUpdate(locationListener)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}
