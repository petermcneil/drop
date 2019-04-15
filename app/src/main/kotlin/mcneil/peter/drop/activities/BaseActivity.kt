package mcneil.peter.drop.activities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import mcneil.peter.drop.R
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

const val LOCATION: Int = 1231

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION)
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), LOCATION, *permissions)
        }

//        locationUtil.locationClient.requestLocationUpdates(locationUtil.locationRequest, locationUtil, null)
    }

    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), LOCATION, *permissions)
    }

    @SuppressLint("MissingPermission")
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if(requestCode == LOCATION) {
//            locationUtil.locationClient.requestLocationUpdates(locationUtil.locationRequest, locationUtil, null)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}