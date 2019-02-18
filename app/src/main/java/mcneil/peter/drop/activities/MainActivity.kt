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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.callback.SingleLocationCallback
import mcneil.peter.drop.fragments.CreateDropFragment
import mcneil.peter.drop.fragments.LoginFragment
import mcneil.peter.drop.model.Drop
import pub.devrel.easypermissions.AfterPermissionGranted

class MainActivity : BaseActivity(), View.OnClickListener, GeoQueryEventListener, ValueEventListener {
    private val TAG = this.javaClass.canonicalName

    private val locationCallback = SingleLocationCallback(this)

    private lateinit var fm: FragmentManager
    private lateinit var text: TextView

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_login_button -> openLogin()
            R.id.main_license_button -> openLicense()
            R.id.main_drop_button -> openDrop()
            R.id.main_show_drop_button -> displayDrops()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fm = supportFragmentManager
        main_login_button.setOnClickListener(this)
        main_license_button.setOnClickListener(this)
        main_drop_button.setOnClickListener(this)
        main_show_drop_button.setOnClickListener(this)

        text = findViewById(R.id.main_drop_text)
    }

    private fun openLogin() {
        LoginFragment().show(fm, "fragment_login")
    }

    private fun openDrop() {
        CreateDropFragment().show(fm, "fragment_create_drop")
    }

    private fun openLicense() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(FINE_LOCATION)
    private fun displayDrops() {
        text.text = ""
        locationUtil.locationClient.requestLocationUpdates(locationUtil.locationRequest, locationCallback, null)
    }

    //Runs on start up
    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        if (key != null) firebaseUtil.readDrop(key, this)
    }

    override fun onDataChange(ds: DataSnapshot) {
        val temp = ds.getValue(Drop::class.java)

        if (temp != null) {
            Log.i(TAG, "Adding drop to currentDrops: $temp")
            val ne = text.text.toString() + temp.toString()
            text.text = ne
        }
    }

    override fun onKeyMoved(key: String?, location: GeoLocation?) {}
    override fun onKeyExited(key: String?) {}
    override fun onGeoQueryError(ignored: DatabaseError?) {}
    override fun onGeoQueryReady() {}
    override fun onCancelled(ignored: DatabaseError) {}

}