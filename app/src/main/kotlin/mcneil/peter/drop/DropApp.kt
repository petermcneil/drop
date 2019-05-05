package mcneil.peter.drop

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import mcneil.peter.drop.util.FirebaseUtil
import mcneil.peter.drop.util.LocationUtil

class DropApp : Application() {
    private val TAG = this.javaClass.simpleName

    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var locationUtil: LocationUtil
        lateinit var firebaseUtil: FirebaseUtil
        lateinit var appContext: Context

        const val LOGGED_IN_PREF = "LoggedIn"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Getting auth instance")
        auth = FirebaseAuth.getInstance()
        firebaseUtil = FirebaseUtil()
        appContext = this
        locationUtil = LocationUtil(getSystemService(Context.LOCATION_SERVICE) as LocationManager, LocationServices.getFusedLocationProviderClient(this))
    }
}
