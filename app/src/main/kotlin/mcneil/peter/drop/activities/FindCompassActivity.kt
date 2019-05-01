package mcneil.peter.drop.activities

import android.Manifest
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R

class FindCompassActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName

    private lateinit var locationCallback: LocationCallback
    private lateinit var currentLocation: Location

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_compass)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                Log.d(TAG, "Location callback")
                currentLocation = locationResult.locations.first()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (coarse == PackageManager.PERMISSION_GRANTED && fine == PackageManager.PERMISSION_GRANTED) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.bearingAccuracy = Criteria.ACCURACY_HIGH
            val provider = locationUtil.locationManager.getBestProvider(criteria, true)

            locationUtil.locationClient.requestLocationUpdates(request, locationCallback, null)
            locationUtil.locationManager.requestLocationUpdates(provider, 500, 2f, locationListener)
        }
    }

    override fun onStop() {
        super.onStop()
        locationUtil.locationClient.removeLocationUpdates(locationCallback)
        locationUtil.locationManager.removeUpdates(locationListener)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
        }
    }

    private val locationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}

        override fun onLocationChanged(location: Location?) {
            Log.d(TAG, "Location changed")
            if (location != null) {
                currentLocation = location
            }
        }
    }

    private val request = LocationRequest.create()?.apply {
        interval = 500
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    //    fun updateCompass() {
    //        if (::currentLocation.isInitialized) {
    //            val brightonPier = Location(LocationManager.GPS_PROVIDER)
    //            brightonPier.latitude = 50.82838
    //            brightonPier.longitude = 0.13947
    //
    //
    //            val distanceBetweenLocations = Math.round(currentLocation.distanceTo(brightonPier))
    //            val angleBetweenLocations = normalise(currentLocation.bearingTo(brightonPier).toDouble())
    //            val rotationAngle = normalise(anglePhoneFacing - angleBetweenLocations)
    //
    //            Log.i(TAG, "Distance between locations: $distanceBetweenLocations")
    //            Log.i(TAG, "Angle between locations: $angleBetweenLocations")
    //            Log.i(TAG, "Angle phone facing from north: $anglePhoneFacing")
    //            Log.i(TAG, "Resulting angle: $rotationAngle")
    //
    //            find_compass_distance.text = getString(R.string.f_dm_compass_distance, distanceBetweenLocations)
    //            find_compass_direction.text = getString(R.string.f_dm_compass_direction, rotationAngle)
    //            rotateCompass(rotationAngle.toFloat())
    //
    //        } else {
    //            Log.i(TAG, "Location not initialised")
    //        }
    //    }
    //
    //    private fun rotateCompass(angle: Float) {
    //        Log.d(TAG, "Rotating compass: $angle")
    //        val matrix = Matrix()
    //        arrow.scaleType = ImageView.ScaleType.MATRIX
    //        matrix.postRotate(angle, 100f, 100f)
    //        arrow.imageMatrix = matrix
    //    }
    //
    //    private fun normalise(value: Double): Double {
    //        return if (value in 0.0f..180.0f) {
    //            value
    //        } else {
    //            180 + (180 + value)
    //        }
    //    }


    //Sensor stuff
    //    private val accelerometerReading = FloatArray(3)
    //    private val magnetometerReading = FloatArray(3)
    //    private val rotationMatrix = FloatArray(9)
    //    private val orientationAngles = FloatArray(3)


    //    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    //    override fun onSensorChanged(event: SensorEvent) {
    //                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
    //            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
    //        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
    //            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
    //        }

    //        updateUI()
    //        updateOrientationAngles()
    //        anglePhoneFacing = normalise(Math.toDegrees(rotationMatrix[0].toDouble()))
    //        anglePhoneFacing = Math.toDegrees(rotationMatrix[0].toDouble())
    //    }

    //    fun updateUI() {
    //        updateOrientationAngles()
    //        anglePhoneFacing = normalise(Math.toDegrees(rotationMatrix[0].toDouble()))
    //        updateCompass()
    //    }

    //
    //    fun updateOrientationAngles() {
    //        // Update rotation matrix, which is needed to update orientation angles.
    //        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
    //
    //        // "mRotationMatrix" now has up-to-date information.
    //
    //        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    //
    //        // "mOrientationAngles" now has up-to-date information.
    //    }

    //    private var anglePhoneFacing = 0.0

    //    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
    //            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
    //        }
    //        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
    //            sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
    //        }

    //        sensorManager.unregisterListener(this)

}