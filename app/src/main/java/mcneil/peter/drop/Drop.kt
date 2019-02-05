package mcneil.peter.drop

import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class Drop : Application() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate() {
        auth = FirebaseAuth.getInstance()
        super.onCreate()
    }
}
