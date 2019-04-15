package mcneil.peter.drop.fragments

import android.graphics.Matrix
import android.hardware.GeomagneticField
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.fragment_find_drop.*
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R

class FindDropFragment : Fragment(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private val request = LocationRequest.create()?.apply {
        interval = 60000
        fastestInterval = 60000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var heading: Float = 0f
    private var lastBearing: Float = 0f

    private lateinit var geoField: GeomagneticField
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private lateinit var arrow: ImageView

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_find_drop, container, false)

        arrow = view.findViewById(R.id.compass_arrow)
        arrow.setImageResource(R.drawable.arrow_white)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                Log.d(TAG, "Location callback")
                locationResult ?: return
                currentLocation = locationResult.locations.first()
                geoField = GeomagneticField(currentLocation.latitude.toFloat(), currentLocation.longitude.toFloat(), currentLocation.altitude.toFloat(), System.currentTimeMillis())
                updateCompass()
            }
        }

        locationUtil.locationClient.requestLocationUpdates(request, locationCallback, null)
        locationUtil.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 2f, locationListener)
        return view
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
        }
    }

    fun updateCompass() {
        val london = Location(LocationManager.GPS_PROVIDER)
        london.latitude = 51.507351
        london.longitude = -0.127758
        val bearing = currentLocation.bearingTo(london)
        val distance = currentLocation.distanceTo(london)

        if (bearing != lastBearing) {
            lastBearing = bearing
            Log.d(TAG, "Distance: $distance - Bearing: $bearing")
            heading += geoField.declination
            heading = (bearing - heading) * -1

            rotateCompass(normalise(heading))
        } else {
            Log.d(TAG, "Bearing hasn't changed")
        }
    }

    private fun rotateCompass(angle: Float) {
        Log.d(TAG, "Rotating compass")
        val matrix = Matrix()
        compass_arrow.scaleType = ImageView.ScaleType.MATRIX
        matrix.postRotate(angle, 100f, 100f)
        compass_arrow.imageMatrix = matrix
    }

    private fun normalise(value: Float): Float {
        return if (value in 0.0f..180.0f) {
            value
        } else {
            180 + (180 + value)
        }
    }

    private val locationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}

        override fun onLocationChanged(location: Location?) {
            Log.d(TAG, "Location changed")
            if (location != null) {
                geoField = GeomagneticField(location.latitude.toFloat(), location.longitude.toFloat(), location.altitude.toFloat(), System.currentTimeMillis())
                currentLocation = location
                updateCompass()
            }
        }
    }
}