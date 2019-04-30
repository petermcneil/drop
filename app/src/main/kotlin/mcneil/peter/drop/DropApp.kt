package mcneil.peter.drop

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import mcneil.peter.drop.util.FirebaseUtil
import mcneil.peter.drop.util.LocationUtil

class DropApp : Application(), FirebaseAuth.AuthStateListener {

    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var locationUtil : LocationUtil
        lateinit var firebaseUtil : FirebaseUtil
        lateinit var appContext : Context
    }

    override fun onCreate() {
        super.onCreate()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            firebaseUtil = FirebaseUtil()
            auth.removeAuthStateListener(this)
        }
        appContext = this
    }

    override fun onAuthStateChanged(state: FirebaseAuth) {
        if(state.currentUser != null) {
            firebaseUtil = FirebaseUtil()
            auth.removeAuthStateListener(this)
        }
    }
}
