package mcneil.peter.drop.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.fragments.CreateDropFragment
import mcneil.peter.drop.fragments.FindDropFragment
import mcneil.peter.drop.fragments.MainFragment
import mcneil.peter.drop.model.Drop
import pub.devrel.easypermissions.AfterPermissionGranted

class MainActivity : BaseActivity(), GeoQueryEventListener, ValueEventListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var locationCallback: LocationCallback
    private val fm = supportFragmentManager

    private val createDrop = CreateDropFragment()
    private val mainFragment = MainFragment()
    private val findDrop = FindDropFragment()
    private var active: Fragment = mainFragment

    ///////////////////////// Activity overrides /////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                updateDrops(locationResult)
            }
        }

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_create -> {
                    fm.beginTransaction().hide(active).show(createDrop).commit()
                    active = createDrop
                }
                R.id.menu_main -> {
                    fm.beginTransaction().hide(active).show(mainFragment).commit()
                    active = mainFragment
                }
                R.id.menu_find -> {
                    fm.beginTransaction().hide(active).show(findDrop).commit()
                    active = findDrop
                }
                else -> {
                    fm.beginTransaction().hide(active).show(createDrop).commit()
                    active = createDrop
                }

            }
            return@setOnNavigationItemSelectedListener true
        }

        fm.beginTransaction().add(R.id.main_frag_container, findDrop, "3").hide(findDrop).commit()
        fm.beginTransaction().add(R.id.main_frag_container, createDrop, "2").hide(createDrop).commit()
        fm.beginTransaction().add(R.id.main_frag_container, mainFragment, "1").commit()

        updateUI()
    }

    private fun updateUI() {
        if (DropApp.auth.currentUser == null) {
            var started = false
            getSharedPreferences("LoginActivity", Context.MODE_PRIVATE).getBoolean("active", started)
            if(!started) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
        startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    ///////////////////////// GeoQueryEventListener overrides /////////////////////////
    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        if (key != null) firebaseUtil.readDrop(key, this)
    }

    ///////////////////////// ValueEventListener overrides /////////////////////////
    /**
     * onDataChange gets called with each key found in the Geofire db.
     */
    override fun onDataChange(ds: DataSnapshot) {
        val temp = ds.getValue(Drop::class.java)

        if (temp != null) {
            Log.d(TAG, "Adding drop to currentDrops: $temp")
        }
    }

    ///////////////////////// Location functions /////////////////////////
    private fun updateDrops(locationResult: LocationResult) {
        firebaseUtil.dropsForLocation(locationResult.locations.first(), this)
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(FINE_LOCATION)
    private fun startLocationUpdates() {
        locationUtil.locationClient.requestLocationUpdates(locationUtil.locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        locationUtil.locationClient.removeLocationUpdates(locationCallback)
    }

    ///////////////////////// Unused overrides /////////////////////////
    override fun onKeyMoved(key: String?, location: GeoLocation?) {}

    override fun onKeyExited(key: String?) {}
    override fun onGeoQueryError(ignored: DatabaseError?) {}
    override fun onGeoQueryReady() {}
    override fun onCancelled(ignored: DatabaseError) {}

}