package mcneil.peter.drop

import android.app.Application
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import mcneil.peter.drop.util.FirebaseUtil
import mcneil.peter.drop.util.LocationUtil

class DropApp : Application() {

    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var locationUtil : LocationUtil
        lateinit var firebaseUtil : FirebaseUtil
        lateinit var appContext : Context
    }

    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
        locationUtil = LocationUtil(this.getSystemService(Context.LOCATION_SERVICE) as LocationManager, FusedLocationProviderClient(this))
        locationUtil.updateLastKnownLocation()
        firebaseUtil = FirebaseUtil()
        appContext = this
    }
}
