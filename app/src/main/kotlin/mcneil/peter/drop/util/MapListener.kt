package mcneil.peter.drop.util

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import mcneil.peter.drop.DropApp.Companion.locationUtil

class MapListener(private val map: GoogleMap) : OnCompleteListener<Location> {
    private val TAG = this.javaClass.simpleName

    override fun onComplete(task: Task<Location>) {
        Log.d(TAG, "Task complete")
        if (task.isSuccessful) {
            val res = task.result
            if (res != null) {
                locationUtil.setLastKnownLocation(res)
                val loc = LatLng(res.latitude, res.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16f))
            }
        } else {
            Log.d(TAG, "Current location is null. Using defaults.")
            Log.e(TAG, "Exception: %s", task.exception)
            //                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM))
            map.uiSettings.isMyLocationButtonEnabled = false
        }
    }

}