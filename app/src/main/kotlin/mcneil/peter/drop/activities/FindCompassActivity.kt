package mcneil.peter.drop.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_find_compass.*
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.model.ACallback
import mcneil.peter.drop.model.Drop
import org.jetbrains.anko.doAsync

class FindCompassActivity : AppCompatActivity(), View.OnClickListener, ACallback<Pair<String, Drop>>, SeekBar.OnSeekBarChangeListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var currentLocation: Location
    private lateinit var foundDrop: Drop
    private lateinit var foundDropLoc: Location
    private lateinit var pd : ProgressDialog
    //The "first" last call from finding a new drop
    private var lastCall = System.currentTimeMillis()
    //Map of cached drops owned by the user
    private val cachedUserDrops = mutableMapOf<String, Drop>()
    //Check that listeners haven't already started
    private var startedUpdates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_compass)

        pd = ProgressDialog(this)
        pd.setCancelable(false)

        find_a_drop_btn.setOnClickListener(this)
        radius_text.text = getString(R.string.radius_text, progressToRadius(1))
        radius_seekbar.setOnSeekBarChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        locationUtil.locationClient.removeLocationUpdates(locationCallback)
        locationUtil.locationManager.removeUpdates(locationListener)
        startedUpdates = false
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.find_a_drop_btn -> clickedFindADrop()
        }
    }

    override fun callback(ret: Pair<String, Drop>) {
        Log.d(TAG, "callback: Found a drop")
        val dropId = ret.first
        val drop = ret.second
        val currentCall = System.currentTimeMillis()

        val diff = currentCall - lastCall
        if (diff > 1000) {
            Log.d(TAG, "callback: First 'correct' drop")
            lastCall = currentCall
            searchForFoundDropUI(drop)
        } else {
            Log.d(TAG, "callback: Storing drop for quick access")
            cachedUserDrops[dropId] = drop
        }
    }

    /**
     * Finds a new drop and displays for users to find.
     *
     * Hides the previous content.
     */
    private fun clickedFindADrop() {
        pd.show()
        if(!startedUpdates) {
            startLocationUpdates()
        }

        find_a_drop_btn.visibility = View.GONE
        radius_seekbar.visibility = View.GONE
        radius_text.visibility = View.GONE

        //Retrieve from cache or get some new ones
        if (cachedUserDrops.isNotEmpty()) {
            Log.d(TAG, "clickedFindADrop: Drops cached: ${cachedUserDrops.size}")
            val key = cachedUserDrops.keys.first()
            val drop = cachedUserDrops[key]
            cachedUserDrops.remove(key)
            if (drop != null) {
                searchForFoundDropUI(drop)
            } else {
                Log.d(TAG, "clickedFindADrop: Drop was null, calling this method again")
                clickedFindADrop()
            }
        } else {
            Log.d(TAG, "clickedFindADrop: No drops cached")
            lastCall = System.currentTimeMillis()
            doAsync {
                while (!::currentLocation.isInitialized) {
                    Thread.sleep(100)
                }

                val radius = progressToRadius(radius_seekbar.progress)
                Log.d(TAG, "clickedFindADrop: Calling findNewDrop")
                firebaseUtil.findNewDrop(currentLocation, this@FindCompassActivity, radius)
            }
        }
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val radius = progressToRadius(progress)
        radius_text.text = getString(R.string.radius_text, radius)
    }

    private fun searchForFoundDropUI(drop: Drop) {
        find_compass_distance.visibility = View.VISIBLE
        pd.dismiss()
        Log.d(TAG, "searchForFoundDropUI: $drop")
        foundDrop = drop
        foundDropLoc = drop.location.toLocation()
        updateSearchUI()
    }

    //Updates searching drop ui
    private fun updateSearchUI() {
        if (::foundDrop.isInitialized && ::currentLocation.isInitialized) {
            val distance = Math.round(currentLocation.distanceTo(foundDropLoc))
            //            val bearing = currentLocation.bearingTo(foundDropLoc)

            find_compass_distance.text = getString(R.string.f_dm_compass_distance, distance)
        }
    }

    private fun startLocationUpdates() {
        startedUpdates = true
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

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            currentLocation = locationResult.locations.first()
            updateSearchUI()
        }
    }

    private val locationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String?) {}
        override fun onProviderDisabled(provider: String?) {}

        override fun onLocationChanged(location: Location?) {
            if (location != null) {
                currentLocation = location
                updateSearchUI()
            }
        }
    }

    private val request = LocationRequest.create()?.apply {
        interval = 500
        fastestInterval = 500
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun progressToRadius(progress: Int): Double {
        return progress * 0.1
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

}