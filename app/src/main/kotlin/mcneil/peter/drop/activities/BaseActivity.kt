package mcneil.peter.drop.activities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import pub.devrel.easypermissions.EasyPermissions

const val LOCATION: Int = 1231

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    private val perms = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), LOCATION, *perms)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        if (!EasyPermissions.hasPermissions(this, *perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), LOCATION, *perms)
        }

        locationUtil.locationClient.requestLocationUpdates(locationUtil.locationRequest, locationUtil, null)
    }

//    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms.toList())) {
//            AppSettingsDialog.Builder(this).build().show()
//        }
//        EasyPermissions.requestPermissions(this, getString(R.string.permission_rationale_location), LOCATION, *perms)
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
//        if(requestCode == LOCATION) {
//            locationUtil.locationClient.requestLocationUpdates(locationUtil.locationRequest, locationUtil, null)
//        }
//    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}