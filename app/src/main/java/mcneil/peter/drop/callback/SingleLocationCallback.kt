package mcneil.peter.drop.callback

import android.util.Log
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.locationUtil

class SingleLocationCallback(private val act: GeoQueryEventListener) : LocationCallback() {
    private val TAG = this.javaClass.canonicalName

    override fun onLocationResult(locationResult: LocationResult?) {
        locationResult ?: return
        Log.d(TAG, "Location found")
        DropApp.firebaseUtil.dropsForLocation(locationResult.locations.first(), act)
        Log.d(TAG, "Removing SingleLocationCallback")
        locationUtil.locationClient.removeLocationUpdates(this)
    }

}