package mcneil.peter.drop.activities

import android.content.Context
import android.graphics.Matrix
import android.hardware.*
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_find_compass.*
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R

class FindCompassActivity : AppCompatActivity(), View.OnClickListener, SensorEventListener {
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //not used
    }

    override fun onSensorChanged(event: SensorEvent) {
        val degree: Float = Math.round(event.values[2]).toFloat()

//        tvHeading.setText("Heading: " + java.lang.Float.toString(degree) + " degrees")

        // create a rotation animation (reverse turn degree degrees)
//        val ra = RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//
//        // how long the animation will take place
//        ra.duration = 210
//
//        // set the animation after the end of the reservation status
//        ra.fillAfter = true

        // Start the animation
//        arrow.startAnimation(ra)

//        rotateCompass(normalise(degree))
        currentDegree = -degree
    }

    private val TAG = this.javaClass.simpleName
    private val request = LocationRequest.create()?.apply {
        interval = 60000
        fastestInterval = 60000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var heading: Float = 0f
    private var lastBearing: Float = 0f
    private var currentDegree: Float = 0f

    private lateinit var geoField: GeomagneticField
    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private lateinit var arrow: AppCompatImageView

    private lateinit var sensor : SensorManager

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_compass)


        arrow = findViewById<AppCompatImageView>(R.id.compass_arrow)
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

        // getting GPS status
        val isGPSEnabled = locationUtil.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        // getting network status
        val isNetworkEnabled = locationUtil.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


        val crta = Criteria()
        crta.accuracy = Criteria.ACCURACY_FINE
        crta.powerRequirement = Criteria.POWER_HIGH
        val provider = locationUtil.locationManager.getBestProvider(crta, true)
        locationUtil.locationClient.requestLocationUpdates(request, locationCallback, null)
//        locationUtil.locationManager.requestLocationUpdates(provider, 1000, 2f, locationListener)

        sensor = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensor.unregisterListener(this)
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