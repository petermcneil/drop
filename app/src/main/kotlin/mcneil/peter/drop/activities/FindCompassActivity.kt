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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_find_compass.*
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.fragments.FoundDropFragment
import mcneil.peter.drop.model.ACallback
import mcneil.peter.drop.model.Drop
import org.jetbrains.anko.doAsync
import java.util.concurrent.Future


class FindCompassActivity : AppCompatActivity(), View.OnClickListener, ACallback<Pair<String, Drop>>, SeekBar.OnSeekBarChangeListener {
    private val TAG = this.javaClass.simpleName
    private val TIMEOUT_MILLISECONDS = 10000L
    private val TIME_DIFF = 500

    private lateinit var currentLocation: Location
    private lateinit var foundDrop: Drop
    private lateinit var foundDropLoc: Location
    private lateinit var dropId: String
    private lateinit var pd: ProgressDialog
    private lateinit var cancelDropDialog: AlertDialog
    private lateinit var fm: FragmentManager
    private lateinit var foundDropFragment: FoundDropFragment
    private lateinit var timeout: Future<Unit>
    private lateinit var snackbar: Snackbar

    //The "first" last call from finding a new drop
    private var lastCall = System.currentTimeMillis()
    //Map of cached drops owned by the user
    private val cachedUserDrops = mutableMapOf<String, Drop>()
    //Check that listeners haven't already started
    private var startedUpdates = false
    //Old radius
    private var oldRadius = 0.0

    private var distance = 0
    private var callbackDisabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_compass)

        pd = ProgressDialog(this)
        pd.setCancelable(false)

        fm = supportFragmentManager
        cancelDropDialog = AlertDialog.Builder(this).setMessage(R.string.cancel_drop_dialog)
            .setPositiveButton(R.string.are_you_sure) { _, _ ->
                removeSearchUI()
            }.create()

        find_a_drop_btn.setOnClickListener(this)
        cancel_this_drop.setOnClickListener(this)
        show_drop.setOnClickListener(this)
        radius_text.text = getString(R.string.radius_text, progressToRadius(1))
        radius_seekbar.setOnSeekBarChangeListener(this)

        snackbar = Snackbar.make(findViewById(android.R.id.content), "Couldn't find a drop, try increasing the radius", Snackbar.LENGTH_LONG)
    }

    override fun onStop() {
        super.onStop()
        removeLocationUpdates()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.find_a_drop_btn -> clickedFindADrop()
            R.id.cancel_this_drop -> cancelDropDialog.show()
            R.id.show_drop -> if (::foundDropFragment.isInitialized) foundDropFragment.show(fm, dropId)
        }
    }

    override fun callback(ret: Pair<String, Drop>) {
        if (::timeout.isInitialized && !timeout.isDone) {
            timeout.cancel(true)
        } else if (callbackDisabled) {
            Log.d(TAG, "callback: Disabled because timeout has been reached")
        } else {
            Log.d(TAG, "callback: Found a drop")
            val di = ret.first
            val drop = ret.second
            val currentCall = System.currentTimeMillis()

            val timeDiff = currentCall - lastCall
            if (timeDiff > TIME_DIFF) {
                Log.d(TAG, "callback: First 'correct' drop")
                lastCall = currentCall
                dropId = di
                searchForFoundDropUI(drop)
            } else {
                Log.d(TAG, "callback: Storing drop for quick access")
                cachedUserDrops[di] = drop
            }
        }
    }

    /**
     * Finds a new drop and displays for users to find.
     *
     * Hides the previous content.
     */
    private fun clickedFindADrop() {
        callbackDisabled = false
        pd.show()
        if (!startedUpdates) {
            startLocationUpdates()
        }

        val radius = progressToRadius(radius_seekbar.progress)

        //Retrieve from cache or get some new ones
        if (cachedUserDrops.isNotEmpty() && radius == oldRadius) {
            Log.d(TAG, "clickedFindADrop: Drops cached: ${cachedUserDrops.size}")
            val key = cachedUserDrops.keys.first()
            val drop = cachedUserDrops[key]
            cachedUserDrops.remove(key)
            if (drop != null) {
                dropId = key
                searchForFoundDropUI(drop)
            } else {
                Log.d(TAG, "clickedFindADrop: Drop was null, calling this method again")
                clickedFindADrop()
            }
        } else {
            oldRadius = radius
            Log.d(TAG, "clickedFindADrop: No drops cached")
            lastCall = System.currentTimeMillis()
            doAsync {
                while (!::currentLocation.isInitialized) {
                    Thread.sleep(100)
                }

                Log.d(TAG, "clickedFindADrop: Calling findNewDrop")
                val timeBefore = System.currentTimeMillis()
                firebaseUtil.findNewDrop(currentLocation, this@FindCompassActivity, radius)

                //Start a timeout for finding a drop
                //If the
                timeout = doAsync {
                    var timeNow = System.currentTimeMillis()
                    while ((timeNow - timeBefore) < TIMEOUT_MILLISECONDS) {
                        timeNow = System.currentTimeMillis()
                    }
                    callbackDisabled = true
                    pd.cancel()
                    snackbar.show()
                }
            }
        }
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val radius = progressToRadius(progress)
        radius_text.text = getString(R.string.radius_text, radius)
    }

    private fun searchForFoundDropUI(drop: Drop) {
        Log.d(TAG, "searchForFoundDropUI: $drop")
        foundDrop = drop
        foundDropLoc = drop.location.toLocation()
        showSearchUI()
    }

    private fun showSearchUI() {
        find_a_drop_btn.visibility = View.GONE
        radius_seekbar.visibility = View.GONE
        radius_text.visibility = View.GONE

        find_compass_explanation.text = getString(R.string.searching_drop)

        find_compass_distance.visibility = View.VISIBLE
        cancel_this_drop.visibility = View.VISIBLE

        updateSearchUI()
        pd.dismiss()
    }

    private fun removeSearchUI() {
        find_compass_distance.visibility = View.GONE
        cancel_this_drop.visibility = View.GONE

        find_a_drop_btn.visibility = View.VISIBLE
        radius_seekbar.visibility = View.VISIBLE
        radius_text.visibility = View.VISIBLE

        find_compass_explanation.text = getString(R.string.find_search_explanation)
    }

    //Updates searching drop ui
    private fun updateSearchUI() {
        if (::foundDrop.isInitialized && ::currentLocation.isInitialized) {
            distance = Math.round(currentLocation.distanceTo(foundDropLoc))
            //val bearing = currentLocation.bearingTo(foundDropLoc)

            find_compass_distance.text = getString(R.string.f_dm_compass_distance, distance)

            //If the distance is under 10m, show the drop
            if (distance < 10000) {
                Log.d(TAG, "updateSearchUI: User found the drop!")
                foundDropFragment = FoundDropFragment.newInstance(foundDrop, dropId)
                foundDropFragment.show(fm, dropId)
                removeLocationUpdates()

                //Gives a way to go back if mis-click
                show_drop.visibility = View.VISIBLE
                cancel_this_drop.visibility = View.GONE
            }
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

    private fun removeLocationUpdates() {
        locationUtil.locationClient.removeLocationUpdates(locationCallback)
        locationUtil.locationManager.removeUpdates(locationListener)
        startedUpdates = false
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