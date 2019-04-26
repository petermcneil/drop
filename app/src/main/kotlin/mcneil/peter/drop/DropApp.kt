package mcneil.peter.drop

import android.app.Application
import android.content.Context
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
        firebaseUtil = FirebaseUtil()
        appContext = this
    }
}
