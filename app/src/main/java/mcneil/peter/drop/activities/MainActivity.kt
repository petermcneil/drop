package mcneil.peter.drop.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.fragments.CreateDropFragment
import mcneil.peter.drop.fragments.LoginFragment
import mcneil.peter.drop.model.Drop
import pub.devrel.easypermissions.AfterPermissionGranted

class MainActivity : BaseActivity(), View.OnClickListener, GeoQueryEventListener, ValueEventListener {
    private val TAG = this.javaClass.canonicalName

    private lateinit var locationCallback: LocationCallback
    private lateinit var fm: FragmentManager
    private lateinit var dropTextView: TextView

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

        fm = supportFragmentManager
        main_login_button.setOnClickListener(this)
        main_license_button.setOnClickListener(this)
        main_drop_button.setOnClickListener(this)

        dropTextView = findViewById(R.id.main_drop_text)
        startLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    ///////////////////////// View.OnClickListener overrides /////////////////////////
    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_login_button -> if(auth.currentUser == null) LoginFragment().show(fm, "fragment_login")
            R.id.main_license_button -> openLicense()
            R.id.main_drop_button -> CreateDropFragment().show(fm, "fragment_create_drop")
        }
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
            val ne = dropTextView.text.toString() + temp.toString()
            dropTextView.text = ne
        }
    }
    ///////////////////////// Location functions /////////////////////////
    private fun updateDrops(locationResult: LocationResult) {
        if (dropTextView.text == "") {
            firebaseUtil.dropsForLocation(locationResult.locations.first(), this, empty = true)
        } else {
            firebaseUtil.dropsForLocation(locationResult.locations.first(), this)
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(FINE_LOCATION)
    private fun startLocationUpdates() {
        locationUtil.locationClient.requestLocationUpdates(locationUtil.locationRequest, locationCallback, null)
    }

    private fun stopLocationUpdates() {
        locationUtil.locationClient.removeLocationUpdates(locationCallback)
    }

    ///////////////////////// Misc. functions /////////////////////////
    private fun openLicense() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
    }

    ///////////////////////// Unused overrides /////////////////////////
    override fun onKeyMoved(key: String?, location: GeoLocation?) {}
    override fun onKeyExited(key: String?) {}
    override fun onGeoQueryError(ignored: DatabaseError?) {}
    override fun onGeoQueryReady() {}
    override fun onCancelled(ignored: DatabaseError) {}

}